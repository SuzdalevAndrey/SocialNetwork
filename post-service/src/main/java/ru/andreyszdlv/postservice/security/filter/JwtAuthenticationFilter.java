package ru.andreyszdlv.postservice.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.andreyszdlv.postservice.context.RequestContext;
import ru.andreyszdlv.postservice.security.service.JwtSecurityService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtSecurityService jwtSecurityService;

    private final RequestContext requestContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("Executing JWT authentication");

        String header = request.getHeader("Authorization");

        if (StringUtils.isEmpty(header)
                || !StringUtils.startsWith(header, "Bearer ")) {
            log.error("Authorization header is missing or presented in the wrong format");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = header.substring(7);
        requestContext.setToken(jwt);
        String email = jwtSecurityService.extractUsername(jwt);
        String role = jwtSecurityService.extractRoles(jwt);

        if (StringUtils.isNotEmpty(email)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Extracted email: {}, role: {}", email, role);

            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    email, "", Stream.of(role).map(SimpleGrantedAuthority::new).toList());

            if (jwtSecurityService.validateToken(jwt)) {
                log.info("JWT is valid");

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                securityContext.setAuthentication(token);
                SecurityContextHolder.setContext(securityContext);
            }
            else {
                log.error("Invalid JWT token for email: {}", email);
            }
        }

        filterChain.doFilter(request, response);
    }
}

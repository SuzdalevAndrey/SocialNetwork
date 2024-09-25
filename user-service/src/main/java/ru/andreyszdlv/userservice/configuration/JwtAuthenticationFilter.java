package ru.andreyszdlv.userservice.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.andreyszdlv.userservice.service.jwt.JwtSecurityService;
import ru.andreyszdlv.userservice.service.jwt.UserService;

import java.io.IOException;

@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtSecurityService jwtSecurityService;

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (StringUtils.isEmpty(header)
                || !org.apache.commons.lang3.StringUtils.startsWith(header, "Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = header.substring(7);
        String email = jwtSecurityService.extractUsername(jwt);

        if (StringUtils.isNotEmpty(email)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService
                    .getDetailsService()
                    .loadUserByUsername(email);

            if (jwtSecurityService.validateToken(jwt, userDetails)) {
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(token);
                SecurityContextHolder.setContext(securityContext);
            }
        }
        filterChain.doFilter(request, response);
    }
}

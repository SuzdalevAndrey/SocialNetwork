//package ru.andreyszdlv.userservice.configuration;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Optional;
//
//@Component
//public class GatewayRequestFilter extends OncePerRequestFilter {
//    private static final Logger logger = LoggerFactory.getLogger(GatewayRequestFilter.class);
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        Optional<String> gatewayHeader = Optional.ofNullable(request.getHeader("Gateway-Request"));
//
//        logger.info("Gateway-Request header: {}", gatewayHeader);
//
//        if(gatewayHeader.orElse("").equals("Secret-Key")){
//            filterChain.doFilter(request, response);
//        }
//        else{
//            response.sendError(HttpStatus.FORBIDDEN.value(), "Forbidden: Access only via Gateway");
//        }
//    }
//}

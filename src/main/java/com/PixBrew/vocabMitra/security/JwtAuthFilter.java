package com.PixBrew.vocabMitra.security;

import com.PixBrew.vocabMitra.entity.Users;
import com.PixBrew.vocabMitra.service.UsersService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthUtil authUtil;
    private final UsersService usersService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String tokenHeader = request.getHeader("Authorization");
        if(tokenHeader == null || !tokenHeader.startsWith("Bearer")){
            filterChain.doFilter(request, response);
            return;
        }
        String token = tokenHeader.split("Bearer ")[1];
        String username = authUtil.getUsernameFromToken(token);
        //now for filling the securityContextHolder we need to craete an Authentication Objects
        // we will first create an UandPAythToken to pass to the Authentication parametrized constructor
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            Users user = usersService.findUserByUsername(username);
            UsernamePasswordAuthenticationToken uAndPAuthToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(uAndPAuthToken);
        }
        filterChain.doFilter(request, response);
    }
}

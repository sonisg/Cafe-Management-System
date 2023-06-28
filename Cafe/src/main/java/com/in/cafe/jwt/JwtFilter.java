package com.in.cafe.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    Claims claims = null;

    private String userName = null;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().matches("/user/login"))
        {
            filterChain.doFilter(request, response);
        }else if(request.getServletPath().matches("/user/signup")){
            filterChain.doFilter(request, response);
        }else if(request.getServletPath().matches("/user/forgotPassword")){
            filterChain.doFilter(request, response);
        }else if(request.getServletPath().matches("/user/checkToken")){
            filterChain.doFilter(request, response);
        }
        else{
            String authorizationHeader = request.getHeader("Authorization");
            String token = null;
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                token = authorizationHeader.substring(7);
                userName = jwtUtil.extractUserName(token);
                claims = jwtUtil.extractAllClaims(token);
            }

            if(userName!= null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = customerUserDetailsService.loadUserByUsername(userName);
                if(jwtUtil.validateToken(token, userDetails)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource()
                            .buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        }
    }

    public boolean isAdmin(){
        System.out.println("Role extracted from JWT: " + claims.get("user"));
        return "admin".equalsIgnoreCase((String) claims.get("user"));
    }

    public boolean isUser(){
        System.out.println("Role extracted from JWT: " + claims.get("user"));
        return "user".equalsIgnoreCase((String) claims.get("user"));
    }

    public String getCurrentUser(){
        return userName;
    }

}

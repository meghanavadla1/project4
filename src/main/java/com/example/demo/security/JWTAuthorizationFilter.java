package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.demo.security.SecurityConstants.*;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    //creating JWTAuthorizationFilter class for providing access to the user using inbuilt class BasicAuthenticationFilter

//constructor
    public JWTAuthorizationFilter(AuthenticationManager authManager) {
       //using super keyword
        super(authManager);
    }






    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        //this method takes the HttpServletRequest,HttpServletResponse,credentials
        String header = req.getHeader(HEADER_STRING);
     //get header is inbuilt method that takes string as input
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            //if the input is null or not starting with TOKEN_PREFIX
            chain.doFilter(req, res);//calling method from filterchain that takes the requests and responses

            return;
        }
////////if yes we do the following opertions
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        //filterchain method
        chain.doFilter(req, res);
    }






    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        //
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .getSubject();

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }
}

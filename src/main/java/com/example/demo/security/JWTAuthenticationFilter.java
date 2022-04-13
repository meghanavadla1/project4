package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.example.demo.security.SecurityConstants.*;

//users can only access their data, and that data can only be accessed in a secure way
public class JWTAuthenticationFilter  extends UsernamePasswordAuthenticationFilter{
// JSON Web Tokens (JWT) to handle the authorization

//UsernamePasswordAuthenticationFilter ia an inbuilt class uses th username and password for authentication
    private AuthenticationManager authenticationManager;
//inbuilt AuthenticationManager interface
    private Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
//in built logger

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
     //constructor
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        //method override for HttpServletRequest,HttpServletResponse
        try {
               //using try catch to hndle the exceptions
            //get the user credentials
            com.example.demo.model.persistence.User credentials = new ObjectMapper()

                    .readValue(req.getInputStream(), com.example.demo.model.persistence.User.class);
            //assign to user class
            return authenticationManager.authenticate(
                      new UsernamePasswordAuthenticationToken(
                            credentials.getUsername(),
                            credentials.getPassword(),
                            new ArrayList<>())
                    //returns the user username and password from credentials of new object UsernamePasswordAuthenticationToken
            );
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
            //throw the exception using logger

        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
     //this is also a inbuilt method that takes all requests and responses ,even filterchain is a inbuilt interface for
        //even Authentication is a inbuilt interface tht takes all the credentials,it also have getpriciple and getdetails methods
    String token = JWT.create().withSubject(((User) auth.getPrincipal()).getUsername()).withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).sign(HMAC512(SECRET.getBytes()));
        //from response
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }
}
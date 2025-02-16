package com.app.fitness_app.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    @Value("${jwt.key}")
    private String SECRET;

    private Key getSignKey(){
        byte [] keyBytes= Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String userName){
        Map<String,Object> claims= new HashMap<>();
        return createToken(claims,userName);
    }

    private String createToken(Map<String,Object> claims,String userName){
        return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userName)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis()+ 1200*60*4))
                    .signWith(getSignKey(),SignatureAlgorithm.HS256)
                    .compact();

    }

    private Date expirationDate(String token){
        Claims claims=Jwts.parserBuilder()
                            .setSigningKey(getSignKey())
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
        return claims.getExpiration();
    }

    public String exractUser(String token){
        Claims claims=Jwts.parserBuilder()
                            .setSigningKey(getSignKey())
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
        return claims.getSubject();
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        String userMail=exractUser(token);
        Date expirationDate=expirationDate(token);
        return userDetails.getUsername().equals(userMail)&& !expirationDate.before(new Date());
    }

    }
    


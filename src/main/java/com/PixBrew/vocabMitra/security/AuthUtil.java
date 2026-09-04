package com.PixBrew.vocabMitra.security;

import com.PixBrew.vocabMitra.entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
public class AuthUtil {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    //creating a method to return the secret key and using hmacshakey for encoding the key
    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }
    //now we will generate the token
    public String createAccessToken(Users user){
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*60))
                .signWith(getSecretKey())
                .compact();
    }
    //once the jwt is created and sent to user it will be saved
    // to sesison and we need to again de crypt it to check wether its the same jwt or not, so
    // to get the username part from the jwt we use the below method to parse any t
    // trailing spaces that can be present and send the username to the jwtAuhFilter
    // class extending the oncePerRequestFilter
    public String getUsernameFromToken(String token) {
        //clean trailing spaces from the username in the subject to dodge error
        String cleanedToken = token.trim().replaceAll("\\s", "");
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(cleanedToken)
                .getPayload();
        return claims.getSubject();
    }
}

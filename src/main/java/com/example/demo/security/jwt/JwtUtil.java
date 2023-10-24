package com.example.demo.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtUtil {//generar y validar token
	
	private String secret ="springboot";
	
	public String extractUsername(String token) {
		return extractClaims(token, Claims::getSubject);
	}
	
	public Date extractExpiration(String token) {//fecha de expiracipon del token
		return extractClaims(token, Claims::getExpiration);
	}
	
	public <T> T extractClaims( String token, Function<Claims, T> claimsResolver) { //extraer datos
		final Claims claims = extractAllClaims(token);	
		return claimsResolver.apply(claims);
	}
	
	public Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}
	
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());//verificar antes de la fecha
	}
	
	public String generateToken(String username,String role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", role);
		return createToken(claims,username);
	}
	
	private String createToken(Map<String, Object> claims, String subject) {//creación del token con expiración y verificar si se modificó
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 100*60*60*10))
				.signWith(SignatureAlgorithm.HS256,secret).compact();
	}
	
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername())&& !isTokenExpired(token));
	}
}

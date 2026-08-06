//package egovframework.smartbusmng.auth.jwt;
//
//import javax.annotation.PostConstruct;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import java.security.Key;
//import java.util.Date;
//import java.util.Base64;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class JwtTokenProvider {
//	@Value("${jwt.secret")
//	private String secretKey;
//	
//	@Value("${jwt.expiration}") // milliseconds
//	private long expireMillis;
//	
//	private Key key;
//	
//	@PostConstruct
//	protected void init() {
//		byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes());
//		this.key = Keys.hmacShaKeyFor(keyBytes);
//	}
//
//	public String generateToken(String facilityId) {
//		Date now = new Date();
//		Date expiryDate = new Date(now.getTime() + expireMillis);
//		
//		return Jwts.builder()
//				.setSubject(facilityId)
//				.setIssuedAt(now)
//				.setExpiration(expiryDate)
//				.signWith(key, SignatureAlgorithm.HS256)
//				.compact();
//	}
//	
//	public String getFacilityId(String token) {
//		return Jwts.parserBuilder()
//				.setSigningKey(key)
//				.build()
//				.parseClaimsJws(token)
//				.getBody()
//				.getSubject();
//	}
//	
//	public boolean validateToken(String token) {
//		try {
//			Jwts.parserBuilder()
//				.setSigningKey(key)
//				.build()
//				.parseClaimsJws(token);
//			return true;
//		} catch (SecurityException | MalformedJwtException e) {
//			log.warn("Invalid JWT signnature.");
//		} catch (ExpiredJwtException e) {
//			log.warn("Expired JWT token.");
//		} catch (UnsupportedJwtException e) {
//			log.warn("Unsupported JWT token.");
//		} catch (IllegalArgumentException e) {
//			log.warn("JWT claims string is empty.");
//		}
//		
//		return false;
//	}
//}

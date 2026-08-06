package egovframework.smartbusmng.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import egovframework.smartbusmng.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private final CustomUserDetailsService userDetailsService;
	
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String email = authentication.getName();
		String password = authentication.getCredentials().toString();
		
		UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		
		if (userDetails == null) {
			throw new BadCredentialsException("사용자를 찾을 수 없습니다.");
		}
		
		if (!userDetails.isEnabled()) {
			throw new BadCredentialsException("승인됮 않은 사용자입니다.");
		}
		System.out.println("입력 이메일: "+email);
		System.out.println("입력 비밀번호 : "+password);
//		String encoded = new BCryptPasswordEncoder().encode("12345");
//		System.out.println(encoded);
		
		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
		}
		
		System.out.println("접속 OK");
		return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
	}
	
	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
	
}

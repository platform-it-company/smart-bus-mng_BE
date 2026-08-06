/**
 * 파일명 : UserDetailsServiceImpl.java
 */
package egovframework.smartbusmng.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import egovframework.smartbusmng.mapper.AuthMapper;
import egovframework.smartbusmng.model.member.UserDetailsImpl;
import egovframework.smartbusmng.model.member.UserDto;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private AuthMapper authMapper;
	
	@Override
	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
		UserDto user = authMapper.findUserByEmail(userEmail);
		
		if (user == null) {
			throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
		}
		
		return new UserDetailsImpl(user);
	}
}

/**
 * 파일명 : UserDetailsImple.java
 * userDetails의 내용을 리턴
 */
package egovframework.smartbusmng.model.member;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = 1L;

	private final UserDto user;

	public UserDetailsImpl(UserDto user) {
		this.user = user;
	}

	public UserDto getUser() {
		return user;
	}

	public Integer getUserId() {
		return user.getUserId();
	}

	public String getUserEmail() {
		return user.getUserEmail();
	}

	public String getuserGroupId() {
		return user.getUserEmail();
	}

	public String getUserName() {
		return user.getUsername();
	}

	public String getGroupId() {
		return user.getGroupId();
	}

	public String getGroupRole() {
		return user.getGroupRole();
	}

	public String getGroupName() {
		return user.getGroupName();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(user.getGroupRole()));
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

//	@Override public String getUserEmail() { return user.getUserEmail(); }
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}

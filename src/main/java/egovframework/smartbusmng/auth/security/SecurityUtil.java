package egovframework.smartbusmng.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import egovframework.smartbusmng.model.member.UserDetailsImpl;

public class SecurityUtil {

	public static String getuserGroupId() {
		UserDetailsImpl userDetails = getCurrentUserDetails();
		return userDetails != null ? userDetails.getuserGroupId() : null;		
	}
	
	public static Integer getUserId() {
		UserDetailsImpl userDetails = getCurrentUserDetails();
		return userDetails != null ? userDetails.getUserId() : null;
	}
	
	public static String getUserName() {
		UserDetailsImpl userDetails = getCurrentUserDetails();
		return userDetails != null ? userDetails.getUserName() : null;
	}

	public static String getGroupId() {
		UserDetailsImpl userDetails = getCurrentUserDetails();
		return userDetails != null ? userDetails.getGroupId() : null;
	}

	public static String getGroupRole() {
		UserDetailsImpl userDetails = getCurrentUserDetails();
		return userDetails != null ? userDetails.getGroupRole() : null;
	}

	public static String getGroupName() {
		UserDetailsImpl userDetails = getCurrentUserDetails();
		return userDetails != null ? userDetails.getGroupName() : null;
	}

	public static String getUserEmail() {
		UserDetailsImpl userDetails = getCurrentUserDetails();
		return userDetails != null ? userDetails.getUserEmail() : null;
	}
	
	private static UserDetailsImpl getCurrentUserDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
			return (UserDetailsImpl) authentication.getPrincipal();
		}
		return null;
	}
}

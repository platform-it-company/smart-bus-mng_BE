/** 
 * 파일명CustomLoginSuccessHandler.java
 */
package egovframework.smartbusmng.auth.security;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import egovframework.smartbusmng.model.member.UserDetailsImpl;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
	@Override
	public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res,
	                                    Authentication auth) throws IOException {
	    HttpSession session = req.getSession();
	    Object principal = auth.getPrincipal();

	    // 예: UserDetailsImpl에 userGroupId, role 등이 있다면 꺼내서
	    String useremail = ((UserDetailsImpl) principal).getUsername(); // userEmail이 설정됨
	    String userRole = auth.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse(null);
	    String username = ((UserDetailsImpl) principal).getUsername();
	    String groupId = ((UserDetailsImpl) principal).getGroupId(); // 또는 getUserGroupId()
	    String groupName = ((UserDetailsImpl) principal).getGroupName(); // 또는 getUserGroupId()
	    Integer userId = ((UserDetailsImpl) principal).getUserId();
	    
	    session.setAttribute("userId", userId);
	    session.setAttribute("userName", username);
	    session.setAttribute("userRole", userRole);
	    session.setAttribute("groupId", groupId);
	    session.setAttribute("groupName", groupName);
	    session.setAttribute("userEmail", useremail);
	    session.setMaxInactiveInterval(30 * 60);
	    session.setAttribute("lastUserTouchAt", System.currentTimeMillis());

	    // AJAX 요청이면 JSON, 아니면 원하는 URL로 리다이렉트
	    if ("XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))) {
	        res.setContentType("application/json;charset=UTF-8");
	        res.getWriter().write("{\"success\":true}");
	    } else {
	        res.sendRedirect("/"); // or 원하는 URL
	    }
	}
}
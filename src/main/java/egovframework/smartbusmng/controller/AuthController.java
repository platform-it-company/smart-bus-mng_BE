//package egovframework.smartbusmng.controller;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@RestController
//@RequestMapping("/api/auth/session")
//public class AuthController {
//	
//	@Autowired
//	private ObjectMapper objectMapper;
//	
//	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
//	private static final int SESSION_TIMEOUT_SECONDS = 30 * 60;
//	
//	/**
//	 * [POST] 세션 연장 요청 - 인증된 사용자만 가능 
//	 * @param session
//	 * @return
//	 */
//	@PostMapping("/extend")
//	public ResponseEntity<Map<String, Object>> extendSession(HttpSession session) {
//	  Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//	  if (auth == null || !auth.isAuthenticated()
//	      || auth.getPrincipal().equals("anonymouseUser")) {
//	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//	      .body(Map.of("message", "인증이 필요합니다."));
//	  }
//	  session.setMaxInactiveInterval(30 * 60);
//	  session.setAttribute("lastUserTouchAt", System.currentTimeMillis()); // ★ 키 통일
//	  return ResponseEntity.ok(Map.of("message","세션이 연장되었습니다.","timeout",30*60));
//	}
//	
//	/**
//	 * [GET] 세션 남은 시간 조회 API
//	 * - 만료된 경우 로그인 페이지로 리다이렉트
//	 * @param session
//	 * @return
//	 */
//	@GetMapping("/remain")
//	public ResponseEntity<Map<String, Object>> getRemainTime(HttpSession session) {
//	  Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//	  boolean authenticated = auth != null && auth.isAuthenticated()
//	      && !(auth instanceof AnonymousAuthenticationToken);
//	  if (!authenticated) return ResponseEntity.status(401).build();
//
//	  final int max = session.getMaxInactiveInterval(); // 초
//	  Long lastUserTouch = (Long) session.getAttribute("lastUserTouchAt");
//	  if (lastUserTouch == null) {
//	    lastUserTouch = session.getLastAccessedTime();  // 최초 1회 대체
//	    session.setAttribute("lastUserTouchAt", lastUserTouch);
//	  }
//	  long now = System.currentTimeMillis();
//	  int elapsed = (int)((now - lastUserTouch) / 1000);
//	  int remain = Math.max(0, max - elapsed);
//
//	  return ResponseEntity.ok(Map.of("remainSeconds", remain, "maxInactiveSeconds", max));
//	}
//}

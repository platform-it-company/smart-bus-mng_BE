package egovframework.smartbusmng.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egovframework.smartbusmng.model.member.GroupUserInfo;
import egovframework.smartbusmng.model.member.MemberDto;
import egovframework.smartbusmng.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class HomeController {

    @Autowired
    MemberService memberService;

    @GetMapping("/login")
    public ResponseEntity<?> loginPage() {
    	return ResponseEntity.ok().body(Map.of("message", "login page"));
    }
    
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam("userEmail") String userEmail,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();

        System.out.println("[login]userEmail: "+userEmail+",password:"+password);
        String role = null;
        if ("ADMIN".equals(userEmail) && "adminpass".equals(password)) role = "ROLE_ADMIN";
        else if ("USER".equals(userEmail) && "userpass".equals(password)) role = "ROLE_USER";

        if (role == null) {
            result.put("success", false);
            result.put("message", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return ResponseEntity.ok(result);
        }

        // 세션 값
        session.setAttribute("userEmail", userEmail);
        session.setAttribute("userRole", role);
        session.setMaxInactiveInterval(30 * 60);
        session.setAttribute("lastUserTouchAt", System.currentTimeMillis());
        
        List<GrantedAuthority> auths =
                java.util.Collections.singletonList(new SimpleGrantedAuthority(role));

            org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(userEmail, "", auths);

            org.springframework.security.authentication.UsernamePasswordAuthenticationToken authentication =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(principal, null, auths);

            org.springframework.security.core.context.SecurityContext sc =
                org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
            sc.setAuthentication(authentication);
            org.springframework.security.core.context.SecurityContextHolder.setContext(sc);

            new org.springframework.security.web.context.HttpSessionSecurityContextRepository()
                .saveContext(sc, request, response);

            result.put("success", true);
            // UI 편의상 ROLE_ 제거한 값도 주고 싶다면:
            result.put("userRole", role.startsWith("ROLE_") ? role.substring(5) : role);
            return ResponseEntity.ok(result);
    }
    
    @GetMapping("/auth/me")
    public ResponseEntity<Map<String, Object>> me(HttpSession session, Authentication auth) {
        boolean authenticated = auth != null
                && auth.isAuthenticated()
                && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);

        if (!authenticated) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        Map<String, Object> body = new HashMap<>();
        body.put("authenticated", true);
        body.put("userId", session.getAttribute("userId"));
        body.put("userRole", session.getAttribute("userRole"));       // null OK
        body.put("groupId", session.getAttribute("groupId"));         // null OK
        body.put("userName", session.getAttribute("userName"));
        body.put("groupName", session.getAttribute("groupName"));
        
        return ResponseEntity.ok(body);
    }

    @GetMapping("/auth/session/remain")
    public ResponseEntity<Map<String,Object>> remain(HttpSession session, Authentication auth) {
        boolean authenticated = (auth != null && auth.isAuthenticated()
                && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken));
        if (!authenticated) {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "status", 401,
                "error", "Unauthorized",
                "message", "로그인이 필요합니다."
            ));
        }

        final int max = session.getMaxInactiveInterval(); // 초
        Long lastUserTouch = (Long) session.getAttribute("lastUserTouchAt");
        if (lastUserTouch == null) {
            lastUserTouch = session.getLastAccessedTime();
            session.setAttribute("lastUserTouchAt", lastUserTouch);
        }
        long now = System.currentTimeMillis();
        int elapsed = (int)((now - lastUserTouch) / 1000);
        int remain = Math.max(0, max - elapsed);

        // 사용자 정보도 내려주고 싶으면
        String name = auth.getName();
        String role = auth.getAuthorities().stream().findFirst()
                      .map(GrantedAuthority::getAuthority).orElse(null);

        Map<String,Object> body = new HashMap<>();
        body.put("success", true);
        body.put("remainSeconds", remain);
        body.put("maxInactiveSeconds", max);
        body.put("userName", name);
        body.put("userRole", role);
        return ResponseEntity.ok(body);
    }
    
    // 보조 API들 (원래 쓰시던 것)
    @GetMapping("/login/groups")
    public ResponseEntity<List<GroupUserInfo>> getGroupList() {
        return ResponseEntity.ok(memberService.getLoginGroupList());
    }

    @PostMapping("/checkEmail")
    public ResponseEntity<Map<String, String>> checkEmail(@RequestBody Map<String, String> req) {
        boolean exists = memberService.existsByEmail(req.get("userEmail"));
        return ResponseEntity.ok(Map.of(
            "success", exists ? "true" : "false",
            "message", exists ? "사용 가능한 메일입니다." : "이미 사용 중인 이메일입니다."
        ));
    }

    @PostMapping("/checkGroupKey")
    public ResponseEntity<Map<String, String>> checkCompanyId(@RequestBody Map<String, String> req) {
        boolean valid = memberService.isValidGroupKey(req.get("groupKey"), req.get("groupId"));
        return ResponseEntity.ok(Map.of(
            "success", valid ? "true" : "false",
            "message", valid ? "인증 성공하였습니다." : "인증 실패하였습니다."
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody MemberDto memberDto, HttpServletRequest request) {
        String ip = getClientIp(request);
        memberDto.setLastIp(ip);
        boolean ok = memberService.registerMember(memberDto);
        return ResponseEntity.ok(Map.of(
            "success", ok ? "true" : "false",
            "message", ok ? "등록 완료" : "등록 실패"
        ));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getHeader("Proxy-Client-IP");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getRemoteAddr();
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}

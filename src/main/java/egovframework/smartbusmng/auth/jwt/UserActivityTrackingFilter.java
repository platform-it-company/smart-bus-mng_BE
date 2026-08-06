package egovframework.smartbusmng.auth.jwt;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class UserActivityTrackingFilter extends OncePerRequestFilter {
  private static final List<String> EXCLUDES = List.of(
      "/auth/session/remain", "/auth/session/extend", "/login", "/logout",
      "/css/", "/js/", "/images/", "/webjars/"
  );

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return EXCLUDES.stream().anyMatch(uri::startsWith)
        || "OPTIONS".equalsIgnoreCase(request.getMethod());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    try {
      chain.doFilter(req, res);
    } finally {
      HttpSession session = req.getSession(false);
      if (session != null) {
        session.setAttribute("lastUserTouchAt", System.currentTimeMillis());
      }
    }
  }
}
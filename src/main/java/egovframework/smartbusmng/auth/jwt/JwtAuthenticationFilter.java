//package egovframework.smartbusmng.auth.jwt;
//
//import java.io.IOException;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter{
//	private final JwtTokenProvider jwtTokenProvider;
//	private final CustomFacilityDetailsService customFacilityDetailsService;
//	
//	@Override 
//	protected void doFilterInternal(HttpServletRequest request, 
//									HttpServletResponse response,
//									FilterChain filterChain)
//									throws ServletException, IOException {
//		String token = resolveToken(request);
//		
//		if (token != null && jwtTokenProvider.validateToken(token)) {
//			String facilityId = jwtTokenProvider.getFacilityId(token);
//			
//			var userDetails = customFacilityDetailsService.loadUserByUsername(facilityId);
//			var authentication = new UsernamePasswordAuthenticationToken(
//					userDetails, null, userDetails.getAuthorities());
//			
//			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));;
//			
//			SecurityContextHolder.getContext().setAuthentication(authentication);
//		}
//		
//		filterChain.doFilter(request, response);
//	}
//	
//	private String resolveToken(HttpServletRequest request) {
//		String bearer = request.getHeader("Authorization");
//		if (bearer != null && bearer.startsWith("Bearer ")) {
//			return bearer.substring(7);
//		}
//		
//		return null;
//	}
//}

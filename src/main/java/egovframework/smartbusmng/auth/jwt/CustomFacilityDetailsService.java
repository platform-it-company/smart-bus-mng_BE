//package egovframework.smartbusmng.auth.jwt;
//
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class CustomFacilityDetailsService implements UserDetailsService {
//	
//	private final FacilityDeviceMapper facilityDeviceMapper;
//	
//	@Override
//	public UserDetails loadUserByUsername(String facilityId) throws UsernameNotFoundException {
//		FacilityDeviceEntity device = facilityDeviceMapper.findByFacilityId(facilityId);
//		
//		if (device == null) {
//			throw new UsernameNotFoundException("등록되지 않은 장비입다: " + facilityId);
//		}
//		
//		return User.builder()
//				.username(device.getFacilityId())
//				.password(device.getDeviceKey())
//				.roles("DEVICDES")
//				.build();
//	}
//}

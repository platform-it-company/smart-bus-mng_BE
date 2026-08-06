package egovframework.smartbusmng.model.oper;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverListRow {

	private Long driverId;
	private String driverName;
	private String loginId;
	private String phone;
	private String email;
	private String groupNm;
	
	private Integer status;
	private Integer acceptedYn;
	private String vehNo;
	private String routeId;
	private String routeNm;
	private String routeTp;

	private LocalDateTime lastLoginDt; 	// 앱 로그인
	private LocalDateTime createdDt;	// 생성
	private LocalDateTime acceptedDt;	// 승인
	
	private String acceptedBy;

}

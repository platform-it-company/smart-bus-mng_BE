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
public class DriverInfo {

	private Long driverId;
	
	private String loginId;
	private String password;
	private String driverName;
	private String phone;
	private String email;
	
	private String groupId;
	private String groupNm;
	
	private Integer status;
	private Integer acceptedYn;
	
	private Integer vehId;
	private String vehNo;
	private String routeId;
	private String routeNm;
	private String routeTp;
	
	private LocalDateTime lastLoginAt;
	
	private LocalDateTime createdDt;
	private String createdBy;
	
	private LocalDateTime updatedDt;
	private String updatedBy;
	
	private LocalDateTime acceptedDt;
	private String acceptedBy;
}

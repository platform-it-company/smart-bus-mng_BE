package egovframework.smartbusmng.model.oper;

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
public class DriverPendingRow {

	private Long driverId;
	private String driverName;
	private String loginId;
	private String phone;
	private String email;
	private String groupNm;
	
	private Integer status;	//계정상태(0:PENDING,1:ACTIVE,2:SUSPENDED,3:LEAVE,4:DELETED)
	private Integer acceptedYn; // 1:승인, 0/null:대기
}

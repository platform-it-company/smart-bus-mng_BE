package egovframework.smartbusmng.model.oper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverAddReq {

	private String loginId;
	private String password;
	private String driverName;
	private String phone;
	private String email;
	
	private String groupId;
	
	private Integer vehId;
	private String vehNo;
	private String routeId;
	private String routeNm;
	private String routeTp;
}

package egovframework.smartbusmng.model.facility;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityVehInfoDto {
	private String facilityId;
	private int vehId;
	private String vehNo;
	private String routeNm;
	private String routeDc;
	private String statCd;
	private String useTp;
	private String attchment;
	private String ip;  
	private String publicIp;
	private Date lastUpdtDt;
	private String groupId;
}

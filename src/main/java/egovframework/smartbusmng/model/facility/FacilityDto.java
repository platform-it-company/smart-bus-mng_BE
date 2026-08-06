package egovframework.smartbusmng.model.facility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityDto {
	private String facilityId;
	private String groupId;
	private String modelId;
	private String modelNm;
	private String manufacturer;
	private Integer unitPrice;
	private String installDe;
	private String displayRes;
	private String routeMapRes;
	private String adAreaRes;
	private String useTp;
	private String statCd;
	private int vehId;
}

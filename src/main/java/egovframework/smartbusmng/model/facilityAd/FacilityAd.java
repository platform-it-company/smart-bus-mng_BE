package egovframework.smartbusmng.model.facilityAd;

import lombok.Data;

@Data
public class FacilityAd {
	private Long id;
	private String facilityId;
	private String adId;
	private String groupId;
	
	private String frstRegistDt; // 또는 LocalDate frstRegistDt;
	private String frstUserId;
	private String frstRegistProgrm;
	private String lastUpdtDt; // 또는 LocalDate lastUpdtDt;
	private String lastUserId;
	private String lastRegistProgrm;
}

package egovframework.smartbusmng.model.facilityCntr;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityCntrMsg {
	private String facilityId;
	private String emerMsg;
	private Date sendDt;
	private Date frstRegistDt; // 또는 LocalDate frstRegistDt;
	private String frstUserId;
	private String frstRegistProgrm;
	private Date lastUpdtDt; // 또는 LocalDate lastUpdtDt;
	private String lastUserId;
	private String lastRegistProgrm;
}               
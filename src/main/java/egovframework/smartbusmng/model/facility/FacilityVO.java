package egovframework.smartbusmng.model.facility;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityVO {
	private long id;
	private String facilityId;
	private String groupId;
	private String modelId;
	private String installDe;
	private String useTp;
	private String statCd;
	private int vehId;
	private Date frstRegistDt;
	private String frstUserId;
	private String frstRegistProgram;
	private Date lastUpdtDt;
	private String lastUserId;
	private String lastRegistProgram;
}

package egovframework.smartbusmng.model.facilityMa;

import java.util.Date;

import lombok.Data;

@Data
public class FacilityMaDto {
    private String facilityId;      // PK1
    private Date   maintainDt;      // PK2
    private String faultTp;         // PK3
    private String maintainTp;      // PK4

    private String maintainMemo;
    private String maintainPerson;
    private String maintainAttach;

    private Date   frstRegistDt;
    private String frstUserId;
    private String frstRegistProgram;

    private Date   lastRegistDt;
    private String lastUserId;
    private String lastRegistProgram;
}

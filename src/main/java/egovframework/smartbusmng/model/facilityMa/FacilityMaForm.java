package egovframework.smartbusmng.model.facilityMa;

import lombok.Data;

@Data
public class FacilityMaForm {
    private String facilityId;
    private String maintainDt;      // "yyyy-MM-dd" 혹은 "yyyy-MM-dd HH:mm:ss"
    private String faultTp;
    private String maintainTp;

    private String maintainMemo;
    private String maintainPerson;
    private String maintainAttach;  // 기존 첨부 코드가 있다면

    // 필요하면 나중에 검증 애노테이션(@NotBlank 등) 추가
}
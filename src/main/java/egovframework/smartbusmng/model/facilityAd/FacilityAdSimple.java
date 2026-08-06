package egovframework.smartbusmng.model.facilityAd;

import lombok.Data;

@Data
public class FacilityAdSimple {
    private String facilityId;  // 시설 ID
    private String vehno;     // 차량 번호
    private String routenm;   // 노선명
    private String routeDc;   // 노선 설명
}

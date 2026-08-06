package egovframework.smartbusmng.model.oper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VehicleInfo {

    private Integer vehId;        // edit 시 필수, add 시 null 가능(직접 입력이면 필수)
    private String vehNo;
    private String groupId;
    private String groupNm;

    private String vin;

    private String makerNm;
    private String modelNm;
    private String modelYear;

    private Integer fuelTp;
    private String euro6Yn;

    private String lowfloorYn;
    private String wheelchairYn;

    private Integer passengerCap;
    private Integer junkTp;

    private String opratAt;       // 운행여부(Y/N)
    private String opratTp;       // 운행타입(코드/문자열)

    private String managerNm;
    private String managerPhone;

    private String remark;
}

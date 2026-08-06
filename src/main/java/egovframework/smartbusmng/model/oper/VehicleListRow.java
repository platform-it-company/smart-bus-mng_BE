package egovframework.smartbusmng.model.oper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VehicleListRow {
	private Integer vehId;
	private String vehNo;
	private String groupId;
	private String groupNm;
	private String vin;
	private String makerNm;
	private String modelNm;
	private String modelYear;
	private Integer fuelTp;
	private String lowfloorYn;
	private String wheelchairYn;
	private String opratAt;
	private String managerNm;
	private String managerPhone;
}

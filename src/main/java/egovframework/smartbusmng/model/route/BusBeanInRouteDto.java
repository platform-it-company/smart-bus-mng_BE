package egovframework.smartbusmng.model.route;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusBeanInRouteDto {
	private int vehId;
	private String vehNo;
	private int routeId;
	private int routeVer;
	private String routeNm;
	private String routeDc;
}

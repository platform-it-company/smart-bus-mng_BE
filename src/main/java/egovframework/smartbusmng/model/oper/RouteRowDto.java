package egovframework.smartbusmng.model.oper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteRowDto {

	private String routeId;
	private int routeVer;
	private String routeNm;
	private String routeTp;

	private String fircarTm;
	private String lstcarTm;
	private Integer caralc;
	
	private String fircarTmS; 	// 주말,주일
	private String lstcarTmS;
	private Integer caralcS;
	
	private String fircarTmH;	// 연휴
	private String lstcarTmH;
	private Integer caralcH;
	
	private String ssttnNm;
	private String esttnNm;
	
	private String useAt;
}

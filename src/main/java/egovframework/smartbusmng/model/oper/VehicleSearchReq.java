package egovframework.smartbusmng.model.oper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VehicleSearchReq {
	private String selGroupId;
    private int vehId;     // 차량번호(부분검색)
    private String makerNm;   // 제조사
    
	@Builder.Default    
    private int page = 1;
    
	@Builder.Default
	private int size = 20;
}

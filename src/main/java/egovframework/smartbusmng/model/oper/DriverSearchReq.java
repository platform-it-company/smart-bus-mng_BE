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
public class DriverSearchReq {
	@Builder.Default
	private String driverName = null;
	
	@Builder.Default
	private Integer status = null;
	
	@Builder.Default
	private int page = 1;
	
	@Builder.Default
	private int size = 20;
}
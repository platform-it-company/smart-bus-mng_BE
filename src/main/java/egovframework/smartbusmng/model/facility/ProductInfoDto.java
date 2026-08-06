package egovframework.smartbusmng.model.facility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductInfoDto {

	private String groupId;
	private String modelId;
	private String modelNm;
	private String manufacturer;
	private Integer unitPrice;
	private String displayRes;
	private String routeMapRes;
	private String adAreaRes;
	
	private String frstUserId;
	private String frstRegistTool;
	private String lastUserId;
	private String lastRegistTool;
	
}

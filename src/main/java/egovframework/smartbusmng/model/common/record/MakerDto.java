package egovframework.smartbusmng.model.common.record;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MakerDto {
	private String makerNm;
	private List<VehModelDto> models = new ArrayList<>();
}

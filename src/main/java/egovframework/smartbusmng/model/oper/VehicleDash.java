package egovframework.smartbusmng.model.oper;

import java.util.List;

import egovframework.smartbusmng.model.common.record.InitVehListItem;
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
public class VehicleDash {

	private long total;
	private List<InitVehListItem> vehList;
	private List<String> makerList;
}

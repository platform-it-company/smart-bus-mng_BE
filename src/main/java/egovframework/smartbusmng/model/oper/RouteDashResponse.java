package egovframework.smartbusmng.model.oper;

import java.util.List;

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
public class RouteDashResponse {

	private long totalCount;
	private List<String> routeTpList;
	private List<RouteSimpleDto> routeList;
	private List<RouteTpCountDto> routeTpCounts;
}

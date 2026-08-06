package egovframework.smartbusmng.model.member;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuDto {
	private Integer menuId;
	private Integer parentId;
	private String menuNm;
	private String menuPath;
	private String role;
	private Integer sortOrder;
	private String url;
	private List<MenuDto> children;
}

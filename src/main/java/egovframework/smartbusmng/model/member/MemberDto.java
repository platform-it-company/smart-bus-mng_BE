package egovframework.smartbusmng.model.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDto {

	private Long userId;
	private String userEmail;
	private String groupId;
	private String groupKey;
	private String groupRole;
	private String password;
	private String managerName;
	private String managedepart;
	private String telNo;
	private String lastIp;
}
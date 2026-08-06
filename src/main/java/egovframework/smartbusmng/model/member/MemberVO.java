package egovframework.smartbusmng.model.member;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberVO {

	private Long userId;
	private String userEmail;
	private String groupId;
	private String groupNm;
	private String groupKey;
	private String groupRole;
	private String password;
	private String userName;
	private String manageDepart;
	private String telNo;
	private LocalDateTime createDt;
	private LocalDateTime updateDt;
	private String lastIp;
	private String confirmedUser;
	private LocalDateTime acceptDt;
	private String withdrawYn;
}
/**
 * 파일명 : UserVO.java
 */
package egovframework.smartbusmng.model.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVO {
	private String userId;
	private String userEmail;
	private String password;
	private String username;
	private String groupId;
	private String groupName;
	private String groupRole;
}

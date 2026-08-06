/**
 * 파일명 : UserDto.java
 */
package egovframework.smartbusmng.model.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
	private Integer userId;
	private String userEmail;
	private String password;
	private String username;
	private String groupId;
	private String groupName;
	private String groupRole;
}
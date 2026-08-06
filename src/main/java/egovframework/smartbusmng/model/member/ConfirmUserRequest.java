package egovframework.smartbusmng.model.member;

import lombok.Data;

@Data
public class ConfirmUserRequest {
	private String userEmail;
	private String groupId;
	private String userName;
}

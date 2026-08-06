package egovframework.smartbusmng.model.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
	private String groupId;
	private String userName;
	private String currentPassword;
	private String newPassword;
}

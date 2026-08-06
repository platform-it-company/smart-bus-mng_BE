package egovframework.smartbusmng.model.notice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoticeReq {

    @NotBlank(message = "groupId는 필수입니다.")
    @Size(max = 30, message = "groupId는 30자 이하여야 합니다.")
    private String groupId;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
    private String notiTitle;

    @NotBlank(message = "내용은 필수입니다.")
    private String notiContent;

    @NotBlank(message = "작성자는 필수입니다.")
    @Size(max = 100, message = "작성자는 100자 이하여야 합니다.")
    private String notiWriter;
}
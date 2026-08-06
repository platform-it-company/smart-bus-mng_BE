package egovframework.smartbusmng.model.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "공지사항 수정 요청")
public class NoticeEditReq {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "제목", example = "배차 점검 일정 변경")
    private String notiTitle;

    @NotBlank
    @Schema(description = "내용", example = "점검 시간이 변경되었습니다.")
    private String notiContent;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "작성자", example = "관리자")
    private String notiWriter;
}
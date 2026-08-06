package egovframework.smartbusmng.model.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "공지사항 등록 요청")
public class NoticeSaveReq {

    @NotBlank
    @Size(max = 30)
    @Schema(description = "그룹 ID", example = "GROUP001")
    private String groupId;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "제목", example = "배차 점검 안내")
    private String notiTitle;

    @NotBlank
    @Schema(description = "내용", example = "4월 25일 점검 예정입니다.")
    private String notiContent;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "작성자", example = "관리자")
    private String notiWriter;
}
package egovframework.smartbusmng.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egovframework.smartbusmng.model.notice.Notice;
import egovframework.smartbusmng.model.notice.NoticeEditReq;
import egovframework.smartbusmng.model.notice.NoticeSaveReq;
import egovframework.smartbusmng.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
@Validated
@Tag(name = "공지사항 관리 API", description = "공지사항 CRUD API")
public class NoticeController {

    private final NoticeService noticeService;

    // =========================
    // 목록 조회 + 검색
    // =========================
    @Operation(summary = "공지사항 목록 조회", description = "제목 검색 및 페이징 지원")
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getNoticeList(
            @Parameter(description = "그룹 ID", example = "GROUP001")
            @RequestParam(value = "groupId", required = false) String groupId,

            @Parameter(description = "제목 검색 키워드")
            @RequestParam(value = "keyword", required = false) String keyword,

            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(value = "page", defaultValue = "1") int page,

            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(value = "size", defaultValue = "10") int size) {

        return ResponseEntity.ok(
                noticeService.getNoticeList(groupId, keyword, page, size)
        );
    }

    // =========================
    // 상세 조회
    // =========================
    @Operation(summary = "공지사항 상세 조회")
    @GetMapping("/{notiId}")
    public ResponseEntity<?> getNotice(
            @Parameter(description = "공지 ID") @PathVariable Integer notiId,
            @Parameter(description = "그룹 ID") @RequestParam String groupId) {

        Notice notice = noticeService.getNotice(notiId, groupId);

        if (notice == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "공지사항을 찾을 수 없습니다."));
        }

        return ResponseEntity.ok(notice);
    }

    // =========================
    // 등록
    // =========================
    @Operation(summary = "공지사항 등록")
    @PostMapping("/saveNoti")
    public ResponseEntity<?> saveNotice(
            @Valid @RequestBody NoticeSaveReq req) {

        noticeService.createNotice(req);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "공지사항이 등록되었습니다."));
    }

    // =========================
    // 수정
    // =========================
    @Operation(summary = "공지사항 수정")
    @PutMapping("/edit/{notiId}")
    public ResponseEntity<?> editNotice(
            @Parameter(description = "공지 ID") @PathVariable Integer notiId,
            @Parameter(description = "그룹 ID") @RequestParam String groupId,
            @Valid @RequestBody NoticeEditReq req) {

        noticeService.updateNotice(notiId, groupId, req);

        return ResponseEntity.ok(Map.of("message", "공지사항이 수정되었습니다."));
    }

    // =========================
    // 삭제
    // =========================
    @Operation(summary = "공지사항 삭제")
    @DeleteMapping("/delete/{notiId}")
    public ResponseEntity<?> deleteNotice(
            @Parameter(description = "공지 ID") @PathVariable Integer notiId,
            @Parameter(description = "그룹 ID") @RequestParam String groupId) {

        noticeService.deleteNotice(notiId, groupId);

        return ResponseEntity.ok(Map.of("message", "공지사항이 삭제되었습니다."));
    }
}
package egovframework.smartbusmng.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import egovframework.smartbusmng.auth.security.SecurityUtil;
import egovframework.smartbusmng.model.member.ChangePasswordRequest;
import egovframework.smartbusmng.model.member.ConfirmUserRequest;
import egovframework.smartbusmng.model.member.GroupUserInfo;
import egovframework.smartbusmng.model.member.MemberVO;
import egovframework.smartbusmng.model.member.MenuDto;
import egovframework.smartbusmng.model.member.OperatorDto;
import egovframework.smartbusmng.model.member.WithdrawRequest;
import egovframework.smartbusmng.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/memberMng")
@Tag(
    name = "회원 관리",
    description = "회원, 관리자 승인, 운수사 및 메뉴 관련 API"
)
public class MemberController {

    @Autowired
    private MemberService memberService;

    // =======================================================
    // 그룹 목록 조회
    // =======================================================
    @Operation(
        summary = "그룹 목록 조회",
        description = "회원가입 및 회원 관리 화면에서 사용할 사용자 그룹(운수사) 목록을 조회합니다."
    )
    @GetMapping("/getGroupList")
    @ResponseBody
    public Map<String, Object> getGroupList() {

        List<GroupUserInfo> groupList = memberService.getGroupList();

        Map<String, Object> response = new HashMap<>();
        response.put("data", groupList);

        System.out.println(
            "## memberController : data: groupList : " + groupList.size()
        );

        return response;
    }


    // =======================================================
    // 메뉴 목록 조회
    // =======================================================
    @Operation(
        summary = "메뉴 목록 조회",
        description = "사용자에게 제공할 시스템 메뉴 목록을 조회합니다."
    )
    @GetMapping("/getMenuList")
    @ResponseBody
    public List<MenuDto> getMenus() {
        return memberService.getMenus();
    }


    // =======================================================
    // 회원 승인 처리
    // =======================================================
    @Operation(
        summary = "회원 승인 처리",
        description = "가입 대기 중인 회원을 승인 처리합니다. 여러 회원을 한 번에 승인할 수 있습니다."
    )
    @PutMapping("/confirmUsers")
    public ResponseEntity<Map<String, Object>> confirmMember(
        @RequestBody List<ConfirmUserRequest> reqUsers
    ) {

        int updatedCount = memberService.confirmMember(reqUsers);

        Map<String, Object> response = new HashMap<>();
        response.put("requested", reqUsers);
        response.put("updated", updatedCount);

        return ResponseEntity.ok(response);
    }


    // =======================================================
    // 운수사 목록 조회
    // =======================================================
    @Operation(
        summary = "운수사 목록 조회",
        description = "등록된 운수사 목록을 조회합니다."
    )
    @GetMapping("/operators")
    public ResponseEntity<List<OperatorDto>> getOperators() {
        return ResponseEntity.ok(memberService.getOperators());
    }


    // =======================================================
    // 운수사 신규 등록
    // =======================================================
    @Operation(
        summary = "운수사 신규 등록",
        description = "새로운 운수사 정보를 등록합니다."
    )
    @PostMapping("/operator")
    public ResponseEntity<Integer> insertOperator(
        @RequestBody OperatorDto operatorDto
    ) {

        int result = memberService.insertOperator(operatorDto);

        return ResponseEntity.ok(result);
    }


    // =======================================================
    // 운수사 정보 수정
    // =======================================================
    @Operation(
        summary = "운수사 정보 수정",
        description = "운수사 ID를 기준으로 기존 운수사 정보를 수정합니다. 관리자 권한이 필요합니다."
    )
    @PutMapping("/operator/{userGroupId}")
    public ResponseEntity<?> updateOperator(
        @PathVariable ("userGroupId") String userGroupId,
        @RequestBody OperatorDto operatorDto
    ) {

        final String userRole = SecurityUtil.getGroupRole();

        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("관리자 권한이 필요합니다.");
        }

        if (userGroupId == null || userGroupId.isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body("운수사의 id를 전달하여야 합니다.");
        }

        operatorDto.setGroupId(userGroupId);

        int result = memberService.updateOperator(operatorDto);

        return ResponseEntity.ok(result);
    }


    // =======================================================
    // 승인 회원 목록 조회
    // =======================================================
    @Operation(
        summary = "승인 회원 목록 조회",
        description = "승인 완료된 회원 목록을 조회합니다. page와 size를 전달하면 페이징 조회를 수행합니다."
    )
    @GetMapping("/member/approved")
    public ResponseEntity<List<MemberVO>> getApprovedMembers(
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size
    ) {

        List<MemberVO> members;

        if (page != null && size != null) {

            int offset = page * size;

            members = memberService.getApprovedMembersPaged(offset, size);

        } else {

            members = memberService.getApprovedMembersAll();
        }

        return ResponseEntity.ok(members);
    }


    // =======================================================
    // 승인 대기 회원 목록 조회
    // =======================================================
    @Operation(
        summary = "승인 대기 회원 목록 조회",
        description = "회원가입 후 관리자 승인을 기다리고 있는 회원 목록을 조회합니다. page와 size를 전달하면 페이징 조회를 수행합니다."
    )
    @GetMapping("/member/pending")
    public ResponseEntity<List<MemberVO>> getPendingMembers(
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size
    ) {

        List<MemberVO> members;

        if (page != null && size != null) {

            int offset = page * size;

            members = memberService.getPendingMembersPaged(offset, size);

        } else {

            members = memberService.getPendingMembersAll();
        }

        return ResponseEntity.ok(members);
    }


    // =======================================================
    // 회원 상세 정보 조회
    // =======================================================
    @Operation(
        summary = "회원 상세 정보 조회",
        description = "회원 ID, 그룹 ID, 사용자 이름을 기준으로 회원 상세 정보를 조회합니다."
    )
    @GetMapping("/member/{userId}")
    public ResponseEntity<MemberVO> getMember(
        @PathVariable (name="userId") Long userId,
        @RequestParam("groupId") String groupId,
        @RequestParam("userName") String userName
    ) {

        MemberVO member = memberService.getMemberInfo(
            userId,
            groupId,
            userName
        );

        if (member == null) {
            return ResponseEntity.notFound().build();
        }

        // 비밀번호는 응답에 포함하지 않음
        member.setPassword(null);

        return ResponseEntity.ok(member);
    }


    // =======================================================
    // 회원 정보 수정
    // =======================================================
    @Operation(
        summary = "회원 정보 수정",
        description = "로그인한 사용자의 회원 정보를 수정합니다. 로그인 사용자와 요청 대상 사용자가 일치해야 합니다."
    )
    @PutMapping("/member/{userId}")
    public ResponseEntity<Integer> updateMember(
        @PathVariable (name="userId") Long userId,
        @RequestParam("groupId") String groupId,
        @RequestParam("userName") String userName,
        @RequestBody MemberVO memberVO
    ) {

        String tmpGroupId = SecurityUtil.getGroupId();
        String tmpUserName = SecurityUtil.getUserName();

        // 인증정보 없음
        if (tmpGroupId == null || tmpUserName == null) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(0);
        }

        // 로그인 사용자와 수정 대상 확인
        if (
            !tmpGroupId.equals(groupId)
            || !tmpUserName.equals(userName)
        ) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(0);
        }

        memberVO.setUserId(userId);
        memberVO.setGroupId(groupId);

        int updated = memberService.updateMember(memberVO);

        if (updated == 0) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(0);
        }

        return ResponseEntity.ok(updated);
    }


    // =======================================================
    // 비밀번호 변경
    // =======================================================
    @Operation(
        summary = "회원 비밀번호 변경",
        description = "현재 비밀번호를 확인한 후 새로운 비밀번호로 변경합니다."
    )
    @PutMapping("/member/{userId}/password")
    public ResponseEntity<Map<String, Object>> changePassword(
        @PathVariable (name="userId") Long userId,
        @RequestBody ChangePasswordRequest req
    ) {

        boolean changed = memberService.changePassword(
            userId,
            req.getGroupId(),
            req.getUserName(),
            req.getCurrentPassword(),
            req.getNewPassword()
        );

        Map<String, Object> body = new HashMap<>();

        body.put("success", changed);

        if (!changed) {

            body.put(
                "message",
                "비밀번호 변경에 실패했습니다."
            );

            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
        }

        body.put(
            "message",
            "비밀번호가 변경되었습니다."
        );

        return ResponseEntity.ok(body);
    }


    // =======================================================
    // 회원 탈퇴
    // =======================================================
    @Operation(
        summary = "회원 탈퇴",
        description = "현재 로그인한 회원의 비밀번호를 확인한 후 회원 탈퇴 처리를 수행합니다."
    )
    @PostMapping("/member/withdraw")
    public ResponseEntity<Map<String, Object>> memberWithdraw(
        @RequestBody WithdrawRequest req
    ) {

        String loginGroupId = SecurityUtil.getGroupId();
        String userEmail = SecurityUtil.getUserEmail();

        if (!req.getGroupId().equals(loginGroupId)) {

            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                    Map.of(
                        "success", false,
                        "message", "권한 없음"
                    )
                );
        }

        memberService.withdraw(
            req.getGroupId(),
            userEmail,
            req.getPassword()
        );

        return ResponseEntity.ok(
            Map.of(
                "success", true,
                "message", "탈퇴 처리되었습니다."
            )
        );
    }
}
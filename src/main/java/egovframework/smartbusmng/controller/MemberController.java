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

@RestController
@RequestMapping("/api/memberMng")
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	// memberMng(login, memberMng) common function
	@GetMapping("/getGroupList")
	@ResponseBody
	public Map<String, Object> getGroupList() {

		List<GroupUserInfo> groupList = memberService.getGroupList();
		
		Map<String, Object> response = new HashMap<>();
		response.put("data", groupList);
		
		System.out.println("## memberController : data: groupList : "+groupList.size());
		
		return response;
	}	
	
	// member function
	@GetMapping("/getMenuList")
	@ResponseBody
	public List<MenuDto> getMenus(){
		return memberService.getMenus();
	}	
	
	@PutMapping("/confirmUsers")
	public ResponseEntity<Map<String, Object>> confirmMember(@RequestBody List<ConfirmUserRequest> reqUsers){
	    int updatedCount = memberService.confirmMember(reqUsers);

	    Map<String, Object> response = new HashMap<>();
	    response.put("requested", reqUsers);
	    response.put("updated", updatedCount );

	    return ResponseEntity.ok(response);
	}
	
    // 운수사 목록 조회
    @GetMapping("/operators")
    public ResponseEntity<List<OperatorDto>> getOperators() {
        return ResponseEntity.ok(memberService.getOperators());
    }

    // 운수사 신규 등록 (POST)
    @PostMapping("/operator")
    public ResponseEntity<Integer> insertOperator(
            @RequestBody OperatorDto operatorDto) {
        int result = memberService.insertOperator(operatorDto);
        return ResponseEntity.ok(result);
    }

    // 운수사 정보 수정 (PUT)
    @PutMapping("/operator/{userGroupId}")
    public ResponseEntity<?> updateOperator(
            @PathVariable String userGroupId,
            @RequestBody OperatorDto operatorDto) {
		
    	final String userRole = SecurityUtil.getGroupRole();
		
		if (!"ADMIN".equals(userRole)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자 권한이 필요합니다.");
		}
		
		if (userGroupId == null || userGroupId.isEmpty()) {
			return ResponseEntity.badRequest().body("운수사의 id를 전달하여야 합니다.");
		}
        operatorDto.setGroupId(userGroupId);
        int result = memberService.updateOperator(operatorDto);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/member/approved")
    public ResponseEntity<List<MemberVO>> getApprovedMembers(
    		@RequestParam(required = false) Integer page,
    		@RequestParam(required = false) Integer size) {
    	
    	List<MemberVO> members;
    	
    	if (page != null && size != null ) {
    		int offset = page * size;
    		members = memberService.getApprovedMembersPaged(offset, size);
    	} else {
    		members = memberService.getApprovedMembersAll();
    	}
    	
    	return ResponseEntity.ok(members);
    }
    
    @GetMapping("/member/pending")
    public ResponseEntity<List<MemberVO>> getPendingMembers(
    		@RequestParam(required = false) Integer page,
    		@RequestParam(required = false) Integer size) {
    	
    	List<MemberVO> members;
    	
    	if (page != null && size != null ) {
    		int offset = page * size;
    		members = memberService.getPendingMembersPaged(offset, size);
    	} else {
    		members = memberService.getPendingMembersAll();
    	}
    	
    	return ResponseEntity.ok(members);
    }
    
    @GetMapping("/member/{userId}")
    public ResponseEntity<MemberVO> getMember(
    		@PathVariable Long userId, 
    		@RequestParam("groupId") String groupId,
    		@RequestParam("userName") String userName) {
    	
    	MemberVO member = memberService.getMemberInfo(userId, groupId, userName);
    	if (member == null) {
    		return ResponseEntity.notFound().build();
    	}
    	member.setPassword(null);
    	return ResponseEntity.ok(member);
    }
    
    @PutMapping("/member/{userId}")
    public ResponseEntity<Integer> updateMember(
    		@PathVariable Long userId, 
    		@RequestParam("groupId") String groupId,
    		@RequestParam("userName") String userName,
    		@RequestBody MemberVO memberVO) {
    	
    	String tmpGroupId = SecurityUtil.getGroupId();
    	String tmpUserName = SecurityUtil.getUserName();
   	

        // (1) null 안전하게 처리
        if (tmpGroupId == null || tmpUserName == null) {
            return ResponseEntity.status(401).body(0); // 인증정보 없음
        }

        // (2) 내용 비교는 equals
        if (!tmpGroupId.equals(groupId) || !tmpUserName.equals(userName)) {
            // 이 경우는 404보다는 403이 더 맞긴 함(접근 금지)
            return ResponseEntity.status(403).body(0);
        }

        memberVO.setUserId(userId);
        memberVO.setGroupId(groupId);
    	int updated = memberService.updateMember(memberVO);
    	
    	if (updated == 0) {
    		return ResponseEntity.status(403).body(0);
    	}
    	return ResponseEntity.ok(updated);
    }
    
    @PutMapping("/member/{userId}/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @PathVariable Long userId,
            @RequestBody ChangePasswordRequest req) {

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
            // 현재 비밀번호 불일치 or 사용자 정보 불일치
            body.put("message", "비밀번호 변경에 실패했습니다.");
            return ResponseEntity.status(400).body(body);
        }

        body.put("message", "비밀번호가 변경되었습니다.");
        return ResponseEntity.ok(body);
    }
    
    @PostMapping("/member/withdraw")
    public ResponseEntity<Map<String, Object>> memberWithdraw(
    	@RequestBody WithdrawRequest req
    ){
    	String loginGroupId = SecurityUtil.getGroupId();
    	String userEmail = SecurityUtil.getUserEmail();
    	
    	if (!req.getGroupId().equals(loginGroupId)) {
    		return ResponseEntity.status(403).body(Map.of("success", false, "message", "권한 없음"));
    	}
    	
    	memberService.withdraw(req.getGroupId(), userEmail, req.getPassword());
    	return ResponseEntity.ok(Map.of("success", true, "message", "탈퇴 처리되었습니다."));
     
    }
}

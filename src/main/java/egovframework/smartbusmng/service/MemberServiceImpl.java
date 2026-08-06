package egovframework.smartbusmng.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import egovframework.smartbusmng.auth.security.SecurityUtil;
import egovframework.smartbusmng.mapper.MemberMapper;
import egovframework.smartbusmng.model.member.ConfirmUserRequest;
import egovframework.smartbusmng.model.member.GroupUserInfo;
import egovframework.smartbusmng.model.member.MemberDto;
import egovframework.smartbusmng.model.member.MemberVO;
import egovframework.smartbusmng.model.member.MenuDto;
import egovframework.smartbusmng.model.member.OperatorDto;

@Service
public class MemberServiceImpl implements MemberService {
	
	@Autowired
	private MemberMapper memberMapper;
	
	@Value("${project.name}")
	private String pName;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Transactional
	public List<GroupUserInfo> getGroupList() {
		String groupId = SecurityUtil.getGroupId();
		String role = SecurityUtil.getGroupRole();

		if (groupId == null) {
			throw new RuntimeException("인증 정보 없음");
		}
		
		if ("ADMIN".equals(role)) {
			List<GroupUserInfo> groupList = memberMapper.getGroupList();			

			return groupList;
		} else {
			return Collections.emptyList();
		}
	
	}
	
	@Transactional
	public List<GroupUserInfo> getLoginGroupList() {
		List<GroupUserInfo> groupList = memberMapper.getGroupList();
		
		return groupList;
	}
	@Transactional
	public List<MenuDto> getMenus() {
		String role = SecurityUtil.getGroupRole();		
		List<MenuDto> allMenus = memberMapper.getMenusByRole(role);
		Map<Integer, MenuDto> menuMap = new HashMap<>();
		
		for (MenuDto menu : allMenus) {
			menu.setChildren(new ArrayList<>());
			menu.setUrl(menu.getMenuPath());
			menuMap.put(menu.getMenuId(), menu);
		}
		
		if ("ADMIN".equals(role)) {
			List<GroupUserInfo> groups = memberMapper.getGroupList();
			
			for (MenuDto menu : allMenus) {		
				if ("/facilityMng".equals(menu.getMenuPath()) || "/adMng".equals(menu.getMenuPath())) {
					if (groups.size() > 0) {
						for (GroupUserInfo g : groups) {
							MenuDto groupMenu = new MenuDto();
							groupMenu.setMenuNm(g.getUserGroupNm());
							groupMenu.setMenuPath(menu.getMenuPath());
							groupMenu.setUrl(menu.getMenuPath() + "?userGroupId=" + g.getUserGroupId());
							groupMenu.setParentId(menu.getMenuId());
							groupMenu.setSortOrder(999);
							menu.getChildren().add(groupMenu);
							System.out.println("########## g.getUserGroupNm : "+groupMenu.getMenuNm()+ ", g.url: "+groupMenu.getUrl());
						}						
					}
				}
			}
		}
			
		List<MenuDto> topMenus = new ArrayList<>();
		for (MenuDto menu : allMenus) {
			if (menu.getParentId() == null) {
				topMenus.add(menu);
			} else {
				MenuDto parent = menuMap.get(menu.getParentId());
				if (parent != null) {
					parent.getChildren().add(menu);
				}
			}
		}
		//System.out.println("topMenus.size: "+topMenus.get(0).getChildren().get(0).getMenuNm());
		
		return topMenus;
	}
	
	@Transactional
	public boolean existsByEmail(String email) {
		return memberMapper.countByEmail(email) == 0 ;
	}
	
	@Transactional(readOnly=true)
	public boolean isValidGroupKey(String groupKey, String groupId) {
		return memberMapper.checkGroupKey(groupKey, groupId) == 1;
	}
	
	@Transactional
	public boolean registerMember(MemberDto memberDto) {
		System.out.println("## userEmail : "+memberDto.getUserEmail());
		if (!existsByEmail(memberDto.getUserEmail())) return false;
		
		String hashedPassword = passwordEncoder.encode(memberDto.getPassword());
		String groupRole = memberMapper.getGroupRole(memberDto.getGroupId());
		
		MemberVO member = new MemberVO();
		member.setUserEmail(memberDto.getUserEmail());
		member.setGroupId(memberDto.getGroupId());
		member.setGroupKey(memberDto.getGroupKey());
		member.setGroupRole(groupRole);
		member.setPassword(hashedPassword);
		member.setUserName(memberDto.getManagerName());
		member.setManageDepart(memberDto.getManagedepart());
		member.setTelNo(memberDto.getTelNo());
		member.setCreateDt(LocalDateTime.now());
		member.setLastIp(memberDto.getLastIp());

		return memberMapper.insertMember(member) > 0;
	}
	
	@Override
	@Transactional
	public int confirmMember(List<ConfirmUserRequest> reqUsers) {
		if (reqUsers == null || reqUsers.isEmpty()) return 0;
		return memberMapper.updateConfirmedUser(reqUsers);
	}
	
    @Override
    public List<OperatorDto> getOperators() {
        return memberMapper.selectOperators();
    }
    
    @Override
    @Transactional
    public int insertOperator(OperatorDto operatorDto) {
    	String userId = SecurityUtil.getUserName();
    	if (userId == null) userId = "SYSTEM";

        String newGroupId = memberMapper.getNextOperatorGroupId();
        if (newGroupId == null || newGroupId.isBlank()) {
        	newGroupId = "293000005";
        }
        
        String newGroupKey = java.util.UUID.randomUUID()
        		.toString()
        		.replace("-", "");
        
        String newGroupRole = "USER";
        
        operatorDto.setGroupId(newGroupId);
        operatorDto.setGroupKey(newGroupKey);
        operatorDto.setGroupRole(newGroupRole); 	
    	
    	operatorDto.setFrstUserId(userId);
    	operatorDto.setFrstRegistProgram(pName);
    	
    	operatorDto.setLastUserId(userId);
    	operatorDto.setLastUpdtProgram(pName);
    	
        return memberMapper.insertOperator(operatorDto);
    }

    @Override
    @Transactional
    public int updateOperator(OperatorDto operatorDto) {
        String userId = SecurityUtil.getUserName();
        
        operatorDto.setLastUserId(userId);
        operatorDto.setLastUpdtProgram(pName);

        return memberMapper.updateOperator(operatorDto);
    }

    @Override
	public List<MemberVO> getApprovedMembersPaged(int offset, Integer size){
    	return memberMapper.getApprovedMembersPaged(offset, size);
    }

    @Override
	public List<MemberVO> getApprovedMembersAll(){
    	return memberMapper.getApprovedMembersAll();
    }

    @Override
    public List<MemberVO> getPendingMembersPaged(int offset, Integer size){
    	return memberMapper.getPendingMembersPaged(offset, size);
    }

    @Override
    public List<MemberVO> getPendingMembersAll(){
    	return memberMapper.getPendingMembersAll();
    }
    
	@Override
    public MemberVO getMemberInfo(Long userId, String groupId, String userName) {
		return memberMapper.getMemberInfo(userId, groupId, userName);
	}

	@Override
	public int updateMember(MemberVO memberVO) {
		return memberMapper.updateMember(memberVO);
	}
	
	@Override
	public boolean changePassword(Long userId, String groupId, String userName, String currentPassword, String newPassword) {
        String encodedPassword = memberMapper.getPasswordByKey(userId, groupId, userName);

        if (encodedPassword == null) {
            // 그런 유저 없음 (또는 조합이 안 맞음)
            return false;
        }

        // 2) 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, encodedPassword)) {
            // 현재 비밀번호 틀림
            return false;
        }

        // 3) 새 비밀번호 해시 후 업데이트
        String newEncoded = passwordEncoder.encode(newPassword);
        int updated = memberMapper.updatePassword(userId, groupId, userName, newEncoded);

        return updated > 0;
	}
	
	@Override
	@Transactional
	public void withdraw(String groupId, String userEmail, String password) {
		MemberVO user = memberMapper.selectOperatorUserForAuth(groupId, userEmail);

		if (user == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다.");
		}
		
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
		}
		
		int updated = memberMapper.withdrawOperatorUser(groupId, userEmail);
		if (updated != 1) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 탈퇴 처리되었거나 처리에 실패하였습니다.");
		}
	}
}

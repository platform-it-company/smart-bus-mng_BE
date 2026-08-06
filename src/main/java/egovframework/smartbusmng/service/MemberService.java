package egovframework.smartbusmng.service;

import java.util.List;

import egovframework.smartbusmng.model.member.ConfirmUserRequest;
import egovframework.smartbusmng.model.member.GroupUserInfo;
import egovframework.smartbusmng.model.member.MemberDto;
import egovframework.smartbusmng.model.member.MemberVO;
import egovframework.smartbusmng.model.member.MenuDto;
import egovframework.smartbusmng.model.member.OperatorDto;

public interface MemberService {

	List<GroupUserInfo> getGroupList();
	
	List<MenuDto> getMenus();

	List<GroupUserInfo> getLoginGroupList();

	boolean existsByEmail(String email);

	boolean isValidGroupKey(String companyKey, String groupId);

	boolean registerMember(MemberDto memberDto);

	int confirmMember(List<ConfirmUserRequest> reqUsers);

	List<OperatorDto> getOperators();

	int insertOperator(OperatorDto operatorDto);

	int updateOperator(OperatorDto operatorDto);

	List<MemberVO> getApprovedMembersPaged(int offset, Integer size);

	List<MemberVO> getApprovedMembersAll();

	List<MemberVO> getPendingMembersPaged(int offset, Integer size);

	List<MemberVO> getPendingMembersAll();

	MemberVO getMemberInfo(Long userId, String groupId, String userName);

	int updateMember(MemberVO memberVO);

	boolean changePassword(Long userId, String groupId, String userName, String currentPassword, String newPassword);

	void withdraw(String groupId, String userEmail, String password);

}

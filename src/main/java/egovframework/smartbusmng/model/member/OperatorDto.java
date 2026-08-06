package egovframework.smartbusmng.model.member;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OperatorDto {
    private Long id;

    private String groupId; 
    private String groupKey;
    private String groupRole;
    private String groupNm;

    private String addrStreet;
    private String addrDetail;
    private String zip;

    private String faxNo;
    private String telNo;

    private String bizRegistNo;
    private String bizCategory;

    private String managerName;
    private String managerDepart;
    private String managerEmail;
    private String managerTelNo;
    
    private LocalDateTime frstRegistDt;
    private String frstUserId;
    private String frstRegistProgram;
    private LocalDateTime lastUpdtDt;
    private String lastUserId;
    private String lastUpdtProgram;
}
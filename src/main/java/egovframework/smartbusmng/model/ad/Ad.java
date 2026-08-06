package egovframework.smartbusmng.model.ad;

import java.util.Date;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Ad {

	private String adId;

    @NotNull
    @NotEmpty
    private String adTitle;

    @NotNull
    @NotEmpty
    private String adTp;

    @NotNull
    @NotEmpty
    private String adDefault;

    @NotNull
    @NotEmpty
    private String useTp;

    private String eventSd; // 또는 LocalDate eventSd;
    private String eventEd; // 또는 LocalDate eventEd;
    private String eventSt;
    private String eventEt;
    private String adOneline;
    private String attchment;
    private Date frstRegistDt; // 또는 LocalDate frstRegistDt;
    private String frstUserId;
    private String frstRegistProgrm;
    private Date lastUpdtDt; // 또는 LocalDate lastUpdtDt;
    private String lastUserId;
    private String lastRegistProgrm;
    private String groupId;
    
// 날짜 변환
    
    public String getFormattedEventSd() {
        return formatDateString(eventSd);
    }
    
    public String getFormattedEventEd() {
        return formatDateString(eventEd);
    }

    public String getFormattedEventSt() {
        return formatTimeString(eventSt);
    }

    public String getFormattedEventEt() {
        return formatTimeString(eventEt);
    }
    
    private String formatDateString(String date) {
        if (date != null && date.length() == 8) {
            return date.substring(0, 4) + "." + date.substring(4, 6) + "." + date.substring(6, 8);
        }
        return date;
    }
    
    private String formatTimeString(String time) {
        if (time != null && time.length() == 4) {
            return time.substring(0, 2) + ":" + time.substring(2, 4);
        }
        return time;
    }
}

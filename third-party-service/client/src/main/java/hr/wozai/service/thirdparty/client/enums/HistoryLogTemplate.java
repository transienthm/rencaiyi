package hr.wozai.service.thirdparty.client.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;
import hr.wozai.service.thirdparty.client.dto.HistoryLogDTO;

/**
 * Created by wangbin on 16/5/12.
 */
public enum HistoryLogTemplate {

    RANK_CHANGE("{{createdTime}}  职级由 {{preValue}} 调整为 {{curValue}}  , 操作人: {{userProfile1}} ",1),
    POSITION_CHANGE("{{createdTime}}  职位由 {{preValue}} 调整为 {{curValue}} , 操作人: {{userProfile1}} ",2),
    REVIEW_SCORE("{{createdTime}}  在评价活动 「{{preValue}}」活动中获得主管评分: {{curValue}} ",3),
    SUPERVISOR_CHANGE("{{createdTime}}  汇报对象由 {{userProfile1}} 变更为 {{userProfile2}} ,操作人: {{userProfile3}}",4),
    TEAM_CHANGE("{{createdTime}}  所属团队由 {{preValue}} 变更为 {{curValue}} ,操作人: {{userProfile1}}",5);

    private String content;
    private Integer logType;

    HistoryLogTemplate(String content,Integer logType){
        this.content = content;
        this.logType = logType;
    }

    public static HistoryLogTemplate getEnumByLogType(Integer logType){
        if (null == logType){
            return null;
        }
        for (HistoryLogTemplate historyLogTemplate:HistoryLogTemplate.values()){
            if (IntegerUtils.equals(logType,historyLogTemplate.logType)){
                return historyLogTemplate;
            }
        }
        return null;
    }

    public String getContent() {
        return content;
    }

    public Integer getLogType() {
        return logType;
    }
}

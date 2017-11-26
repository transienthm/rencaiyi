package hr.wozai.service.api.vo.historylog;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.api.vo.user.CoreUserProfileListVO;
import hr.wozai.service.servicecommons.utils.codec.DecodeDeserializer;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by wangbin on 16/4/29.
 */
@Data
@NoArgsConstructor
public class HistoryLogVO {
    @JsonSerialize(using = EncodeSerializer.class)
    @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
    private Long historyLogId;

    @JsonSerialize(using = EncodeSerializer.class)
    @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
    private Long orgId;

    @JsonSerialize(using = EncodeSerializer.class)
    @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
    private Long userId;

    @JsonSerialize(using = EncodeSerializer.class)
    @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
    private Long actorUserId;

    private Integer logType;

    private String content;

    protected CoreUserProfileListVO coreUserProfileListVO;

    protected List<TeamVO> teamVOs;

    private String preValue;

    private String curValue;

    @JsonSerialize(using = EncodeSerializer.class)
    @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
    private Long createdUserId;

    private Long createdTime;

    private Integer isDeleted;
}

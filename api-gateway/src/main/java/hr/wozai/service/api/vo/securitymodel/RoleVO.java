package hr.wozai.service.api.vo.securitymodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.servicecommons.utils.codec.DecodeDeserializer;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/3
 */
@Data
@NoArgsConstructor
public class RoleVO {
  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long roleId;

  private String roleName;

  private String roleDesc;

  private Boolean flag;

  private Boolean editable = true;
}

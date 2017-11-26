package hr.wozai.service.user.client.userorg.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/29
 */
@Data
@NoArgsConstructor
public class TokenKeyDTO {
  private long orgId;

  private long adminUserId;

  private long userId;

  private boolean allowedRememberedMe;

  private int uuidUsage;

  private static final String ACCESS_TOKEN_KEYNAMA = "accesstoken:";
  private static final String REFRESH_TOKEN_KEYNAMA = "refreshtoken:";
  private static final String TEMPORARY_TOKEN_KEYNAME = "temporarytoken:";
  private static final String SEPERATOR = "@#";

  public String generateAccessTokenKey() {
    return ACCESS_TOKEN_KEYNAMA + SEPERATOR + orgId + SEPERATOR + adminUserId + SEPERATOR + userId;
  }

  public static String generateAccessTokenKey(long orgId, long adminUserId, long userId) {
    return ACCESS_TOKEN_KEYNAMA + SEPERATOR + orgId + SEPERATOR + adminUserId + SEPERATOR + userId;
  }

  public String generateRefreshTokenKey() {
    return REFRESH_TOKEN_KEYNAMA + SEPERATOR + orgId + SEPERATOR + adminUserId + SEPERATOR + userId;
  }

  public static String generateRefreshTokenKey(long orgId, long adminUserId, long userId) {
    return REFRESH_TOKEN_KEYNAMA + SEPERATOR + orgId + SEPERATOR + adminUserId + SEPERATOR + userId;
  }

  public String generateTemporaryAccessTokenKey() {
    return TEMPORARY_TOKEN_KEYNAME + SEPERATOR + orgId + SEPERATOR + userId + SEPERATOR + uuidUsage;
  }

  public static String generateTemporaryAccessTokenKey(long orgId, long userId, int uuidUsage) {
    return TEMPORARY_TOKEN_KEYNAME + SEPERATOR + orgId + SEPERATOR + userId + SEPERATOR + uuidUsage;
  }
}

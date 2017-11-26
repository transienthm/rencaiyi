package hr.wozai.service.user.client.userorg.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.user.client.userorg.dto.NavigationDTO;
import hr.wozai.service.user.client.userorg.dto.TokenPairDTO;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/3
 */
@ThriftService
public interface NavigationFacade {
  @ThriftMethod
  VoidDTO initNaviOrg(long orgId, long userId, long actorUserId, long adminUserId);

  @ThriftMethod
  TokenPairDTO deleteNaviOrgAndRedirectToTrueOrg(long naviOrgId, long naviUserId, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO updateNavigation(NavigationDTO navigationDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  NavigationDTO getNavigation(long naviOrgId, long naviUserId, long actorUserId, long adminUserId);
}

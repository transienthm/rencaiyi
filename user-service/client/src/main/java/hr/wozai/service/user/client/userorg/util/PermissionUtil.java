package hr.wozai.service.user.client.userorg.util;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.user.client.userorg.facade.SecurityModelFacade;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/26
 */
@Component("permissionUtil")
public class PermissionUtil {

  @Autowired
  @Qualifier("securityModelFacadeProxy")
  private ThriftClientProxy securityModelFacadeProxy;

  private SecurityModelFacade securityModelFacade;


  @Autowired
  public void init() throws Exception {
    securityModelFacade = (SecurityModelFacade) securityModelFacadeProxy.getObject();
  }

  public void assignPermissionToObjList(long orgId, long actorUserId, List<PermissionObj> objectList,
                                        String resourceCode, int actionCode) {
    for (PermissionObj permissionObj : objectList) {
      BooleanDTO remoteResult = securityModelFacade.checkUserPermissionOnRecordResource(orgId, actorUserId, resourceCode,
              actionCode, permissionObj.getResourceType(), permissionObj.getOwnerId());
      ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
      if (serviceStatus == ServiceStatus.COMMON_OK) {
        permissionObj.setHasPermission(remoteResult.getData());
      } else {
        permissionObj.setHasPermission(false);
      }
    }
  }

  public boolean getPermissionForSingleObj(long orgId, long actorUserId, long objId, long objOwnerId,
                              String resourceCode, int resourceType, int actionCode) {
    List<PermissionObj> permissionObjs = new ArrayList<>();
    PermissionObj obj = new PermissionObj();
    obj.setId(objId);
    obj.setOwnerId(objOwnerId);
    obj.setResourceType(resourceType);
    permissionObjs.add(obj);
    this.assignPermissionToObjList(orgId, actorUserId, permissionObjs, resourceCode, actionCode);
    obj = permissionObjs.get(0);
    return obj.isHasPermission();
  }
}

// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.api.controller.userorg.userprofile;

import com.amazonaws.util.IOUtils;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.wozai.service.api.component.ProfileMetaPermissionChecker;
import hr.wozai.service.api.component.UserProfilePermissionChecker;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.TotalCountVO;
import hr.wozai.service.api.vo.user.ProfileFieldListVO;
import hr.wozai.service.api.vo.user.ProfileFieldVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.EncodingUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.thrift.dto.IntegerDTO;
import hr.wozai.service.servicecommons.thrift.dto.StringDTO;
import hr.wozai.service.servicecommons.thrift.dto.StringListDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.userorg.dto.ProfileFieldDTO;
import hr.wozai.service.user.client.userorg.dto.ProfileFieldListDTO;
import hr.wozai.service.user.client.userorg.helper.RosterAvailabilityHelper;
import hr.wozai.service.user.client.userorg.util.PermissionUtil;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-08-25
 */
@Controller("rosterManagementController")
public class RosterManagementController {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileController.class);

  private static final String CSV_DOWNLOAD_CONTENT_TYPE = "application/octet-stream; charset=utf-8";
  private static final String CSV_DOWNLOAD_CONTENT_DISPOSITION_KEY = "Content-Disposition";
  private static final String CSV_DOWNLOAD_CONTENT_DISPOSITION_VALUE = "attachment; filename=staff.csv";

  private static final String PARAM_ACCESS_TOKEN = "X-Access-Token";
  private static final String PARAM_REFRESH_TOKEN = "X-Refresh-Token";
  private static final String PARAM_REFERENCE_NAMES = "referenceNames";

  private static final String ENCODING_UTF8 = "UTF-8";
  private static final String ENCODING_UTF16 = "UTF-16";
  private static final String ENCODING_GBK = "GBK";

  @Autowired
  private UserProfilePermissionChecker userProfilePermissionChecker;

  @Autowired
  private ProfileMetaPermissionChecker profileMetaPermissionChecker;

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  PermissionUtil permissionUtil;

  @LogAround

  @RequestMapping(
      value = "/users/roster/available-fields",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<ProfileFieldListVO> listAvailableFieldOfRosterFile() {

    Result<ProfileFieldListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!profileMetaPermissionChecker.canRead(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      // rpc call
      ProfileFieldListDTO listResult = facadeFactory.getProfileTemplateFacade().listAllProfileFieldOfOrg(
          authedOrgId, authedActorUserId, authedAdminUserId);
      // handle result
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(listResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        List<ProfileFieldDTO> profileFieldDTOs = listResult.getProfileFieldDTOs();
        List<ProfileFieldVO> profileFieldVOs = null;
        if (!CollectionUtils.isEmpty(profileFieldDTOs)) {
          profileFieldVOs = new ArrayList<>();
          for (ProfileFieldDTO profileFieldDTO: profileFieldDTOs) {
            // TODO: opt
            if (!RosterAvailabilityHelper.isAvailableProfileField(
                  profileFieldDTO.getReferenceName(), profileFieldDTO.getDataType())) {
              continue;
            }
            ProfileFieldVO profileFieldVO = new ProfileFieldVO();
            BeanHelper.copyPropertiesHandlingJSON(profileFieldDTO, profileFieldVO);
            profileFieldVOs.add(profileFieldVO);
          }
        } else {
          profileFieldVOs = Collections.EMPTY_LIST;
        }
        ProfileFieldListVO profileFieldListVO = new ProfileFieldListVO();
        profileFieldListVO.setProfileFieldVOs(profileFieldVOs);
        result.setData(profileFieldListVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("listAllProfileFieldOfOrg()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/users/roster/download",
      method = RequestMethod.POST)
  public void downloadRosterFile(
      HttpServletRequest request,
      HttpServletResponse response
  ) {

    String accessTokenString = request.getParameter(PARAM_ACCESS_TOKEN);
    String refreshTokenString = request.getParameter(PARAM_REFRESH_TOKEN);
    String referenceNames = request.getParameter(PARAM_REFERENCE_NAMES);

    if (StringUtils.isNullOrEmpty(referenceNames)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    long authedActorUserId = AuthenticationInterceptor.getActorUserIdFromTokenPair(accessTokenString, refreshTokenString);
    long authedAdminUserId = AuthenticationInterceptor.getAdminUserIdFromTokenPair(accessTokenString, refreshTokenString);
    long authedOrgId = AuthenticationInterceptor.getOrgIdFromTokenPair(accessTokenString, refreshTokenString);

    if (!userProfilePermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      response.setContentType(CSV_DOWNLOAD_CONTENT_TYPE);
      response.setHeader(CSV_DOWNLOAD_CONTENT_DISPOSITION_KEY, CSV_DOWNLOAD_CONTENT_DISPOSITION_VALUE);
      StringDTO rpcHeader = facadeFactory.getUserProfileFacade().getHeaderOfRosterFile(
          authedOrgId, Arrays.asList(referenceNames.split(",")), authedActorUserId, authedAdminUserId);
      int dataColumnCount = referenceNames.split(",").length;
      String appendingCommas = generateAppendingCommas(dataColumnCount);
      ServiceStatus rpcHeaderStatus = ServiceStatus.getEnumByCode(rpcHeader.getServiceStatusDTO().getCode());
      if (ServiceStatus.COMMON_OK.equals(rpcHeaderStatus)) {
        String header = "sep=,\n" + rpcHeader.getData();
        StringListDTO rpcStaff = facadeFactory.getUserProfileFacade().listStaffOfRosterFile(
            authedOrgId, authedActorUserId, authedAdminUserId);
        ServiceStatus rpcStaffStatus = ServiceStatus.getEnumByCode(rpcStaff.getServiceStatusDTO().getCode());
        if (ServiceStatus.COMMON_OK.equals(rpcStaffStatus)
            && !CollectionUtils.isEmpty(rpcStaff.getData())) {
          for (String oneStaff: rpcStaff.getData()) {
            header += oneStaff + appendingCommas + "\n";
          }
        }
        InputStream inputStream = new ByteArrayInputStream(header.getBytes("GBK"));
//        InputStream inputStream = new ByteArrayInputStream(header.getBytes("UTF8"));
        IOUtils.copy(inputStream, response.getOutputStream());
        // TODO: populate staff roster
        response.flushBuffer();
      }
    } catch (Exception e) {
      LOGGER.info("downloadRosterCSVFile()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

  }

  @LogAround

  @RequestMapping(
      value = "/users/roster/upload",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  public Result<TotalCountVO> uploadRosterFile(
      @RequestParam(value = "file") MultipartFile csvFile
  ) {

    Result<TotalCountVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!userProfilePermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {

      String encoding = EncodingUtils.detectCharset(csvFile.getBytes());
      if (!ENCODING_UTF8.equals(encoding)
          && !ENCODING_UTF16.equals(encoding)
          && !ENCODING_GBK.equals(encoding)) {
        encoding = ENCODING_GBK;
      }
      LOGGER.info("uploadRosterFile(): csv-encoding=" + encoding);
      // TODO: need robust solution
      if (!ENCODING_UTF8.equals(encoding)
          && !ENCODING_GBK.equals(encoding)) {
        encoding = ENCODING_GBK;
      }
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(csvFile.getInputStream(), encoding));
      List<List<String>> rawFieldValuesList = new ArrayList<>();
      String line = null;
      int lineCount = 0;
      List<String> headers = null;
      while ((line = bufferedReader.readLine()) != null) {

        LOGGER.info("BLABLABLA: count={}, line={}", lineCount, line);

        if (lineCount == 0) {
          // compatibility 1: has or has-not first line (sep=,)
          if (!line.contains("sep=")) {
            lineCount ++;
          }
        }
        if (lineCount == 0) {
          lineCount ++;
          continue;
        }

        // compatibility 1: allow ; as separator
        line = line.replace(";", ",");

        if (!StringUtils.isNullOrEmpty(line)) {
          List<String> oneLineFieldValues = Arrays.asList(line.split(",", -1));
          if (lineCount == 1) {
            headers = oneLineFieldValues;
          } else {
            rawFieldValuesList.add(oneLineFieldValues);
          }
          LOGGER.info("uploadRosterFile(): line={}, size={}, currLine={}, line={}",
                      lineCount, oneLineFieldValues.size(), oneLineFieldValues, line);
        }
        lineCount++;
      }
      if (lineCount <= 2) {
        result.setCodeAndMsg(ServiceStatus.UP_CSV_EMPTY);
      } else {
        if (CollectionUtils.isEmpty(rawFieldValuesList)) {
          throw new ServiceStatusException(ServiceStatus.UP_CSV_EMPTY);
        }
        IntegerDTO rpcResult = facadeFactory.getUserProfileFacade().batchUpdateRosterData(
            authedOrgId, headers, rawFieldValuesList, authedActorUserId, authedAdminUserId);
        ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcResult.getServiceStatusDTO().getCode());
        if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
          TotalCountVO totalCountVO = new TotalCountVO();
          totalCountVO.setTotalCount(rpcResult.getData());
          result.setData(totalCountVO);
        }
        result.setCodeAndMsg(rpcStatus);
        result.setErrorInfo(rpcResult.getServiceStatusDTO().getErrorInfo());
      }
    } catch (Exception e) {
      LOGGER.error("uploadRosterFile()-error: invalid csv file");
      if (e instanceof ServiceStatusException) {
        throw (ServiceStatusException) e;
      }
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/users/roster/manual-operation/{documentId}",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  public Result grantManualOperationOfRosterFile(
      @PathVariable("documentId") String encrypedDocumentId
  ) {

    Result result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long documentId = 0L;

    if (!userProfilePermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      documentId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedDocumentId));
    } catch (Exception e) {
      LOGGER.error("grantManualOperationOfRosterFile()-error: invalid id", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    try {
      VoidDTO rpcResult = facadeFactory.getOnboardingFlowFacade()
          .grantManualOperationOfCSVFile(authedOrgId, documentId, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("grantManualOperationOfRosterFile()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  private String generateAppendingCommas(int dataColumnCount) {
    String str = "";
    for (int i = 0; i < dataColumnCount; i++) {
      str += ",";
    }
    return str;
  }

}

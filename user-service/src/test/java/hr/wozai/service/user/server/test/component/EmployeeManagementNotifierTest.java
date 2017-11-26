package hr.wozai.service.user.server.test.component;

import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.thirdparty.client.facade.MessageCenterFacade;
import hr.wozai.service.user.client.userorg.dto.*;
import hr.wozai.service.user.server.component.EmployeeManagementNotifier;
import hr.wozai.service.user.server.model.userorg.*;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.test.utils.AopTargetUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 16/7/18.
 */
public class EmployeeManagementNotifierTest extends TestBase{

    @Autowired
    EmployeeManagementNotifier employeeManagementNotifier;

    @Mock
    MessageCenterFacade messageCenterFacade;

    @Before
    public void SetUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(AopTargetUtils.getTarget(employeeManagementNotifier), "messageCenterFacade", messageCenterFacade);

        Mockito.doReturn(new VoidDTO()).when(messageCenterFacade).addPersonalMessage(Mockito.anyObject(), Mockito.anyList());

    }
    @Test
    public void testAll() {
    /*    Org org = new Org();
        org.setOrgId(1l);
        org.setFullName("闪签");
        CoreUserProfile staff = new CoreUserProfile();
        staff.setUserId(5l);
        staff.setCoreUserProfileId(5l);
        staff.setFullName("张三");
        staff.setEmailAddress("wangbin@sqian.com");
        staff.setJobTitle(1l);
        CoreUserProfile hr = new CoreUserProfile();
        hr.setUserId(5l);
        hr.setCoreUserProfileId(5l);
        hr.setFullName("张三");
        hr.setEmailAddress("wangbin@sqian.com");

        TeamMemberDTO teamMemberDTO = new TeamMemberDTO();
        teamMemberDTO.setTeamName("团队3");
        teamMemberDTO.setTeamId(1l);
        teamMemberDTO.setUserId(2l);
        CoreUserProfileDTO staffCUP = new CoreUserProfileDTO();
        staffCUP.setTeamMemberDTO(teamMemberDTO);
        staffCUP.setUserId(5l);
        staffCUP.setProfileTemplateId(5l);
        staffCUP.setFullName("张三");
        staffCUP.setEmailAddress("wangbin@sqian.com");
        staffCUP.setJobTitleName("工程师");
        CoreUserProfileDTO hrCUP = new CoreUserProfileDTO();
        BeanUtils.copyProperties(staffCUP, hrCUP);
        hrCUP.setUserId(6l);
        hrCUP.setProfileTemplateId(6l);
        hrCUP.setFullName("李四");
        hrCUP.setTeamMemberDTO(teamMemberDTO);
        org.setFullName("闪签");
        CoreUserProfile toNotifyCUP = new CoreUserProfile();
        toNotifyCUP.setUserId(5l);
        toNotifyCUP.setCoreUserProfileId(5l);
        toNotifyCUP.setFullName("张三");
        toNotifyCUP.setEmailAddress("wangbin@sqian.com");


        JobTransferResponseDTO jobTransferResponseDTO = new JobTransferResponseDTO();
        TeamDTO teamDTO = new TeamDTO();
        OrgPickOptionDTO pickOptionDTO = new OrgPickOptionDTO();
        SimpleUserProfileDTO simpleUserProfileDTO = new SimpleUserProfileDTO();

        simpleUserProfileDTO.setFullName("张三");
        simpleUserProfileDTO.setGender(1);
        simpleUserProfileDTO.setUserId(6l);

        teamDTO.setOrgId(1l);
        teamDTO.setTeamId(10l);
        teamDTO.setTeamName("团队1");

        pickOptionDTO.setOrgId(1l);
        pickOptionDTO.setOptionIndex(1);
        pickOptionDTO.setOrgPickOptionId(1l);
        pickOptionDTO.setOptionValue(null);
        pickOptionDTO.setIsDefault(1);


        jobTransferResponseDTO.setOrgId(1l);
        jobTransferResponseDTO.setJobTransferId(11l);
        jobTransferResponseDTO.setUserId(5l);
        jobTransferResponseDTO.setBeforeJobLevelOrgPickOptionDTO(pickOptionDTO);
        jobTransferResponseDTO.setBeforeJobTitleOrgPickOptionDTO(pickOptionDTO);
        jobTransferResponseDTO.setBeforeReporterSimpleUserProfileDTO(simpleUserProfileDTO);

        jobTransferResponseDTO.setAfterJobLevelOrgPickOptionDTO(pickOptionDTO);
        jobTransferResponseDTO.setAfterJobTitleOrgPickOptionDTO(pickOptionDTO);
        jobTransferResponseDTO.setBeforeTeamDTO(teamDTO);
        simpleUserProfileDTO.setFullName("李四");
        jobTransferResponseDTO.setAfterReporterSimpleUserProfileDTO(simpleUserProfileDTO);
        teamDTO.setTeamName("团队2");
        jobTransferResponseDTO.setAfterTeamDTO(teamDTO);
        long now = TimeUtils.getNowTimestmapInMillis();
        jobTransferResponseDTO.setTransferDate(now);
        jobTransferResponseDTO.setTransferType("调岗类型");
        jobTransferResponseDTO.setDescription("无说明");

        StatusUpdate statusUpdate = new StatusUpdate();
        statusUpdate.setOrgId(1l);
        statusUpdate.setUserId(5l);
        statusUpdate.setStatusUpdateId(1l);
        statusUpdate.setUpdateDate(now);
        statusUpdate.setToNotifyUserIds(Arrays.asList(1l));
        statusUpdate.setDescription(null);
        statusUpdate.setUpdateType("调岗");

        employeeManagementNotifier.sendEmailAndMessageAfterJobTransfer(org, jobTransferResponseDTO, hr, staff, Arrays.asList(toNotifyCUP));
        employeeManagementNotifier.sendEmailAndMessageAfterPassProbation(org, statusUpdate, staffCUP, Arrays.asList(toNotifyCUP), hrCUP.getUserId());
        employeeManagementNotifier.sendEmailAndMessageAfterResign(org, statusUpdate, staffCUP, Arrays.asList(toNotifyCUP), hr.getUserId());
    */}
}
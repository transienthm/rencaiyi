package hr.wozai.service.user.server.test.component;

import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.thirdparty.client.facade.MessageCenterFacade;
import hr.wozai.service.user.server.component.OnboardingFlowNotifier;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.test.base.TestBase;

import hr.wozai.service.user.server.test.utils.AopTargetUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Created by wangbin on 16/7/18.
 */
public class OnboardingFlowNotifierTest extends TestBase{

    @Autowired
    OnboardingFlowNotifier onboardingFlowNotifier;

    @Mock
    MessageCenterFacade messageCenterFacade;

    @Before
    public void SetUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(AopTargetUtils.getTarget(onboardingFlowNotifier), "messageCenterFacade", messageCenterFacade);

        Mockito.doReturn(new VoidDTO()).when(messageCenterFacade).addPersonalMessage(Mockito.anyObject(), Mockito.anyList());
    }
/*
    @Test
    public void sendOpenAccountEmailToFirstStaff() throws Exception {
        CoreUserProfile coreUserProfile = new CoreUserProfile();
        long now = TimeUtils.getNowTimestmapInMillis();
        coreUserProfile.setCreatedTime(now);
        coreUserProfile.setCreatedUserId(10l);
        coreUserProfile.setEmailAddress("wangbin@sqian.com");
        coreUserProfile.setEnrollDate(now + 1000);
        coreUserProfile.setFullName("张三");
        coreUserProfile.setGender(1);
        coreUserProfile.setJobTitle(1l);
        coreUserProfile.setMobilePhone("123");

        onboardingFlowNotifier.sendOpenAccountEmailToFirstStaff("闪签", coreUserProfile, "123");

    }

    @Test
    public void sendInvitationEmailToOnboardingStaff() throws Exception {
        CoreUserProfile coreUserProfile = new CoreUserProfile();
        long now = TimeUtils.getNowTimestmapInMillis();
        coreUserProfile.setCreatedTime(now);
        coreUserProfile.setCreatedUserId(10l);
        coreUserProfile.setEmailAddress("wangbin@sqian.com");
        coreUserProfile.setDateOfBirth(now + 1000);
        coreUserProfile.setFullName("张三");
        coreUserProfile.setGender(1);
        coreUserProfile.setJobTitle(1l);
        coreUserProfile.setMobilePhone("123");
        onboardingFlowNotifier.sendInvitationEmailToOnboardingStaff("闪签", coreUserProfile, "123");
    }

    @Test
    public void sendEmailAndMessageToHrAfterStaffSubmitOnboardingFlow() throws Exception {
        Org org = new Org();
        org.setFullName("闪签");
        org.setOrgId(10l);
        CoreUserProfile hrCUP = new CoreUserProfile();
        hrCUP.setFullName("张三");
        hrCUP.setUserId(5l);
        hrCUP.setCoreUserProfileId(5l);
        hrCUP.setEmailAddress("wangbin@sqian.com");
        CoreUserProfile staffCUP = new CoreUserProfile();
        staffCUP.setFullName("李四");
        staffCUP.setCoreUserProfileId(6l);
        staffCUP.setCoreUserProfileId(1l);
        staffCUP.setEmailAddress("wangbin@sqian.com");

        onboardingFlowNotifier.sendEmailAndMessageToHrAfterStaffSubmitOnboardingFlow(org, staffCUP, hrCUP);
    }

    @TestsendInvitationEmailToOnboardingStaff(
    public void sendEmailAndMessageToStaffAfterHrRejectOnboardingFlow() throws Exception {
        Org org = new Org();
        org.setFullName("闪签");
        org.setOrgId(10l);
        CoreUserProfile hrCUP = new CoreUserProfile();
        hrCUP.setFullName("张三");
        hrCUP.setUserId(5l);
        hrCUP.setCoreUserProfileId(5l);
        CoreUserProfile staffCUP = new CoreUserProfile();
        staffCUP.setFullName("李四");
        staffCUP.setUserId(5l);
        staffCUP.setCoreUserProfileId(6l);
        staffCUP.setEmailAddress("wangbin@sqian.com");
        onboardingFlowNotifier.sendEmailAndMessageToStaffAfterHrRejectOnboardingFlow(org, staffCUP, hrCUP, "123");
    }

    @Test
    public void sendEmailAndMessageToStaffAfterHrApproveOnboardingFlow() throws Exception {
        Org org = new Org();
        org.setFullName("闪签");
        org.setOrgId(10l);
        CoreUserProfile hrCUP = new CoreUserProfile();
        hrCUP.setFullName("张三");
        hrCUP.setUserId(5l);
        hrCUP.setCoreUserProfileId(5l);
        CoreUserProfile staffCUP = new CoreUserProfile();
        staffCUP.setFullName("李四");
        staffCUP.setUserId(5l);
        staffCUP.setCoreUserProfileId(6l);
        staffCUP.setEmailAddress("wangbin@sqian.com");
        onboardingFlowNotifier.sendEmailAndMessageToStaffAfterHrApproveOnboardingFlow(org, staffCUP, hrCUP);
    }

    @Test
    public void resendInvitationActivationEmailToImportedStaff() {
        CoreUserProfile coreUserProfile = new CoreUserProfile();
        long now = TimeUtils.getNowTimestmapInMillis();
        coreUserProfile.setCreatedTime(now);
        coreUserProfile.setCreatedUserId(10l);
        coreUserProfile.setEmailAddress("wangbin@sqian.com");
        coreUserProfile.setDateOfBirth(now + 1000);
        coreUserProfile.setFullName("李四");
        coreUserProfile.setGender(1);
        coreUserProfile.setJobTitle(1l);
        coreUserProfile.setMobilePhone("123");
        String orgShortName = "闪签";
        String uuid = "1234567";
        onboardingFlowNotifier.resendInvitationActivationEmailToImportedStaff(orgShortName, coreUserProfile, uuid);
    }



    @Test
    public void sendOpenAccountEmail() {
        CoreUserProfile coreUserProfile = new CoreUserProfile();
        long now = TimeUtils.getNowTimestmapInMillis();
        coreUserProfile.setCreatedTime(now);
        coreUserProfile.setCreatedUserId(10l);
        coreUserProfile.setEmailAddress("wangbin@sqian.com");
        coreUserProfile.setDateOfBirth(now + 1000);
        coreUserProfile.setFullName("张三");
        coreUserProfile.setGender(1);
        coreUserProfile.setJobTitle(1l);
        coreUserProfile.setMobilePhone("123");

        onboardingFlowNotifier.sendOpenAccountEmailToFirstStaff("闪签", coreUserProfile, "123");
        onboardingFlowNotifier.sendOpenAccountEmailToAdmin("闪签", "wangbin@sqian.com");
    }
*/    @Test
    public void sendEmailToBroadCase() {
        /*CoreUserProfile coreUserProfile = new CoreUserProfile();
        long now = TimeUtils.getNowTimestmapInMillis();
        coreUserProfile.setCreatedTime(now);
        coreUserProfile.setCreatedUserId(10l);
        coreUserProfile.setEmailAddress("wangbin@sqian.com");
        coreUserProfile.setEnrollDate(now + 1000);
        coreUserProfile.setFullName("张三");
        coreUserProfile.setGender(1);
        coreUserProfile.setJobTitle(1l);
        coreUserProfile.setMobilePhone("123");
        onboardingFlowNotifier.broadcastEmailToStaffAfterHrApproveOnboardingFlow("闪签科技", coreUserProfile, "工程师", Arrays.asList(coreUserProfile));
    */}

}
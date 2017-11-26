package hr.wozai.service.api.controller;

import hr.wozai.service.feed.client.facade.FeedFacade;
import hr.wozai.service.feed.client.facade.RewardFacade;
import hr.wozai.service.nlp.client.labelcloud.facade.LabelCloudFacade;
import hr.wozai.service.review.client.facade.*;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.thirdparty.client.facade.CaptchaFacade;
import hr.wozai.service.thirdparty.client.facade.HistoryLogFacade;
import hr.wozai.service.thirdparty.client.facade.MessageCenterFacade;
import hr.wozai.service.thirdparty.client.facade.SmsFacade;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.user.client.common.facade.CommonToolFacade;
import hr.wozai.service.user.client.conversation.facade.ConvrFacade;
import hr.wozai.service.user.client.document.facade.AvatarFacade;
import hr.wozai.service.user.client.document.facade.DocumentFacade;
import hr.wozai.service.user.client.okr.facade.OkrFacade;
import hr.wozai.service.user.client.onboarding.facade.OnboardingFlowFacade;
import hr.wozai.service.user.client.onboarding.facade.OnboardingTemplateFacade;
import hr.wozai.service.user.client.survey.facade.SurveyFacade;
import hr.wozai.service.user.client.userorg.facade.*;
import org.springframework.asm.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/27
 */
@Component
public class FacadeFactory {
  @Autowired
  @Qualifier("userFacadeProxy")
  private ThriftClientProxy userFacadeProxy;

  @Autowired
  @Qualifier("userProfileFacadeProxy")
  private ThriftClientProxy userProfileFacadeProxy;

  @Autowired
  @Qualifier("tokenFacadeProxy")
  private ThriftClientProxy tokenFacadeProxy;

  @Autowired
  @Qualifier("captchaFacadeProxy")
  private ThriftClientProxy captchaFacadeProxy;

  @Autowired
  @Qualifier("smsFacadeProxy")
  private ThriftClientProxy smsFacadeProxy;

  @Autowired
  @Qualifier("securityModelFacadeProxy")
  private ThriftClientProxy securityModelFacadeProxy;

  @Autowired
  @Qualifier("feedFacadeProxy")
  private ThriftClientProxy feedFacadeProxy;

  @Autowired
  @Qualifier("reviewTemplateFacadeProxy")
  private ThriftClientProxy reviewTemplateFacadeProxy;

  @Autowired
  @Qualifier("reviewActivityFacadeProxy")
  private ThriftClientProxy reviewActivityFacadeProxy;

  @Autowired
  @Qualifier("reviewActivityDetailFacadeProxy")
  private ThriftClientProxy reviewActivityDetailFacadeProxy;

  @Autowired
  @Qualifier("reviewActivityProjectFacadeProxy")
  private ThriftClientProxy reviewActivityProjectFacadeProxy;

  @Autowired
  @Qualifier("reviewInvitationFacadeProxy")
  private ThriftClientProxy reviewInvitationFacadeProxy;

  @Autowired
  @Qualifier("reviewInvitationDetailFacadeProxy")
  private ThriftClientProxy reviewInvitationDetailFacadeProxy;

  @Autowired
  @Qualifier("reviewInvitationProjectFacadeProxy")
  private ThriftClientProxy reviewInvitationProjectFacadeProxy;

  @Autowired
  @Qualifier("okrFacadeProxy")
  private ThriftClientProxy okrFacadeProxy;

  @Autowired
  @Qualifier("profileTemplateFacadeProxy")
  private ThriftClientProxy profileTemplateFacadeProxy;

  @Autowired
  @Qualifier("onboardingTemplateFacadeProxy")
  private ThriftClientProxy onboardingTemplateFacadeProxy;

  @Autowired
  @Qualifier("onboardingFlowFacadeProxy")
  private ThriftClientProxy onboardingFlowFacadeProxy;

  @Autowired
  @Qualifier("orgFacadeProxy")
  private ThriftClientProxy orgFacadeProxy;

  @Autowired
  @Qualifier("documentFacadeProxy")
  private ThriftClientProxy documentFacadeProxy;

  @Autowired
  @Qualifier("avatarFacadeProxy")
  private ThriftClientProxy avatarFacadeProxy;

  @Autowired
  @Qualifier("messageCenterFacadeProxy")
  private ThriftClientProxy messageCenterFacadeProxy;

  @Autowired
  @Qualifier("commonToolFacadeProxy")
  private ThriftClientProxy commonToolFacadeProxy;

  @Autowired
  @Qualifier("historyLogFacadeProxy")
  private ThriftClientProxy historyLogFacadeProxy;

  @Autowired
  @Qualifier("navigationFacadeProxy")
  private ThriftClientProxy navigationFacadeProxy;

  @Autowired
  @Qualifier("rewardFacadeProxy")
  private ThriftClientProxy rewardFacadeProxy;

  @Autowired
  @Qualifier("convrFacadeProxy")
  private ThriftClientProxy convrFacadeProxy;

  @Autowired
  @Qualifier("surveyFacadeProxy")
  private ThriftClientProxy surveyFacadeProxy;
/**
  @Autowired
  @Qualifier("labelCloudFacadeProxy")
  private ThriftClientProxy labelCloudFacadeProxy;
**/
  private UserFacade userFacade;
  private UserProfileFacade userProfileFacade;
  private TokenFacade tokenFacade;
  private CaptchaFacade captchaFacade;
  private SmsFacade smsFacade;
  private SecurityModelFacade securityModelFacade;
  private FeedFacade feedFacade;
  private ReviewTemplateFacade reviewTemplateFacade;
  private ReviewActivityFacade reviewActivityFacade;
  private ReviewActivityDetailFacade reviewActivityDetailFacade;
  private ReviewActivityProjectFacade reviewActivityProjectFacade;
  private ReviewInvitationFacade reviewInvitationFacade;
  private ReviewInvitationDetailFacade reviewInvitationDetailFacade;
  private ReviewInvitationProjectFacade reviewInvitationProjectFacade;
  private OkrFacade okrFacade;
  private ProfileTemplateFacade profileTemplateFacade;
  private OnboardingTemplateFacade onboardingTemplateFacade;
  private OnboardingFlowFacade onboardingFlowFacade;
  private OrgFacade orgFacade;
  private DocumentFacade documentFacade;
  private AvatarFacade avatarFacade;
  private MessageCenterFacade messageCenterFacade;
  private CommonToolFacade commonToolFacade;
  private HistoryLogFacade historyLogFacade;
  private NavigationFacade navigationFacade;
  private RewardFacade rewardFacade;
  private ConvrFacade convrFacade;
  private SurveyFacade surveyFacade;
//  private LabelCloudFacade labelCloudFacade;

  @PostConstruct
  public void init() throws Exception {
    userFacade = (UserFacade) userFacadeProxy.getObject();
    userProfileFacade = (UserProfileFacade) userProfileFacadeProxy.getObject();
    tokenFacade = (TokenFacade) tokenFacadeProxy.getObject();
    captchaFacade = (CaptchaFacade) captchaFacadeProxy.getObject();
    smsFacade = (SmsFacade) smsFacadeProxy.getObject();
    securityModelFacade = (SecurityModelFacade) securityModelFacadeProxy.getObject();
    feedFacade = (FeedFacade) feedFacadeProxy.getObject();
    reviewTemplateFacade = (ReviewTemplateFacade) reviewTemplateFacadeProxy.getObject();
    reviewActivityFacade = (ReviewActivityFacade) reviewActivityFacadeProxy.getObject();
    reviewActivityDetailFacade = (ReviewActivityDetailFacade) reviewActivityDetailFacadeProxy.getObject();
    reviewActivityProjectFacade = (ReviewActivityProjectFacade) reviewActivityProjectFacadeProxy.getObject();
    reviewInvitationFacade = (ReviewInvitationFacade) reviewInvitationFacadeProxy.getObject();
    reviewInvitationDetailFacade = (ReviewInvitationDetailFacade) reviewInvitationDetailFacadeProxy.getObject();
    reviewInvitationProjectFacade = (ReviewInvitationProjectFacade) reviewInvitationProjectFacadeProxy.getObject();
    okrFacade = (OkrFacade) okrFacadeProxy.getObject();
    profileTemplateFacade = (ProfileTemplateFacade) profileTemplateFacadeProxy.getObject();
    onboardingTemplateFacade = (OnboardingTemplateFacade) onboardingTemplateFacadeProxy.getObject();
    onboardingFlowFacade = (OnboardingFlowFacade) onboardingFlowFacadeProxy.getObject();
    orgFacade = (OrgFacade) orgFacadeProxy.getObject();
    documentFacade = (DocumentFacade) documentFacadeProxy.getObject();
    avatarFacade = (AvatarFacade) avatarFacadeProxy.getObject();
    messageCenterFacade = (MessageCenterFacade) messageCenterFacadeProxy.getObject();
    commonToolFacade = (CommonToolFacade) commonToolFacadeProxy.getObject();
    historyLogFacade = (HistoryLogFacade) historyLogFacadeProxy.getObject();
    navigationFacade = (NavigationFacade) navigationFacadeProxy.getObject();
    rewardFacade = (RewardFacade) rewardFacadeProxy.getObject();
    convrFacade = (ConvrFacade) convrFacadeProxy.getObject();
    surveyFacade = (SurveyFacade) surveyFacadeProxy.getObject();
  //  labelCloudFacade = (LabelCloudFacade) labelCloudFacadeProxy.getObject();
  }

  public UserFacade getUserFacade() {
    return userFacade;
  }

  public UserProfileFacade getUserProfileFacade() {
    return userProfileFacade;
  }

  public TokenFacade getTokenFacade() {
    return tokenFacade;
  }

  public CaptchaFacade getCaptchaFacade() {
    return captchaFacade;
  }

  public SmsFacade getSmsFacade() {
    return smsFacade;
  }

  public SecurityModelFacade getSecurityModelFacade() {
    return securityModelFacade;
  }

  public FeedFacade getFeedFacade() {
    return feedFacade;
  }

  public ReviewTemplateFacade getReviewTemplateFacade() {
    return reviewTemplateFacade;
  }

  public ReviewActivityFacade getReviewActivityFacade() {
    return reviewActivityFacade;
  }

  public ReviewActivityDetailFacade getReviewActivityDetailFacade() {
    return reviewActivityDetailFacade;
  }

  public ReviewActivityProjectFacade getReviewActivityProjectFacade() {
    return reviewActivityProjectFacade;
  }

  public ReviewInvitationFacade getReviewInvitationFacade() {
    return reviewInvitationFacade;
  }

  public ReviewInvitationDetailFacade getReviewInvitationDetailFacade() {
    return reviewInvitationDetailFacade;
  }

  public ReviewInvitationProjectFacade getReviewInvitationProjectFacade() {
    return reviewInvitationProjectFacade;
  }

  public OkrFacade getOkrFacade() {
    return okrFacade;
  }

  public ProfileTemplateFacade getProfileTemplateFacade() {
    return profileTemplateFacade;
  }

  public OnboardingTemplateFacade getOnboardingTemplateFacade() {
    return onboardingTemplateFacade;
  }

  public OnboardingFlowFacade getOnboardingFlowFacade() {
    return onboardingFlowFacade;
  }

  public OrgFacade getOrgFacade() {
    return orgFacade;
  }

  public DocumentFacade getDocumentFacade() {
    return documentFacade;
  }

  public AvatarFacade getAvatarFacade() {
    return avatarFacade;
  }

  public MessageCenterFacade getMessageCenterFacade() {
    return messageCenterFacade;
  }

  public CommonToolFacade getCommonToolFacade() {
    return commonToolFacade;
  }

  public HistoryLogFacade getHistoryLogFacade() {
    return historyLogFacade;
  }

  public NavigationFacade getNavigationFacade() {
    return navigationFacade;
  }

  public RewardFacade getRewardFacade(){
    return rewardFacade;
  }

  public ConvrFacade getConvrFacade() {
    return convrFacade;
  }

  public SurveyFacade getSurveyFacade() {
    return surveyFacade;
  }
/**
  public LabelCloudFacade getLabelCloudFacade() {
    return labelCloudFacade;
  }
**/

  public void checkServiceStatus(ServiceStatusDTO serviceStatusDTO, ServiceStatus serviceStatus) {
    ServiceStatus serviceStatusFromDTO = ServiceStatus.getEnumByCode(serviceStatusDTO.getCode());
    if (serviceStatusFromDTO != serviceStatus) {
      throw new ServiceStatusException(serviceStatusFromDTO);
    }
  }
}

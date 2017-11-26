package hr.wozai.service.user.server.test.service.impl;

import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.user.server.service.SecurityModelService;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/25
 */
public class MockTestSample{
  private static Logger LOGGER = LoggerFactory.getLogger(MockTestSample.class);

  /*@Autowired
  SecurityModelService securityModelService;

  @Mock
  TeamService spyTeamService;

  @Autowired
  TeamService teamService;*/



  /*@Before
  public void initMock() throws Exception {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(securityModelService), "teamService", spyTeamService);
  }

  public void test() throws Exception {
    List<Long> userIds = new ArrayList<>();
    userIds.add(1L);
    userIds.add(2L);
    Mockito.doReturn(userIds).when(spyTeamService).getUserIdsByOrgIdAndTeamIds(Mockito.anyLong(), Mockito.anyList(), 1, 20);
  }*/

  @Test
  public void test() {

  }
}
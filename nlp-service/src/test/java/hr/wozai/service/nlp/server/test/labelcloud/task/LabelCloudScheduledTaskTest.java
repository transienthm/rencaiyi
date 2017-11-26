package hr.wozai.service.nlp.server.test.labelcloud.task;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hr.wozai.service.nlp.server.test.labelcloud.base.TestBase;
import hr.wozai.service.nlp.server.task.labelcloud.LabelCloudScheduledTask;


public class LabelCloudScheduledTaskTest extends TestBase {

  @Autowired
  private LabelCloudScheduledTask labelCloudScheduledTask;

  @Test
  public void testLabelCloudScheduledTask() throws Throwable {
    this.labelCloudScheduledTask.run();
  }

}
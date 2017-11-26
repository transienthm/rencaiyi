package hr.wozai.service.review.server.test.service;

import hr.wozai.service.review.server.model.ReviewComment;
import hr.wozai.service.review.server.model.ReviewInvitation;
import hr.wozai.service.review.server.service.ReviewCommentService;
import hr.wozai.service.review.server.service.ReviewInvitationService;
import hr.wozai.service.review.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.HashSet;
import java.util.List;

//import com.huaban.analysis.jieba.JiebaSegmenter;
//import com.huaban.analysis.jieba.WordDictionary;
//import com.huaban.analysis.jieba.SegToken;
//import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
//import hr.wozai.service.review.server.jgibblda.*;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-03
 */
public class ReviewInvitationServiceTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewInvitationServiceTest.class);

  @Autowired
  private ReviewInvitationService reviewInvitationService;

  @Autowired
  private ReviewCommentService reviewCommentService;

  private ReviewInvitation reviewInvitation;

  private ReviewComment reviewComment;

  private long orgId = 100L;

  private long templateId = 1L;

  private long revieweeId = 11L;
  private long reviewerId = 12L;

  private int isManager = 1;
  private int isSubmitted = 0;
  private int isCanceled = 0;
  private int isBackuped = 0;

  private int pageNumber = 1;
  private int pageSize = 5;

  private int itemType = 0;
  private long itemId = 55;
  private String content = "好";

  @Before
  public void setup() throws Exception {

    reviewInvitation = new ReviewInvitation();

    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsManager(isManager);
    reviewInvitation.setIsSubmitted(isSubmitted);
    reviewInvitation.setIsCanceled(isCanceled);
    reviewInvitation.setIsBackuped(isBackuped);
    reviewInvitation.setLastModifiedUserId(revieweeId);


    reviewComment = new ReviewComment();

    reviewComment.setOrgId(orgId);
    reviewComment.setTemplateId(templateId);
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(itemType);
    reviewComment.setItemId(itemId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);
  }

  //@Test
  //public void testInsertReviewInvitation() throws Exception {
  //}

  //@Test
  //public void testBatchInsertReviewInvitations() throws Exception {
  //}

  //@Test
  //public void testFindReviewInvitation() throws Exception {

  //}

  //@Test
  //public void testListReviewInvitationAsReviewee() throws Exception {

  //}

  //@Test
  //public void testListReviewInvitationAsReviewer() throws Exception {

  //}

  //@Test
  //public void testListReviewInvitationOfTemplate() throws Exception {

  //}

  //@Test
  //public void testUpdateReviewInvitation() throws Exception {

  //}

  //@Test
  //public void testCancelReviewInvitation() throws Exception {

  //}

  @Test
  public void testRefuseReviewInvitation() throws Exception {

    long reviewInvitationId;
    ReviewInvitation result;
    List<ReviewComment> reviewComments;
    //refuse "reviewerId"

    reviewComment.setReviewerId(revieweeId);
    reviewCommentService.insertReviewComment(reviewComment);

    reviewInvitationId = reviewInvitationService.insertReviewInvitation(reviewInvitation);

    reviewComment.setReviewerId(reviewerId);
    reviewCommentService.insertReviewComment(reviewComment);

    reviewComments = reviewCommentService.listReviewAllCommentByReviewer(
        orgId, templateId, revieweeId, reviewerId);
    Assert.assertEquals(reviewComments.size(), 1);

    reviewInvitation.setIsCanceled(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationService.refuseReviewInvitation(reviewInvitation);

    result = reviewInvitationService.findReviewInvitation(orgId, reviewInvitationId);
    Assert.assertEquals(result.getIsCanceled().intValue(), 1);
    Assert.assertEquals(result.getIsSubmitted().intValue(), 0);

    reviewComments = reviewCommentService.listReviewAllCommentByReviewer(
        orgId, templateId, revieweeId, reviewerId);
    Assert.assertEquals(reviewComments.size(), 0);

  }

  @Test
  public void testReviewInvitation() {
    reviewInvitationService.batchInsertReviewInvitations(null);

    try {
      reviewInvitationService.findReviewInvitation(0L, 0L);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      reviewInvitationService.findManagerInvitation(0L, 0L, 0L);
    } catch (Exception e) {
      e.printStackTrace();
    }

    reviewInvitationService.updateReviewInvitationBackupStatus(0L, 0L, 0);
    reviewInvitationService.countReviewInvitationOfTemplate(0L, 0L);
    reviewInvitationService.countFinishedReviewInvitationOfTemplate(0L, 0L);
    try {
      reviewInvitationService.listAllReviewInvitationsByTemplatesAndReviewer(0L, null, 0L);
    } catch (Exception e) {
      e.printStackTrace();
    }
    reviewInvitationService.listAllReviewInvitationByTemplateIdAndRevieweeIdExceptManager(0, 0, 0);

    reviewInvitation.setOrgId(0L);
    reviewInvitation.setTemplateId(0L);
    reviewInvitation.setReviewerId(0L);
    reviewInvitation.setIsManager(1);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);
    reviewInvitationService.listAllReviewInvitationByTemplateIdAndReviewerIdAndIsManager(0, 0, 0, 1);
  }



//  @Test
//  public void testLabelCloud() {
//    {
//      try {
//        //File file_r = new File("/Users/liuzhenfu/WORKSPACE/PROJECTS/RENCAIYI/BACKEND/label-cloud/SogouC.reduced/Reduced/C000008/10.txt");
//        File file_r = new File("/Users/liuzhenfu/WORKSPACE/PROJECTS/RENCAIYI/BACKEND/label-cloud/SogouC.reduced/Reduced/C000008/965.txt");
//        BufferedReader reader = new BufferedReader(
//                new InputStreamReader(
//                        new FileInputStream(file_r),
//                        "GBK"
//                )
//        );
//
//        File file_w = new File("/Users/liuzhenfu/WORKSPACE/PROJECTS/RENCAIYI/BACKEND/label-cloud/JGibbLDA-v.1.0/data/train/sougou_news.dat");
//        file_w.delete();
//        file_w.createNewFile();
//        BufferedWriter writer = new BufferedWriter(
//                new OutputStreamWriter(
//                        new FileOutputStream(file_w),
//                        "UTF-8")
//        );
//
//        File file_s = new File("/Users/liuzhenfu/WORKSPACE/PROJECTS/RENCAIYI/BACKEND/label-cloud/JGibbLDA-v.1.0/data/train/stop_words.txt");
//        BufferedReader reader_s = new BufferedReader(
//                new InputStreamReader(
//                        new FileInputStream(file_s),
//                        "UTF-8"
//                )
//        );
//
//        String line = null;
//        HashSet stopWords = new HashSet();
//        while ((line = reader_s.readLine()) != null) {
//          line = line.trim();
//          if (!line.isEmpty()) {
//            stopWords.add(line);
//          }
//        }
//
//        JiebaSegmenter segmenter = new JiebaSegmenter();
//
//        long lineNumber = 0;
//        String content = "";
//        while ((line = reader.readLine()) != null) {
//          //line = line.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-，。]", "");
//          //line = line.replaceAll("[。“”，：；,—、《》（）的　]", "");
//          List<SegToken> segRes = segmenter.process(line, SegMode.SEARCH);
//          String line_res = "";
//          for (SegToken segToken : segRes) {
//            if (stopWords.contains(segToken.word)) {
//              continue;
//            }
//            if (!line_res.isEmpty()) {
//              line_res += " ";
//            }
//            line_res += segToken.word;
//          }
//          line_res = line_res.trim();
//          if (!line_res.isEmpty()) {
//            lineNumber++;
//            content += line_res + "\n";
//          }
//        }
//        if (lineNumber > 0) {
//          content = String.valueOf(lineNumber) + "\n" + content;
//          System.out.println(content);
//          writer.write(content);
//        }
//
//        reader.close();
//        writer.close();
//        reader_s.close();
//
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }
//
//    {
//      LDACmdOption ldaOption = new LDACmdOption();
//      ldaOption.est = true;
//      ldaOption.estc = false;
//      ldaOption.modelName = "model-final";
//      ldaOption.dir = "/Users/liuzhenfu/WORKSPACE/PROJECTS/RENCAIYI/BACKEND/label-cloud/JGibbLDA-v.1.0/data/train/";
//      ldaOption.dfile = "sougou_news.dat";
//      ldaOption.alpha = 0.5;
//      ldaOption.beta = 0.1;
//      ldaOption.K = 15;
//      ldaOption.niters = 1000;
//      //ldaOption.savestep = 100;
//      ldaOption.twords = 15;
//      Estimator estimator = new Estimator();
//      estimator.init(ldaOption);
//      estimator.estimate();
//    }
//
//    {
//      LDACmdOption ldaOption = new LDACmdOption();
//      ldaOption.inf = true;
//      ldaOption.estc = false;
//      ldaOption.dir = "/Users/liuzhenfu/WORKSPACE/PROJECTS/RENCAIYI/BACKEND/label-cloud/JGibbLDA-v.1.0/data/train/";
//      ldaOption.modelName = "model-final";
//      ldaOption.dfile = "sougou_news.dat";
//      ldaOption.niters = 30;
//      ldaOption.twords = 15;
//      Inferencer inferencer = new Inferencer();
//      inferencer.init(ldaOption);
//      inferencer.inference();
//    }
//
//    {
//      try {
//        File file = new File("/Users/liuzhenfu/WORKSPACE/PROJECTS/RENCAIYI/BACKEND/label-cloud/JGibbLDA-v.1.0/data/train/sougou_news.dat.model-final.twords");
//        BufferedReader reader = new BufferedReader(
//                new InputStreamReader(
//                        new FileInputStream(file),
//                        "UTF-8"
//                )
//        );
//
//        String line = null;
//        HashSet finalLabels = new HashSet();
//        while ((line = reader.readLine()) != null) {
//          if (!line.contains("\t")) {
//            continue;
//          }
//          line = line.trim();
//          if (line.isEmpty()) {
//            continue;
//          }
//          String[] strArray = line.split(" ");
//          if (strArray.length != 2) {
//            continue;
//          }
//          double weight = Double.parseDouble(strArray[1]);
//          if (weight < 0.03) {
//            continue;
//          }
//          finalLabels.add(strArray[0]);
//        }
//        reader.close();
//        System.out.println(finalLabels);
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }
//  }
}
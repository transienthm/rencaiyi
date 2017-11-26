package hr.wozai.service.api.vo.review;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-24
 */
@Data
@NoArgsConstructor
public class ReviewTemplateContainRevieweeProfilesListVO {

  private List<ReviewTemplateContainRevieweeProfilesVO> reviewTemplates;

  private Long totalNumber;

}

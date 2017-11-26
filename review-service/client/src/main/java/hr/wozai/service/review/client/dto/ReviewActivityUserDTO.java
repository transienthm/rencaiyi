package hr.wozai.service.review.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-05-12
 */
@ThriftStruct
public final class ReviewActivityUserDTO extends BaseThriftObject {

  private ReviewActivityDTO activityDTO;

  private ReviewInvitationDTO managerInvitationDTO;

  private List<ReviewInvitationDTO> staffInvitationDTOs;

  private String managerFullName;

  @ThriftField(1)
  public ReviewActivityDTO getActivityDTO() {
    return activityDTO;
  }

  @ThriftField
  public void setActivityDTO(ReviewActivityDTO activityDTO) {
    this.activityDTO = activityDTO;
  }

  @ThriftField(2)
  public ReviewInvitationDTO getManagerInvitationDTO() {
    return managerInvitationDTO;
  }

  @ThriftField
  public void setManagerInvitationDTO(ReviewInvitationDTO managerInvitationDTO) {
    this.managerInvitationDTO = managerInvitationDTO;
  }

  @ThriftField(3)
  public List<ReviewInvitationDTO> getStaffInvitationDTOs() {
    return staffInvitationDTOs;
  }

  @ThriftField
  public void setStaffInvitationDTOs(List<ReviewInvitationDTO> staffInvitationDTOs) {
    this.staffInvitationDTOs = staffInvitationDTOs;
  }

  @ThriftField(4)
  public String getManagerFullName() {
    return managerFullName;
  }

  @ThriftField
  public void setManagerFullName(String managerFullName) {
    this.managerFullName = managerFullName;
  }
}

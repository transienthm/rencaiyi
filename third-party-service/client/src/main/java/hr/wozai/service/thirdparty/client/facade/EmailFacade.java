// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.client.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;

import java.io.IOException;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-08-24
 */
@ThriftService
public interface EmailFacade {

  /**
   * Send a signup verification email
   *
   * @param emailAddress
   * @return
   */
  @ThriftMethod
  BooleanDTO sendSignupEmail(String fullName, String emailAddress);

  /**
   * Verify the signup url against database
   *
   * @param emailAddress
   * @param verificationCode
   * @return
   */
  @ThriftMethod
  BooleanDTO verifySignupEmail(String emailAddress, String verificationCode);

  /**
   * Send a signup verification email
   *
   * @param emailAddress
   * @return
   */
  @ThriftMethod
  BooleanDTO sendInvitationEmail(String fromFullName, String emailAddress);

  /**
   * Verify the signup url against database
   *
   * @param emailAddress
   * @param verificationCode
   * @return
   */
  @ThriftMethod
  BooleanDTO verifyInvitationEmail(String emailAddress, String verificationCode);

  /**
   * Send signer signing request email
   *
   * @param dstEmailAddress
   * @param participandId
   * @param senderFullName
   * @param senderEmailAddress
   * @param documentName
   * @param publicMessage
   * @return
   */
  @ThriftMethod
  BooleanDTO sendSigningRequestEmail(String dstEmailAddress, String participandId, String documentId,
                                     String senderFullName, String senderEmailAddress,
                                     String documentName, String publicMessage);

  /**
   * Send cc viewing request email
   *
   * @param dstEmailAddress
   * @param participantId
   * @param documentId
   * @param senderFullName
   * @param senderEmailAddress
   * @param documentName
   * @param publicMessage
   * @return
   */
  @ThriftMethod
  BooleanDTO sendCCRequestEmail(String dstEmailAddress, String participantId, String documentId,
                                String senderFullName, String senderEmailAddress,
                                String documentName, String publicMessage);

  /**
   * Verify the document relevant url sent via email, including:
   *  signing-request email;
   *  cc-request email;
   *  document-notification email
   *
   * @param participantId
   * @param verification
   * @return
   */
  @ThriftMethod
  BooleanDTO verifyDocumentRelevantEmail(String participantId, String verification);

  /**
   * Send the reset-password email to user's email address
   *
   * @param fullName
   * @param emailAddress
   * @return
   */
  @ThriftMethod
  BooleanDTO sendResetPasswordEmail(String fullName, String emailAddress);

  /**
   * Verify the url send via email
   *
   * @param emailAddress
   * @param verification
   * @return
   */
  @ThriftMethod
  BooleanDTO verifyResetPasswordEmail(String emailAddress, String verification);

}

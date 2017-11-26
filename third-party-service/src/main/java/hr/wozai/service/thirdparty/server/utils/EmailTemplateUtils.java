// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.server.utils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-10-15
 */
public class EmailTemplateUtils {

  private static final String HTTP_OR_HTTPS = "http";
  private static final String DOMAIN_NAME = "secretapp.sqian.com";

  public static String getSignupActivationEmailContent(String fullName, String dstEmailAddress, String verificationCode) {
    return //getEmailInfoTemplatePreDiv()
           getEmailInfoTemplateSignupActivationDiv(fullName, dstEmailAddress, verificationCode);
  }

  public static String getSignupInvitationEmailContent(String fromFullName, String dstEmailAddress, String verification) {

    return //getEmailInvitationTemplatePreDiv()
           getEmailInvitationcTemplateDiv(fromFullName, dstEmailAddress, verification);
  }

  public static String getResetPasswordEmailContent(String fullName, String emailAddress, String verification) {
    return getEmailInfoTemplateResetPasswordDiv(fullName, emailAddress, verification);
  }

  public static String getSigningRequestEmailContent(String participantId, String documentId, String verification,
                                                     String senderFullName, String senderEmailAddress,
                                                     String documentName, String publicMessage) {
//    return getEmailDocPlusTemplatePreDiv()
//           + getEmailDocPlusTemplateSigningRequestDiv(participantId, documentId, verification, senderFullName,
//                                                      senderEmailAddress, documentName, publicMessage)
//           + getEmailDocPlusTemplatePostDiv();
    return getEmailDocPlusTemplateSigningRequestDiv(participantId, documentId, verification, senderFullName,
                                                    senderEmailAddress, documentName, publicMessage);
  }

  public static String getCCRequestEmailContent(String participantId, String documentId, String verification,
                                              String senderFullName, String senderEmailAddress,
                                              String documentName, String publicMessage) {
//    return getEmailDocPlusTemplatePreDiv()
//           + getEmailDocPlusTemplateCCRequestDiv(participantId, documentId, verification, senderFullName,
//                                                 senderEmailAddress, documentName, publicMessage)
//           + getEmailDocPlusTemplatePostDiv();
    return getEmailDocPlusTemplateCCRequestDiv(participantId, documentId, verification, senderFullName,
                                               senderEmailAddress, documentName, publicMessage);
  }

  private static String getEmailInfoTemplatePreDiv() {

    String emailContent =  "<head>\n"
                          + "  <meta charset=\"UTF-8\">\n"
                          + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
                          + "  <title>闪签</title>\n"
                          + "</head>\n"
                          + "<body>\n"
                          + "<style type=\"text/css\">\n"
                          + "  body{\n"
                          + "    background: #eee;\n"
                          + "    font-size: 14px;\n"
                          + "    color: #555;\n"
                          + "  }\n"
                          + "  /*a {\n"
                          + "    color: #3e98df;\n"
                          + "  }\n"
                          + "  .container{\n"
                          + "    padding: 10px;\n"
                          + "    max-width: 800px;margin: 0 auto;\n"
                          + "  }\n"
                          + "  .main{\n"
                          + "    position: relative;\n"
                          + "    background: #fff;\n"
                          + "    padding: 23px 30px 18px 25px;\n"
                          + "    box-shadow: 1px 3px 8px 0px #ddd;\n"
                          + "  }\n"
                          + "  .main .inner{max-width: 650px;}\n"
                          + "  .main .from{\n"
                          + "    color:#aaa;\n"
                          + "    text-align: right;\n"
                          + "    font-size: 12px;\n"
                          + "    padding: 10px 3px 0 0;\n"
                          + "  }\n"
                          + "  .footer{\n"
                          + "    padding: 10px 20px;\n"
                          + "    text-align: center;\n"
                          + "    color: #999;\n"
                          + "  }*/\n"
                          + "</style>\n";

    return emailContent;

  }


  private static String getEmailInfoTemplateSignupActivationDiv(
      String fullName, String dstEmailAddress, String verificationCode) {

    String url =  HTTP_OR_HTTPS + "://" + DOMAIN_NAME + "/auth/signup/email_activation?email=" + dstEmailAddress + "&verification=" + verificationCode;

    String emailContent = "<div class=\"wrapper\" style=\"background: #eee;font-size: 14px;color: #555;\">\n"
                          + "  <div class=\"container\" style=\"padding: 10px;max-width: 800px;margin: 0 auto;\">\n"
                          + "    <div class=\"main\" style=\"position: relative;background: #fff;padding: 23px 30px 18px 25px;box-shadow: 1px 3px 8px 0px #ddd;\">\n"
                          + "      <img src=\"http://img.shanqian.cc/email/logo-min.png\" width=\"65\" height=\"26\" alt=\"\" class=\"logo\" style=\"float:right;\">\n"
                          + "      <div class=\"inner\" style=\"max-width: 650px;\">\n"
                          + "        " + fullName + "，您好：<br><br>\n"
                          + "        感谢注册闪签。请点击以下链接验证邮箱，激活帐号。<br>\n"
                          + "        <a href=\"" + url + "\" style=\"color: #3e98df\">" + url + "</a><br><br>\n"
                          + "\n"
                          + "        如果以上网址不可点击，请将它复制到浏览地址栏直接访问。\n"
                          + "      </div>\n"
                          + "      <div class=\"from\" style=\"color:#aaa;text-align: right;font-size: 12px;padding: 10px 3px 0 0;\">发送自：<a style=\"color: #3e98df\" class=\"link\" href=\"https://www.sqian.com\">www.sqian.com</a></div>\n"
                          + "    </div>\n"
                          + "    <div class=\"footer\" style=\"padding: 20px 20px 50px 20px;text-align: center;color: #999;\">\n"
                          + "      闪签·最快的合法签约方式 　\n"
                          + "    </div>\n"
                          + "    <div class=\"legal\" style=\"color:#999;font-size: 12px;text-align: center;padding-bottom: 50px;\">\n"
                          + "      <h3 style=\"font-size: 14px;font-weight: 14px;\">关于电子合同与电子签名的法律效力说明</h3>\n"
                          + "      <div class=\"content\" style=\"padding: 10px;max-width: 800px;border: 1px solid #e5e5e5;margin: auto;text-align: left;border-radius: 4px;\">\n"
                          + "       《中华人民共和国合同法》第十一条明确规定“书面形式是指合同书、信件和数据电文（包括电报、电传、传真、电子数据交换和电子邮件）等可以有形地表现所载内容的形式”。电子合同属于有效的书面形式。<br>\n"
                          + "       《中华人民共和国电子签名法》第十四条明确规定“可靠的电子签名与传统手写签名或盖章具有同等的法律效力”。电子签名作为一种民事行为，同样受《合同法》等相关法律法规保护。\n"
                          + "      </div>\n"
                          + "    </div>\n"
                          + "  </div>\n"
                          + "</div>";

    return emailContent;
  }

  private static String getEmailInfoTemplateResetPasswordDiv(
      String fullName, String dstEmailAddress, String verificationCode) {

    String url =  HTTP_OR_HTTPS + "://" + DOMAIN_NAME + "/reset_password?email=" + dstEmailAddress + "&verification=" + verificationCode;

    String emailContent = "<div class=\"wrapper\" style=\"background: #eee;font-size: 14px;color: #555;\">\n"
                          + "  <div class=\"container\" style=\"padding: 10px;max-width: 800px;margin: 0 auto;\">\n"
                          + "    <div class=\"main\" style=\"position: relative;background: #fff;padding: 23px 30px 18px 25px;box-shadow: 1px 3px 8px 0px #ddd;\">\n"
                          + "      <img src=\"http://img.shanqian.cc/email/logo-min.png\" width=\"65\" height=\"26\" alt=\"\" class=\"logo\" style=\"float:right;\">\n"
                          + "      <div class=\"inner\" style=\"max-width: 650px;\">\n"
                          + "        " + fullName + "，您好：<br><br>\n"
                          + "        请点击以下链接重新设置您的密码。<br>\n"
                          + "        <a href=\"" + url + "\" style=\"color: #3e98df\">" + url + "</a><br><br>\n"
                          + "\n"
                          + "        如果以上网址不可点击，请将它复制到浏览地址栏直接访问。\n"
                          + "      </div>\n"
                          + "      <div class=\"from\" style=\"color:#aaa;text-align: right;font-size: 12px;padding: 10px 3px 0 0;\">发送自：<a style=\"color: #3e98df\" class=\"link\" href=\"https://www.sqian.com\">www.sqian.com</a></div>\n"
                          + "    </div>\n"
                          + "    <div class=\"footer\" style=\"padding: 20px 20px 50px 20px;text-align: center;color: #999;\">\n"
                          + "      闪签·最快的合法签约方式 　\n"
                          + "    </div>\n"
                          + "    <div class=\"legal\" style=\"color:#999;font-size: 12px;text-align: center;padding-bottom: 50px;\">\n"
                          + "      <h3 style=\"font-size: 14px;font-weight: 14px;\">关于电子合同与电子签名的法律效力说明</h3>\n"
                          + "      <div class=\"content\" style=\"padding: 10px;max-width: 800px;border: 1px solid #e5e5e5;margin: auto;text-align: left;border-radius: 4px;\">\n"
                          + "       《中华人民共和国合同法》第十一条明确规定“书面形式是指合同书、信件和数据电文（包括电报、电传、传真、电子数据交换和电子邮件）等可以有形地表现所载内容的形式”。电子合同属于有效的书面形式。<br>\n"
                          + "       《中华人民共和国电子签名法》第十四条明确规定“可靠的电子签名与传统手写签名或盖章具有同等的法律效力”。电子签名作为一种民事行为，同样受《合同法》等相关法律法规保护。\n"
                          + "      </div>\n"
                          + "    </div>\n"
                          + "  </div>\n"
                          + "</div>";

    return emailContent;
  }

  private static String getEmailInvitationTemplatePreDiv() {

    String emailContent =
        "<meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
        + "<title></title>\n"
        + "<style type=\"text/css\">body{\n"
        + "    background: #eee;\n"
        + "    font-size: 14px;\n"
        + "    color: #555;\n"
        + "  }\n"
        + "  /*a {\n"
        + "    color: #3e98df;\n"
        + "  }\n"
        + "  .container{\n"
        + "    padding: 10px;\n"
        + "    max-width: 800px;margin: 0 auto;\n"
        + "  }\n"
        + "  .main{\n"
        + "    position: relative;\n"
        + "    background: #fff;\n"
        + "    padding: 23px 30px 18px 25px;\n"
        + "    box-shadow: 1px 3px 8px 0px #ddd;\n"
        + "  }\n"
        + "  .main .inner{max-width: 650px;}\n"
        + "  .main .from{\n"
        + "    color:#aaa;\n"
        + "    text-align: right;\n"
        + "    font-size: 12px;\n"
        + "    padding: 10px 3px 0 0;\n"
        + "  }\n"
        + "  .footer{\n"
        + "    padding: 10px 20px;\n"
        + "    text-align: center;\n"
        + "    color: #999;\n"
        + "  }*/\n"
        + "</style>\n";

    return emailContent;
  }

  private static String getEmailInvitationcTemplateDiv(String fromFullName, String dstEmailAddress, String verification) {

    String url = HTTP_OR_HTTPS + "://" + DOMAIN_NAME
                 + "/auth/signup/invitation?email=" + dstEmailAddress
                 + "&verification=" + verification;

    String emailContent = "<div class=\"wrapper\" style=\"background: #eee;font-size: 14px;color: #555;\">\n"
                          + "  <div class=\"container\" style=\"padding: 10px;max-width: 800px;margin: 0 auto;\">\n"
                          + "    <div class=\"main\" style=\"position: relative;background: #fff;padding: 23px 30px 18px 25px;box-shadow: 1px 3px 8px 0px #ddd;\">\n"
                          + "      <img src=\"http://img.shanqian.cc/email/logo-min.png\" width=\"65\" height=\"26\" alt=\"\" class=\"logo\" style=\"float:right;\">\n"
                          + "      <div class=\"inner\" style=\"max-width: 650px;\">\n"
                          + "        您好：<br><br>\n"
                          + "        " + fromFullName + " 邀请您使用闪签。请点击以下链接验证邮箱，激活帐号。<br>\n"
                          + "        <a href=\"" + url + "\" style=\"color: #3e98df\">" + url + "</a><br><br>\n"
                          + "\n"
                          + "        如果以上网址不可点击，请将它复制到浏览地址栏直接访问。\n"
                          + "      </div>\n"
                          + "      <div class=\"from\" style=\"color:#aaa;text-align: right;font-size: 12px;padding: 10px 3px 0 0;\">发送自：<a style=\"color: #3e98df\" class=\"link\" href=\"https://www.sqian.com\">www.sqian.com</a></div>\n"
                          + "    </div>\n"
                          + "    <div class=\"footer\" style=\"padding: 20px 20px 50px 20px;text-align: center;color: #999;\">\n"
                          + "      闪签·最快的合法签约方式 　\n"
                          + "    </div>\n"
                          + "    <div class=\"legal\" style=\"color:#999;font-size: 12px;text-align: center;padding-bottom: 50px;\">\n"
                          + "      <h3 style=\"font-size: 14px;font-weight: 14px;\">关于电子合同与电子签名的法律效力说明</h3>\n"
                          + "      <div class=\"content\" style=\"padding: 10px;max-width: 800px;border: 1px solid #e5e5e5;margin: auto;text-align: left;border-radius: 4px;\">\n"
                          + "       《中华人民共和国合同法》第十一条明确规定“书面形式是指合同书、信件和数据电文（包括电报、电传、传真、电子数据交换和电子邮件）等可以有形地表现所载内容的形式”。电子合同属于有效的书面形式。<br>\n"
                          + "       《中华人民共和国电子签名法》第十四条明确规定“可靠的电子签名与传统手写签名或盖章具有同等的法律效力”。电子签名作为一种民事行为，同样受《合同法》等相关法律法规保护。\n"
                          + "      </div>\n"
                          + "    </div>\n"
                          + "  </div>\n"
                          + "</div>";
    return emailContent;
  }

  private static String getEmailDocPlusTemplatePreDiv() {

    String
        emailContent =
        "<meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
        + "<title></title>\n"
        + "<style type=\"text/css\">body{\n"
        + "    background: #eee;\n"
        + "    font-size: 14px;\n"
        + "  }\n"
        + "  /*\n"
        + "  .container{\n"
        + "    background: #fff;\n"
        + "    padding: 10px;\n"
        + "  }\n"
        + "  .header{\n"
        + "    padding: 10px 5px 15px 5px;\n"
        + "  }\n"
        + "  .main{\n"
        + "    background: #3e98df;\n"
        + "    color: #fff;\n"
        + "    padding: 40px 20px 40px 20px;\n"
        + "    text-align: center;\n"
        + "  }\n"
        + "  .main .desc{\n"
        + "    font-size: 16px;\n"
        + "  }\n"
        + "  .main .ps{\n"
        + "    max-width: 500px;\n"
        + "    margin: 0 auto;\n"
        + "    padding: 15px 15px;\n"
        + "    text-align: left;\n"
        + "    background: #4ea8ef;\n"
        + "    line-height: 2em;\n"
        + "  }\n"
        + "  .sender{\n"
        + "    padding: 20px 10px 10px 10px;\n"
        + "    color: #def;\n"
        + "    text-align: right;\n"
        + "    line-height: 1.2em;\n"
        + "  }\n"
        + "  .sender .name{\n"
        + "    color: #fff;\n"
        + "    font-weight: bold;\n"
        + "  }\n"
        + "  .main .btn-go{\n"
        + "    font-size: 16px;\n"
        + "    display: block;\n"
        + "    margin: 0 auto;\n"
        + "    width: 120px;\n"
        + "    margin-top: 20px;\n"
        + "    padding:  10px 0;\n"
        + "    border: 1px solid #fff;\n"
        + "    border-radius: 10px;\n"
        + "    color: #fff;\n"
        + "    text-decoration: none;\n"
        + "  }\n"
        + "  .footer{\n"
        + "    padding: 20px;\n"
        + "    text-align: center;\n"
        + "    color: #999;\n"
        + "  }*/\n"
        + "  .main .btn-go:hover{\n"
        + "    background: #4ea8ef;\n"
        + "  }\n"
        + "</style>";

    return emailContent;
  }

  private static String getEmailDocPlusTemplatePostDiv() {
    String emailContent = "<h3 style=\"font-size: 14px;font-weight: 14px;\">关于电子合同与电子签名的法律效力说明</h3>\n"
                          + "\n"
                          + "<div class=\"content\" style=\"padding: 10px;max-width: 800px;border: 1px solid #ddd;margin: auto;text-align: left;border-radius: 4px;\">《中华人民共和国电子签名法》第十四条明确规定&ldquo;可靠的电子签名与传统手写签名或盖章具有同等的法律效力&rdquo;。电子签名作为一种民事行为，同样受《合同法》等相关法律法规保护。<br />\n"
                          + "《中华人民共和国合同法》第十一条：书面形式是指合同书、信件和数据电文（包括电报、电传、传真、电子数据交换和电子邮件）等可以有形地表现所载内容的形式。</div>\n"
                          + "</div>\n"
                          + "</div>\n";
    return emailContent;
  }

  private static String getEmailDocPlusTemplateSigningRequestDiv(String participantId, String documentId,
                                                                 String verification, String senderFullName,
                                                                 String senderEmailAddress, String documentName,
                                                                 String publicMessage) {

    String url = HTTP_OR_HTTPS + "://" + DOMAIN_NAME + "/document?participantId="
                 + participantId + "&verification=" + verification + "/#/documents/" + documentId;

    String emailContent = "<div class=\"wrapper\" style=\"background: #eee;font-size: 14px;padding:10px\">\n"
                          + "  <div class=\"container\" style=\"background: #fff;max-width: 800px;margin: 0 auto;padding: 10px;box-shadow: 1px 3px 8px 0px #ddd;\">\n"
                          + "    <div class=\"header\" style=\"padding: 10px 5px 15px 5px;\">\n"
                          + "            <img src=\"http://img.shanqian.cc/email/logo-min.png\" width=\"65\" height=\"26\" alt=\"\" class=\"logo\">\n"
                          + "    </div>\n"
                          + "    <div class=\"main\" style=\"background: #3e98df;color: #fff;padding: 40px 20px 40px 20px;text-align: center;\">\n"
                          + "      <img src=\"http://img.shanqian.cc/email/icon-sign-min.png\" width=\"108\" src=\"\">\n"
                          + "      <p class=\"desc\" style=\"font-size: 16px;\">" + senderFullName + "给您发送了一份文档，请查阅和签署。</p>\n"
                          + "      <div class=\"ps\" style=\"max-width: 500px;margin: 0 auto;padding: 15px 15px;text-align: left;background: #4ea8ef;line-height: 2em;\">\n"
                          + "        " + publicMessage + "\n"
                          + "        <div class=\"sender\" style=\"padding: 20px 10px 10px 10px;color: #def;text-align: right;line-height: 1.2em;\">\n"
                          + "          <div class=\"name\" style=\"color: #fff;font-weight: bold;\">" + senderFullName + "</div>\n"
                          + "          <a style=\"color:#fff;text-decoration: none;\" href=\"" + senderEmailAddress + "\">" + senderEmailAddress + "</a>\n"
                          + "        </div>\n"
                          + "      </div>\n"
                          + "      <a href=\"" + url + "\" class=\"btn-go\" style=\"font-size: 16px;display: block;margin: 0 auto;width: 120px;margin-top: 20px;padding:  10px 0;border: 1px solid #fff;border-radius: 10px;color: #fff;text-decoration: none;\">查看文档</a>\n"
                          + "    </div>\n"
                          + "  </div>\n"
                          + "  <div class=\"footer\" style=\"padding: 20px 20px 50px 20px;text-align: center;color: #999;\">闪签·最快的合法签约方式</div>\n"
                          + "  <div class=\"legal\" style=\"color:#999;font-size: 12px;text-align: center;padding-bottom: 50px;\">\n"
                          + "    <h3 style=\"font-size: 14px;font-weight: 14px;\">关于电子合同与电子签名的法律效力说明</h3>\n"
                          + "    <div class=\"content\" style=\"padding: 10px;max-width: 800px;border: 1px solid #e5e5e5;margin: auto;text-align: left;border-radius: 4px;\">\n"
                          + "     《中华人民共和国合同法》第十一条明确规定“书面形式是指合同书、信件和数据电文（包括电报、电传、传真、电子数据交换和电子邮件）等可以有形地表现所载内容的形式”。电子合同属于有效的书面形式。<br>\n"
                          + "     《中华人民共和国电子签名法》第十四条明确规定“可靠的电子签名与传统手写签名或盖章具有同等的法律效力”。电子签名作为一种民事行为，同样受《合同法》等相关法律法规保护。\n"
                          + "    </div>\n"
                          + "  </div>\n"
                          + "</div>";

//    String emailContent = "<div class=\"wrapper\" style=\"background: #eee;font-size: 14px;\">\n"
//                          + "  <div class=\"container\" style=\"background: #fff;padding: 10px;\">\n"
//                          + "    <div class=\"header\" style=\"padding: 10px 5px 15px 5px;\">\n"
//                          + "      <img src=\"http://img.sqian.com/email/logo.png\" width=\"65\" height=\"26\" alt=\"\" class=\"logo\">\n"
//                          + "    </div>\n"
//                          + "    <div class=\"main\" style=\"background: #3e98df;color: #fff;padding: 40px 20px 40px 20px;text-align: center;\">\n"
//                          + "      <img src=\"http://img.sqian.com/email/icon-sign.png\" width=\"108\">\n"
//                          + "      <p class=\"desc\" style=\"font-size: 16px;\">" + senderFullName + " 给您发送了一份文档，请查阅并签署。</p>\n"
//                          + "      <div class=\"ps\" style=\"max-width: 500px;margin: 0 auto;padding: 15px 15px;text-align: left;background: #4ea8ef;line-height: 2em;\">\n"
//                          + "        " + publicMessage + "\n"
//                          + "        <div class=\"sender\" style=\"padding: 20px 10px 10px 10px;color: #def;text-align: right;line-height: 1.2em;\">\n"
//                          + "          <div class=\"name\" style=\"color: #fff;font-weight: bold;\">" + senderFullName + "</div>\n"
//                          + "          " + senderEmailAddress + "\n"
//                          + "        </div>\n"
//                          + "      </div>\n"
//                          + "      <a href=\"" + url + "\" class=\"btn-go\" style=\"font-size: 16px;display: block;margin: 0 auto;width: 120px;margin-top: 20px;padding:  10px 0;border: 1px solid #fff;border-radius: 10px;color: #fff;text-decoration: none;\">查看文档</a>\n"
//                          + "    </div>\n"
//                          + "  </div>\n"
//                          + "<div class=\"footer\" style=\"padding: 20px;text-align: center;color: #999;\">闪签·最快的合法签约方式</div>\n"
//                          + "</div>";
//
    return emailContent;

  }


  private static String getEmailDocPlusTemplateCCRequestDiv(String participantId, String documentId,
                                                            String verification, String senderFullName,
                                                            String senderEmailAddress, String documentName,
                                                            String publicMessage) {


    String url = HTTP_OR_HTTPS + "://" + DOMAIN_NAME + "/document?participantId="
                 + participantId + "&verification=" + verification + "/#/documents/" + documentId;

    String emailContent = "<div class=\"wrapper\" style=\"background: #eee;font-size: 14px;padding:10px\">\n"
                          + "  <div class=\"container\" style=\"background: #fff;max-width: 800px;margin: 0 auto;padding: 10px;box-shadow: 1px 3px 8px 0px #ddd;\">\n"
                          + "    <div class=\"header\" style=\"padding: 10px 5px 15px 5px;\">\n"
                          + "            <img src=\"http://img.shanqian.cc/email/logo-min.png\" width=\"65\" height=\"26\" alt=\"\" class=\"logo\">\n"
                          + "    </div>\n"
                          + "    <div class=\"main\" style=\"background: #3e98df;color: #fff;padding: 40px 20px 40px 20px;text-align: center;\">\n"
                          + "      <img src=\"http://img.shanqian.cc/email/icon-sign-min.png\" width=\"108\" src=\"\">\n"
                          + "      <p class=\"desc\" style=\"font-size: 16px;\">" + senderFullName + "给您发送了一份文档，请查阅。</p>\n"
                          + "      <div class=\"ps\" style=\"max-width: 500px;margin: 0 auto;padding: 15px 15px;text-align: left;background: #4ea8ef;line-height: 2em;\">\n"
                          + "        " + publicMessage + "\n"
                          + "        <div class=\"sender\" style=\"padding: 20px 10px 10px 10px;color: #def;text-align: right;line-height: 1.2em;\">\n"
                          + "          <div class=\"name\" style=\"color: #fff;font-weight: bold;\">" + senderFullName + "</div>\n"
                          + "          <a style=\"color:#fff;text-decoration: none;\" href=\"" + senderEmailAddress + "\">" + senderEmailAddress + "</a>\n"
                          + "        </div>\n"
                          + "      </div>\n"
                          + "      <a href=\"" + url + "\" class=\"btn-go\" style=\"font-size: 16px;display: block;margin: 0 auto;width: 120px;margin-top: 20px;padding:  10px 0;border: 1px solid #fff;border-radius: 10px;color: #fff;text-decoration: none;\">查看文档</a>\n"
                          + "    </div>\n"
                          + "  </div>\n"
                          + "  <div class=\"footer\" style=\"padding: 20px 20px 50px 20px;text-align: center;color: #999;\">闪签·最快的合法签约方式</div>\n"
                          + "  <div class=\"legal\" style=\"color:#999;font-size: 12px;text-align: center;padding-bottom: 50px;\">\n"
                          + "    <h3 style=\"font-size: 14px;font-weight: 14px;\">关于电子合同与电子签名的法律效力说明</h3>\n"
                          + "    <div class=\"content\" style=\"padding: 10px;max-width: 800px;border: 1px solid #e5e5e5;margin: auto;text-align: left;border-radius: 4px;\">\n"
                          + "     《中华人民共和国合同法》第十一条明确规定“书面形式是指合同书、信件和数据电文（包括电报、电传、传真、电子数据交换和电子邮件）等可以有形地表现所载内容的形式”。电子合同属于有效的书面形式。<br>\n"
                          + "     《中华人民共和国电子签名法》第十四条明确规定“可靠的电子签名与传统手写签名或盖章具有同等的法律效力”。电子签名作为一种民事行为，同样受《合同法》等相关法律法规保护。\n"
                          + "    </div>\n"
                          + "  </div>\n"
                          + "</div>";
    return emailContent;

  }



//  public static String getEmailInfoTemplateInvitationEmailContent( String fromFullName, String dstEmailAddress, String verificationCode) {
//    return getEmailInfoTemplatePreDiv()
//           + getEmailInfoTemplateInvitationDiv(fromFullName, dstEmailAddress, verificationCode)
//           + getEmailInfoTemplatePostDiv();
//  }

  //
//  private static String getEmailInfoTemplateInvitationDiv(
//      String fromFullName, String dstEmailAddress, String verificationCode) {
//
//    String url =  HTTP_OR_HTTPS + "://" + DOMAIN_NAME + "/auth/signup/invitation?email=" + dstEmailAddress + "&verification=" + verificationCode;
//
//    String emailContent = "<div class=\"wrapper\" style=\"background: #eee;font-size: 14px;color: #555;\">\n"
//                          + "  <div class=\"container\" style=\"padding: 10px;max-width: 800px;margin: 0 auto;\">\n"
//                          + "    <div class=\"main\" style=\"position: relative;background: #fff;padding: 23px 30px 18px 25px;box-shadow: 1px 3px 8px 0px #ddd;\">\n"
//                          + "      <div class=\"inner\" style=\"max-width: 650px;\">\n"
//                          + "        您好：<br><br>\n"
//                          + "            " + fromFullName + " 邀请您使用闪签" +  "。请点击以下链接验证邮箱，激活帐号。<br>\n"
//                          + "        <a href=\"" + url + "\" style=\"color: #3e98df\">" + url + "</a><br><br>\n"
//                          + "\n"
//                          + "        如果以上网址不可点击，请将它复制到浏览地址栏直接访问。\n"
//                          + "      </div>\n"
//                          + "      <div class=\"from\" style=\"color:#aaa;text-align: right;font-size: 12px;padding: 10px 3px 0 0;\">发送自：<a style=\"color: #3e98df\" class=\"link\" href=\"https://www.sqian.com\">www.sqian.com</a></div>\n"
////                          + "      <img src=\"https://liuxun.b0.upaiyun.com/misc/cry.jpg\" width=\"65\" height=\"26\" alt=\"\" class=\"logo\" style=\"position: absolute; right: 30px; top: 23px;\">\n"
//                          + "      <img src=\"" + "http://img.sqian.com/email/logo.png\" width=\"65\" height=\"26\" alt=\"\" class=\"logo\" style=\"position: absolute; right: 30px; top: 23px;\">\n"
//                          + "    </div>\n"
//                          + "    <div class=\"footer\" style=\"padding: 10px 20px;text-align: center;color: #999;\">\n"
//                          + "      闪签·最快的合法签约方式 　\n"
//                          + "    </div>\n"
//                          + "  </div>\n"
//                          + "</div>\n";
//
//    return emailContent;
//
//  }

}

// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.thirdparty.server.constant.TimeConst;
import hr.wozai.service.thirdparty.server.dao.CaptchaVerificationDao;
import hr.wozai.service.thirdparty.server.model.CaptchaVerification;
import hr.wozai.service.thirdparty.server.service.CaptchaService;
import hr.wozai.service.thirdparty.server.utils.ChenCaptchaUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-01
 */
@Service("chenCaptchaService")
public class CaptchaServiceChenImpl implements CaptchaService {

  public  static final String PARAM_CAPTCHA_IMAGE = "captcha_image";
  public  static final String PARAM_CAPTCHA_TIMESTAMP = "captcha_timestamp";

  private static final int CAPTCHA_TEXT_SIZE = 4;
  private static final int CAPTCHA_IMAGE_WIDTH = 80;
  private static final int CAPTCHA_IMAGE_HEIGHT = 30;
  private static final String CAPTCHA_IMAGE_FORMAT = "png";
  private static final long EXPIRE_PERIOD_IN_MILLIS = TimeConst.FIVE_MUNITES_IN_MILLIS;


  @Autowired
  CaptchaVerificationDao captchaVerificationDao;

  @Override
  public Map<String, Object> getCaptcha() {

    Map<String, Object> params = new HashMap<>();
    try {

      String verificationCode = ChenCaptchaUtils.createText(CAPTCHA_TEXT_SIZE);
      BufferedImage bi = ChenCaptchaUtils.createImage(CAPTCHA_IMAGE_WIDTH, CAPTCHA_IMAGE_HEIGHT, verificationCode);
      String captchaImageBase64 = imageToBase64String(bi, CAPTCHA_IMAGE_FORMAT);

      long now = TimeUtils.getNowTimestmapInMillis();
      CaptchaVerification captchaVerification = new CaptchaVerification();
      captchaVerification.setCreateTime(now);
      captchaVerification.setVerificationCode(verificationCode);
      captchaVerification.setExpireTime(now + EXPIRE_PERIOD_IN_MILLIS);
      captchaVerificationDao.insert(captchaVerification);

      params.put(PARAM_CAPTCHA_TIMESTAMP, captchaVerification.getCreateTime());
      params.put(PARAM_CAPTCHA_IMAGE, captchaImageBase64);

    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return params;
  }

  @Override
  public boolean verifyCaptcha(long createTime, String verificationCode) {

    if (StringUtils.isNullOrEmpty(verificationCode)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    boolean isVerified = false;
    long now = TimeUtils.getNowTimestmapInMillis();
    CaptchaVerification captchaVerification =
        captchaVerificationDao.findByCreateTimeAndVerificationCode(createTime, verificationCode);
    if (null == captchaVerification) {
      throw new ServiceStatusException(ServiceStatus.TP_INVALID_CAPTCHA_VERIFICATION);
    } else {
      if (captchaVerification.getExpireTime() < now) {
        throw new ServiceStatusException(ServiceStatus.TP_EXPIRED_VERIFICATION);
      } else {
        isVerified = true;
      }
    }

    return isVerified;
  }

  /**
   * Convert image to base64
   *
   * @param img
   * @param formatName
   * @return
   */
  private String imageToBase64String(final RenderedImage img, final String formatName) {
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      ImageIO.write(img, formatName, Base64.getEncoder().wrap(os));
      return os.toString(StandardCharsets.ISO_8859_1.name());
    } catch (final IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }

 /* private byte[] imageToByteArray(final RenderedImage img, final String formatName) {
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      ImageIO.write(img, formatName, os);
      return os.toByteArray();
    } catch (final IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }

  *//**
   * Convert base64 to image
   *
   * @param base64ImageString
   * @return
   *//*
  private BufferedImage base64StringToImage(final String base64ImageString) {
    try {
      return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64ImageString)));
    } catch (final IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }*/
}


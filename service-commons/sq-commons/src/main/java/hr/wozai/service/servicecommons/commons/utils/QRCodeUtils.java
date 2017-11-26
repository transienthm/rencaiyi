// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.utils;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.imageio.ImageIO;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-10-21
 */
public class QRCodeUtils {

  private static final String HTTP_OR_HTTPS = "http";
  private static final String DOMAIN = "secretapp.sqian.com";
  private static final String PORT = "8888";
  private static final String URL = "/s";
  private static final String DEFAULT_SIGNATURE_URL = "/s/default";
  private static final int QRCODE_WIDTH = 560;
  private static final int QRCODE_HEIGHT = 560;


  public static String generateQRCodeImageFromShortUrl(String shortUrl) throws IOException {
    String imageBase64 = imageSteamToBase64String(
        QRCode.from(shortUrl).to(ImageType.PNG).withSize(QRCODE_WIDTH, QRCODE_HEIGHT).stream(), "png");
    return imageBase64;
  }

  public static String generateQRCodeSiganatureUrl(String shortUrlCode) {
    return HTTP_OR_HTTPS + "://" + DOMAIN + URL + "?code=" + shortUrlCode;
  }

  public static String generateQRCodeDefaultSignatureUrl(String shortUrlCode) {
    return HTTP_OR_HTTPS + "://" + DOMAIN + DEFAULT_SIGNATURE_URL + "?code=" + shortUrlCode;
  }


  /**
   * Convert image to base64
   *
   * @param img
   * @param formatName
   * @return
   */
  public static String imageToBase64String(final RenderedImage img, final String formatName) {
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      ImageIO.write(img, formatName, Base64.getEncoder().wrap(os));
      return os.toString(StandardCharsets.ISO_8859_1.name());
    } catch (final IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }

  public static String imageSteamToBase64String(ByteArrayOutputStream imgOutputStream, final String formatName)
      throws IOException {

    byte[] bytes = imgOutputStream.toByteArray();
    BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
    return imageToBase64String(image, formatName);
  }

}

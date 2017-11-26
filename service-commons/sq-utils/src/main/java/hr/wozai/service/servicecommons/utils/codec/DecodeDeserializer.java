// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.servicecommons.utils.codec;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import java.io.IOException;

/**
 * 对称解密反序列化，将加密字符串转换为数字
 *
 * @author liangyafei
 * @version 1.0
 * @created 15-9-3 下午2:49
 */
public class DecodeDeserializer {

  @JacksonStdImpl
  public final static class IntegerDeserializer extends StdDeserializer<Integer> {

    private static final long serialVersionUID = 1L;

    public IntegerDeserializer() {
      super(Integer.class);
    }

    protected IntegerDeserializer(Class<?> vc) {
      super(vc);
    }

    @Override
    public Integer deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
      String value = jp.getValueAsString();
      if (value == null) {
        return 0;
      }

      try {
        String original = EncryptUtils.symmetricDecrypt(value, EncryptConstants.DEFAULT_PRIVATE_KEY);
        return Integer.valueOf(original);
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("symmetricDecrypt fail");
      }
    }

    @Override
    public Integer deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
            throws IOException {
      String value = jp.getValueAsString();
      if (value == null) {
        return 0;
      }

      try {
        String original = EncryptUtils.symmetricDecrypt(value.toLowerCase(), EncryptConstants.DEFAULT_PRIVATE_KEY);
        return Integer.valueOf(original);
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("symmetricDecrypt fail");
      }
    }
  }

  @JacksonStdImpl
  public final static class LongDeserializer extends StdDeserializer<Long> {

    private static final long serialVersionUID = 1L;

    public LongDeserializer() {
      super(Long.class);
    }

    protected LongDeserializer(Class<?> vc) {
      super(vc);
    }

    @Override
    public Long deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

      String value = jp.getValueAsString();
      if (value == null) {
        return null;
      }
      if ("".equals(value)) {
        // 当前端需要设置某ID为null, 则会上传空串 "xxId":""
        return null;
      }
      try {
        String original = EncryptUtils.symmetricDecrypt(value, EncryptConstants.DEFAULT_PRIVATE_KEY);
        return Long.valueOf(original);
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("symmetricDecrypt fail");
      }
    }

    @Override
    public Long deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
            throws IOException, JsonProcessingException {

      String value = jp.getValueAsString();
      if (value == null) {
        return 0L;
      }
      if ("".equals(value)) {
        // 当前端需要设置某ID为null, 则会上传空串 "xxId":""
        return 0L;
      }
      try {
        String original = EncryptUtils.symmetricDecrypt(value, EncryptConstants.DEFAULT_PRIVATE_KEY);
        return Long.valueOf(original);
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("symmetricDecrypt fail");
      }
    }
  }


}

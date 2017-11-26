// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.servicecommons.utils.codec;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.IOException;

/**
 * 进行特殊的json序列化处理，对称加密
 *
 * @author liangyafei
 * @version 1.0
 * @created 15-9-3 下午2:49
 */
@JacksonStdImpl
public class EncodeSerializer extends ToStringSerializer {

    public EncodeSerializer() { super(); }


    @Override
    public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
        String result = null;
        try {
            result = EncryptUtils.symmetricEncrypt(value.toString(), EncryptConstants.DEFAULT_PRIVATE_KEY);
            jgen.writeString(result.toString().toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("symmetricEncrypt fail");
        }
    }
}

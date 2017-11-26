// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.servicecommons.thrift.annotation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.apache.thrift.transport.TSocket;

import com.facebook.swift.codec.ThriftCodecManager;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import hr.wozai.service.servicecommons.thrift.annotation.metadata.ThriftMethodMetadata;
import hr.wozai.service.servicecommons.thrift.annotation.metadata.ThriftServiceMetadata;

/**
 * @author liangyafei
 * @version 1.0
 * @created 15-8-30 下午1:21
 */
public class ThriftInvocationHandler  implements InvocationHandler {

    private static final Object[] NO_ARGS = new Object[0];

    private final Map<Method, ThriftMethodHandler> methods;

    public ThriftInvocationHandler(
            String clientDescription,
            Map<Method, ThriftMethodHandler> methods) {
        this.methods = methods;
    }

    @Override public Object invoke (Object proxy, Method method, Object[]args)
            throws Throwable {
        return null;
    }

    private Map.Entry<Object, Throwable> invoking(Object proxy, Method method, Object[]args,TSocket socket)
            throws Throwable {
        return null;
    }

    @Immutable
    public static class ThriftClientMetadata
    {
        private final String clientType;
        private final String clientName;
        private final ThriftServiceMetadata thriftServiceMetadata;
        private final Map<Method, ThriftMethodHandler> methodHandlers;

        public ThriftClientMetadata(
                Class<?> clientType,
                String clientName,
                ThriftCodecManager codecManager)
        {
            Preconditions.checkNotNull(clientType, "clientType is null");
            Preconditions.checkNotNull(clientName, "clientName is null");
            Preconditions.checkNotNull(codecManager, "codecManager is null");

            this.clientName = clientName;
            thriftServiceMetadata = new ThriftServiceMetadata(clientType, codecManager.getCatalog());
            this.clientType = thriftServiceMetadata.getName();
            ImmutableMap.Builder<Method, ThriftMethodHandler> methods = ImmutableMap.builder();
            for (ThriftMethodMetadata methodMetadata : thriftServiceMetadata.getMethods().values()) {
                ThriftMethodHandler methodHandler = new ThriftMethodHandler(methodMetadata, codecManager);
                methods.put(methodMetadata.getMethod(), methodHandler);
            }
            methodHandlers = methods.build();
        }

        public String getClientType()
        {
            return clientType;
        }

        public String getClientName()
        {
            return clientName;
        }

        public String getName()
        {
            return thriftServiceMetadata.getName();
        }

        public Map<Method, ThriftMethodHandler> getMethodHandlers()
        {
            return methodHandlers;
        }
    }

    @Immutable
    public static class TypeAndName {
        private final Class<?> type;
        private final String name;

        public TypeAndName(Class<?> type, String name) {
            Preconditions.checkNotNull(type, "type is null");
            Preconditions.checkNotNull(name, "name is null");
            this.type = type;
            this.name = name;
        }

        public Class<?> getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TypeAndName that = (TypeAndName) o;

            if (!name.equals(that.name)) {
                return false;
            }
            if (!type.equals(that.type)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("TypeAndName");
            sb.append("{type=").append(type);
            sb.append(", name='").append(name).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}

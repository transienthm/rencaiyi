// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.servicecommons.thrift.annotation.metadata;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.facebook.swift.codec.metadata.ReflectionHelper;
import com.facebook.swift.codec.metadata.ThriftCatalog;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

/**
 * @author liangyafei
 * @version 1.0
 * @created 15-8-30 下午1:18
 */
@Immutable
public class ThriftServiceMetadata {
    private final String name;
    private final Map<String, ThriftMethodMetadata> methods;
    private final Map<String, ThriftMethodMetadata> declaredMethods;
    private final ImmutableList<ThriftServiceMetadata> parentServices;
    private final ImmutableList<String> documentation;

    public ThriftServiceMetadata(Class<?> serviceClass, ThriftCatalog catalog) {
        Preconditions.checkNotNull(serviceClass, "serviceClass is null");
        ThriftService thriftService = getThriftServiceAnnotation(serviceClass);
        if(thriftService.value().length() == 0) {
            this.name = serviceClass.getSimpleName();
        } else {
            this.name = thriftService.value();
        }

        this.documentation = ThriftCatalog.getThriftDocumentation(serviceClass);
        Builder builder = ImmutableMap.builder();
        Function methodMetadataNamer = new Function() {
            @Nullable
            @Override
            public Object apply(
                    @Nullable
                    Object input) {
                ThriftMethodMetadata methodMetadata = (ThriftMethodMetadata)input;
                return methodMetadata.getName();
            }
        };
        TreeMultimap declaredMethods = TreeMultimap.create(Ordering.natural().nullsLast(), Ordering.natural().onResultOf(methodMetadataNamer));
        Iterator parentService = ReflectionHelper.findAnnotatedMethods(serviceClass, ThriftMethod.class).iterator();

        while(parentService.hasNext()) {
            Method parentServiceBuilder = (Method)parentService.next();
            if(parentServiceBuilder.isAnnotationPresent(ThriftMethod.class)) {
                ThriftMethodMetadata methodMetadata = new ThriftMethodMetadata(this.name, parentServiceBuilder, catalog);
                builder.put(methodMetadata.getName(), methodMetadata);
                if(parentServiceBuilder.getDeclaringClass().equals(serviceClass)) {
                    declaredMethods.put(ThriftCatalog.getMethodOrder(parentServiceBuilder), methodMetadata);
                }
            }
        }

        this.methods = builder.build();
        this.declaredMethods = Maps.uniqueIndex(declaredMethods.values(), methodMetadataNamer);
        parentService = null;
        ImmutableList.Builder var13 = ImmutableList.builder();
        Class[] var14 = serviceClass.getInterfaces();
        int var10 = var14.length;

        for(int var11 = 0; var11 < var10; ++var11) {
            Class parent = var14[var11];
            if(!ReflectionHelper.getEffectiveClassAnnotations(parent, ThriftService.class).isEmpty()) {
                var13.add(new ThriftServiceMetadata(parent, catalog));
            }
        }

        this.parentServices = var13.build();
    }

    public ThriftServiceMetadata(String name, ThriftMethodMetadata... methods) {
        this.name = name;
        Builder builder = ImmutableMap.builder();
        ThriftMethodMetadata[] var4 = methods;
        int var5 = methods.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            ThriftMethodMetadata method = var4[var6];
            builder.put(method.getName(), method);
        }

        this.methods = builder.build();
        this.declaredMethods = this.methods;
        this.parentServices = ImmutableList.of();
        this.documentation = ImmutableList.of();
    }

    public String getName() {
        return this.name;
    }

    public ThriftMethodMetadata getMethod(String name) {
        return (ThriftMethodMetadata)this.methods.get(name);
    }

    public Map<String, ThriftMethodMetadata> getMethods() {
        return this.methods;
    }

    public Map<String, ThriftMethodMetadata> getDeclaredMethods() {
        return this.declaredMethods;
    }

    public ImmutableList<String> getDocumentation() {
        return this.documentation;
    }

    public static ThriftService getThriftServiceAnnotation(Class<?> serviceClass) {
        Set serviceAnnotations = ReflectionHelper.getEffectiveClassAnnotations(serviceClass, ThriftService.class);
        Preconditions.checkArgument(!serviceAnnotations.isEmpty(), "Service class %s is not annotated with @ThriftService", new Object[]{serviceClass.getName()});
        Preconditions.checkArgument(serviceAnnotations.size() == 1, "Service class %s has multiple conflicting @ThriftService annotations: %s", new Object[]{serviceClass.getName(), serviceAnnotations});
        return (ThriftService)Iterables.getOnlyElement(serviceAnnotations);
    }

    public ImmutableList<ThriftServiceMetadata> getParentServices() {
        return this.parentServices;
    }

    public ThriftServiceMetadata getParentService() {
        Preconditions.checkState(this.parentServices.size() <= 1);
        return this.parentServices.isEmpty()?null:(ThriftServiceMetadata)this.parentServices.get(0);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.name, this.methods, this.parentServices});
    }

    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(obj != null && this.getClass() == obj.getClass()) {
            ThriftServiceMetadata other = (ThriftServiceMetadata)obj;
            return Objects.equals(this.name, other.name) && Objects.equals(this.methods, other.methods) && Objects.equals(this.parentServices, other.parentServices);
        } else {
            return false;
        }
    }
}

// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.utils.monitor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2016-05-25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Monitor {}

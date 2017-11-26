// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.utils.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogAround {}

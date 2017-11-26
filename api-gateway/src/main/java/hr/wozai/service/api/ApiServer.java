// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.api;

import hr.wozai.service.api.interceptor.AccessTokenInterceptor;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-11-18
 */
@SpringBootApplication
@ImportResource("classpath:application-context.xml")
@ComponentScan(value = {
    "hr.wozai.service.api",
    "hr.wozai.service.user.client.userorg.util",
    "hr.wozai.service.thirdparty.client.utils",
    "hr.wozai.service.servicecommons.utils.logging"})
public class ApiServer extends WebMvcConfigurerAdapter {

  public static void main(String[] args) {
    SpringApplication.run(ApiServer.class);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 拦截拥有Access token 或Temporary token的请求
      registry.addInterceptor(new AuthenticationInterceptor())
              .addPathPatterns("/**")
              .excludePathPatterns(
                      // root for jumping
                      "/",

                      // jump to app.js or auth.js depends on whether logged in
                      "/u",
                      "/u/",

                      // jump to admin.js or auth.js
                      "/admin",
                      "/admin/",

                      "/charts",
                      "/charts/",

                      // init password
                      "/u/init-password",

                      // onboarding
                      "/onboarding-flows/staff",

                      "/onboarding-flows/org-account",
                      "/onboarding-flows/org-account/",
                      "/onboarding-flows/org-account/avatar",
                      "/onboarding-flows/org-account/avatar/",

                      "/onboarding-flows/staff/init-pwd-email",

                      "/addresses",
                      "/addresses/",

                      // sign up
                      "/auths/signup",

                      // login
                      "/auths/login",

                      // verify mobile
                      "/auths/mobile_verify",

                      "/auth/captcha",
                      //"/auth/**",
                      "/auths/find-password/send-url",
                      "/u/#/find-password",

                      //send message to trusted mobile phone
                      "/auths/sms-code/trusted-mobile-phones",

                      "/onboarding-flows/profile-templates-csv-files/download/**",

                      "/users/roster/download/**"

              );

    // 拦截必须有Access token的请求
    registry.addInterceptor(new AccessTokenInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns(
                    // root for jumping
                    "/",

                    // jump to app.js or auth.js depends on whether logged in
                    "/u",
                    "/u/",

                    // jump to admin.js or auth.js
                    "/admin",
                    "/admin/",

                    "/charts",
                    "/charts/",

                    // init password
                    "/u/init-password",

                    // onboarding
                    "/onboarding-flows/staff",
                    "/onboarding-flows/staff/onboarding-template",
                    "/onboarding-flows/staff/onboarding-document",
                    "/onboarding-flows/staff/user-profile",
                    "/onboarding-flows/staff/core-profile",
                    "/onboarding-flows/staff/user-profile/fields",
                    "/onboarding-flows/staff/submission",
                    "/documents/temp/**",

                    "/onboarding-flows/staff/init-pwd-email",

                    "/addresses",
                    "/addresses/",

                    // create org
                    "/onboarding-flows/org-account",
                    "/onboarding-flows/org-account/",
                    "/onboarding-flows/org-account/avatar",
                    "/onboarding-flows/org-account/avatar/",


                    // sign up
                    "/auths/signup",

                    // login
                    "/auths/login",

                    // verify mobile
                    "/auths/mobile_verify",

                    "/auths/captcha",
                    //"/auths/**",

                    // reset password
                    "/auths/init-password",
                    "/auths/reset-password",
                    "/auths/find-password/send-url",
                    "/auths/find-password/reset",
                    "/u/#/find-password",

                    //send message to trusted mobile phone
                    "/auths/sms-code/trusted-mobile-phones",

                    "/onboarding-flows/profile-templates-csv-files/download/**",

                    "/users/roster/download/**",

                    "/org-pick-options"

            );
  }

}
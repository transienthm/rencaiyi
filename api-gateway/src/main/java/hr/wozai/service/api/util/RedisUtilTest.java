package hr.wozai.service.api.util;

import hr.wozai.service.api.interceptor.AuthenticationInterceptor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/31
 */
public class RedisUtilTest extends Thread {

  public void run() {
    for (int i = 0; i < 100; i++) {
      String accessToken = "eyJhbGciOiJSUzI1NiJ9.eyJhZG1pblVzZXJJZCI6IjJiMWU2ZDAyNWFjMjg4NjgiLCJzdWIiOiI3ZGJlYTNkNzdmMDQ5ZDkyIiwiaXNzIjoiY29tLnNoYW5xaWFuIiwidHlwZSI6ImFjY2Vzcy10b2tlbiIsImV4cCI6MTQ1OTg2Mzk0MCwidXNlcklkIjoiN2RiZWEzZDc3ZjA0OWQ5MiIsImlhdCI6MTQ1OTI1OTE0MCwib3JnSWQiOiJiYjcwM2Q3NWE2NjIyZGVhIn0.QronHktffEOnGa7qNMgsfzxU8UcncfneSfKINczI1wPZUy39m-GuJIGCjILlp1whdiQRyGdicxYM7QYzYBlBFddms6ur_jdJRJByiZHP3ZHQTjFynG30M81olU_ZL0wYwrolBy6kp3RnZKFFFaVqbtCdnameHJZFMv2bmVc2KTAuiTIfw8d2tmDY5c3vu5fawj9HMdNGubaZmRqnOIEoyhrTrK94sXxVVrlwoTSkSmVir8ePURsCLyoJ2_WbOZCJ5hu6yditlDbsndw5ghncy1p3FO5pzLPzzFiOpcCazpitky1fYrOm49vA9NzzKyCcjmzomkTyWCiuxS9EaabIhw";
      String refreshToken = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJjb20uc2hhbnFpYW4iLCJzdWIiOiI3ZGJlYTNkNzdmMDQ5ZDkyIiwidHlwZSI6InJlZnJlc2gtdG9rZW4iLCJleHAiOjkyMjMzNzIwMzY4NTQ3NzUsImlhdCI6MTQ1ODIxMzU2OX0.DZAfmz0aKqXAq13l5h5Omks-cc8XwQ-Q6OmG353NTf1rdyhS4djMQJUoYW3lFkYUn3tSKWIuu0_-TOKg3EmRLMWrLc9YEC0v-OcVsF_3wn9PUtuXY-gxEUzBkiqtpEFHo38nInL01OxjSvaPs9CgqNIB03qbF6SiDkoH9yiWgAm-Kbm5jcAvEUduIZrVLLFjDIAL1umAsLWdDcCd9v7mkI_xqx62XW_Wp-eON-ETODxRFbwoIGT5Rw-b9Zng9--75cE7n1DqXdENRTbk0OLYY6y6PjxP6lx_RA_Km7RUHMiGVIMz0zITlnAfxHYrCtYtFevbpWULPKvvX0dRQhIXIA";
      System.out.println("interceptor" + AuthenticationInterceptor.isValidTokenPair(accessToken, refreshToken));
      // System.out.println(RedisUtil.sismember("accesstoken:@#99@#0@#54", "aaa"));
    }
  }

  public static void main(String[] args) {
    for (int i = 0; i < 1000; i++) {
      RedisUtilTest r = new RedisUtilTest();
      Thread thread = new Thread(r);
      thread.start();
    }
  }



}

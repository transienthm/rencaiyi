package hr.wozai.service.nlp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ImportResource("classpath:application/application-context.xml")
@ComponentScan(
        value = {
                "hr.wozai.service.nlp.server",
                "hr.wozai.service.servicecommons.utils.logging"
        }
)
@EnableTransactionManagement
public class NLPServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(NLPServerApplication.class);
  }
}
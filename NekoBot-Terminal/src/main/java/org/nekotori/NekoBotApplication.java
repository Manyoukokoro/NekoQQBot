package org.nekotori;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author: JayDeng
 * @date: 2021/6/10 14:20
 * @description:
 * @version: {@link }
 */

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class NekoBotApplication {
  public static void main(String[] args) {
      SpringApplication.run(NekoBotApplication.class,args);
  }
}
    
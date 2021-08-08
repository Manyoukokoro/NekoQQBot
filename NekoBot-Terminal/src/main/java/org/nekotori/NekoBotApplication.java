package org.nekotori;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author: JayDeng
 * @date: 2021/6/10 14:20
 * @description:
 * @version: {@link }
 */

@SpringBootApplication
//允许异步和定时器
@EnableAsync
@EnableScheduling
//强制使用CGLIB代理，否则会导致类型转换异常（简单说就是jdk代理会导致代理类跳过抽象类实现更高层的接口）
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class NekoBotApplication {
  public static void main(String[] args) {
      SpringApplication.run(NekoBotApplication.class,args);
  }
}
    
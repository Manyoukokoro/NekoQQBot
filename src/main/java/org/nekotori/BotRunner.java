package org.nekotori;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: JayDeng
 * @date: 02/08/2021 15:39
 * @description:
 * @version: {@link }
 */
@Component
public class BotRunner implements ApplicationRunner {

    @Resource
    private BotSimulator botSimulator;


    @Value("${bot.account}")
    private Long id;

    @Value("${bot.password}")
    private String password;

    @Value("${bot.device-file}")
    private String deviceInfoLocation;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        botSimulator.run(id,password,deviceInfoLocation);

    }
}
    
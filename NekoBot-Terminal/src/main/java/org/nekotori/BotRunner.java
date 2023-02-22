package org.nekotori;

import lombok.extern.slf4j.Slf4j;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.handler.GlobalAtMeHandler;
import org.nekotori.handler.GlobalCommandHandler;
import org.nekotori.utils.ImageUtil;
import org.nekotori.utils.LoginUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * @author: JayDeng
 * @date: 02/08/2021 15:39
 * @description:
 * @version: {@link }
 */
@Component
@Slf4j
public class BotRunner implements ApplicationRunner {


    @Value("${bot.account}")
    private Long id;

    @Value("${bot.password}")
    private String password;

    @Value("${bot.device-file}")
    private String deviceInfoLocation;

    @Value("${bot.proxy.host}")
    private String host;

    @Value("${bot.proxy.port}")
    private Integer port;

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(password)) {
            id = LoginUtils.getUserId();
            password = LoginUtils.getPassword();
        }
        GlobalCommandHandler.init();
        GlobalAtMeHandler.init();
        ChainMessageSelector.init();
        BotSimulator.runDc(token,host,port);
        BotSimulator.run(id, password, deviceInfoLocation);
    }

    public static void main(String[] args) {
        BotSimulator.runDc(token,"127.0.0.1",7890);
    }
}
    
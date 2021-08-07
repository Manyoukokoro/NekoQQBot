package org.nekotori;

import lombok.extern.slf4j.Slf4j;
import org.nekotori.annotations.Event;
import org.nekotori.commands.GlobalCommandHandler;
import org.nekotori.dao.ChatHistoryMapper;
import org.nekotori.entity.ChatHistoryDo;
import org.nekotori.service.GroupService;
import org.nekotori.utils.LoginUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Scanner;

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
    @Resource
    private GlobalCommandHandler globalCommandHandler;
    @Resource
    private GroupService groupService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(ObjectUtils.isEmpty(id)||ObjectUtils.isEmpty(password)) {
            id = LoginUtils.getUserId();
            password = LoginUtils.getPassword();
        }
        GlobalCommandHandler.init();
        BotSimulator.run(id,password,deviceInfoLocation,globalCommandHandler,groupService);
    }
}
    
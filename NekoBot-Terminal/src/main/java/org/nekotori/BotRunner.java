package org.nekotori;

import lombok.extern.slf4j.Slf4j;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.handler.GlobalAtMeHandler;
import org.nekotori.handler.GlobalCommandHandler;
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

//    @Resource
//    private ChainMessageSelector chainMessageSelector;

    @Override
    public void run(ApplicationArguments args) {
        if(ObjectUtils.isEmpty(id)||ObjectUtils.isEmpty(password)) {
            id = LoginUtils.getUserId();
            password = LoginUtils.getPassword();
        }
        GlobalCommandHandler.init();
        GlobalAtMeHandler.init();
        ChainMessageSelector.init();
        BotSimulator.run(id,password,deviceInfoLocation);
//        final ContactList<Group> groups = BotSimulator.getBot().getGroups();
//        groups.forEach(group->{
//            chainMessageSelector.registerChannel(group.getId(),new SimpleHandler());
//            group.getMembers().stream().map(NormalMember::getId).forEach(id->{
//                chainMessageSelector.joinChannel(group.getId(),SimpleHandler.class.getAnnotation(TaskHash.class).value(),id);
//            });
//        });
    }
}
    
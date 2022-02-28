package org.nekotori.job;


import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.GroupCommandChannel;
import org.nekotori.dao.ChatMemberMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class ScheduledJob {

    @Resource
    private ChainMessageSelector chainMessageSelector;

    @Resource
    private ChatMemberMapper chatMemberMapper;

    @Scheduled(cron = "0/10 * * * * ? ")
    public void removeExpireChannel(){
        final Map<String, GroupCommandChannel> channels = chainMessageSelector.getChannels();
        if (channels == null){
            return;
        }
        channels.forEach((key, value) -> {
            if (value.getExpireTime() < System.currentTimeMillis()) {
                chainMessageSelector.removeChannel(key);
            }
        });
    }
    @Scheduled(cron = "0 0 2 * * ?")
    public void removeEveryDayWelcome(){
        chatMemberMapper.updateAllEveryDayWelcome();
    }
}

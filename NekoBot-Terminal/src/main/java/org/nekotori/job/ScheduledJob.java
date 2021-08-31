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

    @Scheduled(cron = "0/10 * * * * ? ")
    public void removeExpireChannel(){
        final Map<String, GroupCommandChannel> channels = chainMessageSelector.getChannels();
        channels.forEach((key, value) -> {
            if (value.getExpireTime() < System.currentTimeMillis()) {
                chainMessageSelector.removeChannel(key);
            }
        });
    }
}

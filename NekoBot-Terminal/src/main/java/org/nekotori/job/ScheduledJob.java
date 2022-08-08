package org.nekotori.job;


import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.GroupCommandChannel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class ScheduledJob {

    @Resource
    private ChainMessageSelector chainMessageSelector;

    @Scheduled(cron = "0/10 * * * * ? ")
    public void removeExpireChannel(){
        final Map<String, GroupCommandChannel> channels = chainMessageSelector.getChannels();
        if (channels == null){
            return;
        }
        channels.forEach((key, value) -> {
            if(ObjectUtils.isEmpty(value)){
                return;
            }
            if (value.getExpireTime() < System.currentTimeMillis()) {
                chainMessageSelector.removeChannel(key);
            }
        });
    }
}

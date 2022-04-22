package org.nekotori.job;


import org.nekotori.chain.ChainMessageSelector;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ScheduledJob {

    @Resource
    private ChainMessageSelector chainMessageSelector;

//    @Scheduled(cron = "0/10 * * * * ? ")
//    public void removeExpireChannel(){
//        final Map<String, GroupCommandChannel> channels = chainMessageSelector.getChannels();
//        if (channels == null){
//            return;
//        }
//        channels.forEach((key, value) -> {
//            if(ObjectUtils.isEmpty(value)){
//                return;
//            }
//            if (value.getExpireTime() < System.currentTimeMillis()) {
//                chainMessageSelector.removeChannel(key);
//            }
//        });
//    }
}

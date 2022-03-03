package org.nekotori.job;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.GroupCommandChannel;
import org.nekotori.dao.ChatMemberMapper;
import org.nekotori.entity.ChatMemberDo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
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
        List<ChatMemberDo> chatMemberDos = chatMemberMapper.selectList(new QueryWrapper<>());
        chatMemberDos.forEach(chatMemberDo -> chatMemberDo.setTodayWelcome(false));
        chatMemberDos.forEach(chatMemberDo -> chatMemberMapper.updateById(chatMemberDo));
    }
}

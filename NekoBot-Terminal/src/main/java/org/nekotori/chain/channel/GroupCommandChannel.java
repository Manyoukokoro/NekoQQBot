package org.nekotori.chain.channel;

import lombok.Builder;
import lombok.Data;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.chain.channel.handler.ChannelHandler;

import java.util.Deque;
import java.util.List;

/**
 * @author: JayDeng
 * @date: 31/08/2021 14:50
 * @description:
 * @version: {@link }
 */
@Data
@Builder
public class GroupCommandChannel {

    private Long groupId;

    private List<Long> members;

    private Deque<GroupMessageEvent> messageHisQueue;

    private List<String> stages;

    private String nowStage;

    private Long expireTime;

    private ChannelHandler channelHandler;

    public void handleIncomeMessage(GroupMessageEvent groupMessageEvent){
        this.channelHandler.handleMessage(this, groupMessageEvent);
        messageHisQueue.add(groupMessageEvent);
    }

    public boolean checkMember(Long memberId){
        return members.stream().anyMatch(l->l.equals(memberId));
    }

    public void addMember(Long memberId){
        members.add(memberId);
    }
}
    
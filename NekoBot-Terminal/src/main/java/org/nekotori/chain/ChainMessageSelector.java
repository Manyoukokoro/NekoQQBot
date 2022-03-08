package org.nekotori.chain;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.annotations.HandlerId;
import org.nekotori.chain.channel.handler.ChannelHandler;
import org.nekotori.chain.channel.GroupCommandChannel;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: JayDeng
 * @date: 31/08/2021 14:49
 * @description:
 * @version: {@link }
 */

@Component
public class ChainMessageSelector {

    private static final Map<String, GroupCommandChannel> channels = new HashMap<>();

    public static void init(){}

    //消息分发器，从event中读取消息，分发给不同信道进行处理
    public void selectMessage(GroupMessageEvent groupMessageEvent){
        final long group = groupMessageEvent.getGroup().getId();
        final List<String> channelKeys = channels.keySet().stream().filter(s -> s.startsWith(String.valueOf(group))).collect(Collectors.toList());
        channelKeys.forEach(key->{
            final GroupCommandChannel groupCommandChannel = channels.get(key);
            if(!ObjectUtils.isEmpty(groupCommandChannel)&&groupCommandChannel.checkMember(groupMessageEvent.getSender().getId())){

                groupCommandChannel.handleIncomeMessage(groupMessageEvent);
            }
        });
    }


    //注册信道
    public void registerChannel(Long groupId,ChannelHandler channelHandler){
        final String value = channelHandler.getClass().getAnnotation(HandlerId.class).value();
        final String key = groupId + "@" + value;
        if(channels.get(key)!=null){
            final GroupCommandChannel groupCommandChannel = channels.get(key);
            groupCommandChannel.setExpireTime(System.currentTimeMillis()+5*60*1000);
            channels.replace(key,groupCommandChannel);
            throw new RuntimeException("existed channel");
        }
        final GroupCommandChannel build = GroupCommandChannel.builder()
                .messageHisQueue(new ArrayDeque<>())
                .members(new ArrayList<>())
                .stages(channelHandler.getStages())
                .groupId(groupId)
                .channelHandler(channelHandler)
                .expireTime(System.currentTimeMillis()+5*60*1000)
                .build();
        channels.put(key,build);
    }


    //移除信道
    public void unregisterChannel(Long groupId,String taskHash){
        channels.remove(groupId+"@"+taskHash);
    }

    //添加信道成员
    public void joinChannel(Long groupId,String taskHash,Long senderId){
        final GroupCommandChannel groupCommandChannel = channels.get(groupId + "@" + taskHash);
        if(ObjectUtils.isEmpty(groupCommandChannel)){
            throw new RuntimeException("no such channel exist");
        }
        if(!ObjectUtils.isEmpty(groupCommandChannel)&&!groupCommandChannel.checkMember(senderId)){
            groupCommandChannel.addMember(senderId);
        }
    }

    public void joinChannel(Long groupId,ChannelHandler channelHandler,Long senderId){
        joinChannel(groupId, channelHandler.getClass().getAnnotation(HandlerId.class).value(),senderId);
    }

    public void joinChannel(Long groupId,Class<? extends ChannelHandler> clazz,Long senderId){
        joinChannel(groupId,clazz.getAnnotation(HandlerId.class).value(),senderId);
    }

    public void removeChannel(String key){
        channels.remove(key);
    }

    public Map<String, GroupCommandChannel> getChannels(){
        return channels;
    }
}
    
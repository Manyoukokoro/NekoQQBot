package org.nekotori.chain.channel.handler.impl;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.nekotori.annotations.HandlerId;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.GroupCommandChannel;
import org.nekotori.chain.channel.handler.ChannelHandler;
import org.nekotori.utils.FiveChessUtil;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author: JayDeng
 * @date: 2022/3/8 下午3:30
 * @description: FiveChessHandler
 * @version: {@link }
 */

@HandlerId("442379512")
public class FiveChessHandler implements ChannelHandler {

    @Override
    public List<String> getStages() {
        return Arrays.asList("B","W","END");
    }

    @Resource
    private ChainMessageSelector chainMessageSelector;

    private int[][] field;

    private Map<Long,String> pair = new HashMap<>();

    public void init(Long blackId){
        if(this.field == null){
            field = FiveChessUtil.generateField(20);
        }

        pair.put(blackId,"B");
    }

    public void join(Long whiteId){
        if (ObjectUtils.isEmpty(pair.get(whiteId))){
            pair.put(whiteId,"W");
        }
    }

    public boolean isFull(){
        return pair.size() == 2;
    }

    public InputStream drawMap(){
        try{
           return FiveChessUtil.bufferedImageToInputStream(FiveChessUtil.draw(this.field));
        }catch (Exception ignore){
        }
        return null;
    }

    @Override
    public void handleMessage(GroupCommandChannel channel, GroupMessageEvent groupMessageEvent) {
        if(ObjectUtils.isEmpty(channel.getNowStage())){
            channel.setNowStage("B");
        }
        String s = groupMessageEvent.getMessage().contentToString();
        if(StringUtils.isEmpty(s)|| !Pattern.compile("[A-Z] [0-9]{1,2}").matcher(s).matches()){
            return;
        }
        int[] ints = resolveLocation(s);
        if(ints == null){
            return;
        }
        if(channel.getNowStage().equals("B") && pair.get(groupMessageEvent.getSender().getId()).equals("B")){
            this.field[ints[1]][ints[0]] = 1;
            groupMessageEvent.getGroup().sendMessage(new MessageChainBuilder().append("黑方落子!").append(Contact.uploadImage(groupMessageEvent.getSubject(),
                    drawMap())).build());
            channel.setNowStage("W");
        }
        else if(channel.getNowStage().equals("W") && pair.get(groupMessageEvent.getSender().getId()).equals("W")){
            this.field[ints[1]][ints[0]] = -1;
            groupMessageEvent.getGroup().sendMessage(new MessageChainBuilder().append("白方落子!").append(Contact.uploadImage(groupMessageEvent.getSubject(),
                    drawMap())).build());
            channel.setNowStage("B");
        }
        int winner = FiveChessUtil.isWin(ints[1],ints[0],field);
        if(winner== 1){
            groupMessageEvent.getGroup().sendMessage("黑方胜利");
            chainMessageSelector.unregisterChannel(groupMessageEvent.getGroup().getId(),
                    this.getClass().getAnnotation(HandlerId.class).value());
        }
        if(winner== -1){
            groupMessageEvent.getGroup().sendMessage("白方胜利");
            chainMessageSelector.unregisterChannel(groupMessageEvent.getGroup().getId(),
                    this.getClass().getAnnotation(HandlerId.class).value());
        }
    }

    private int[] resolveLocation(String s){
        if(StringUtils.isEmpty(s)|| !Pattern.compile("[A-Z] [0-9]{1,2}").matcher(s).matches()){
            return null;
        }
        String[] s1 = s.split(" ");
        String letter = s1[0];
        String number = s1[1];
        letter = letter.toUpperCase();
        int l = letter.charAt(0)-'A';
        int n = Integer.parseInt(number);
        return new int[]{l,n};
    }

}

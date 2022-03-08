package org.nekotori.chain.channel.handler.impl;

import lombok.Data;
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
import javax.annotation.sql.DataSourceDefinition;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author: JayDeng
 * @date: 2022/3/8 下午3:30
 * @description: FiveChessHandler
 * @version: {@link }
 */

@HandlerId("442379512")
public class FiveChessHandler implements ChannelHandler {

    public static final int size = 16;

    @Override
    public List<String> getStages() {
        return Arrays.asList("B","W");
    }

    @Resource
    private ChainMessageSelector chainMessageSelector;

    private static final Map<Long,Resources> res = new HashMap<>();

    @Data
    private static class Resources{
        private int[][] field;

        private Map<Long,String> pair = new HashMap<>();
    }

    public static void init(Long blackId,Long groupId){
        Resources resources = res.get(groupId);
        Resources re = new Resources();
        if(ObjectUtils.isEmpty(resources)){
            re.setField(FiveChessUtil.generateField(size));
            Map<Long, String> pair = re.getPair();
            pair.put(blackId,"B");
            re.setPair(pair);
        }
        res.put(groupId,re);
    }

    public static void join(Long whiteId,Long groupId){
        Resources resources = res.get(groupId);
        Map<Long, String> pair = resources.getPair();
        if (ObjectUtils.isEmpty(pair.get(whiteId))){
            pair.put(whiteId,"W");
        }
        resources.setPair(pair);
        res.put(groupId,resources);
    }

    private static void clear(Long groupId){
        res.remove(groupId);
    }

    public static boolean isFull(Long groupId){
        return res.get(groupId)!=null&& res.get(groupId).getPair().size() == 2;
    }

    public static InputStream drawMap(Long groupId){
        try{
            Resources resources = res.get(groupId);
            return FiveChessUtil.bufferedImageToInputStream(FiveChessUtil.draw(resources.getField()));
        }catch (Exception ignore){
        }
        return null;
    }

    @Override
    public void handleMessage(GroupCommandChannel channel, GroupMessageEvent groupMessageEvent) {
        if(ObjectUtils.isEmpty(channel.getNowStage())){
            channel.setNowStage("B");
        }
        if("终止游戏".equals(groupMessageEvent.getMessage().serializeToMiraiCode())){
            clear(groupMessageEvent.getSubject().getId());
            chainMessageSelector.unregisterChannel(groupMessageEvent.getGroup().getId(),
                    this.getClass().getAnnotation(HandlerId.class).value());
            groupMessageEvent.getSubject().sendMessage("对局已终止");
            return;
        }
        if("投降".equals(groupMessageEvent.getMessage().serializeToMiraiCode())){
            clear(groupMessageEvent.getSubject().getId());
            chainMessageSelector.unregisterChannel(groupMessageEvent.getGroup().getId(),
                    this.getClass().getAnnotation(HandlerId.class).value());
            groupMessageEvent.getSubject().sendMessage(groupMessageEvent.getSender().getNick()+"已投降,游戏结束");
            return;
        }
        Resources resources = res.get(groupMessageEvent.getSubject().getId());
        int[][] field = resources.getField();
        Map<Long, String> pair = resources.getPair();
        String s = groupMessageEvent.getMessage().contentToString();
        if(StringUtils.isEmpty(s)|| !Pattern.compile("[A-Z] ?[0-9]+").matcher(s).matches()){
            return;
        }
        int[] ints = resolveLocation(s);
        if(ints == null){
            return;
        }
        if(ints[0]>size||ints[0]<1||ints[1]>size||ints[1]<1||field[ints[1]][ints[0]] != 0){
            groupMessageEvent.getGroup().sendMessage(new MessageChainBuilder().append("不能在此处落子！").build());
            return;
        }
        if(channel.getNowStage().equals("B") && pair.get(groupMessageEvent.getSender().getId()).equals("B")){
            field[ints[1]][ints[0]] = 1;
            groupMessageEvent.getGroup().sendMessage(new MessageChainBuilder().append("黑方落子!").build());
            groupMessageEvent.getGroup().sendMessage(new MessageChainBuilder().append(Contact.uploadImage(groupMessageEvent.getSubject(),
                    Objects.requireNonNull(drawMap(groupMessageEvent.getSubject().getId())))).build());
            channel.setNowStage("W");
        }
        else if(channel.getNowStage().equals("W") && pair.get(groupMessageEvent.getSender().getId()).equals("W")){
            field[ints[1]][ints[0]] = -1;
            groupMessageEvent.getGroup().sendMessage(new MessageChainBuilder().append("白方落子!").build());
            groupMessageEvent.getGroup().sendMessage(new MessageChainBuilder().append(Contact.uploadImage(groupMessageEvent.getSubject(),
                    Objects.requireNonNull(drawMap(groupMessageEvent.getSubject().getId())))).build());
            channel.setNowStage("B");
        }
        boolean winner = FiveChessUtil.isWin(field);
        Long winnerId = 0L;
        if(winner && "W".equals(channel.getNowStage())){
            groupMessageEvent.getGroup().sendMessage("黑方胜利");
            clear(groupMessageEvent.getSubject().getId());
            chainMessageSelector.unregisterChannel(groupMessageEvent.getGroup().getId(),
                    this.getClass().getAnnotation(HandlerId.class).value());
        }
        if(winner && "B".equals(channel.getNowStage())){
            groupMessageEvent.getGroup().sendMessage("白方胜利");
            clear(groupMessageEvent.getSubject().getId());
            chainMessageSelector.unregisterChannel(groupMessageEvent.getGroup().getId(),
                    this.getClass().getAnnotation(HandlerId.class).value());
        }
        if(isFilled(field)){
            groupMessageEvent.getGroup().sendMessage("平局！");
            clear(groupMessageEvent.getSubject().getId());
            chainMessageSelector.unregisterChannel(groupMessageEvent.getGroup().getId(),
                    this.getClass().getAnnotation(HandlerId.class).value());
        }
    }

    private int[] resolveLocation(String s){
        if(StringUtils.isEmpty(s)|| !Pattern.compile("[A-Z] ?[0-9]+").matcher(s).matches()){
            return null;
        }
        String number = s.trim().substring(1);
        String letter = s.trim().substring(0,1);
        letter = letter.toUpperCase();
        int l = letter.charAt(0)-'A';
        int n = Integer.parseInt(number)-1;
        return new int[]{n,l};
    }

    private boolean isFilled(int[][] map){
        for(int[] raw:map){
            for (int i:raw){
                if(i==0) return false;
            }
        }
        return true;
    }

}

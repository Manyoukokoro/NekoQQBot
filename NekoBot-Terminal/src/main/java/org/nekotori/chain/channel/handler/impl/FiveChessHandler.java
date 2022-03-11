package org.nekotori.chain.channel.handler.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageSource;
import org.nekotori.annotations.HandlerId;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.GroupCommandChannel;
import org.nekotori.chain.channel.handler.ChannelHandler;
import org.nekotori.dao.ChatMemberMapper;
import org.nekotori.entity.ChatMemberDo;
import org.nekotori.utils.FiveChessUtil;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
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

    public static final int size = 15;

    @Override
    public List<String> getStages() {
        return Arrays.asList("B","W");
    }

    @Resource
    private ChainMessageSelector chainMessageSelector;

    @Resource
    private ChatMemberMapper chatMemberMapper;

    private static final Map<Long,Resources> res = new HashMap<>();

    @Data
    private static class Resources{
        private int[][] field;

        private Map<Long,String> pair = new HashMap<>();

        private Set<Integer> forbids = new HashSet<>();

        private Deque<MessageReceipt<Group>> historyImages = new ArrayDeque<>();
    }

    public static void init(Long blackId,Long groupId,Set<Integer> forbids){
        Resources resources = res.get(groupId);
        Resources re = new Resources();
        if(ObjectUtils.isEmpty(resources)){
            re.setField(FiveChessUtil.generateField(size));
            Map<Long, String> pair = re.getPair();
            pair.put(blackId,"B");
            re.setPair(pair);
            re.setForbids(forbids);
            re.setHistoryImages(new ArrayDeque<>());
        }
        res.put(groupId,re);
    }

    public static void putImage(Long groupId, MessageReceipt<Group> messageReceipt){
        res.get(groupId).getHistoryImages().add(messageReceipt);
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

    public static InputStream drawMap(Long groupId,int x,int y){
        try{
            Resources resources = res.get(groupId);
            return FiveChessUtil.bufferedImageToInputStream(FiveChessUtil.draw(resources.getField(),x,y));
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
        Set<Integer> forbids = resources.getForbids();
        Map<Long, String> pair = resources.getPair();
        String s = groupMessageEvent.getMessage().contentToString();
        if(StringUtils.isEmpty(s)|| !Pattern.compile("[A-Z] ?[0-9]+").matcher(s).matches()){
            return;
        }
        int[] ints = resolveLocation(s);
        if(ints == null){
            return;
        }
        if(ints[0]>=size||ints[0]<0||ints[1]>=size||ints[1]<0||field[ints[1]][ints[0]] != 0){
            groupMessageEvent.getGroup().sendMessage(new MessageChainBuilder().append("不能在此处落子！").build());
            return;
        }
        if(channel.getNowStage().equals("B") && pair.get(groupMessageEvent.getSender().getId()).equals("B")){
            field[ints[1]][ints[0]] = 1;
            boolean overThree = false,overFour = false,longLink = false;
            try{
                overThree = FiveChessUtil.isOverThree(ints[1], ints[0], field);
                overFour = FiveChessUtil.isOverFour(ints[1], ints[0], field);
                longLink = FiveChessUtil.longLink(ints[1], ints[0], field);
            }catch (Exception ignore){

            }
            if (forbids.contains(0)&&overThree){
                field[ints[1]][ints[0]] = 0;
                groupMessageEvent.getGroup().sendMessage(new MessageChainBuilder().append("三三禁手，不能在此处落子！").build());
            }
            if (forbids.contains(1)&&overFour) {
                field[ints[1]][ints[0]] = 0;
                groupMessageEvent.getGroup().sendMessage(new MessageChainBuilder().append("四四禁手，不能在此处落子！").build());

            }
            if (forbids.contains(2)&&longLink){
                field[ints[1]][ints[0]] = 0;
                groupMessageEvent.getGroup().sendMessage(new MessageChainBuilder().append("长连禁手，不能在此处落子！").build());

            }
            groupMessageEvent.getGroup().sendMessage(new MessageChainBuilder().append("黑方落子:").append(s).build());
            MessageChain build = new MessageChainBuilder().append(Contact.uploadImage(groupMessageEvent.getSubject(),
                    Objects.requireNonNull(drawMap(groupMessageEvent.getSubject().getId(), ints[1], ints[0])))).build();
            MessageReceipt<Group> groupMessageReceipt = groupMessageEvent.getGroup().sendMessage(build);
            putImage(channel.getGroupId(),groupMessageReceipt);
            channel.setNowStage("W");
        }
        else if(channel.getNowStage().equals("W") && pair.get(groupMessageEvent.getSender().getId()).equals("W")){
            field[ints[1]][ints[0]] = -1;
            groupMessageEvent.getGroup().sendMessage(new MessageChainBuilder().append("白方落子:").append(s).build());
            MessageChain build = new MessageChainBuilder().append(Contact.uploadImage(groupMessageEvent.getSubject(),
                    Objects.requireNonNull(drawMap(groupMessageEvent.getSubject().getId(), ints[1], ints[0])))).build();
            MessageReceipt<Group> groupMessageReceipt = groupMessageEvent.getGroup().sendMessage(build);
            putImage(channel.getGroupId(),groupMessageReceipt);
            channel.setNowStage("B");
        }
        while(resources.getHistoryImages().size()>1){
            MessageReceipt<Group> groupMessageReceipt = resources.getHistoryImages().removeFirst();
            groupMessageReceipt.recall();
        }
        boolean winner = FiveChessUtil.isWin(field);
        Long winnerId = 0L;
        if(winner && "W".equals(channel.getNowStage())){
            groupMessageEvent.getGroup().sendMessage("黑方胜利");
            clear(groupMessageEvent.getSubject().getId());
            chainMessageSelector.unregisterChannel(groupMessageEvent.getGroup().getId(),
                    this.getClass().getAnnotation(HandlerId.class).value());
            Long b = pair.entrySet().stream().filter(ss -> ss.getValue().equals("B")).map(Map.Entry::getKey).findAny().orElse(0L);
            ChatMemberDo chatMemberDo = chatMemberMapper.selectOne(new QueryWrapper<ChatMemberDo>().eq("member_id", b).eq("group_id",
                    groupMessageEvent.getSubject().getId()));
            chatMemberDo.setLevel(chatMemberDo.getLevel()+10);
            chatMemberMapper.updateById(chatMemberDo);
            groupMessageEvent.getGroup().sendMessage(chatMemberDo.getNickName()+"群等级上升10");
        }
        if(winner && "B".equals(channel.getNowStage())){
            groupMessageEvent.getGroup().sendMessage("白方胜利");
            clear(groupMessageEvent.getSubject().getId());
            chainMessageSelector.unregisterChannel(groupMessageEvent.getGroup().getId(),
                    this.getClass().getAnnotation(HandlerId.class).value());
            Long b = pair.entrySet().stream().filter(ss -> ss.getValue().equals("B")).map(Map.Entry::getKey).findAny().orElse(0L);
            ChatMemberDo chatMemberDo = chatMemberMapper.selectOne(new QueryWrapper<ChatMemberDo>().eq("member_id", b).eq("group_id",
                    groupMessageEvent.getSubject().getId()));
            chatMemberDo.setLevel(chatMemberDo.getLevel()+10);
            chatMemberMapper.updateById(chatMemberDo);
            groupMessageEvent.getGroup().sendMessage(chatMemberDo.getNickName()+"群等级上升10");
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

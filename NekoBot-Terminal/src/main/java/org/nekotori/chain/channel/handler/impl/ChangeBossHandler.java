package org.nekotori.chain.channel.handler.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import kotlinx.serialization.json.JsonObject;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.annotations.HandlerId;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.GroupCommandChannel;
import org.nekotori.chain.channel.handler.ChannelHandler;
import org.nekotori.dao.GroupGachaMapper;
import org.nekotori.entity.GroupGachaDo;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.List;

/**
 * @author: JayDeng
 * @date: 2022/2/28 下午3:02
 * @description: CustomComHandler
 * @version: {@link }
 */
@HandlerId("3245236521")
public class ChangeBossHandler implements ChannelHandler {

    public static final List<String> stages = Arrays.asList("NAME", "ROUND");
    @Resource
    private ChainMessageSelector chainMessageSelector;

    @Override
    public List<String> getStages() {
        return stages;
    }

    @Override
    public void handleMessage(GroupCommandChannel channel, GroupMessageEvent groupMessageEvent) {
        int size = channel.getMessageHisQueue().size();
        if (ObjectUtils.isEmpty(channel.getNowStage())) {
            channel.setNowStage(channel.getStages().get(1));
            groupMessageEvent.getGroup().sendMessage("请输入第" +size+ "轮Boss血量，输入“结束”进行保存");
            return;
        }
        try {
            if(groupMessageEvent.getMessage().serializeToMiraiCode().contains("结束")){
                save(channel.getMessageHisQueue());
                groupMessageEvent.getGroup().sendMessage("保存成功");
                chainMessageSelector.unregisterChannel(groupMessageEvent.getSubject().getId(),this.getClass().getAnnotation(HandlerId.class).value());
                return;
            }
            Integer.parseInt(groupMessageEvent.getMessage().serializeToMiraiCode());
            groupMessageEvent.getGroup().sendMessage("请输入第" +size+ "轮Boss血量");
        } catch (NumberFormatException e) {
            channel.getMessageHisQueue().removeLast();
            groupMessageEvent.getGroup().sendMessage("输入有误,请输入第" +(size-1)+ "轮Boss血量");
        }
    }

    private void save(Deque<GroupMessageEvent> messageEvents){
        GroupMessageEvent groupMessageEvent = messageEvents.removeFirst();
        messageEvents.removeLast();
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce("name",groupMessageEvent.getMessage().serializeToMiraiCode());

        List<String> hps = new ArrayList<>();
        while (!messageEvents.isEmpty()){
            hps.add(messageEvents.removeFirst().getMessage().serializeToMiraiCode());
        }
        jsonObject.putOnce("hps",hps);
        jsonObject.putOnce("history",new ArrayList<>());
        jsonObject.putOnce("chancePerUser",3);
        jsonObject.putOnce("currentUser",new ArrayList<>());
        jsonObject.putOnce("stageNames",new ArrayList<>());
        FileUtil.writeString(jsonObject.toStringPretty(),new File("worldBoss"+groupMessageEvent.getSubject().getId()), StandardCharsets.UTF_8);
    }
}

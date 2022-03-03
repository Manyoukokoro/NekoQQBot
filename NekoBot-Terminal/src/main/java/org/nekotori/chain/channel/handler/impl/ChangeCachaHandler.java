package org.nekotori.chain.channel.handler.impl;

import cn.hutool.core.collection.CollectionUtil;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.nekotori.annotations.HandlerId;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.GroupCommandChannel;
import org.nekotori.chain.channel.handler.ChannelHandler;
import org.nekotori.commands.CustomCommand;
import org.nekotori.dao.GroupGachaMapper;
import org.nekotori.entity.GroupGachaDo;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
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
@HandlerId("3245236522")
public class ChangeCachaHandler implements ChannelHandler {

    public static final List<String> stages = Arrays.asList("UR","SSR","SR","R","N");

    @Resource
    private GroupGachaMapper groupGachaMapper;

    @Resource
    private ChainMessageSelector chainMessageSelector;

    @Override
    public List<String> getStages() {
        return stages;
    }

    @Override
    public void handleMessage(GroupCommandChannel channel, GroupMessageEvent groupMessageEvent) {
        if(ObjectUtils.isEmpty(channel.getNowStage())){
            channel.setNowStage(channel.getStages().get(0));
        }
        int index = channel.getStages().indexOf(channel.getNowStage());
        if(index == channel.getStages().size()-1){
            GroupGachaDo groupGachaDo = new GroupGachaDo();
            groupGachaDo.setGroupId(groupMessageEvent.getGroup().getId());
            groupGachaDo.setCreateTime(new Date());
            int r = Integer.parseInt(channel.getMessageHisQueue().removeLast().getMessage().contentToString());
            int sr = Integer.parseInt(channel.getMessageHisQueue().removeLast().getMessage().contentToString());
            int ssr = Integer.parseInt(channel.getMessageHisQueue().removeLast().getMessage().contentToString());
            int ur = Integer.parseInt(channel.getMessageHisQueue().removeLast().getMessage().contentToString());
            int n;
            try {
                n = Integer.parseInt(groupMessageEvent.getMessage().contentToString());
            }catch (NumberFormatException e){
                n = 0;
            }
            groupGachaDo.setNP(n);
            groupGachaDo.setRP(r);
            groupGachaDo.setSrP(sr);
            groupGachaDo.setSsrP(ssr);
            groupGachaDo.setUrP(ur);
            groupGachaMapper.insert(groupGachaDo);
            chainMessageSelector.unregisterChannel(groupMessageEvent.getGroup().getId(), this.getClass().getAnnotation(HandlerId.class).value());
            groupMessageEvent.getGroup().sendMessage("修改成功");
        }
        try {
            Integer.parseInt(groupMessageEvent.getMessage().contentToString());
            channel.setNowStage(channel.getStages().get(index+1));
            groupMessageEvent.getGroup().sendMessage("请输入"+channel.getNowStage()+"概率");
        }catch (NumberFormatException e){
            groupMessageEvent.getGroup().sendMessage("输入有误,请输入"+channel.getNowStage()+"概率");
        }
    }
}

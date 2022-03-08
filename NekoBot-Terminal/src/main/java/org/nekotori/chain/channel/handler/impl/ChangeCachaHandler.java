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
import java.util.*;

/**
 * @author: JayDeng
 * @date: 2022/2/28 下午3:02
 * @description: CustomComHandler
 * @version: {@link }
 */
@HandlerId("3245236522")
public class ChangeCachaHandler implements ChannelHandler {

    public static final List<String> stages = Arrays.asList("NAME","UR","SSR","SR","R","N");

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
            channel.setNowStage(channel.getStages().get(1));
            groupMessageEvent.getGroup().sendMessage("请输入" + channel.getNowStage() + "概率");
            return;
        }
        int index = channel.getStages().indexOf(channel.getNowStage());

        if(index == channel.getStages().size()-1){

            GroupGachaDo groupGachaDo = new GroupGachaDo();
            groupGachaDo.setGroupId(groupMessageEvent.getGroup().getId());
            groupGachaDo.setCreateTime(new Date());
            Deque<GroupMessageEvent> messageHisQueue = channel.getMessageHisQueue();
            try {
                Integer.parseInt(groupMessageEvent.getMessage().serializeToMiraiCode());
            } catch (NumberFormatException e) {
                groupMessageEvent.getGroup().sendMessage("输入有误,请输入" + channel.getNowStage() + "概率");
                return;
            }
            messageHisQueue.add(groupMessageEvent);
            List<String> hisMesReverse = new ArrayList<>();
            while(!messageHisQueue.isEmpty()){
               hisMesReverse.add(messageHisQueue.removeLast().getMessage().serializeToMiraiCode());
            }
            List<String> validMes = new ArrayList<>();
            for(int i=0;i<hisMesReverse.size();i++){
                if(validMes.size()==stages.size()){
                    break;
                }
                else if(validMes.size()==stages.size()-1){
                    validMes.add(hisMesReverse.get(i));
                }
                else {
                    try {
                        int i1 = Integer.parseInt(hisMesReverse.get(i));
                        validMes.add(String.valueOf(i1));
                    }catch (NumberFormatException ignored){
                    }
                }
            }
            int n = Integer.parseInt(validMes.get(0));
            int r = Integer.parseInt(validMes.get(1));
            int sr = Integer.parseInt(validMes.get(2));
            int ssr = Integer.parseInt(validMes.get(3));
            int ur = Integer.parseInt(validMes.get(4));
            String name = validMes.get(5);
            groupGachaDo.setNP(n);
            groupGachaDo.setRP(r);
            groupGachaDo.setSrP(sr);
            groupGachaDo.setSsrP(ssr);
            groupGachaDo.setUrP(ur);
            groupGachaDo.setPollName(name);
            groupGachaMapper.insert(groupGachaDo);
            chainMessageSelector.unregisterChannel(groupMessageEvent.getGroup().getId(), this.getClass().getAnnotation(HandlerId.class).value());
            groupMessageEvent.getGroup().sendMessage("修改成功");
        }
        try {
            Integer.parseInt(groupMessageEvent.getMessage().serializeToMiraiCode());
            channel.setNowStage(channel.getStages().get(index + 1));
            groupMessageEvent.getGroup().sendMessage("请输入" + channel.getNowStage() + "概率");
        } catch (NumberFormatException e) {
            groupMessageEvent.getGroup().sendMessage("输入有误,请输入" + channel.getNowStage() + "概率");
        }
    }
}

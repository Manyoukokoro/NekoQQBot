package org.nekotori.commands.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.IsCommand;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.handler.impl.ChangeCachaHandler;
import org.nekotori.chain.channel.handler.impl.SauceNaoChannelHandler;
import org.nekotori.commands.ManagerGroupCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.dao.GroupGachaMapper;
import org.nekotori.entity.CommandAttr;
import org.nekotori.entity.GroupGachaDo;
import org.nekotori.utils.CommandUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@IsCommand(name = {"修改概率","加载卡池","查询卡池","gachap"},description = "修改抽卡概率，格式:(!/-/#)修改概率；加载卡池：格式:(!/-/#)加载卡池 卡池名")
public class ChangeGachaCommand extends ManagerGroupCommand {

    @Resource
    private ChainMessageSelector chainMessageSelector;

    @Resource
    private ChangeCachaHandler changeCachaHandler;

    @Resource
    private GroupGachaMapper groupGachaMapper;

    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        CommandAttr commandAttr = CommandUtils.resolveCommand(messageChain.serializeToMiraiCode());
        if("查询卡池".equals(commandAttr.getCommand())){
            List<GroupGachaDo> groupGacha = groupGachaMapper.selectList(new QueryWrapper<GroupGachaDo>().eq("group_id",
                    subject.getId()));
            MessageChainBuilder singleMessages = new MessageChainBuilder();
            singleMessages.append("本群卡池信息如下：\n");
            groupGacha.forEach(gacha->
                    singleMessages.append(gacha.getPollName()).append(":")
                    .append(String.valueOf(gacha.getUrP())).append(":")
                    .append(String.valueOf(gacha.getSsrP())).append(":")
                    .append(String.valueOf(gacha.getSrP())).append(":")
                    .append(String.valueOf(gacha.getRP())).append(":")
                    .append(String.valueOf(gacha.getNP())).append("\n"));
            return singleMessages.build();

        }
        if("加载卡池".equals(commandAttr.getCommand())){
            String trim = messageChain.serializeToMiraiCode().replace(commandAttr.getCommand(), "").substring(1).trim();
            List<GroupGachaDo> groupGacha = groupGachaMapper.selectList(new QueryWrapper<GroupGachaDo>().eq("group_id",
                    subject.getId()));
            Optional<GroupGachaDo> any = groupGacha.stream().filter(groupGachaDo -> groupGachaDo.getPollName().equals(trim)).findAny();
            MessageChainBuilder singleMessages = new MessageChainBuilder();
            any.ifPresent(groupGachaDo -> {
                groupGachaDo.setCreateTime(new Date());
                groupGachaMapper.updateById(groupGachaDo);
                singleMessages.append("加载：").append(groupGachaDo.getPollName()).append(" 成功!");
            });
            return singleMessages.build();
        }
        try {
            chainMessageSelector.registerChannel(subject.getId(), changeCachaHandler);
        }catch (RuntimeException e){
            return new MessageChainBuilder().append("已经在修改中").build();
        }
        subject.sendMessage(new PlainText("请输入卡池名称"));
        chainMessageSelector.joinChannel(subject.getId(),ChangeCachaHandler.class,sender.getId());
        return null;
        }
}

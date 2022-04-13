package org.nekotori.commands.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.IsCommand;
import org.nekotori.chain.channel.handler.impl.FiveChessHandler;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.commands.PrivilegeGroupCommand;
import org.nekotori.dao.GroupGachaMapper;
import org.nekotori.entity.CommandAttr;
import org.nekotori.entity.GroupGachaDo;
import org.nekotori.utils.CommandUtils;
import org.nekotori.utils.GachaUtils;
import org.nekotori.utils.ImageUtil;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@IsCommand(name = {"抽卡","gacha","建造"},description = "模拟抽卡，测试人品，格式:(!/-/#)gacha 数量")
public class GachaCommand extends PrivilegeGroupCommand {

    @Resource
    private GroupGachaMapper groupGachaMapper;

    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        CommandAttr commandAttr = CommandUtils.resolveCommand(messageChain.serializeToMiraiCode());
        if(commandAttr.getCommand().equals("建造")) {
            int type = 0;
            if(!CollectionUtil.isEmpty(commandAttr.getParam())){
                String s = commandAttr.getParam().get(0);
                if( Arrays.asList("轻型","轻").contains(s)) {
                    type = 0;
                }else if( Arrays.asList("重型","重").contains(s)) {
                    type = 1;
                }else if( Arrays.asList("特型","特").contains(s)) {
                    type = 2;
                }else if( Arrays.asList("限定","限").contains(s)) {
                    type = 3;
                }
            }
            if(type == 3){
                try{
                    List<ImageUtil.AzureLaneCard> azureLaneCards = GachaUtils.gachaAzureLaneSp();
                    InputStream inputStream = ImageUtil.generateAzureCardImage(azureLaneCards);
                    MessageReceipt<Group> groupMessageReceipt = subject.sendMessage(new MessageChainBuilder().append(Contact.uploadImage(subject,
                            Objects.requireNonNull(inputStream))).build());
                    return null;

                }catch (Exception e){
                    return new MessageChainBuilder().append("发生了未知错误").build();
                }
            }
            try{
                List<ImageUtil.AzureLaneCard> azureLaneCards = GachaUtils.gachaAzureLaneAll(type);
                InputStream inputStream = ImageUtil.generateAzureCardImage(azureLaneCards);
                MessageReceipt<Group> groupMessageReceipt = subject.sendMessage(new MessageChainBuilder().append(Contact.uploadImage(subject,
                        Objects.requireNonNull(inputStream))).build());
            }catch (Exception e){
                return new MessageChainBuilder().append("发生了未知错误").build();
            }
            return null;
        }
        Optional<GroupGachaDo> first = groupGachaMapper.selectList(new QueryWrapper<GroupGachaDo>().eq("group_id", subject.getId()).orderByDesc(
                "create_time")).stream().findFirst();
        if (first.isEmpty()){
            return new MessageChainBuilder().append("请先修改概率").build();
        }
        GroupGachaDo groupGachaDo = first.get();
        List<String> param = commandAttr.getParam();
        List<String> gachas;
        if(CollectionUtil.isEmpty(param)){
            gachas = gacha(1,groupGachaDo);
        }
        else {
            try{
                int i = Integer.parseInt(param.get(0));
                if (i<1){
                    return new MessageChainBuilder().append("抽你马呢").build();
                }
                if(i>40){
                    return new MessageChainBuilder().append("输入范围有误，仅支持1～40次抽卡").build();
                }
                gachas = gacha(i,groupGachaDo);
            }catch (NumberFormatException e){
                gachas = gacha(1,groupGachaDo);
            }
        }
        MessageChainBuilder singleMessages = new MessageChainBuilder();
        gachas.forEach(s->{
            singleMessages.append("  ").append(s);
        });

        return singleMessages.build();
    }

    private List<String> gacha(Integer num,GroupGachaDo groupGachaDo){
       Integer np = groupGachaDo.getNP();
        Integer ssrP = groupGachaDo.getSsrP();
        Integer srP = groupGachaDo.getSrP();
        Integer rP = groupGachaDo.getRP();
        Integer urP = groupGachaDo.getUrP();
        return GachaUtils.gacha(num,urP,ssrP,srP,rP,np);
    }
}

package org.nekotori.commands.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.PrivilegeGroupCommand;
import org.nekotori.dao.ChatMemberMapper;
import org.nekotori.dao.GroupGachaMapper;
import org.nekotori.dao.NikkeCharactersMapper;
import org.nekotori.entity.ChatMemberDo;
import org.nekotori.entity.CommandAttr;
import org.nekotori.entity.GroupGachaDo;
import org.nekotori.entity.NikkeCharacters;
import org.nekotori.utils.CommandUtils;
import org.nekotori.utils.GachaUtils;
import org.nekotori.utils.ImageUtil;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


@IsCommand(name = {"抽卡", "建造","招募"}, description = "模拟抽卡，测试人品\n格式:\n    (!/-/#)抽卡 [数量]")
public class GachaCommand extends PrivilegeGroupCommand {

    @Resource
    private GroupGachaMapper groupGachaMapper;

    @Resource
    private NikkeCharactersMapper nikkeCharactersMapper;

    @Resource
    private ChatMemberMapper chatMemberMapper;

    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        if (commandAttr.getCommand().equals("建造")) {
            int type = 0;
            if (!CollectionUtil.isEmpty(commandAttr.getParam())) {
                String s = commandAttr.getParam().get(0);
                if (Arrays.asList("轻型", "轻").contains(s)) {
                    type = 0;
                } else if (Arrays.asList("重型", "重").contains(s)) {
                    type = 1;
                } else if (Arrays.asList("特型", "特").contains(s)) {
                    type = 2;
                } else if (Arrays.asList("限定", "限").contains(s)) {
                    type = 3;
                }
            }
            if (type == 3) {
                try {
                    List<ImageUtil.AzureLaneCard> azureLaneCards = GachaUtils.gachaAzureLaneSp();
                    InputStream inputStream = ImageUtil.generateAzureCardImage(azureLaneCards);
                    MessageReceipt<Group> groupMessageReceipt = subject.sendMessage(new MessageChainBuilder().append(Contact.uploadImage(subject,
                            Objects.requireNonNull(inputStream))).build());
                    return null;

                } catch (Exception e) {
                    return new MessageChainBuilder().append("发生了未知错误").build();
                }
            }
            try {
                List<ImageUtil.AzureLaneCard> azureLaneCards = GachaUtils.gachaAzureLaneAll(type);
                InputStream inputStream = ImageUtil.generateAzureCardImage(azureLaneCards);
                MessageReceipt<Group> groupMessageReceipt = subject.sendMessage(new MessageChainBuilder().append(Contact.uploadImage(subject,
                        Objects.requireNonNull(inputStream))).build());
            } catch (Exception e) {
                return new MessageChainBuilder().append("发生了未知错误").build();
            }
            return null;
        }

        if("招募".equals(commandAttr.getCommand())){


            ChatMemberDo chatMemberDo = chatMemberMapper.selectOne(Wrappers.<ChatMemberDo>lambdaQuery().eq(ChatMemberDo::getMemberId, sender.getId()).eq(ChatMemberDo::getGroupId, subject.getId()));
            Integer gold = chatMemberDo.getGold();
            if(gold<10){
                return new MessageChainBuilder().append(new QuoteReply(messageChain)).append("猫代币不足，请签到获取猫代币").build();
            }
            List<String> gacha = GachaUtils.gacha(10, 0, 4, 43, 53, 0);
            int getGold = gacha.stream().map(s -> {
                if ("SSR".equals(s)) {
                    return 5;
                }
                if ("SR".equals(s)) {
                    return 1;
                }
                return 0;
            }).mapToInt(Integer::intValue).sum();
            chatMemberDo.setGold(gold-10+getGold);
            chatMemberMapper.updateById(chatMemberDo);
            List<NikkeCharacters> nikkeCharacters = nikkeCharactersMapper.selectList(Wrappers.lambdaQuery());
            Map<String, List<NikkeCharacters>> collect = nikkeCharacters.stream().collect(Collectors.groupingBy(NikkeCharacters::getRarity));
            List<ImageUtil.CardAttr> cardAttrs = new ArrayList<>();
            for (String s : gacha) {
                List<NikkeCharacters> nikkeCharacters1 = collect.get(s);
                NikkeCharacters nikkeCharacters2 = nikkeCharacters1.get(new Random().nextInt(nikkeCharacters1.size()));
                cardAttrs.add(new ImageUtil.CardAttr(nikkeCharacters2.getName(), "SSR".equals(s) ?2: "SR".equals(s) ?1:0));
            }
            InputStream nikkeGachaImageStream = ImageUtil.getNikkeGachaImageStream(cardAttrs);
            assert nikkeGachaImageStream != null;
            return new MessageChainBuilder().append(new QuoteReply(messageChain)).append("消耗10枚猫代币进行招募\n返还"+getGold+"枚猫代币\n").append(Contact.uploadImage(subject,nikkeGachaImageStream)).build();
        }


        Optional<GroupGachaDo> first = groupGachaMapper.selectList(new QueryWrapper<GroupGachaDo>().eq("group_id", subject.getId()).orderByDesc(
                "create_time")).stream().findFirst();
        if (first.isEmpty()) {
            return new MessageChainBuilder().append("请先修改概率").build();
        }
        GroupGachaDo groupGachaDo = first.get();
        List<String> param = commandAttr.getParam();
        List<String> gachas;
        if (CollectionUtil.isEmpty(param)) {
            gachas = gacha(1, groupGachaDo);
        } else {
            try {
                int i = Integer.parseInt(param.get(0));
                if (i < 1) {
                    return new MessageChainBuilder().append("抽你马呢").build();
                }
                if (i > 40) {
                    return new MessageChainBuilder().append("输入范围有误，仅支持1～40次抽卡").build();
                }
                gachas = gacha(i, groupGachaDo);
            } catch (NumberFormatException e) {
                gachas = gacha(1, groupGachaDo);
            }
        }
        MessageChainBuilder singleMessages = new MessageChainBuilder();
        gachas.forEach(s -> {
            singleMessages.append("  ").append(s);
        });

        return singleMessages.build();
    }

    private List<String> gacha(Integer num, GroupGachaDo groupGachaDo) {
        Integer np = groupGachaDo.getNP();
        Integer ssrP = groupGachaDo.getSsrP();
        Integer srP = groupGachaDo.getSrP();
        Integer rP = groupGachaDo.getRP();
        Integer urP = groupGachaDo.getUrP();
        return GachaUtils.gacha(num, urP, ssrP, srP, rP, np);
    }
}

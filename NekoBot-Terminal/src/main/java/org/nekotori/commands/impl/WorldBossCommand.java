package org.nekotori.commands.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import org.nekotori.annotations.IsCommand;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.chain.channel.handler.impl.ChangeBossHandler;
import org.nekotori.commands.ManagerGroupCommand;
import org.nekotori.entity.CommandAttr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@IsCommand(name = {"修改Boss","修改BossJson","加载Boss"}, description = "修改工会战Boss\n格式:\n    (!/-/#)修改Boss[Json]")
public class WorldBossCommand extends ManagerGroupCommand {

    @Resource
    private ChainMessageSelector chainMessageSelector;

    @Resource
    private ChangeBossHandler changeBossHandler;

    @Value("${bot.dir}")
    private String botDir;

    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        if("加载Boss".equals(commandAttr.getCommand())){
            File[] bosses = FileUtil.ls(botDir+"/boss");
            List<String> bossesNames = Arrays.stream(bosses).sequential().map(File::getName).collect(Collectors.toList());
            String join = String.join("\n", bossesNames);
            if(CollectionUtils.isEmpty(commandAttr.getParam())){
                return new MessageChainBuilder()
                        .append(new QuoteReply(messageChain))
                        .append("请指定Boss类型，现已有：\n")
                        .append(join).build();
            }
            String s = commandAttr.getParam().get(0);

            for (File boss : bosses) {
                if(s.equals(boss.getName())){
                    String content = FileUtil.readUtf8String(boss);
                    File file = new File("raid/worldBoss" + subject.getId());
                    FileUtil.writeString(content,file, StandardCharsets.UTF_8);
                    return new MessageChainBuilder()
                            .append(new QuoteReply(messageChain))
                            .append("加载成功")
                            .build();
                }
            }
            return null;
        }
        if("修改BossJson".equals(commandAttr.getCommand())){
            File file = new File("raid/worldBoss" + subject.getId());
            String s = FileUtil.readUtf8String(file);
            if(CollectionUtils.isEmpty(commandAttr.getParam())){
                return new MessageChainBuilder().append("未携带请求参数，原Json如下：\n").append(s).build();
            }
            String join = String.join(" ", commandAttr.getParam());
            JSONObject jsonObject = JSONUtil.parseObj(join);
            FileUtil.writeString(jsonObject.toStringPretty(),file, StandardCharsets.UTF_8);
            return new MessageChainBuilder().append("修改成功").build();

        }

        try {
            chainMessageSelector.registerChannel(subject.getId(), changeBossHandler);
        } catch (RuntimeException e) {
            return new MessageChainBuilder().append("已经在修改中").build();
        }
        subject.sendMessage(new PlainText("请输入Boss战名称"));
        chainMessageSelector.joinChannel(subject.getId(), ChangeBossHandler.class, sender.getId());
        return null;
    }
}

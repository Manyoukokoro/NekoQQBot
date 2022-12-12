package org.nekotori.commands.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.ManagerGroupCommand;
import org.nekotori.entity.CommandAttr;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

@IsCommand(name = "set-chat-gpt-key",description = "设置chatGpt的Token")
public class SetChatGPTApiCommand extends ManagerGroupCommand {
    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        List<String> param = commandAttr.getParam();
        String key = String.join(" ", param);
        JSONObject jsonObject = new JSONObject();
        JSONObject set = jsonObject.set("api-key", key);
        String s = set.toString();
        try {
            FileUtil.writeString(s,new File("chat-gpt.conf"),StandardCharsets.UTF_8);
        }catch (Exception e){
            return null;
        }
        return new MessageChainBuilder().append("设置成功").build();
    }
}

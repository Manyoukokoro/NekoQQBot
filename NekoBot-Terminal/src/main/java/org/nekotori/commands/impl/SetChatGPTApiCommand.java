package org.nekotori.commands.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.ManagerGroupCommand;
import org.nekotori.entity.CommandAttr;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

@IsCommand(name = "gpt-key",description = "设置chatGpt的Token\n格式:\n    (!/-/#)gpt-key <key>")
public class SetChatGPTApiCommand extends ManagerGroupCommand {

    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        List<String> param = commandAttr.getParam();
        String key = String.join(" ", param);
        JSONObject jsonObject = new JSONObject();
        JSONObject set = jsonObject.set("api-key", key);
        String s = set.toString();
        try {
            FileUtil.writeString(s,new File("gpt-"+subject.getId()+".conf"),StandardCharsets.UTF_8);
        }catch (Exception e){
            return null;
        }
        return new MessageChainBuilder().append("设置成功").build();
    }

    public static String getGptKey(Group subject){
        try {
            String s = FileUtil.readUtf8String(new File("gpt-" + subject.getId() + ".conf"));
            return JSONUtil.parseObj(s).getStr("api-key");
        }catch (Exception e){
            return "";
        }

    }
}

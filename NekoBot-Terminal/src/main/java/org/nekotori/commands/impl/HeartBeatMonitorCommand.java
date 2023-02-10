package org.nekotori.commands.impl;


import cn.hutool.core.io.FileUtil;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.commands.PrivilegeGroupCommand;
import org.nekotori.entity.CommandAttr;
import org.nekotori.utils.ChromeUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@IsCommand(name = {"监控","删除监控","监控列表"},description = "[程]监控业务运行状态\n格式:\n    (!/-/#)监控 <url>\n详情:\n    本功能用于协助开发者监控自己的云端业务系统，会定期对指定url发送get请求，如果系统正常响应，则不会做任何处理，如果系统无响应，则会在群中发送报警信息。\n开发者也可以定制报警信息，只要响应满足如下json结构：\n{\n   \"code\":200,\n   \"message\":\"content\",\n   \"display\":true\n}\n  其中，code和message会被组装为报警信息，display决定是否发送该条信息。")
public class HeartBeatMonitorCommand extends PrivilegeGroupCommand {


    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        if("监控列表".equals(commandAttr.getCommand())){
            List<String> urls = FileUtil.readLines(new File("monitor.info"), StandardCharsets.UTF_8);
            MessageChainBuilder append = new MessageChainBuilder().append(new QuoteReply(messageChain));
            append.append("本群存在以下监控");
            for (String url : urls) {
                String[] split = url.split("#sp#");
                if(String.valueOf(subject.getId()).equals(split[0])){
                    append.append("\n").append(split[2]);
                }
            }
            return append.build();
        }
        if("删除监控".equals(commandAttr.getCommand())){
            List<String> urls = FileUtil.readLines(new File("monitor.info"), StandardCharsets.UTF_8);
            List<String> res = new ArrayList<>();
            for (String url : urls) {
                String[] split = url.split("#sp#");
                if(!commandAttr.getParam().get(0).equals(split[2])){
                    res.add(url);
                }
            }
            FileUtil.writeLines(res,new File("monitor.info"), StandardCharsets.UTF_8);
            return new MessageChainBuilder().append(new QuoteReply(messageChain)).append("删除成功").build();
        }

        List<String> param = commandAttr.getParam();
        if(param == null){
            return null;
        }
        List<String> collect = param.stream().filter(ChromeUtils::isUrl).map(url->subject.getId()+"#sp#"+sender.getId()+"#sp#"+url).collect(Collectors.toList());
        FileUtil.appendLines(collect,new File("monitor.info"), StandardCharsets.UTF_8);
        return new MessageChainBuilder().append("添加").append(String.valueOf(collect.size())).append("条监控地址").build();
    }
}

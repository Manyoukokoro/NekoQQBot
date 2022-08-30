package org.nekotori.commands.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.dao.ChatGroupMapper;
import org.nekotori.entity.ChatGroupDo;
import org.nekotori.entity.CommandAttr;
import org.nekotori.entity.CustomResponse;
import org.nekotori.job.AsyncJob;
import org.nekotori.utils.CommandUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@IsCommand(name = {"回复", "撤销回复", "查询回复", "response"}, description = "自定义回复，格式:(!/-/#)回复 触发方式 触发文 回复文")
public class CustomResponseCommand extends NoAuthGroupCommand {

    @Resource
    private ChatGroupMapper chatGroupMapper;

    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {

        String s = messageChain.contentToString().replace("\\n", "");
        CommandAttr commandAttr = CommandUtils.resolveTextCommand(s);
        //查询回复
        if ("查询回复".equals(commandAttr.getCommand())) {
            ChatGroupDo group = chatGroupMapper.selectOne(Wrappers.<ChatGroupDo>lambdaQuery().eq(ChatGroupDo::getGroupId, subject.getId()));
            List<CustomResponse> customResponses = getCustomResponses(group);
            StringBuilder stringBuilder = new StringBuilder();
            for (CustomResponse c : customResponses) {
                stringBuilder.append(c.getWay()).append(":").append(c.getKeyWord()).append(":").append(c.getResponse()).append("\n");
            }
            return new MessageChainBuilder().append(new At(sender.getId())).append("查询到本群自定义回复如下：\n").append(stringBuilder.toString()).build();
        }
        List<String> param = commandAttr.getParam();

        if (param.size() < 2) {
            return new MessageChainBuilder().append(new PlainText("参数过少，请检查一下哦")).build();
        }

        if (param.size() == 2 && ("回复".equals(commandAttr.getCommand()) || "response".equals(commandAttr.getCommand()))) {

        }
        String way = param.get(0);
        String keyWord = param.get(1);
        //检查匹配类型
        if (Arrays.stream(CustomResponse.WAY.values()).filter(v -> v.toString().equals(way)).findAny().isEmpty()) {
            return new MessageChainBuilder().append(new PlainText("什么冥王星匹配方式，你逗你机器人大爷呢")).build();
        }

        String join = String.join(" ", param.subList(2, param.size()));

        if (CustomResponse.WAY.REGEX.toString().equals(way)) {
            try {
                Pattern.compile(keyWord);
            } catch (Exception e) {
                return new MessageChainBuilder().append(new PlainText("您的正则好像有点机车欸")).build();
            }
        }
        try {
            CustomResponse build = CustomResponse.builder()
                    .keyWord(keyWord)
                    .way(CustomResponse.WAY.of(way))
                    .response(join).build();
            ChatGroupDo group = chatGroupMapper.selectOne(Wrappers.<ChatGroupDo>lambdaQuery().eq(ChatGroupDo::getGroupId, subject.getId()));
            List<CustomResponse> customResponses = getCustomResponses(group);
            if (commandAttr.getCommand().equals("撤销回复")) {
                customResponses =
                        customResponses.stream().filter(se -> !se.getKeyWord().equals(keyWord)).collect(Collectors.toList());
            } else {
                customResponses.add(build);
            }
            AsyncJob.localCache.put(subject.getId(), customResponses);
            group.setCustomResponse(JSONUtil.toJsonStr(customResponses));
            chatGroupMapper.updateById(group);
            if (commandAttr.getCommand().equals("撤销回复")) {
                return new MessageChainBuilder().append(new PlainText("撤销自定义回复成功!")).build();
            }
            return new MessageChainBuilder().append(new PlainText("新增自定义回复成功!")).build();
        } catch (Exception e) {
            return new MessageChainBuilder().append(new PlainText("发生了未知错误～QAQ")).build();
        }
    }

    private List<CustomResponse> getCustomResponses(ChatGroupDo group) {
        String customResponse = group.getCustomResponse();
        List<CustomResponse> customResponses;
        if (customResponse == null) {
            customResponses = new ArrayList<>();
        } else {
            customResponses = JSONUtil.toBean(customResponse,
                    new TypeReference<>() {
                    }, true);
        }
        return customResponses;
    }
}
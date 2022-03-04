package org.nekotori.commands.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
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
import java.util.List;
import java.util.stream.Collectors;


@IsCommand(name = {"回复","撤销回复","response"},description = "自定义回复，格式:(!/-/#)回复 触发方式 触发文 回复文")
public class CustomResponseCommand extends NoAuthGroupCommand {

    @Resource
    private ChatGroupMapper chatGroupMapper;

    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {


        String s = messageChain.serializeToMiraiCode().replace("\\n","");
        CommandAttr commandAttr = CommandUtils.resolveCommand(s);
        List<String> param = commandAttr.getParam();
        if(param.size()<3){
            return new MessageChainBuilder().append(new PlainText("参数过少，请检查一下哦")).build();
        }
        String way = param.get(0);
        String keyWord = param.get(1);
        StringBuilder response = new StringBuilder();
        for(int i=2;i<param.size();i++){
            response.append(param.get(i));
        }
        try {
            CustomResponse build = CustomResponse.builder()
                    .keyWord(keyWord)
                    .way(CustomResponse.WAY.of(way))
                    .response(response.toString()).build();
            ChatGroupDo group = chatGroupMapper.selectOne(new QueryWrapper<ChatGroupDo>().eq("group_id", subject.getId()));
            String customResponse = group.getCustomResponse();
            List<CustomResponse> customResponses;
            if (customResponse ==null){
                customResponses = new ArrayList<>();
            }
            else {
                customResponses = JSONUtil.toBean(customResponse,
                        new TypeReference<List<CustomResponse>>() {
                        }, true);
            }
            if(commandAttr.getCommand().equals("撤销回复")){
               customResponses =
                       customResponses.stream().filter(se->!se.getKeyWord().equals(keyWord)).collect(Collectors.toList());
            }else{
                customResponses.add(build);
            }
            AsyncJob.localCache.put(subject.getId(),customResponses);
            group.setCustomResponse(JSONUtil.toJsonStr(customResponses));
            chatGroupMapper.updateById(group);
            return new MessageChainBuilder().append(new PlainText("新增自定义回复成功!")).build();
        }catch (Exception e){
            return new MessageChainBuilder().append(new PlainText("发生了未知错误～QAQ")).build();
        }
    }
}
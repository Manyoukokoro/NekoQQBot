package org.nekotori.commands.impl;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.commands.PrivilegeGroupCommand;
import org.nekotori.entity.CommandAttr;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@IsCommand(name = {"AI"}, description = "进行AI画图，该功能只有在bot部署在有显卡和Diffusion服务的物理机上才能使用\n格式:\n    (!/-/#)AI ...<英文提词>")
@Slf4j
public class AIDrawCommand extends PrivilegeGroupCommand {
    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        if(CollectionUtils.isEmpty(commandAttr.getParam())|| !StringUtils.hasLength(commandAttr.getParam().get(0))){
            return new MessageChainBuilder().append("没有参数哦").build();
        }
        List<String> param = commandAttr.getParam();
        String join = String.join(" ", param);
        String template = "{\"fn_index\":11,\"data\":[\"<--param-->\",\"\",\"None\",\"None\",20,\"Euler a\",false,false,1,1,7,-1,-1,0,0,0,false,512,512,false,false,0.7,\"None\",false,false,null,\"\",\"Seed\",\"\",\"Steps\",\"\",true,false,null,\"\",\"\"],\"session_hash\":\"f7p1cedn03n\"}";
        String replace = template.replace("<--param-->", join);
        try{
            HttpResponse execute = HttpRequest.post("http://localhost:7860/api/predict/").body(replace).execute();
            String body = execute.body();
            String data = JSONUtil.parseObj(body).getStr("data");
            System.out.println(data);
        }catch (Exception ignore){
        }
        return new MessageChainBuilder().append("RTX OFF").build();
    }

}

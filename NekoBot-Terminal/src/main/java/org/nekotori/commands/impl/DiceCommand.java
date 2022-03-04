package org.nekotori.commands.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Dice;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.common.InnerConstants;
import org.nekotori.entity.CommandAttr;
import org.nekotori.utils.CommandUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;


@IsCommand(name = {"d","dice"},description = "随机骰子，格式:(!/-/#)dice ...[数量]")
public class DiceCommand extends NoAuthGroupCommand {
    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        String s = messageChain.serializeToMiraiCode();
        CommandAttr commandAttr = CommandUtils.resolveCommand(s);
        List<String> param = commandAttr.getParam();
        int num  =  1;
        if(!CollectionUtils.isEmpty(param)){
            num = param.stream().map(n->{
                try{
                    return Integer.parseInt(n);
                }catch (Exception e){
                    return 0;
                }
            }).reduce(Integer::sum).orElse(1);
        }
        if(num>6) {
            subject.sendMessage(new PlainText("太多了，您是想投114514个骰子吗？"));
            return null;
        }
        if(num<1) num=1;
        for(int i=0;i<num;i++){
            if(InnerConstants.admin.equals(sender.getId())){
                subject.sendMessage(new Dice(6));
            }else{
                subject.sendMessage(Dice.random());
            }
        }
        return null;
    }
}

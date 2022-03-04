package org.nekotori.commands.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.commands.PrivilegeGroupCommand;
import org.nekotori.dao.GroupGachaMapper;
import org.nekotori.entity.CommandAttr;
import org.nekotori.entity.GroupGachaDo;
import org.nekotori.utils.CommandUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@IsCommand(name = {"抽卡","gacha","g"},description = "模拟抽卡，测试人品，格式:(!/-/#)gacha 数量")
public class GachaCommand extends PrivilegeGroupCommand {

    @Resource
    private GroupGachaMapper groupGachaMapper;

    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        Optional<GroupGachaDo> first = groupGachaMapper.selectList(new QueryWrapper<GroupGachaDo>().eq("group_id", subject.getId()).orderByDesc(
                "create_time")).stream().findFirst();
        if (first.isEmpty()){
            return new MessageChainBuilder().append("请先修改概率").build();
        }
        GroupGachaDo groupGachaDo = first.get();
        CommandAttr commandAttr = CommandUtils.resolveCommand(messageChain.serializeToMiraiCode());
        List<String> param = commandAttr.getParam();
        List<String> gachas;
        if(CollectionUtil.isEmpty(param)){
            gachas = gacha(1,groupGachaDo);
        }
        else {
            try{
                int i = Integer.parseInt(param.get(0));
                if(i<1||i>40){
                    return new MessageChainBuilder().append("输入范围有误，仅支持0～40次抽卡").build();
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
        Integer sum = np+ssrP+srP+rP+urP;
        np = (int) (np.doubleValue()/sum.doubleValue() * 100000);
        ssrP = (int) (ssrP.doubleValue()/sum.doubleValue() * 100000)+np;
        srP = (int) (srP.doubleValue()/sum.doubleValue() * 100000)+ssrP;
        rP = (int) (rP.doubleValue()/sum.doubleValue() * 100000)+srP;
        List<String> gachas = new ArrayList<>();
        Random random = new Random();
        for(int j = 0;j<num;j++){
            int i = random.nextInt(100000);
            if(i<np){
                gachas.add("N");
            }
            else if(i<ssrP){
                gachas.add("SSR");
            }
            else if(i<srP){
                gachas.add("SR");
            }
            else if(i<rP){
                gachas.add("R");
            }
            else {
                gachas.add("UR");
            }
        }

        return gachas;
    }
}

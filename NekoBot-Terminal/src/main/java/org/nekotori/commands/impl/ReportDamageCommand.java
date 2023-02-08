package org.nekotori.commands.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.message.data.SingleMessage;
import net.mamoe.mirai.utils.ExternalResource;
import org.jetbrains.annotations.NotNull;
import org.nekotori.annotations.IsCommand;
import org.nekotori.commands.NoAuthGroupCommand;
import org.nekotori.common.InnerConstants;
import org.nekotori.entity.CommandAttr;
import org.nekotori.utils.ExcelUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@IsCommand(name = {"报刀",
        "出刀",
        "申请出刀",
        "申请合刀",
        "撤回上刀",
        "撤回出刀",
        "撤回",
        "撤回申请",
        "查看历史",
        "出刀历史",
        "初始化",
        "查看",
        "查看血量",
        "重置",
        "重置阶段",
        "修改出刀上限",
        "预约出刀",
        "预约"}, description = "世界Boss报刀\nnikke工会战指令说明（[]中为必需参数，<>为可选参数）\n" +
        "报刀流程：\n" +
        "1.【申请出刀/申请合刀】，【申请出刀】将占用当前工会的出刀名额，其他人无法再进行申请。如果当前有人申请出刀，需要使用【申请合刀】指令才能完成报刀，申请指令可以被【撤销申请】取消。\n" +
        "2.【报刀】，指令格式：报刀 [伤害量] <艾特成员|QQ以外成员名字>，报刀之前必须先【申请出刀】或者【申请合刀】，避免盲目出刀。如果后面带了艾特成员的参数，将视为代替成员报刀。\n" +
        "3.【撤回上刀/撤回出刀】，该指令会撤回上一次有效的报刀操作，并返还出刀次数。包括【报刀】和【重置】指令。\n" +
        "预约：\n" +
        "1.【预约出刀】，参数为预约Boss轮次的绝对索引，如：每轮有5个Boss，那么第一轮第二个Boss的索引为2，第二轮第三个Boss的索引为5+3=8。使用 预约出刀 [索引值] 进行预约后，机器人会在到达该Boss时自动艾特发起预约的成员。\n" +
        "Boss操作：\n" +
        "1.【初始化】，初始化当前Boss战，清除所有报刀记录，并将Boss回退至阶段1。\n" +
        "2.【重置/重置阶段】，重置Boss当前阶段，清除当前阶段所有已造成的伤害。\n" +
        "3.【查看/查看血量】，查看Boss当前血量。\n" +
        "4.【修改出刀上限】，修改工会成员每天的出刀次数上限，格式为：修改出刀上限 [次数]。")
public class ReportDamageCommand extends NoAuthGroupCommand {
    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        String s = FileUtil.readUtf8String(new File("raid/worldBoss" + subject.getId()));
        JSONObject jsonObject = JSONUtil.parseObj(s);
        JSONArray currentUser = jsonObject.getJSONArray("currentUser");
        JSONArray history = jsonObject.getJSONArray("history");
        JSONArray hps = jsonObject.getJSONArray("hps");
        JSONArray stageNames = jsonObject.getJSONArray("stageNames");
        Integer chancePerUser = jsonObject.getInt("chancePerUser");
        JSONArray order = jsonObject.getJSONArray("order");
        String name = jsonObject.getStr("name");
        if("查看历史".equals(commandAttr.getCommand()) ||"出刀历史".equals(commandAttr.getCommand())){
            if(sender.getPermission().equals(MemberPermission.ADMINISTRATOR) ||
                    sender.getPermission().equals(MemberPermission.OWNER) ||
                        sender.getId()== InnerConstants.admin){
                uploadHistory(subject, history, stageNames, name);
                return new MessageChainBuilder().append(new QuoteReply(messageChain)).append("出刀情况详见Excel文件").build();
            }
            return null;
        }

        if("预约出刀".equals(commandAttr.getCommand())||"预约".equals(commandAttr.getCommand())){
            if(!CollectionUtils.isEmpty(commandAttr.getParam())){
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.putOnce("user",sender.getId());
                jsonObject1.putOnce("orderStage",Integer.parseInt(commandAttr.getParam().get(0)));
                if(CollectionUtils.isEmpty(order)){
                    order = new JSONArray();
                    jsonObject.putIfAbsent("order",order);
                }
                order.add(jsonObject1);
                jsonObject.replace("order",order);
                saveFile(subject,jsonObject);
                return new MessageChainBuilder().append(new QuoteReply(messageChain)).append("预约成功").build();
            }
        }
        if("修改出刀上限".equals(commandAttr.getCommand())){
            if(CollectionUtils.isEmpty(commandAttr.getParam())){
                return null;
            }
            String s1 = commandAttr.getParam().get(0);
            int limit = Integer.parseInt(s1);
            jsonObject.replace("chancePerUser",limit);
            saveFile(subject,jsonObject);
            return new MessageChainBuilder().append(new QuoteReply(messageChain)).append("修改成功").build();
        }
        if("重置".equals(commandAttr.getCommand())||"重置阶段".equals(commandAttr.getCommand())) {
            return reset(sender, subject, messageChain, jsonObject, history, chancePerUser);
        }
        if("查看".equals(commandAttr.getCommand())||"查看血量".equals(commandAttr.getCommand())){
            return getNowBossHp(sender, messageChain, history, hps, stageNames, chancePerUser);
        }
        if("初始化".equals(commandAttr.getCommand())){
            jsonObject.replace("history",new JSONArray());
            saveFile(subject, jsonObject);
            return new MessageChainBuilder()
                    .append(new QuoteReply(messageChain))
                    .append("Boss战初始化成功")
                    .build();
        }
        if("撤回上刀".equals(commandAttr.getCommand())
                ||"撤回出刀".equals(commandAttr.getCommand())
                ||"撤回".equals(commandAttr.getCommand())){
            return recall(subject, jsonObject, history, stageNames);
        }
        if("撤回申请".equals(commandAttr.getCommand())){
            return recallReq(sender, subject, messageChain, jsonObject, currentUser);
        }
        if("申请合刀".equals(commandAttr.getCommand())){
            return reqCombine(sender, subject, messageChain, jsonObject, currentUser);
        }
        if("申请出刀".equals(commandAttr.getCommand())){
            return reqBattle(sender, subject, messageChain, jsonObject, currentUser);
        }

        long reporter = sender.getId();
        List<SingleMessage> extMessage = commandAttr.getExtMessage();
        if(extMessage != null ) {
            for (SingleMessage singleMessage : extMessage) {
                if (singleMessage instanceof At) {
                    reporter = ((At) singleMessage).getTarget();
                }
            }
        }
        long finalReporterId = reporter;
        NormalMember reportMember = null;
        for (NormalMember member : subject.getMembers()) {
            if(member.getId() == finalReporterId){
                reportMember = member;
            }
        }
        List<Object> collect = currentUser.stream().filter(id -> Long.parseLong(String.valueOf(id)) != finalReporterId).collect(Collectors.toList());
        if (reporter==sender.getId() &&(collect.size()==currentUser.size()||currentUser.size()==0)){
            return new MessageChainBuilder()
                    .append(new QuoteReply(messageChain))
                    .append("\n你还未申请出刀，请先申请出刀，防止撞刀")
                    .build();
        }
        jsonObject.replace("currentUser",collect);
        int nowStage = 0;
        long totalDamage = 0;
        if(history.size()>0) {
            long already = history.stream()
                    .filter(o-> LocalDateTimeUtil.of(JSONUtil.parseObj(o.toString()).getLong("timestamp"))
                            .isAfter(LocalDateTimeUtil.beginOfDay(LocalDateTime.now())))
                    .map(o -> JSONUtil.parseObj(o.toString()).getLong("reporterId"))
                    .filter(id -> finalReporterId == id)
                    .count();
            if(already-chancePerUser>=0){
                return new MessageChainBuilder().append(new QuoteReply(messageChain)).append("\n").append("本日出刀机会已用完").build();
            }
            String newest = history.getStr(history.size() - 1);
            JSONObject before = JSONUtil.parseObj(newest);
            nowStage = before.getInt("nextStage");
            totalDamage = before.getLong("totalDamage");
        }
        int totalBossNum = hps.size();
        List<String> param = commandAttr.getParam();
        long sum = Long.parseLong(param.get(0));
        totalDamage+=sum;
        boolean isOverStage = false;
        if(totalDamage>=Long.parseLong(hps.getStr(nowStage))){
            isOverStage = true;
            nowStage++;
            totalDamage=0;
            //call order
            if(!CollectionUtils.isEmpty(order)){
                int finalNowStage = nowStage;
                List<JSONObject> collect1 = order.stream().map(o -> {
                    JSONObject jsonObject1 = JSONUtil.parseObj(o.toString());
                    Long user = jsonObject1.getLong("user");
                    Integer orderStage = jsonObject1.getInt("orderStage");
                    if(orderStage==null){
                        return null;
                    }
                    if (finalNowStage+1 == orderStage) {
                        subject.sendMessage(new MessageChainBuilder().append(new At(user)).append("\n已经到您预约的出刀阶段，请及时出刀").build());
                        return null;
                    }
                    return jsonObject1;
                }).filter(Objects::nonNull).collect(Collectors.toList());
                jsonObject.replace("order",collect1);
            }
        }

        String otherReporter = "";
        if(!CollectionUtils.isEmpty(commandAttr.getParam())&&commandAttr.getParam().size()>=2){
            otherReporter = commandAttr.getParam().get(1);
        }
        JSONObject income = new JSONObject();
        income.putOnce("nowStage",isOverStage?nowStage-1:nowStage);
        income.putOnce("nextStage",nowStage);
        income.putOnce("totalDamage",totalDamage);
        income.putOnce("timestamp",System.currentTimeMillis());
        income.putOnce("time",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        income.putOnce("reporterId",StringUtils.hasLength(otherReporter)?-1L:sender.getId());
        income.putOnce("reporter",StringUtils.hasLength(otherReporter)?otherReporter:StringUtils.hasLength(reportMember.getNameCard())?reportMember.getNameCard():reportMember.getNick());
        income.putOnce("thisDamage",sum);
        history.add(income);
        jsonObject.replace("history",history);
       if(nowStage>totalBossNum-1){
            MessageChainBuilder singleMessages = new MessageChainBuilder();
            singleMessages.append("本次").append(name).append("工会战结束,出刀情况详见Excel文件");
//            for (Object o : history) {
//                JSONObject jsonObject1 = JSONUtil.parseObj(o.toString());
//                if(Long.parseLong(jsonObject1.getStr("thisDamage"))<=0){
//                    continue;
//                }
//                singleMessages
//                        .append("\n阶段：").append(stageNames.size()>nowStage?stageNames.get(nowStage).toString():String.valueOf(nowStage+1))
//                        .append("\n  时间:").append(jsonObject1.getStr("time"))
//                        .append("\n  出刀:").append(jsonObject1.getStr("reporter"))
//                        .append("\n  伤害:").append(jsonObject1.getStr("thisDamage"));
//            }
           uploadHistory(subject, history, stageNames, name);
           jsonObject.replace("history",new JSONArray());
           saveFile(subject, jsonObject);
           return singleMessages.build();
        }
        saveFile(subject, jsonObject);
        long remain = hps.getLong(nowStage)-totalDamage;
        MessageChainBuilder singleMessages = new MessageChainBuilder();
        if(isOverStage){
            singleMessages.append("阶段Boss已被击败\n");
        }
        return singleMessages
                .append("\n时间: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                .append("\n出刀: ").append(StringUtils.hasLength(otherReporter)?otherReporter:StringUtils.hasLength(reportMember.getNameCard())?reportMember.getNameCard():reportMember.getNick())
                .append("\n伤害: ").append(String.valueOf(sum))
                .append("\n目前阶段: ").append(stageNames.size()>nowStage?stageNames.get(nowStage).toString():String.valueOf(nowStage+1))
                .append("\nBoss剩余血量: ").append(String.valueOf(remain)).append("/").append(String.valueOf(hps.getLong(nowStage))).build();
    }

    private void uploadHistory(Group subject, JSONArray history, JSONArray stageNames, String name) {
        InputStream inputStream = saveToExcel(name, history, stageNames);
        try {
            subject.getFiles().uploadNewFile(name +"统计.xlsx", ExternalResource.create(inputStream));
        }catch (Exception ignore){
        }
    }

    @NotNull
    private static MessageChain reset(Member sender, Group subject, MessageChain messageChain, JSONObject jsonObject, JSONArray history, Integer chancePerUser) {
        int nowStage = 0;
        long totalDamage = 0;
        if(history.size()>0) {
            String newest = history.getStr(history.size() - 1);
            JSONObject before = JSONUtil.parseObj(newest);
            nowStage = before.getInt("nowStage");
            totalDamage = before.getLong("totalDamage");
        }
        JSONObject income = new JSONObject();
        income.putOnce("nowStage",nowStage);
        income.putOnce("nextStage",nowStage);
        income.putOnce("totalDamage",0L);
        income.putOnce("timestamp",System.currentTimeMillis());
        income.putOnce("time",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        income.putOnce("reporterId", 0);
        income.putOnce("reporter", "system");
        income.putOnce("thisDamage", -totalDamage);
        history.add(income);
        jsonObject.replace("history", history);
        saveFile(subject, jsonObject);
        return new MessageChainBuilder()
                .append(new QuoteReply(messageChain))
                .append("重置阶段成功")
                .build();
    }

    private static void saveFile(Group subject, JSONObject jsonObject) {
        FileUtil.writeString(jsonObject.toStringPretty(),new File("raid/worldBoss"+ subject.getId()), StandardCharsets.UTF_8);
    }

    @NotNull
    private static MessageChain reqBattle(Member sender, Group subject, MessageChain messageChain, JSONObject jsonObject, JSONArray currentUser) {
        for (Object o : currentUser) {
            if (sender.getId() == Long.parseLong(o.toString())){
                return new MessageChainBuilder()
                        .append(new QuoteReply(messageChain))
                        .append("\n你已经申请出刀")
                        .build();
            }
        }
        if (!CollectionUtils.isEmpty(currentUser)){
            List<Long> collect = currentUser.stream().map(String::valueOf).map(Long::parseLong).collect(Collectors.toList());
            ContactList<NormalMember> members = subject.getMembers();
            ArrayList<String> strings = new ArrayList<>();
            for (NormalMember member : members) {
                if(collect.contains(member.getId())){
                    strings.add(StringUtils.hasLength(member.getNameCard())?member.getNameCard():member.getNick());
                }
            }
            String join = String.join("/", strings);
            MessageChainBuilder append = new MessageChainBuilder()
                    .append(new QuoteReply(messageChain))
                    .append(join)
                    .append("\n");
            currentUser.stream().map(String::valueOf).map(Long::parseLong).forEach(id->{
                append.append(new At(id));
            });
            return append.append("  正在出刀中...申请失败")
                    .build();
        }
        currentUser.add(String.valueOf(sender.getId()));
        jsonObject.replace("currentUser", currentUser);
        saveFile(subject, jsonObject);
        return new MessageChainBuilder()
                .append(new QuoteReply(messageChain))
                .append("申请出刀成功")
                .build();
    }

    @NotNull
    private static MessageChain reqCombine(Member sender, Group subject, MessageChain messageChain, JSONObject jsonObject, JSONArray currentUser) {
        JSONArray history = jsonObject.getJSONArray("history");
        Integer chancePerUser = jsonObject.getInt("chancePerUser");
        if(history.size()>0) {
            long already = history.stream()
                    .filter(o-> LocalDateTimeUtil.of(JSONUtil.parseObj(o.toString()).getLong("timestamp"))
                            .isAfter(LocalDateTimeUtil.beginOfDay(LocalDateTime.now())))
                    .map(o -> JSONUtil.parseObj(o.toString()).getLong("reporterId"))
                    .filter(id -> sender.getId() == id)
                    .count();
            if(already- chancePerUser >=0){
                return new MessageChainBuilder().append(new QuoteReply(messageChain)).append("\n").append("本日出刀机会已用完").build();
            }
        }
        currentUser.add(String.valueOf(sender.getId()));
        jsonObject.replace("currentUser", currentUser);
        saveFile(subject, jsonObject);
        List<Long> collect = currentUser.stream().map(String::valueOf).map(Long::parseLong).collect(Collectors.toList());
        ContactList<NormalMember> members = subject.getMembers();
        ArrayList<String> strings = new ArrayList<>();
        for (NormalMember member : members) {
            if(collect.contains(member.getId())){
                strings.add(StringUtils.hasLength(member.getNameCard())?member.getNameCard():member.getNick());
            }
        }
        String join = String.join("/", strings);
        return new MessageChainBuilder()
                .append(new QuoteReply(messageChain))
                .append("申请合刀成功\n")
                .append("当前合并出刀成员:")
                .append(join)
                .build();
    }

    @NotNull
    private static MessageChain recallReq(Member sender, Group subject, MessageChain messageChain, JSONObject jsonObject, JSONArray currentUser) {
        JSONArray history = jsonObject.getJSONArray("history");
        Integer chancePerUser = jsonObject.getInt("chancePerUser");
        if(history.size()>0) {
            long already = history.stream()
                    .filter(o-> LocalDateTimeUtil.of(JSONUtil.parseObj(o.toString()).getLong("timestamp"))
                            .isAfter(LocalDateTimeUtil.beginOfDay(LocalDateTime.now())))
                    .map(o -> JSONUtil.parseObj(o.toString()).getLong("reporterId"))
                    .filter(id -> sender.getId() == id)
                    .count();
            if(already- chancePerUser >=0){
                return new MessageChainBuilder().append(new QuoteReply(messageChain)).append("\n").append("本日出刀机会已用完").build();
            }
        }
        List<String> collect = currentUser.stream().map(String::valueOf).map(Long::parseLong).filter(id -> sender.getId() != id).map(String::valueOf).collect(Collectors.toList());
        jsonObject.replace("currentUser",collect);
        saveFile(subject, jsonObject);
        return new MessageChainBuilder()
                .append(new QuoteReply(messageChain))
                .append("撤回出刀/合刀申请成功")
                .build();
    }

    @NotNull
    private static MessageChain recall(Group subject, JSONObject jsonObject, JSONArray history, JSONArray stageNames) {
        Object remove = history.remove(history.size() - 1);
        JSONObject jsonObject1 = JSONUtil.parseObj(remove);
        MessageChainBuilder singleMessages = new MessageChainBuilder();
        saveFile(subject, jsonObject);
        int nowStage = Integer.parseInt(jsonObject1.getStr("nowStage"));
        singleMessages
                .append("撤回了以下出刀")
                .append("\n  阶段：").append(stageNames.size()>nowStage? stageNames.get(nowStage).toString():String.valueOf(nowStage+1))
                .append("\n  时间:").append(jsonObject1.getStr("time"))
                .append("\n  出刀:").append(jsonObject1.getStr("reporter"))
                .append("\n  伤害:").append(jsonObject1.getStr("thisDamage"));
        return singleMessages.build();
    }

    @NotNull
    private static MessageChain getNowBossHp(Member sender, MessageChain messageChain, JSONArray history, JSONArray hps, JSONArray stageNames, Integer chancePerUser) {
        int nowStage = 0;
        long totalDamage = 0;
        if(history.size()>0) {
            String newest = history.getStr(history.size() - 1);
            JSONObject before = JSONUtil.parseObj(newest);
            nowStage = before.getInt("nextStage");
            totalDamage = before.getLong("totalDamage");
        }
        long remain = hps.getLong(nowStage)-totalDamage;

        return new MessageChainBuilder()
                .append("阶段: ").append(stageNames.size() > nowStage ? stageNames.get(nowStage).toString() : String.valueOf(nowStage + 1))
                .append("\nBoss剩余血量: ").append(String.valueOf(remain)).append("/").append(String.valueOf(hps.getLong(nowStage))).build();
    }

    private InputStream saveToExcel(String name,JSONArray his,JSONArray stageNames){
        List<JSONObject> collect = his.stream().map(o -> {
            JSONObject jsonObject = JSONUtil.parseObj(o.toString());
            Integer nowStage = jsonObject.getInt("nowStage");
            jsonObject.putOnce("nowStageName", stageNames.size() > nowStage ? stageNames.get(nowStage).toString() : String.valueOf(nowStage + 1));
            return jsonObject;
        }).collect(Collectors.toList());
        return ExcelUtils.generateWBExcel(name, collect);
    }


}

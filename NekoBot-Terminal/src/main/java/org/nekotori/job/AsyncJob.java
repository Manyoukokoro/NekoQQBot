package org.nekotori.job;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.internal.deps.io.ktor.http.ContentType;
import net.mamoe.mirai.message.data.LightApp;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.message.data.SingleMessage;
import org.nekotori.BotSimulator;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.common.InnerConstants;
import org.nekotori.dao.ChatGroupMapper;
import org.nekotori.dao.GroupSyncMapper;
import org.nekotori.entity.ChatGroupDo;
import org.nekotori.entity.CustomResponse;
import org.nekotori.entity.GroupSyncDo;
import org.nekotori.handler.CustomCommandHandler;
import org.nekotori.handler.GlobalAtMeHandler;
import org.nekotori.handler.GlobalCommandHandler;
import org.nekotori.service.GroupService;
import org.nekotori.utils.ChromeUtils;
import org.nekotori.utils.JsonUtils;
import org.nekotori.utils.LittleUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AsyncJob {

    public static Map<Long, List<CustomResponse>> localCache;

    public static List<Long> noRepeatGroup = new ArrayList<>();

    public static long nowDispatchGroup = 0L;

    @Resource
    private GlobalCommandHandler globalCommandHandler;

    @Resource
    private GlobalAtMeHandler globalAtMeHandler;

    @Resource
    private GroupService groupService;

    @Resource
    private ChatGroupMapper chatGroupMapper;

    @Resource
    private ChainMessageSelector chainMessageSelector;

    @Resource
    private GroupSyncMapper groupSyncMapper;


    @Bean(name = "groupCusRes")
    public Map<Long, List<CustomResponse>> getGroupCustomResponse() {
        localCache = groupService.getGroupCustomResponses();
        List<ChatGroupDo> chatGroupDos = chatGroupMapper.selectList(Wrappers.<ChatGroupDo>lambdaQuery());
        if (!ObjectUtils.isEmpty(chatGroupDos)) {
            chatGroupDos.forEach(
                    chatGroupDo -> {
                        if (chatGroupDo.getIsBlock()) {
                            noRepeatGroup.add(chatGroupDo.getGroupId());
                        }
                    }
            );
        }
        return localCache;
    }

    @Async
    public void handleCustomResponse(GroupMessageEvent groupMessageEvent) {
        List<CustomResponse> customResponses = localCache.get(groupMessageEvent.getGroup().getId());
        String message = groupMessageEvent.getMessage().contentToString();
        List<String> probResponses = new ArrayList<>();
        if (CollectionUtils.isEmpty(customResponses)) {
            return;
        }
        customResponses.stream().filter(customResponse -> {
            if (ObjectUtils.isEmpty(customResponse) || customResponse.getWay() == null) {
                return false;
            }
            CustomResponse.WAY way = customResponse.getWay();
            switch (way) {
                case BEGIN:
                    return message.startsWith(customResponse.getKeyWord());
                case CONTAINS:
                    return message.contains(customResponse.getKeyWord());
                case END:
                    return message.endsWith(customResponse.getKeyWord());
                case REGEX:
                    return Pattern.compile(customResponse.getKeyWord()).matcher(message).matches();
                case FULL_CONTEXT:
                    return message.equals(customResponse.getKeyWord());
                case FORMAT:
                    return CustomResponse.canFormat(message, customResponse.getKeyWord());
                default:
                    return false;
            }
        }).forEach(v ->{
            if (CustomResponse.WAY.FORMAT.equals(v.getWay())) {
                probResponses.add(CustomResponse.genResponse(v.getKeyWord(),message,v.getResponse()));
            }else {
                probResponses.add(v.getResponse());
            }
        }
        );
        if (probResponses.size() > 0) {
            int i = new Random().nextInt(probResponses.size());
            String finalResponse = probResponses.get(i);
            groupMessageEvent.getSubject().sendMessage(finalResponse);
//            MessageChain singleMessages = resolveFinalResponse(finalResponse);
//            groupMessageEvent.getSubject().sendMessage(singleMessages);
        }

    }

    private static MessageChain resolveFinalResponse(String finalResponse) {
        MessageChain singleMessages = MessageChain.deserializeFromJsonString(finalResponse);
        MessageChainBuilder builder = new MessageChainBuilder();
        for (int i = 0; i < singleMessages.size(); i++) {
            Pattern compile = Pattern.compile("\\$\\{.*}");
            Matcher matcher = compile.matcher(singleMessages.get(i).contentToString());
            if (!matcher.find()) {
                builder.append(singleMessages.get(i));
            }
            String groups = matcher.group();
            String[] split = groups.split(",");
            for (String group : split) {
                String replace = group.replace("${", "").replace("}", "");
                try {
                    Process proc = Runtime.getRuntime().exec(replace);
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));// read the output from the command
                    StringBuilder input = new StringBuilder();
                    String s = null;
                    while ((s = stdInput.readLine()) != null) {
                        input.append(s).append("\n");
                    }
                    replace = input.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                builder.append(new PlainText(singleMessages.get(i).contentToString().replace(group, replace)));
            }

        }

        return builder.build();
    }

    public static void main(String[] args) {
        MessageChain hello = new MessageChainBuilder().append(new PlainText("23123123${ls ..}\n,${ls ../..}")).build();
        String s = MessageChain.serializeToJsonString(hello);
        MessageChain singleMessages = resolveFinalResponse(s);
        System.out.println(singleMessages.contentToString());
    }

    public void cal(GroupMessageEvent groupMessageEvent) {
        String s = groupMessageEvent.getMessage().contentToString();
        if(s.matches("[\\d()\\+\\-\\*/^]+[\\+\\-\\*/^][\\d()\\+\\-\\*/^]+")){
            try {
                groupMessageEvent.getSubject().sendMessage(LittleUtils.resolve(s));
            }catch (Exception ignore){
            }
        }

    }

    public void syncMessage(GroupMessageEvent groupMessageEvent){
        Group subject = groupMessageEvent.getSubject();
        MessageChain message = groupMessageEvent.getMessage();
        List<GroupSyncDo> groupSyncDos = groupSyncMapper.selectList(Wrappers.<GroupSyncDo>lambdaQuery().eq(GroupSyncDo::getSourceGroupId, subject.getId()));
        if(!CollectionUtils.isEmpty(groupSyncDos)){
            List<String> targets = groupSyncDos.stream().flatMap(target -> {
                String targetGroups = target.getTargetGroups();
                if (!StringUtils.hasLength(targetGroups)) {
                    return Stream.empty();
                }
                String[] split = targetGroups.split("#");
                return Stream.of(split);
            }).filter(StringUtils::hasLength).collect(Collectors.toList());
            targets.forEach(target->{
                Group group = BotSimulator.getBot().getGroup(Long.parseLong(target));
                message.add(new PlainText("\n------来自<<"+subject.getName()+">>的 "+groupMessageEvent.getSender().getNick()));
                if(group!=null){
                    group.sendMessage(message);
                }
            });
        }
    }

    @Async
    public void urlScreenshot(GroupMessageEvent groupMessageEvent){
        Optional<LightApp> any = groupMessageEvent.getMessage().stream().filter(mes -> mes instanceof LightApp).map(mes -> (LightApp) mes).findAny();
        try {
            any.ifPresent(app -> {
                String content = app.getContent();
                JSONObject jsonObject = JSONUtil.parseObj(content);
                JSONObject meta = jsonObject.getJSONObject("meta");
                JSONObject detail_1 = meta.getJSONObject("detail_1");
                String title = detail_1.getStr("title");
                String url = detail_1.getStr("url");
                String qqdocurl = detail_1.getStr("qqdocurl");
                MessageChainBuilder builder = new MessageChainBuilder();
                MessageChain build = builder.append(new QuoteReply(groupMessageEvent.getMessage()))
                        .append("小程序信息如下\n")
                        .append("标题： ")
                        .append(title)
                        .append("\n")
                        .append("链接： ")
                        .append(url)
                        .append("\n")
                        .append("原始链接： ")
                        .append(qqdocurl).build();
                groupMessageEvent.getSubject().sendMessage(build);
            });
        }catch (Exception ignore){
        }
        String s = groupMessageEvent.getMessage().contentToString();
        if(ChromeUtils.isUrl(s)){
            InputStream screenShotStream = ChromeUtils.getScreenShotStream(s);
            Group subject = groupMessageEvent.getSubject();
            subject.sendMessage(new MessageChainBuilder().append(Contact.uploadImage(subject,screenShotStream)).build());
        }
    }

    @Async
    public void handleCommand(GroupMessageEvent groupMessageEvent) {
        globalCommandHandler.handle(groupMessageEvent);
    }

    @Async
    public void handleAtMe(GroupMessageEvent groupMessageEvent) {
        globalAtMeHandler.handle(groupMessageEvent);
    }

    public void messageSelect(GroupMessageEvent groupMessageEvent) {
        chainMessageSelector.selectMessage(groupMessageEvent);
    }

    @Async
    public void doRecord(GroupMessageEvent groupMessageEvent) {
        groupService.saveHistory(groupMessageEvent);
    }

    public void dispatchMessage(GroupMessageEvent groupMessageEvent){
        if(nowDispatchGroup == groupMessageEvent.getSubject().getId()){
            Friend friend = BotSimulator.getBot().getFriendOrFail(InnerConstants.admin);
            String name = "\n----[" + groupMessageEvent.getSender().getNick() + "](" + groupMessageEvent.getSender().getId() + ")";
            MessageChain message = groupMessageEvent.getMessage();
            MessageChainBuilder singleMessages = new MessageChainBuilder();
            singleMessages.addAll(message);
            singleMessages.append(new PlainText(name));
            friend.sendMessage(singleMessages.build());
        }
    }

}

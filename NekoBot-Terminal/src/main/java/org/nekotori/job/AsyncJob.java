package org.nekotori.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.chain.ChainMessageSelector;
import org.nekotori.dao.ChatGroupMapper;
import org.nekotori.dao.ChatMemberMapper;
import org.nekotori.entity.ChatGroupDo;
import org.nekotori.entity.CustomResponse;
import org.nekotori.handler.CustomCommandHandler;
import org.nekotori.handler.GlobalAtMeHandler;
import org.nekotori.handler.GlobalCommandHandler;
import org.nekotori.service.GroupService;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AsyncJob {

    public static Map<Long, List<CustomResponse>> localCache;

    public static List<Long> noRepeatGroup = new ArrayList<>();

    @Resource
    private GlobalCommandHandler globalCommandHandler;

    @Resource
    private CustomCommandHandler customCommandHandler;

    @Resource
    private GlobalAtMeHandler globalAtMeHandler;

    @Resource
    private GroupService groupService;

    @Resource
    private ChatMemberMapper chatMemberMapper;

    @Resource
    private ChatGroupMapper chatGroupMapper;

    @Resource
    private ChainMessageSelector chainMessageSelector;


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
                default:
                    return false;
            }
        }).forEach(v ->
                probResponses.add(v.getResponse())
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

    public void repeat(GroupMessageEvent groupMessageEvent) {
        if (noRepeatGroup.contains(groupMessageEvent.getGroup().getId())) {
            return;
        }
        int randomInt = new Random().nextInt(100);
        if (randomInt < 2) {
            groupMessageEvent.getSubject().sendMessage(groupMessageEvent.getMessage());
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

}

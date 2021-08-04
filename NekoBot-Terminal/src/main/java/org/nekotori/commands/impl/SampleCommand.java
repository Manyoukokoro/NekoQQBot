package org.nekotori.commands.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.nekotori.annotations.Command;
import org.nekotori.commands.PrivilegeGroupCommand;
import org.nekotori.entity.CommandAttr;
import org.nekotori.utils.CommandUtils;

import java.util.List;

/**
 * @author: JayDeng
 * @date: 04/08/2021 11:28
 * @description:
 * @version: {@link }
 */

/**
 * @Command注解，打上此注解后，spring容器会自动管理此指令的实现
 * value为指令名数组
 */
@Command({"测试命令"})
public class SampleCommand extends PrivilegeGroupCommand {


    /**
     * 重写的execute方法，决定指令的输入输出
     * @param sender 发起指令的人
     * @param messageChain 带有指令的那条消息
     * @param subject 发指令人所在的群
     * @return
     */
    @Override
    public MessageChain execute(Member sender, MessageChain messageChain, Group subject) {
        /**
         * 标准方法，解析指令数据
         */
        final CommandAttr commandAttr = CommandUtils.resolveCommand(messageChain.contentToString());
        /**
         * 指令头：！ - #
         */
        final String header = commandAttr.getHeader();
        /**
         * 指令名字
         */
        final String command = commandAttr.getCommand();
        /**
         * 指令参数，主要用这个进行处理
         */
        final List<String> param = commandAttr.getParam();

        /**
         * 简单响应消息的demo代码
         */
        //1.消息构建器
        MessageChainBuilder singleMessages = new MessageChainBuilder();
        //2.向消息构建器中加入消息（顺序）
        singleMessages.append(new PlainText("demo"));
        singleMessages.append(new At(sender.getId()));
        //3.构建消息
        final MessageChain build = singleMessages.build();
        /**
         * 发送消息至群
         */
        return build;
    }
}
    
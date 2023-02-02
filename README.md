# NekoQQBot

#### Description

A Mirai based chat bot for IM software Tencent QQ.

#### architecture

1. SpringBoot
2. Mirai-Core-Jvm
3. Mybatis-Plus + Sqlite

#### Introduction

1.The entry point of this project is same as normal Springboot program [NekoBotApplication](https://github.com/Manyoukokoro/NekoQQBot/blob/dev-1.0.5/NekoBot-Terminal/src/main/java/org/nekotori/NekoBotApplication.java)  
2. When the Spring process start, the botRunner which implements ApplicationRunner will run a BotSimulator at the same time. All configurations of this bot will be completed when the botSimulator is running, including eventListenner registry and bot account login. While the bot is running, eventListener intercept all of the chat messages and dispatch them to the relevant handlers(chainMessageHandler,commandHandler,etc.).  
3. How to implement a QQ bot commmandHandler:  first of all, a annotation named [@IsCommand](https://github.com/Manyoukokoro/NekoQQBot/blob/dev-1.0.5/NekoBot-Terminal/src/main/java/org/nekotori/annotations/IsCommand.java) wich extend the basic commponent annotation of Springboot is defined, so that all classes marked by this annotation will be managed in the spring container as instances, which means you do not need to maintain commands manually. Otherwise, all commands should extends three kinds of abstract commands (PrivilegeGroupCommand, ManagerGroupCommand,NoAuthGroupCommand) in which the accessibility of Commmand id predefined. PrivilegeGroupCommand means group members can use it only if it's active by the command management command; ManagerGroupCommand means only group managers and owner can use it; NoAuthGroupCommand can be used by all group members.  
4. The code below is a sample of PrivilegeGroupCommmand:  
```java
/**
 * @author: Nekotori
 * @date: 04/08/2021 11:28
 * @description:
 * @version: {@link }
 */

/**
 * @IsCommand annotation, classes marked by this will be auto registerd when the application started.
 * name[]: trigger words of this command
 * discription: a short description of this command, which will be used in the command <help>. 
 */
@IsCommand(name={"ping"},discription = "ping it")
public class SampleCommand extends PrivilegeGroupCommand {
    

    /**
     * this method is override from Command Interface. determine the input and the output of a command.
     * @param sender: who start this command
     * @param messageChain: the whole message of command
     * @param subject: the group of this commnad
     * @param commandAttr: command details
     * @return: response of command
     */
    @Override
    public MessageChain execute(Member sender, Group subject, CommandAttr commandAttr, MessageChain messageChain) {
        /**
         * command header：！ - #
         */
        final String header = commandAttr.getHeader();
        /**
         * command name (defined in the @IsCommnad annotation)
         */
        final String command = commandAttr.getCommand();
        /**
         * the param follows the command
         */
        final List<String> param = commandAttr.getParam();

        /**
         * simply response pong to the subject group
         */
        //1.message builder
        MessageChainBuilder singleMessages = new MessageChainBuilder();
        //2.append messages to builder
        singleMessages.append(new PlainText("demo"));
        singleMessages.append(new At(sender.getId()));
        final MessageChain build = singleMessages.build();
        /**
         * send response
         */
        return build;
    }
}
    
```
5. [already defined commands](https://github.com/Manyoukokoro/NekoQQBot/tree/dev-1.0.5/NekoBot-Terminal/src/main/java/org/nekotori/commands/impl)  

#### history
1. add sample command implement  
2. add chain command support

#### deploy

1. enviroment: jdk11

# NekoQQBot

#### 描述
一个基于Springboot和Mirai框架的QQbot

#### 软件架构
1. SpringBoot
2. Mirai-Core
3. Mirai-Api-Jvm


#### 引导

1.  控制台程序总入口为Springboot项目入口[NekoBotApplication](https://gitee.com/nekotori/neko-qqbot/blob/master/NekoBot-Terminal/src/main/java/org/nekotori/NekoBotApplication.java) 
2.  Spring项目启动时，会同时按照配置文件创建bot登录QQ，并注册event包下的所有事件(net.mamoe.mirai.event),事件类实现SimpleListenerHost
3.  mirai事件可以监听不同的QQ动作，并触发相应的动作，比如Command
4.  如何处理Command: 在command事件中，调用了GlobalCommandHandler对象，此对象中注册了所有实现了Command接口的类(需要实现类用@Command注解被Spring容器管理),command事件被触发后，会遍历注册的Command类测试是否有符合条件的Command需要被执行。
5.  指令实现类示例:
```java
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
    
```
#### 历史
1. add sentence generator
2. add sample command implement


#### 贡献

1.  Fork the repository
2.  Create Feat_xxx branch
3.  Commit your code
4.  Create Pull Request


#### Gitee Feature

1.  You can use Readme\_XXX.md to support different languages, such as Readme\_en.md, Readme\_zh.md
2.  Gitee blog [blog.gitee.com](https://blog.gitee.com)
3.  Explore open source project [https://gitee.com/explore](https://gitee.com/explore)
4.  The most valuable open source project [GVP](https://gitee.com/gvp)
5.  The manual of Gitee [https://gitee.com/help](https://gitee.com/help)
6.  The most popular members  [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
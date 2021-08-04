# NekoQQBot

#### Description
A QQBot application based on mirai and springboot

#### Software Architecture
1. SpringBoot
2. Mirai-Core
3. Mirai-Api-Jvm


#### Instructions

1.  xxxx
2.  xxxx
3.  xxxx

#### History
1. add sentence generator
2. add sample command implement
```java
/**
 * @author: JayDeng
 * @date: 04/08/2021 11:28
 * @description:
 * @version: {@link }
 */

/**
 * @Command注解，打上此注解后，spring容器会自动管理此指令的实现
 */
@Command
public class DanbooruCommand extends PrivilegeGroupCommand {

    /**
     * 构造函数，决定触发词
     */
    public DanbooruCommand() {
        super("测试指令");
    }

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

#### Contribution

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
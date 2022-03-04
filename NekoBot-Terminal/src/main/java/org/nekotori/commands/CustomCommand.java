package org.nekotori.commands;

import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: JayDeng
 * @date: 2022/2/28 下午3:39
 * @description: CustomCommand
 * @version: {@link }
 */
public class CustomCommand {

    public enum AUTHORITY{
        EVERYONE,ADMIN,OWNER;
        @Override
        public String toString(){
            return this.name();
        }
    }

    public enum STAGES{
        RECORD,CONDITION,HANDLE;

        @Override
        public String toString(){
            return this.name();
        }
    }

    public enum RECORD{
        TARGET_NAME("S",new char[]{'c','e'}),
        TARGET_ID("N",new char[]{'g','l','e'}),
        TARGET_MESSAGE("S",new char[]{'c','e'}),
        EVENT_TIME("T",new char[]{'a','b'}),
        GROUP_ID("N",new char[]{'g','l','e'}),
        GROUP_NAME("S",new char[]{'c','e'});
        private String type;
        private char[] operate;

        RECORD(String s, char[] chars) {
            this.type = s;
            this.operate = chars;
        }

        @Override
        public String toString(){
            return this.name();
        }
    }

    public enum HANDLE{
        MEMBER_KICK_OUT,MEMBER_MUTE,MESSAGE,DICE;

        @Override
        public String toString(){
            return this.name();
        }
    }

    private AUTHORITY authority;

    private List<RECORD> records;

    private List<String> recordedInfo;

    private HANDLE handle;

    public boolean checkAuthority(GroupMessageEvent event){
        if(AUTHORITY.EVERYONE.equals(this.authority)){
            return true;
        }
        MemberPermission permission = event.getSender().getPermission();
        if(AUTHORITY.ADMIN.equals(this.authority)){
            return permission.equals(MemberPermission.ADMINISTRATOR)
                    || permission.equals(MemberPermission.OWNER);
        }
        return permission.equals(MemberPermission.OWNER);
    }

    public void recordInfo(GroupMessageEvent event){
        this.recordedInfo = records.stream().map(record -> {
            String info = "";
            switch (record) {
                case GROUP_ID:
                    info = String.valueOf(event.getGroup().getId());
                    break;
                case TARGET_ID:
                    info = String.valueOf(event.getSender().getId());
                    break;
                case EVENT_TIME:
                    info = String.valueOf(event.getTime());
                    break;
                case GROUP_NAME:
                    info = event.getGroup().getName();
                    break;
                case TARGET_NAME:
                    info = event.getSender().getNick();
                    break;
                case TARGET_MESSAGE:
                    info = event.getMessage().serializeToMiraiCode();
                    break;
                default:
                    break;
            }
            return info;
        }).collect(Collectors.toList());
    }

    public void handleMessage(GroupMessageEvent event){

    }
}

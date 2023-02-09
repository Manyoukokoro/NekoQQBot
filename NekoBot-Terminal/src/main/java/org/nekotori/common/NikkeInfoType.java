package org.nekotori.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum NikkeInfoType {
    GENERAL("角色"),SKILL("技能"),PANEL("面板");

    @Getter
    private final String info;

}

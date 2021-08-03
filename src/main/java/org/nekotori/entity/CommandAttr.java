package org.nekotori.entity;

import lombok.Data;

import java.util.List;

/**
 * @author: JayDeng
 * @date: 03/08/2021 16:16
 * @description:
 * @version: {@link }
 */

@Data
public class CommandAttr {

    private String header;

    private String command;

    private List<String> param;
}
    
package org.nekotori.entity;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * @author: JayDeng
 * @date: 03/08/2021 09:19
 * @description:
 * @version: {@link }
 */
@Data
public class LoliconData {

    private int pid;

    private int p;

    private int uid;

    private String title;

    private String author;

    private JSONObject urls;

    private boolean R18;

    private int width;

    private int height;

    private List<String> tags;
}

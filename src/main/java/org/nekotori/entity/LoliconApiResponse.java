package org.nekotori.entity;

import lombok.Data;

import java.util.List;

/**
 * @author: JayDeng
 * @date: 03/08/2021 09:19
 * @description:
 * @version: {@link }
 */
@Data
public class LoliconApiResponse {
    private Integer code;
    private String msg;
    private Integer quota;
    private Integer quota_min_ttl;
    private Integer count;
    private List<LoliconData> data;
 }

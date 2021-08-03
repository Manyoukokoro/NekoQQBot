package org.nekotori.entity;

import lombok.Data;

import java.util.List;

/**
 * @author: JayDeng
 * @date: 03/08/2021 16:48
 * @description:
 * @version: {@link }
 */

@Data
public class WeatherResponse {

    private String code;

    private List<WeatherDailyData> daily;
}
    
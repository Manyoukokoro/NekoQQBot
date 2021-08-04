package org.nekotori.entity;

import lombok.Data;

/**
 * @author: JayDeng
 * @date: 03/08/2021 16:49
 * @description:
 * @version: {@link }
 */

@Data
public class WeatherDailyData {

    private String fxDate;

    private String textDay;

    private String tempMax;

    private String tempMin;

    private String precip;
}
    
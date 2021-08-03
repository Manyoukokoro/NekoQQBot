package org.nekotori.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author: JayDeng
 * @date: 03/08/2021 16:40
 * @description:
 * @version: {@link }
 */
@Data
public class WeatherLocationData {

    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private String id;
}
    
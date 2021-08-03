package org.nekotori.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: JayDeng
 * @date: 03/08/2021 16:39
 * @description:
 * @version: {@link }
 */
@Data
public class WeatherLocationResponse {

    @JsonProperty("code")
    private String code;

    @JsonProperty("location")
    private List<WeatherLocationData> location;
}
    
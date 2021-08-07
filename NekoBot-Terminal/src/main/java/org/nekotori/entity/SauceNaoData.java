package org.nekotori.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SauceNaoData {

    private String thumbnailUrl;

    private String similarity;

    private List<String> extUrls;

    private String tittle;
}

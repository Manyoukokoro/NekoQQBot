package org.nekotori.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class WechatController {

    @GetMapping
    public String validate(@RequestParam(value = "echostr")String echoStr){
        return echoStr;
    }
}

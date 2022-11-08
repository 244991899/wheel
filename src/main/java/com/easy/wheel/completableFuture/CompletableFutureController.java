package com.easy.wheel.completableFuture;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description
 * @Author Mr.Yuxd
 * @Date 2022/11/8
 * @Version 1.0
 */
@Controller
@Slf4j
@RequestMapping("/completable")
public class CompletableFutureController {

    @Autowired
    private QueryService queryService;

    @RequestMapping("get")
    @ResponseBody
    public Map<String,Object> get(){
        return queryService.queryGoodsInfo();
    }

}
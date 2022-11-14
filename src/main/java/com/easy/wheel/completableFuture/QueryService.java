package com.easy.wheel.completableFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description 查询业务接口，基于异步线程查询 CompletableFuture 接口
 * 这里简单进行了业务上的实现（ps：业务可能有些不符合实际，这里只是例子，简单好用至上）
 * CompletableFuture 默认使用的是内部线程池，可自定义线程池使用
 * CompletableFuture.supplyAsync 创建异步调用任务
 * future.thenAccept  执行完成后通知
 * CompletableFuture.allOf 编排异步任务 - 全部成功后通知thenAccept， 类似创建一个异步监听
 * future.join()  主线程会等待
 * 老爷和两个管家
 * 老爷想喝茶
 * 让两个管家协助
 * 一个烧水
 * 一个选茶叶
 * 自己去上厕所
 * 回来就可以喝茶
 * 岂不乐哉？？？
 * @Author Mr.Yuxd
 * @Date 2022/11/8
 * @Version 1.0
 */
@Service
@Slf4j
public class QueryService {

    @Autowired
    private TeaService teaService;

    public Map<String,Object> queryGoodsInfo(){
        Map<String, Object> res = new HashMap<>();

        //异步执行任务 烧水
        log.info("老李,去帮我把水烧一下，一会我要喝茶");
        CompletableFuture<Integer> numFuture = CompletableFuture.supplyAsync(() -> teaService.heatUpWater());

        numFuture.thenAccept((result) -> {
            log.info("老爷水烧好了,花了{}分钟",result);
            res.put("烧水时间", result);
        }).exceptionally((e) ->{
            log.error("锅烧炸了: {}", e.getMessage(), e);
            res.put("烧水时间", null);
            return null;
        });


        //异步执行任务 挑选茶叶
        log.info("小蓉,去帮我挑选一下茶叶");
        CompletableFuture<Double> priceFuture = CompletableFuture.supplyAsync(() -> teaService.getTeas());
        priceFuture.thenAccept((result) ->{
            log.info("老爷,茶叶挑选好了,先试试{}克的碧螺春",result);
            res.put("挑选茶叶时间", result);
        }).exceptionally((e) ->{
            log.error("茶被老鼠吃了: {}", e.getMessage(), e);
            res.put("挑选茶叶时间", null);
            return null;
        });

        //编排任务allOf  全部等待完成
        CompletableFuture<Void> allQuery = CompletableFuture
            .allOf(numFuture, priceFuture);
        //执行完成后回调
        CompletableFuture<Map<String, Object>> future = allQuery.thenApply((result) -> {
            log.info("------------------ 老爷不放心，看一下监控，发现都弄好了 ------------------ ");
            return res;
        }).exceptionally((e) -> {
            log.error(e.getMessage(), e);
            return null;
        });
        log.info("我去上个厕所，弄好了给我发消息，一会回来喝茶");
        // --这里主线程会等待异步线程全部执行完毕
        // --去掉future.join() 主线程不等待
        future.join();
        log.info("开始喝茶...");
        return res;
    }

}
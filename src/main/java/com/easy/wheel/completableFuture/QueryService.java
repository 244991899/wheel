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
 * @Author Mr.Yuxd
 * @Date 2022/11/8
 * @Version 1.0
 */
@Service
@Slf4j
public class QueryService {

    @Autowired
    private GoodService goodService;

    public Map<String,Object> queryGoodsInfo(){
        Map<String, Object> res = new HashMap<>();

        //异步执行任务 查询商品数量
        CompletableFuture<Integer> numFuture = CompletableFuture.supplyAsync(() -> goodService.getGoodsNum());

        numFuture.thenAccept((result) -> {
            log.info("查询numFuture: {}",result);
            res.put("goodsNum", result);
        }).exceptionally((e) ->{
            log.error("查询异常: {}", e.getMessage(), e);
            res.put("goodsNum", null);
            return null;
        });


        //异步执行任务 查询商品价格
        CompletableFuture<Double> priceFuture = CompletableFuture.supplyAsync(() -> goodService.getGoodsPrice());
        priceFuture.thenAccept((result) ->{
            log.info("查询priceFuture: {}",result);
            res.put("goodsPrice", result);
        }).exceptionally((e) ->{
            log.error("查询异常: {}", e.getMessage(), e);
            res.put("goodsPrice", null);
            return null;
        });

        //编排任务allOf  全部等待完成
        CompletableFuture<Void> allQuery = CompletableFuture
            .allOf(numFuture, priceFuture);
        //执行完成后回调
        CompletableFuture<Map<String, Object>> future = allQuery.thenApply((result) -> {
            log.info("------------------ 全部查询都完成 ------------------ ");
            return res;
        }).exceptionally((e) -> {
            log.error(e.getMessage(), e);
            return null;
        });

        // --这里主线程会等待异步线程全部执行完毕
        // --去掉future.join() 主线程不等待
        future.join();
        log.info("主线程执行--完成");
        return res;
    }

}
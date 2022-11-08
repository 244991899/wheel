package com.easy.wheel.completableFuture;

import org.springframework.stereotype.Service;

/**
 * @Description 商品查询类 (业务模拟)
 * @Author Mr.Yuxd
 * @Date 2022/11/8
 * @Version 1.0
 */
@Service
public class GoodService {

    /**
     * 获取商品数量
     * @return 返回数量
     * @throws Exception 异常
     */
    public Integer getGoodsNum(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 20;
    }

    /**
     * 获取商品价格
     * @return 返回价格
     * @throws Exception 异常
     */
    public Double getGoodsPrice(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 20D;
    }
}
package com.easy.wheel.completableFuture;

import org.springframework.stereotype.Service;

/**
 * @Description 泡茶相关类 (业务模拟)
 * @Author Mr.Yuxd
 * @Date 2022/11/8
 * @Version 1.0
 */
@Service
public class TeaService {

    /**
     * 烧茶水
     * @return 返回分钟 int
     * @throws Exception 异常
     */
    public Integer heatUpWater(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 20;
    }

    /**
     * 挑选茶
     * @return 返回价格
     * @throws Exception 异常
     */
    public Double getTeas(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 20D;
    }
}
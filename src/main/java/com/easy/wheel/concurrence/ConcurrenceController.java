package com.easy.wheel.concurrence;

import java.util.Queue;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description  相关业务介绍：BusinessThread  主要业务处理线程
 * ConcurrenceThreadPoolManager  线程池调度管理器
 * 业务线程池：多线程并发处理业务,当线程池满负载的时候,handler 处理，进入外部缓存队列
 * 任务的队列：线程池的任务队列，不易设置过大，因为只有超过core pool size个线程后就会放入缓冲队列，缓冲队列满了之后才会创建新的线程
 * 调度线程池：每秒查看业务线程池处理情况，是否有空闲队列（发挥多线程的威力），如果有空闲队列，加入到业务线程池中
 * msgQueue: 外部缓存队列
 * @Author Mr.Yxd
 * @Date 2022/5/31
 * @Version 1.0
 */
@RestController
@RequestMapping("/con")
public class ConcurrenceController {

    @Autowired
    ConcurrenceThreadPoolManager threadPoolManager;

    /**
     * 测试模拟下单请求 入口
     */
    @RequestMapping("/start")
    public String start() {
        //模拟的随机数
        String orderNo = System.currentTimeMillis() + UUID.randomUUID().toString();

        threadPoolManager.addOrders(orderNo);

        return "Test ThreadPoolExecutor start";
    }

    /**
     * 停止服务
     */
    @GetMapping("/end")
    public String end(@PathVariable Long id) {

        threadPoolManager.shutdown();

        Queue q = threadPoolManager.getMsgQueue();
        System.out.println("关闭了线程服务，还有未处理的信息条数：" + q.size());
        return "Test ThreadPoolExecutor start";
    }
}
package com.easy.wheel.scheduler;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author Mr.Yuxd
 * @Date 2022/11/15
 * @Version 1.0
 */
@Slf4j
public class RunSameThing implements Runnable{

    @Override
    public void run() {
        log.info("===我执行了===");
    }
}
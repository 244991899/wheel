package com.easy.wheel.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author Mr.Yuxd
 * @Date 2022/11/15
 * @Version 1.0
 */
@RestController
@RequestMapping("/task")
public class TestTaskController {

    @Autowired
    private CommonTask commonTask;

    @RequestMapping("start")
    public void startTask(){
        commonTask.startTask();
    }

    @RequestMapping("end")
    public void endTask(){
        commonTask.stop();
    }

}
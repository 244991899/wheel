package com.easy.wheel.concurrence;

/**
 * @Description
 * @Author Mr.Yxd
 * @Date 2022/5/31
 * @Version 1.0
 */
public class BusinessThread implements Runnable{
    private String acceptStr;

    public BusinessThread(String acceptStr) {
        this.acceptStr = acceptStr;
    }

    public String getAcceptStr() {
        return acceptStr;
    }

    public void setAcceptStr(String acceptStr) {
        this.acceptStr = acceptStr;
    }

    @Override
    public void run() {
        //业务操作
        //线程阻塞
        try {
            Thread.sleep(10000);
            System.out.println("多线程已经处理订单插入系统，订单号："+acceptStr);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
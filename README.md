# wheel
一个提供解决方案的轮子工程，目前在慢慢积累中，如果你有好的轮子小demo，欢迎你的加入，希望能帮助到大家

---------
* com.easy.wheel   运行项目进行自测，建议使用postman-调用
    * concurrence 多线程高并发处理demo
        * 线程池的合理运用 ThreadPoolExecutor
        * 调度线程池结合，发挥多线程的最大威力
        * 自带测试controller ： ConcurrenceController
    * excel 自定义导出
        * 导出任意想要导出的对象列
        * 50w 数据大概用时10秒左右
        * 自带测试controller ： ExcelController
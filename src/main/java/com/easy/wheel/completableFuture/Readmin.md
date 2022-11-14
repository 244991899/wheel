
#CompletableFuture使用样例

---

# 前言

CompletableFuture是对Future的扩展和增强。CompletableFuture实现了Future接口，并在此基础上进行了丰富的扩展，完美弥补了Future的局限性，同时CompletableFuture实现了对任务编排的能力。借助这项能力，可以轻松地组织不同任务的运行顺序、规则以及方式。从某种程度上说，这项能力是它的核心能力# 系列文章目录

---


# 一、故事
 * 老爷和两个管家
 * 老爷想喝茶
 * 让两个管家协助
 * 一个烧水
 * 一个选茶叶
 * 自己去上厕所
 * 回来就可以喝茶
 * 岂不乐哉？？？

# 二、使用前用到的api

 * CompletableFuture 默认使用的是内部线程池，可自定义线程池使用
 * CompletableFuture.supplyAsync 创建异步调用任务
 * future.thenAccept  执行完成后通知
表示第一个任务执行完成后，执行第二个回调方法任务，会将该任务的执行结果，作为入参，传递到回调方法中，并且回调方法是有返回值的。
* thenRun/thenRunAsync
通俗点讲就是，做完第一个任务后，再做第二个任务,第二个任务也没有返回值
 * CompletableFuture.allOf 编排异步任务 - 全部成功后通知thenAccept， 类似创建一个异步监听
 *  future.join()  主线程会等待
 *  supplyAsync  执行任务，支持返回值。
 *  runAsync  执行任务，没有返回值。

# 三、直接上代码
管家需要做的两件事
```c
@Service
public class TeaService {

    //烧茶水
    public Integer heatUpWater(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 20;
    }

    //挑选茶
    public Double getTeas(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 20D;
    }
}
```
老爷的吩咐

```c
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
```
老爷的任务开始执行

```c
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
```

老爷任务执行结果

```c
[nio-8080-exec-2] c.e.w.completableFuture.QueryService     : 老李,去帮我把水烧一下，一会我要喝茶
[nio-8080-exec-2] c.e.w.completableFuture.QueryService     : 小蓉,去帮我挑选一下茶叶
[nio-8080-exec-2] c.e.w.completableFuture.QueryService     : 我去上个厕所，弄好了给我发消息，一会回来喝茶
[onPool-worker-2] c.e.w.completableFuture.QueryService     : 老爷,茶叶挑选好了,先试试20.0克的碧螺春
[onPool-worker-1] c.e.w.completableFuture.QueryService     : ------------------ 老爷不放心，看一下监控，发现都弄好了 ------------------ 
[onPool-worker-1] c.e.w.completableFuture.QueryService     : 老爷水烧好了,花了20分钟
[nio-8080-exec-2] c.e.w.completableFuture.QueryService     : 开始喝茶...
```


# 总结

 - 不建议使用默认线程池
> CompletableFuture代码中又使用了默认的「ForkJoin线程池」，处理的线程个数是电脑「CPU核数-1」。在大量请求过来的时候，处理逻辑复杂的话，响应会很慢。一般建议使用自定义线程池，优化线程池配置参数。
 - CompletableFuture 功能很多，异步任务编排很灵活，有很多值得学习，有时间可以看看学习下，一定能解决工作中的很多问题
 - 获取返回值
 >join()和get()方法都是用来获取CompletableFuture异步之后的返回值
 >get 会有检查异常，需要手动处理（try  catch）
 >join 方法抛出的是uncheck异常（即RuntimeException),不会强制开发者抛出，处理
 
 * get和join 都是阻塞的方法
>如果使用它来获取异步调用的返回值，需要添加超时时间。
CompletableFuture.get(5, TimeUnit.SECONDS);  
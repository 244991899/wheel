##java poi excel 导出任意对象-任意属性-数据导出
> 一行代码解决导出
> 
> 50w数据测试，导出时间10秒左右
>
> pom 依赖
```
    <!--excel-poi-->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>3.17</version>
    </dependency>
```


ExcelUtils工具类中export方法
```
export(String sheetName,Map<String,String> headMap, List<?> resultList,
        HttpServletResponse response)
```
> 说明：
>
* sheetName ： sheetName 第一个sheet 名称
* headMap ： excel表头 ，需要导出的字段，以及对应的翻译词，表头行
* resultList ： 需要导出的数据对象集合，用户自定义的导出对象vo集合

> 程序拓展
* 可拓展导出文件名称
* 多sheet导出等
* 一些常用导出，可使用easy-poi即可
package com.easy.wheel.poi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @Description excel导出工具类
 * @Author Mr.Yxd
 * @Date 2022/8/15
 * @Version 1.0
 */
public class ExcelUtils {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * @param sheetName sheet名字
     * @param headMap  excel 表头行信息 example  {city:城市,name:姓名,sex:性别}
     * @param resultList 查询出的需要导出的数据
     * @throws Exception
     */
    public static void export(String sheetName,Map<String,String> headMap, List<?> resultList,
        HttpServletResponse response) throws Exception{
        //创建poi导出数据对象
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook();
        //创建sheet页
        SXSSFSheet sheet = sxssfWorkbook.createSheet(sheetName);
        //创建表头
        SXSSFRow headRow = sheet.createRow(0);
        //设置表头信息
        headRow.createCell(0).setCellValue("序号");
        //获取数据库对应字段map表
        int i = 1;
        for (String key : headMap.keySet()) {
            headRow.createCell(i).setCellValue(headMap.get(key));
            i++;
        }
        // 遍历上面数据库查到的数据
        //序号
        int num = 1;
        for (Object ele : resultList) {
            //填充数据
            SXSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
            //序号
            dataRow.createCell(0).setCellValue(num);
            setDataRow(dataRow,ele, headMap);
            num++;
        }
        // 下载导出
        String filename =System.currentTimeMillis() + ""; //文件名
        // 设置头信息
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream");
        //一定要设置成xlsx格式
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder
            .encode(filename + ".xlsx", "UTF-8"));
        //创建一个输出流
        ServletOutputStream outputStream = response.getOutputStream();
        sxssfWorkbook.write(outputStream);
        logger.info("sxssfWorkbook写入数据成功");
        // 关闭
        outputStream.close();
        sxssfWorkbook.close();
    }

    /**
     * 设置导出值
     */
    private static void setDataRow(SXSSFRow dataRow, Object o, Map<String, String> columnsMap) {
        int i = 1;
        for (String column : columnsMap.keySet()) {
            dataRow.createCell(i).setCellValue(getFieldValueByNameSuper(o,column));
            i++;
        }
    }

    /**
     * 根据对象的属性名获取属性值(String) (简单对象)
     * @param o
     * @param fieldName
     * @return
     */
    private static Object getFieldValueByName(Object o, String fieldName) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    /**
     * 根据对象的属性名获取属性值(String)  super(可获取组合对象)
     * @param o
     * @param fieldName
     * @return
     */
    private static String getFieldValueByNameSuper(Object o, String fieldName){
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                //打开私有访问（暴力反射）
                field.setAccessible(true);
                if(!field.toString().contains("java") && field.get(o) != null){
                    String res = getFieldValueByNameSuper(field.get(o),fieldName);
                    if(!StringUtils.isEmpty(res)){
                        return res;
                    }
                }
                String name = field.getName();
                if(fieldName.equals(name)){
                    if(field.get(o)!=null){
                        return field.get(o).toString();
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}
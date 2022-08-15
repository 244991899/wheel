package com.easy.wheel.poi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description excel 导出测试
 * @Author Mr.Yxd
 * @Date 2022/8/15
 * @Version 1.0
 */
@RestController
@RequestMapping("/excel")
public class ExcelController {

    /**
     * 50w 数据导出测试  ：  可以使用postman 的
     * @param response
     * @throws Exception
     */
    @RequestMapping("/export")
    public void test(HttpServletResponse response) throws Exception {
        List<User> resultList = new ArrayList<>();
        for (int i = 0; i < 500000; i++) {
            resultList.add(new User("张三_"+i,"nv-"+i,i,new Student("id_"+i,"你好"+i)));
        }
        Map<String, String> head = new HashMap<>();
        head.put("name","姓名");
        head.put("sex","性别");
        head.put("age","年龄");
        head.put("id","id值");
        head.put("meg","meg值");
        ExcelUtils.export("测试",head,resultList,response);
    }



    @Data
    public class User{
        private String name;
        private String sex;
        private Integer age;
        private Student student;

        public User() {
        }

        public User(String name, String sex, Integer age, Student student) {
            this.name = name;
            this.sex = sex;
            this.age = age;
            this.student = student;
        }
    }

    @Data
    public class Student {
        private String id;
        private String meg;

        public Student() {
        }

        public Student(String id, String meg) {
            this.id = id;
            this.meg = meg;
        }
    }

}
package com.mmall.controller;

import com.google.common.collect.Maps;
import com.mmall.Param.TestVo;
import com.mmall.common.ApplicationContextHelper;
import com.mmall.common.JsonData;
import com.mmall.dao.SysAclMapper;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysAclModule;
import com.mmall.model.SysUser;
import com.mmall.util.BeanValidator;
import com.mmall.util.JsonMapper;
import com.thread.Test.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {

    @RequestMapping("/hello.json")
    @ResponseBody
    public JsonData hello(HttpServletRequest request, HttpServletResponse response) {
        log.info("this is test!");
        return JsonData.success("hello permission");
        //throw new RuntimeException("test exception");
    }


    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData validate(TestVo vo) {
        log.info("this is validate test!");
        try{
            Map<String,String> map = BeanValidator.validateObject(vo);
            if(map != null && map.entrySet().size() > 0){
                for (Map.Entry<String,String> entry : map.entrySet()){
                    log.info("{}->{}",entry.getKey(),entry.getValue());
                }
            }
        }catch (Exception e){
            log.error("has error");
        }

        return JsonData.success("validate");
        //throw new RuntimeException("test exception");
    }

    @RequestMapping("/validate1.json")
    @ResponseBody
    public JsonData validate1(TestVo vo) throws ParamException {
        log.info("this is validate test!");
        SysAclModuleMapper moduleMapper = ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        SysAclModule module = moduleMapper.selectByPrimaryKey(1);
        log.info("/validate1:"+JsonMapper.obj2String(module));
        BeanValidator.check(vo);
        return JsonData.success("validate1 ");
    }

    @Test
    public void Test() {
        //连接本地的 Redis 服务
        Jedis jedis = new Jedis("localhost");
        Map<byte[],byte[]> map = Maps.newHashMap();
        Student user = new Student();
        user.setAge(27);
        user.setId("1");
        user.setName("wuqihui");

        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        System.out.println(user);
        try{
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(user);
            byte[] bytes = baos.toByteArray();
            map.put("user_1".getBytes(),bytes);
            jedis.hmset("javatestMap".getBytes(),map);

            Map<byte[],byte[]> results = jedis.hgetAll("javatestMap".getBytes());
            for(Map.Entry<byte[],byte[]> result: results.entrySet()){
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(result.getValue()));
                Student newuser = (Student)ois.readObject();
                System.out.println("new name :" + newuser.getName());

            }
        }catch (Exception e){
            System.out.println(e);
        }
        jedis.close();
    }

    @Test
    public void Test2(){
        Jedis jedis = new Jedis("localhost");
        Map<byte[],byte[]> results = jedis.hgetAll("javatestMapTest".getBytes());
        System.out.println(results.size());
    }

}

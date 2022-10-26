package com.atguigu.yygh.hosp.mongotest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mongo2")
public class testMongo2 {

    @Autowired
    private UserRepository userRepository;

    //添加
    @GetMapping("create")
    public void createUser() {
        User user = new User();
        user.setAge(20);
        user.setName("张三");
        user.setEmail("3332200@qq.com");

        User user1 = userRepository.save(user);
        System.out.println("user1 = " + user1);
    }

    //查询所有
    @GetMapping("findAll")
    public void findUser() {
        List<User> users = userRepository.findAll();
        users.forEach(System.out::println);
    }

    //根据id查询
    @GetMapping("findId")
    public void getById() {
        User user = userRepository.findById("633b99de898eca6df350efdb").get();
        System.out.println("user = " + user);
    }
    //条件查询
    @GetMapping("findUser")
    public void findUserList() {
        User user = new User();
        user.setName("张三");
        user.setAge(20);
        Example<User> example = Example.of(user);
        List<User> all = userRepository.findAll(example);
        all.forEach(System.out::println);
    }
    //条件模糊查询
    @GetMapping("findLike")
    public void findLike() {
        //创建查询条件
        User user = new User();
        user.setName("3");
        //创建模板构造器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //创建模板
        Example<User> example = Example.of(user,matcher);

        List<User> users = userRepository.findAll(example);
        users.forEach(System.out::println);
    }


    //分页查询
    @GetMapping("findPage")
    public void findUsersPage() {
        //排序对象
        Sort sort = Sort.by(Sort.Direction.DESC,"age");
        //创建分页条件
        //第一页从0开始
        Pageable pageable = PageRequest.of(0,2,sort);
        //创建查询条件
        User user = new User();
        user.setName("3");
        //创建模板构造器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //创建模板
        Example<User> example = Example.of(user,matcher);

        Page<User> page = userRepository.findAll(example, pageable);
        System.out.println("page = " + page);
    }

    //修改
    @GetMapping("update")
    public void updateUser() {
        User user = userRepository.findById("633b99de898eca6df350efdb").get();
        user.setName("张三_1");
        user.setAge(25);
        user.setEmail("883220990@qq.com");
        User save = userRepository.save(user);
        System.out.println(save);
    }

    //删除
    @GetMapping("delete")
    public void delete() {
        userRepository.deleteById("633b99de898eca6df350efdb");
    }

    @GetMapping("testMethod1")
    public void testMethod1() {
        List<User> users = userRepository.getByNameAndAge("张三",20);

        users.forEach(System.out::println);
    }

    @GetMapping("testMethod2")
    public void testMethod2() {
        List<User> users = userRepository.getByNameLike("三");
        users.forEach(System.out::println);
    }


}

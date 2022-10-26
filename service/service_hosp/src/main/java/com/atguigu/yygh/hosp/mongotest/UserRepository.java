package com.atguigu.yygh.hosp.mongotest;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    List<User> getByNameLike(String name);

    List<User> getByNameAndAge(String name, int age);
}

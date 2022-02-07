package com.leqiwl.novel.repository;

import com.leqiwl.novel.domain.entify.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * @author 飞鸟不过江
 */
@Repository
public interface UserRepository extends MongoRepository<User, String>{

    User findByUserName(String userName);

    User findByUserNameAndUserPassword(String userName,String password);

}

package com.xin.sharding.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.xin.sharding.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xin.sharding.entity.User;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class UserServiceImpl implements ExampleService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initEnvironment() throws SQLException {
    	log.info("initEnvironment");
        userRepository.createTableIfNotExists();
        userRepository.truncateTable();
        log.info("initEnvironment createTable OK");
    }
    
    @Override
    public void cleanEnvironment() throws SQLException {
    	log.info("cleanEnvironment");
        userRepository.dropTable();
    }
    
    @Override
    public void processSuccess() throws SQLException {
    	log.info("-------------- Process Success Begin ---------------");
        List<Long> userIds = insertData();
        printData();
        deleteData(userIds);
        printData();
        log.info("-------------- Process Success Finish --------------");
    }
    
    private List<Long> insertData() throws SQLException {
        log.info("---------------------------- Insert Data ----------------------------");
        List<Long> result = new ArrayList<>(10);
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setUserId(i);
            user.setUserName("test_mybatis_" + i);
            user.setPwd("pwd_mybatis_" + i);
            userRepository.insert(user);
            result.add((long) user.getUserId());
        }
        return result;
    }
    
    @Override
    public void processFailure() throws SQLException {
        log.info("-------------- Process Failure Begin ---------------");
        insertData();
        log.info("-------------- Process Failure Finish --------------");
        throw new RuntimeException("Exception occur for transaction test.");
    }
    
    private void deleteData(final List<Long> userIds) throws SQLException {
        log.info("---------------------------- Delete Data ----------------------------");
        for (Long each : userIds) {
            userRepository.delete(each);
        }
    }
    
    @Override
    public void printData() throws SQLException {
        log.info("---------------------------- Print User Data -----------------------");
        for (Object each : userRepository.selectAll()) {
            log.info("obj -> {}", each);
        }
    }
}

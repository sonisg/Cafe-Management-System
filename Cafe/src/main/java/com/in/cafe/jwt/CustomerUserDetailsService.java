package com.in.cafe.jwt;

import com.in.cafe.dao.UserDao;
import com.in.cafe.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
public class CustomerUserDetailsService implements UserDetailsService {


    @Autowired
    UserDao userDao;

    private User userDetail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUserName", username);
       userDetail = userDao.findByEmail(username);
       if(!Objects.isNull(userDetail)){
           return new org.springframework.security.core.userdetails.User(userDetail.getEmail(), userDetail.getPassword(),  new ArrayList<>());
       }else{
           throw new UsernameNotFoundException("User Not Found");
       }
    }

    public User getUserDetail(){
        User user = userDetail;
        user.setPassword(null);
        return user;
    }


}

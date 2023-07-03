package com.in.cafe.dao;

import com.in.cafe.model.User;
import com.in.cafe.wrapper.UserWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserDao extends JpaRepository<User, Integer> {

    User findByEmail(@Param("email") String email);

    @Query("select new com.in.cafe.wrapper.UserWrapper(u.id, u.name, u.contactNumber, u.email, u.password, u.role) from User u where u.role='user'")
    List<UserWrapper> getAllUser();

    @Query("select u.email from User u where u.role='admin'")
    List<String> getAllAdmin();

    @Transactional
    @Modifying
    @Query("update User u set u.status=:status where u.id=:id")
    Integer updateStatus(@Param("status") String status, @Param("id") Integer id);


}

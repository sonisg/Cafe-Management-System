package com.in.cafe.serviceImp;

import com.google.common.base.Strings;
import com.in.cafe.constants.CafeConstants;
import com.in.cafe.dao.UserDao;
import com.in.cafe.jwt.CustomerUserDetailsService;
import com.in.cafe.jwt.JWTUtil;
import com.in.cafe.jwt.JwtFilter;
import com.in.cafe.model.User;
import com.in.cafe.service.UserService;
import com.in.cafe.utils.CafeUtils;
import com.in.cafe.utils.EmailUtils;
import com.in.cafe.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImp implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signUp {}", requestMap);
        try {
            if (validateSignUp(requestMap)) {
                User user = userDao.findByEmail(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);

                } else {
                    return CafeUtils.getResponseEntity("Email already exits", HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
                    if(authentication.isAuthenticated()) {
                        if (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                            return new ResponseEntity<String>("{\"token\":\"" + jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                                    customerUserDetailsService.getUserDetail().getRole())
                                    + "\"}", HttpStatus.OK);
                        } else {
                            return new ResponseEntity<String>("{\"message\":\"" + "Wait for Admin approval" + "\"}", HttpStatus.BAD_REQUEST);
                        }
                    }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<String>("{\"message\":\"" + "Bad credentials" + "\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if(jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
            }
            else{
                new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
       try{
           if(jwtFilter.isAdmin()){
              Optional<User> user = userDao.findById(Integer.valueOf(requestMap.get("id")));
              if(user.isPresent()){
                  userDao.updateStatus(requestMap.get("status"), Integer.valueOf(requestMap.get("id")));
                 // sendMailToAllAdmin(requestMap.get("status"), user.get().getEmail(), userDao.getAllAdmin());
                  return CafeUtils.getResponseEntity("User Status Updated SuccessFully", HttpStatus.OK);
              }else{
                  return CafeUtils.getResponseEntity("User Id doesnt exist", HttpStatus.OK);
              }
           }else{
               return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORISED_ACCESS, HttpStatus.UNAUTHORIZED);
           }

       } catch (Exception e) {
           e.printStackTrace();
       }
       return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
      allAdmin.remove(jwtFilter.getCurrentUser());
      if(status!=null && status.equalsIgnoreCase("true")){
          emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account approved", "USER:-" + user + "\n is approved by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
      }else{
          emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account disabled", "USER:-" + user + "\n is disabled by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);

      }

    }

    private boolean validateSignUp(Map<String, String> requestMap){
        if(requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email") && requestMap.containsKey("password")){
            return true;
        }
        else
            return false;
    }

    private User getUserFromMap(Map<String,String> request){
        User user = new User();
        user.setName(request.get("name"));
        user.setEmail(request.get("email"));
        user.setContactNumber(request.get("contactNumber"));
        user.setPassword(request.get("password"));
        user.setRole("user");
        user.setStatus("false");
        return user;
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User currentUser = userDao.findByEmail(jwtFilter.getCurrentUser());
            if(currentUser != null){
                if(currentUser.getPassword().equals(requestMap.get("oldPassword"))){
                    currentUser.setPassword(requestMap.get("newPassword"));
                    userDao.save(currentUser);
                    return CafeUtils.getResponseEntity("Password Updated Successfully", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Incorrect Old password", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {
                emailUtils.forgotMail(user.getEmail(), "Credentials by Cafe Management", user.getPassword());
                return CafeUtils.getResponseEntity("Check Your mail for credentials", HttpStatus.OK);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

}

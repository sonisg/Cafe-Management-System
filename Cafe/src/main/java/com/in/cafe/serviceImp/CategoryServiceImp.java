package com.in.cafe.serviceImp;

import com.google.common.base.Strings;
import com.in.cafe.constants.CafeConstants;
import com.in.cafe.dao.CategoryDao;
import com.in.cafe.jwt.JwtFilter;
import com.in.cafe.model.Category;
import com.in.cafe.service.CategoryService;
import com.in.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImp implements CategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, false)){
                    categoryDao.save(getCategoryFromMap(requestMap, false));
                    return CafeUtils.getResponseEntity("Category Added Successfully", HttpStatus.OK);
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
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
      try{
          if(!Strings.isNullOrEmpty(filterValue)) {
              return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(), HttpStatus.OK);
          }
         return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);
      } catch (Exception e) {
          e.printStackTrace();
      }
      return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, true)){
                    Optional optional = categoryDao.findById(Integer.valueOf(requestMap.get("id")));
                    if(optional.isPresent()){
                        categoryDao.save(getCategoryFromMap(requestMap, true));
                        return CafeUtils.getResponseEntity("Category Updated Successfully", HttpStatus.OK);
                    }else{
                        return CafeUtils.getResponseEntity("Category Id does not exist", HttpStatus.OK);
                    }
                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }else{
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORISED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }
        catch (Exception e) {
        e.printStackTrace();
         }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validate) {
        if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validate){
                return true;
            }else if (!validate){
                return true;
            }
        }
        return false;
    }

    private Category getCategoryFromMap(Map<String, String> requestMap, Boolean isAdd){
        Category category = new Category();
        if(isAdd){
            category.setId(Integer.valueOf(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }
}

package com.in.cafe.dao;


import com.in.cafe.model.Product;

import com.in.cafe.wrapper.ProductWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductDao extends JpaRepository<Product, Integer> {

    @Query("select new com.in.cafe.wrapper.ProductWrapper(p.id, p.name, p.description, p.price, p.status, p.category.id, p.category.name) from Product p")
    List<ProductWrapper> getAllProduct();

    @Modifying
    @Transactional
    @Query("update Product p set p.status =:status where p.id =:id")
    Integer updateProductStatus(@Param("status") String status, @Param("id") Integer id);

    @Query("select new com.in.cafe.wrapper.ProductWrapper(p.id, p.name) from Product p where p.category.id =:id and p.status='true'")
    List<ProductWrapper> getByCategory(@Param("id") Integer id);

    @Query("select new com.in.cafe.wrapper.ProductWrapper(p.id, p.name, p.description, p.price) from Product p where p.id =:id")
    List<ProductWrapper> getProductById(@Param("id") Integer id);
}

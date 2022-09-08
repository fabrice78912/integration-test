package com.fab.crud.example.repository;

import com.fab.crud.example.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
    Product findByName(String name);


    @Transactional
    @Modifying
    @Query(value = "truncate table product_tbl", nativeQuery = true)
    int truncateTable();

}


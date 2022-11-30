package com.ljn7.restapiwithdb;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface SqlDataController extends JpaRepository<User, Integer> {

    @Query("select users from User users where users.name like %:name%")
    List<User> findByName(@Param("name") String name);

    @Query("select users from User users where users.age between :ageOne and :ageTwo")
    List<User> findByAge(@Param("ageOne") int ageOne, @Param("ageTwo") int ageTwo);

}

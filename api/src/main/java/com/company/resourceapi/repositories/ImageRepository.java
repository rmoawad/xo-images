package com.company.resourceapi.repositories;

import com.company.resourceapi.entities.Image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long>, QuerydslPredicateExecutor<Image> {

  
}

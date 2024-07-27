package com.ufrn.frootmart.repository;

import com.ufrn.frootmart.model.Fruit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FruitRepository extends JpaRepository<Fruit, Long> {
    List<Fruit> findByIsDeletedNull();
}

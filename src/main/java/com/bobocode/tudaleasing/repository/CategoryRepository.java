package com.bobocode.tudaleasing.repository;

import com.bobocode.tudaleasing.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

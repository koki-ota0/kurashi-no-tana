package com.kurashi.repository;

import com.kurashi.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Item> findByUserIdAndCategoryOrderByCreatedAtDesc(Long userId, String category);

    @Query("SELECT DISTINCT i.category FROM Item i WHERE i.userId = :userId")
    List<String> findDistinctCategoriesByUserId(@Param("userId") Long userId);

    List<Item> findAllByOrderByCreatedAtDesc();

    @Query("SELECT i.category, COUNT(i) as count FROM Item i GROUP BY i.category ORDER BY count DESC")
    List<Object[]> findCategoryCounts();
}

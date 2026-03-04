package com.kurashi.repository;

import com.kurashi.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Item> findByUserIdAndCategoryOrderByCreatedAtDesc(Long userId, String category);

    Optional<Item> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT DISTINCT i.category FROM Item i WHERE i.userId = :userId")
    List<String> findDistinctCategoriesByUserId(@Param("userId") Long userId);

    @Query("SELECT i.category, COUNT(i) as count FROM Item i WHERE i.userId = :userId GROUP BY i.category ORDER BY count DESC")
    List<Object[]> findCategoryCountsByUserId(@Param("userId") Long userId);
}

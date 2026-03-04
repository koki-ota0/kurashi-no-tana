package com.kurashi.repository;

import com.kurashi.entity.UsageLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsageLogRepository extends JpaRepository<UsageLog, Long> {

    List<UsageLog> findByItemIdAndUserIdOrderByUsedAtDesc(Long itemId, Long userId);

    List<UsageLog> findByUserIdOrderByUsedAtDesc(Long userId);

    long countByItemId(Long itemId);
}

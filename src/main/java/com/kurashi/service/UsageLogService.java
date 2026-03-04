package com.kurashi.service;

import com.kurashi.dto.UsageLogCreateRequest;
import com.kurashi.entity.UsageLog;
import com.kurashi.repository.UsageLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsageLogService {

    private final UsageLogRepository usageLogRepository;
    private final ItemService itemService;

    public List<UsageLog> getItemUsageLogs(Long userId, Long itemId) {
        return usageLogRepository.findByItemIdAndUserIdOrderByUsedAtDesc(itemId, userId);
    }

    public List<UsageLog> getUserUsageLogs(Long userId) {
        return usageLogRepository.findByUserIdOrderByUsedAtDesc(userId);
    }

    @Transactional
    public UsageLog createUsageLog(Long userId, UsageLogCreateRequest request) {
        itemService.getUserItemById(userId, request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found: " + request.getItemId()));

        UsageLog log = UsageLog.builder()
                .itemId(request.getItemId())
                .userId(userId)
                .usageScene(request.getUsageScene())
                .build();

        UsageLog savedLog = usageLogRepository.save(log);
        itemService.incrementUsageCount(userId, request.getItemId());

        return savedLog;
    }
}

package com.kurashi.dto;

import com.kurashi.entity.UsageLog;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsageLogResponse {
    private Long id;
    private Long itemId;
    private Long userId;
    private String usageScene;
    private LocalDateTime usedAt;
    private LocalDateTime createdAt;

    public static UsageLogResponse fromEntity(UsageLog log) {
        UsageLogResponse response = new UsageLogResponse();
        response.setId(log.getId());
        response.setItemId(log.getItemId());
        response.setUserId(log.getUserId());
        response.setUsageScene(log.getUsageScene());
        response.setUsedAt(log.getUsedAt());
        response.setCreatedAt(log.getCreatedAt());
        return response;
    }
}

package com.kurashi.controller;

import com.kurashi.dto.UsageLogCreateRequest;
import com.kurashi.dto.UsageLogResponse;
import com.kurashi.entity.UsageLog;
import com.kurashi.service.CurrentUserService;
import com.kurashi.service.UsageLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usage-logs")
@RequiredArgsConstructor
public class UsageLogController {

    private final UsageLogService usageLogService;
    private final CurrentUserService currentUserService;

    @GetMapping("/item/{itemId}")
    public List<UsageLogResponse> listByItem(@PathVariable Long itemId) {
        Long userId = currentUserService.getCurrentUserId();
        return usageLogService.getItemUsageLogs(userId, itemId).stream()
                .map(UsageLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UsageLogResponse create(@Valid @RequestBody UsageLogCreateRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        UsageLog log = usageLogService.createUsageLog(userId, request);
        return UsageLogResponse.fromEntity(log);
    }
}

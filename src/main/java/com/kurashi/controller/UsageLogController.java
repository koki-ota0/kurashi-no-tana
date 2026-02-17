package com.kurashi.controller;

import com.kurashi.dto.UsageLogCreateRequest;
import com.kurashi.dto.UsageLogResponse;
import com.kurashi.entity.UsageLog;
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

    private static final Long DEFAULT_USER_ID = 1L;
    private final UsageLogService usageLogService;

    @GetMapping("/item/{itemId}")
    public List<UsageLogResponse> listByItem(@PathVariable Long itemId) {
        return usageLogService.getItemUsageLogs(itemId).stream()
                .map(UsageLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UsageLogResponse create(@Valid @RequestBody UsageLogCreateRequest request) {
        UsageLog log = usageLogService.createUsageLog(DEFAULT_USER_ID, request);
        return UsageLogResponse.fromEntity(log);
    }
}

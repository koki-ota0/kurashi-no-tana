package com.kurashi.controller;

import com.kurashi.dto.CategoryCount;
import com.kurashi.dto.ItemResponse;
import com.kurashi.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/discovery")
@RequiredArgsConstructor
public class DiscoveryController {

    private final ItemService itemService;

    @GetMapping("/recent")
    public List<ItemResponse> recentItems(@RequestParam(defaultValue = "20") int limit) {
        return itemService.getRecentItems(limit).stream()
                .map(ItemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/popular-categories")
    public List<CategoryCount> popularCategories(@RequestParam(defaultValue = "10") int limit) {
        return itemService.getPopularCategories(limit);
    }
}

package com.kurashi.controller;

import com.kurashi.dto.*;
import com.kurashi.entity.Item;
import com.kurashi.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private static final Long DEFAULT_USER_ID = 1L;
    private final ItemService itemService;

    @GetMapping
    public List<ItemResponse> list() {
        return itemService.getUserItems(DEFAULT_USER_ID).stream()
                .map(ItemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/category/{category}")
    public List<ItemResponse> byCategory(@PathVariable String category) {
        return itemService.getItemsByCategory(DEFAULT_USER_ID, category).stream()
                .map(ItemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/categories")
    public List<String> categories() {
        return itemService.getItemCategories(DEFAULT_USER_ID);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> detail(@PathVariable Long id) {
        return itemService.getItemById(id)
                .map(item -> ResponseEntity.ok(ItemResponse.fromEntity(item)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ItemResponse create(@Valid @RequestBody ItemCreateRequest request) {
        Item item = itemService.createItem(DEFAULT_USER_ID, request);
        return ItemResponse.fromEntity(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ItemUpdateRequest request) {
        try {
            Item item = itemService.updateItem(id, request);
            return ResponseEntity.ok(ItemResponse.fromEntity(item));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}

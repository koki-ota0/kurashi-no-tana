package com.kurashi.controller;

import com.kurashi.dto.ItemCreateRequest;
import com.kurashi.dto.ItemResponse;
import com.kurashi.dto.ItemUpdateRequest;
import com.kurashi.entity.Item;
import com.kurashi.service.CurrentUserService;
import com.kurashi.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<ItemResponse> list() {
        Long userId = currentUserService.getCurrentUserId();
        return itemService.getUserItems(userId).stream()
                .map(ItemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/category/{category}")
    public List<ItemResponse> byCategory(@PathVariable String category) {
        Long userId = currentUserService.getCurrentUserId();
        return itemService.getItemsByCategory(userId, category).stream()
                .map(ItemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/categories")
    public List<String> categories() {
        Long userId = currentUserService.getCurrentUserId();
        return itemService.getItemCategories(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> detail(@PathVariable Long id) {
        Long userId = currentUserService.getCurrentUserId();
        return itemService.getUserItemById(userId, id)
                .map(item -> ResponseEntity.ok(ItemResponse.fromEntity(item)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ItemResponse create(@Valid @RequestBody ItemCreateRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        Item item = itemService.createItem(userId, request);
        return ItemResponse.fromEntity(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ItemUpdateRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        try {
            Item item = itemService.updateItem(userId, id, request);
            return ResponseEntity.ok(ItemResponse.fromEntity(item));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = currentUserService.getCurrentUserId();
        itemService.deleteItem(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String photoUrl = itemService.saveImage(file);
            Map<String, String> response = new HashMap<>();
            response.put("photoUrl", photoUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "ファイルの保存に失敗しました"));
        }
    }
}

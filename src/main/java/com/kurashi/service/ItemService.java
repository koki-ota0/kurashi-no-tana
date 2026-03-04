package com.kurashi.service;

import com.kurashi.dto.CategoryCount;
import com.kurashi.dto.ItemCreateRequest;
import com.kurashi.dto.ItemUpdateRequest;
import com.kurashi.entity.Item;
import com.kurashi.repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Item> getUserItems(Long userId) {
        return itemRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Item> getItemsByCategory(Long userId, String category) {
        return itemRepository.findByUserIdAndCategoryOrderByCreatedAtDesc(userId, category);
    }

    public List<String> getItemCategories(Long userId) {
        return itemRepository.findDistinctCategoriesByUserId(userId);
    }

    public Optional<Item> getUserItemById(Long userId, Long id) {
        return itemRepository.findByIdAndUserId(id, userId);
    }

    @Transactional
    public Item createItem(Long userId, ItemCreateRequest request) {
        String photoUrlsJson = null;
        if (request.getPhotoUrls() != null && !request.getPhotoUrls().isEmpty()) {
            try {
                photoUrlsJson = objectMapper.writeValueAsString(request.getPhotoUrls());
            } catch (Exception e) {
                photoUrlsJson = "[]";
            }
        }

        Item item = Item.builder()
                .userId(userId)
                .name(request.getName())
                .category(request.getCategory())
                .brand(request.getBrand())
                .memo(request.getMemo())
                .photoUrls(photoUrlsJson)
                .usageCount(0)
                .build();

        return itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(Long userId, Long id, ItemUpdateRequest request) {
        Item item = itemRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + id));

        if (request.getName() != null) {
            item.setName(request.getName());
        }
        if (request.getCategory() != null) {
            item.setCategory(request.getCategory());
        }
        if (request.getBrand() != null) {
            item.setBrand(request.getBrand());
        }
        if (request.getMemo() != null) {
            item.setMemo(request.getMemo());
        }
        if (request.getPhotoUrls() != null) {
            try {
                item.setPhotoUrls(objectMapper.writeValueAsString(request.getPhotoUrls()));
            } catch (Exception e) {
                item.setPhotoUrls("[]");
            }
        }

        return itemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long userId, Long id) {
        Item item = itemRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + id));
        itemRepository.delete(item);
    }

    @Transactional
    public void incrementUsageCount(Long userId, Long itemId) {
        Item item = itemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
        item.setUsageCount(item.getUsageCount() + 1);
        itemRepository.save(item);
    }

    public List<Item> getRecentItems(Long userId, int limit) {
        return itemRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<CategoryCount> getPopularCategories(Long userId, int limit) {
        return itemRepository.findCategoryCountsByUserId(userId).stream()
                .limit(limit)
                .map(row -> new CategoryCount((String) row[0], (Long) row[1]))
                .collect(Collectors.toList());
    }
}

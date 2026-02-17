package com.kurashi.dto;

import com.kurashi.entity.Item;
import lombok.Data;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
public class ItemResponse {
    private Long id;
    private Long userId;
    private String name;
    private String category;
    private String brand;
    private String memo;
    private List<String> photoUrls;
    private Integer usageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ItemResponse fromEntity(Item item) {
        ItemResponse response = new ItemResponse();
        response.setId(item.getId());
        response.setUserId(item.getUserId());
        response.setName(item.getName());
        response.setCategory(item.getCategory());
        response.setBrand(item.getBrand());
        response.setMemo(item.getMemo());
        response.setUsageCount(item.getUsageCount());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());

        if (item.getPhotoUrls() != null && !item.getPhotoUrls().isEmpty()) {
            try {
                response.setPhotoUrls(objectMapper.readValue(
                    item.getPhotoUrls(),
                    new TypeReference<List<String>>() {}
                ));
            } catch (Exception e) {
                response.setPhotoUrls(Collections.emptyList());
            }
        } else {
            response.setPhotoUrls(Collections.emptyList());
        }

        return response;
    }
}

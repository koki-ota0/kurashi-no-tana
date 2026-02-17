package com.kurashi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryCount {
    private String category;
    private Long count;
}

package com.kurashi.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ItemUpdateRequest {

    @Size(max = 255, message = "名前は255文字以内で入力してください")
    private String name;

    @Size(max = 100, message = "カテゴリは100文字以内で入力してください")
    private String category;

    @Size(max = 255, message = "ブランドは255文字以内で入力してください")
    private String brand;

    private String memo;

    private List<String> photoUrls;
}

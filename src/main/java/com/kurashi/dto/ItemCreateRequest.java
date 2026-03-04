package com.kurashi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ItemCreateRequest {

    @NotBlank(message = "名前は必須です")
    @Size(max = 255, message = "名前は255文字以内で入力してください")
    private String name;

    @NotBlank(message = "カテゴリは必須です")
    @Size(max = 100, message = "カテゴリは100文字以内で入力してください")
    private String category;

    @Size(max = 255, message = "ブランドは255文字以内で入力してください")
    private String brand;

    @Size(max = 100, message = "販売地は100文字以内で入力してください")
    private String salesLocation;  // 販売地

    private Integer price;  // 値段

    @Size(max = 255, message = "窯元は255文字以内で入力してください")
    private String maker;  // 窯元

    private String memo;

    private List<String> photoUrls;
}

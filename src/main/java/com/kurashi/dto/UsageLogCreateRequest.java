package com.kurashi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsageLogCreateRequest {

    @NotNull(message = "アイテムIDは必須です")
    private Long itemId;

    @Size(max = 100, message = "使用シーンは100文字以内で入力してください")
    private String usageScene;
}

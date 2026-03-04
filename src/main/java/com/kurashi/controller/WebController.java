package com.kurashi.controller;

import com.kurashi.dto.ItemResponse;
import com.kurashi.entity.Item;
import com.kurashi.service.ItemService;
import com.kurashi.service.UsageLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WebController {

    private static final Long DEFAULT_USER_ID = 1L;
    private final ItemService itemService;
    private final UsageLogService usageLogService;

    @Value("${file.upload.dir}")
    private String uploadDir;

    @GetMapping("/")
    public String home(Model model) {
        List<ItemResponse> items = itemService.getUserItems(DEFAULT_USER_ID).stream()
                .map(ItemResponse::fromEntity)
                .collect(Collectors.toList());
        List<String> categories = itemService.getItemCategories(DEFAULT_USER_ID);

        model.addAttribute("items", items);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", "all");
        return "home";
    }

    @GetMapping("/category/{category}")
    public String byCategory(@PathVariable String category, Model model) {
        List<ItemResponse> items = itemService.getItemsByCategory(DEFAULT_USER_ID, category).stream()
                .map(ItemResponse::fromEntity)
                .collect(Collectors.toList());
        List<String> categories = itemService.getItemCategories(DEFAULT_USER_ID);

        model.addAttribute("items", items);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        return "home";
    }

    @GetMapping("/items/new")
    public String newItemForm(Model model) {
        model.addAttribute("categories", List.of("皿", "カップ", "花器", "雑貨"));
        return "item-form";
    }

    @GetMapping("/items/{id}")
    public String itemDetail(@PathVariable Long id, Model model) {
        return itemService.getItemById(id)
                .map(item -> {
                    model.addAttribute("item", ItemResponse.fromEntity(item));
                    model.addAttribute("usageLogs", usageLogService.getItemUsageLogs(id));
                    return "item-detail";
                })
                .orElse("redirect:/");
    }

    @GetMapping("/items/{id}/edit")
    public String editItemForm(@PathVariable Long id, Model model) {
        return itemService.getItemById(id)
                .map(item -> {
                    model.addAttribute("item", ItemResponse.fromEntity(item));
                    model.addAttribute("categories", List.of("皿", "カップ", "花器", "雑貨"));
                    return "item-edit";
                })
                .orElse("redirect:/");
    }

    @GetMapping("/discovery")
    public String discovery(Model model) {
        model.addAttribute("recentItems", itemService.getRecentItems(20).stream()
                .map(ItemResponse::fromEntity)
                .collect(Collectors.toList()));
        model.addAttribute("popularCategories", itemService.getPopularCategories(10));
        return "discovery";
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {
        Path filepath = Paths.get(uploadDir).resolve(filename);
        
        if (!Files.exists(filepath)) {
            return ResponseEntity.notFound().build();
        }

        byte[] imageData = Files.readAllBytes(filepath);
        
        // ファイル拡張子からContent-Typeを判定
        MediaType mediaType = MediaType.IMAGE_JPEG;
        if (filename.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else if (filename.endsWith(".gif")) {
            mediaType = MediaType.IMAGE_GIF;
        } else if (filename.endsWith(".webp")) {
            mediaType = MediaType.valueOf("image/webp");
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                .body(imageData);
    }
}

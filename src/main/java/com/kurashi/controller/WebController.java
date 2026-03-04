package com.kurashi.controller;

import com.kurashi.dto.ItemResponse;
import com.kurashi.service.CurrentUserService;
import com.kurashi.service.ItemService;
import com.kurashi.service.UsageLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ItemService itemService;
    private final UsageLogService usageLogService;
    private final CurrentUserService currentUserService;

    @Value("${file.upload.dir}")
    private String uploadDir;

    @GetMapping("/")
    public String home(Model model) {
        Long userId = currentUserService.getCurrentUserId();
        List<ItemResponse> items = itemService.getUserItems(userId).stream()
                .map(ItemResponse::fromEntity)
                .collect(Collectors.toList());
        List<String> categories = itemService.getItemCategories(userId);

        model.addAttribute("items", items);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", "all");
        return "home";
    }

    @GetMapping("/category/{category}")
    public String byCategory(@PathVariable String category, Model model) {
        Long userId = currentUserService.getCurrentUserId();
        List<ItemResponse> items = itemService.getItemsByCategory(userId, category).stream()
                .map(ItemResponse::fromEntity)
                .collect(Collectors.toList());
        List<String> categories = itemService.getItemCategories(userId);

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
        Long userId = currentUserService.getCurrentUserId();
        return itemService.getUserItemById(userId, id)
                .map(item -> {
                    model.addAttribute("item", ItemResponse.fromEntity(item));
                    model.addAttribute("usageLogs", usageLogService.getItemUsageLogs(userId, id));
                    return "item-detail";
                })
                .orElse("redirect:/");
    }

    @GetMapping("/items/{id}/edit")
    public String editItemForm(@PathVariable Long id, Model model) {
        Long userId = currentUserService.getCurrentUserId();
        return itemService.getUserItemById(userId, id)
                .map(item -> {
                    model.addAttribute("item", ItemResponse.fromEntity(item));
                    model.addAttribute("categories", List.of("皿", "カップ", "花器", "雑貨"));
                    return "item-edit";
                })
                .orElse("redirect:/");
    }

    @GetMapping("/discovery")
    public String discovery(Model model) {
        model.addAttribute("recentItems", itemService.getRecentItems(currentUserService.getCurrentUserId(), 20).stream()
                .map(ItemResponse::fromEntity)
                .collect(Collectors.toList()));
        model.addAttribute("popularCategories", itemService.getPopularCategories(currentUserService.getCurrentUserId(), 10));
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

package com.project.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.DTO.ErrDTO;
import com.project.DTO.FoodDTO;
import com.project.Entity.Food;
import com.project.Service.CategoryService;
import com.project.Service.FoodService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/foods")
@CrossOrigin(origins = "http://localhost:4200/")
public class FoodController {

    FoodService foodService;
    CategoryService categoryService;

    public FoodController(FoodService foodService, CategoryService categoryService) {
        this.foodService = foodService;
        this.categoryService = categoryService;
    }

    // Lấy tất cả món
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
    public ResponseEntity<List<Food>> getAllFoods() {
        List<Food> foods = foodService.getAllFoods();
        return new ResponseEntity<>(foods, HttpStatus.OK);
    }

    // Tìm món phổ biến nhất (theo số lượng đơn đã đặt)
    @GetMapping("/popular")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
    public ResponseEntity<List<FoodDTO>> getTopPopularFoods(@RequestParam(defaultValue = "3") int limit) {
        List<FoodDTO> foods = foodService.getTopPopularFoods(limit);
        return new ResponseEntity<>(foods, HttpStatus.OK);
    }

    // Tìm món theo danh mục
    @GetMapping("/by-category")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
    public ResponseEntity<List<FoodDTO>> getFoodsByCategory(@RequestParam int categoryId) {
        List<FoodDTO> foods = foodService.getFoodsByCategory(categoryId);
        return new ResponseEntity<>(foods, HttpStatus.OK);
    }

    // Tìm món theo tên (có thể chứa một phần tên)
    @GetMapping("/by-name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
    public ResponseEntity<List<FoodDTO>> getFoodsByName(@RequestParam String name) {
        List<FoodDTO> foods = foodService.getFoodsByName(name);
        return new ResponseEntity<>(foods, HttpStatus.OK);
    }

    // Tìm món theo trạng thái
    @GetMapping("/by-status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
    public ResponseEntity<List<FoodDTO>> getFoodsByStatus(@RequestParam String status) {
        List<FoodDTO> foods = foodService.getFoodsByStatus(status);
        return new ResponseEntity<>(foods, HttpStatus.OK);
    }

    // Tìm món theo khoảng giá
    @GetMapping("/by-price")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
    public ResponseEntity<List<FoodDTO>> getFoodsByPrice(@RequestParam double minPrice, @RequestParam double maxPrice) {
        List<FoodDTO> foods = foodService.getFoodsByPriceRange(minPrice, maxPrice);
        return new ResponseEntity<>(foods, HttpStatus.OK);
    }

    // Lấy món theo id
    @GetMapping("/by-id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
    public ResponseEntity<Food> getFoodById(@RequestParam int foodId) {
        Food food = foodService.getFoodById(foodId);
        if (food == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(food, HttpStatus.OK);
    }

    // Chỉnh sửa món
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateFood(@RequestBody Food food, HttpServletRequest request) {
        try {
            Food existingFood = foodService.getFoodById(food.getFoodId());
            if (existingFood == null) {
                ErrDTO err = new ErrDTO("Món ăn không tồn tại", request.getRequestURI());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
            }
            existingFood.setName(food.getName());
            existingFood.setDescription(food.getDescription());
            existingFood.setPrice(food.getPrice());
            existingFood.setImageUrl(food.getImageUrl());
            existingFood.setStatus(food.getStatus());
            existingFood.setIsPromotion(food.getIsPromotion());
            existingFood.setCategory(categoryService.getCategoryById(food.getCategory().getCategoryId()));
            existingFood.setOrderCount(food.getOrderCount());
            existingFood.setCreatedAt(food.getCreatedAt());
            Food updatedFood = foodService.addFood(existingFood);

            System.out.println(food.toString());
            return ResponseEntity.ok(updatedFood);
        } catch (Exception e) {
            ErrDTO err = new ErrDTO("Không thể cập nhật món ăn: " + e.getMessage(), request.getRequestURI());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    // Thêm món mới
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addFood(@RequestBody Food foodDTO, HttpServletRequest request) {
        try {
            Food food = new Food();
            food.setName(foodDTO.getName());
            food.setDescription(foodDTO.getDescription());
            food.setPrice(foodDTO.getPrice());
            food.setImageUrl(foodDTO.getImageUrl());
            food.setStatus(foodDTO.getStatus());
            food.setOrderCount(0);
            food.setCreatedAt(java.time.LocalDateTime.now());
            food.setIsPromotion(foodDTO.getIsPromotion());
            food.setCategory(categoryService.getCategoryById(foodDTO.getCategory().getCategoryId()));
            Food savedFood = foodService.addFood(food);
            System.out.println(savedFood.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedFood);
        } catch (Exception e) {
            ErrDTO err = new ErrDTO("Không thể thêm món ăn: " + e.getMessage(), request.getRequestURI());
            System.out.println("vào đến đây");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    // Xóa món
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFood(@RequestParam int foodId, HttpServletRequest request) {
        try {
            boolean isDeleted = foodService.deleteFood(foodId);
            if (isDeleted) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                ErrDTO err = new ErrDTO("Món ăn không tồn tại", request.getRequestURI());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
            }
        } catch (Exception e) {
            ErrDTO err = new ErrDTO("Không thể xóa món ăn: " + e.getMessage(), request.getRequestURI());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> importFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = foodService.importFromExcel(file.getInputStream(), categoryService);

            byte[] errorFile = (byte[]) result.get("errorFile");

            if (errorFile != null) { // Có lỗi thì trả về file Excel
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Error_Foods.xlsx")
                        .contentType(MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml."
                                + "sheet"))
                        .body(errorFile);
            }

            // Không có lỗi → trả JSON kết quả
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);

        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("errorMessage", "Lỗi khi đọc file Excel: " + e.getMessage());
            errorResponse.put("insertedCount", 0);
            errorResponse.put("skippedCount", 0);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }
}
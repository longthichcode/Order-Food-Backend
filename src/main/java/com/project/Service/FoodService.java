package com.project.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.project.DTO.FoodDTO;
import com.project.Entity.Category;
import com.project.Entity.Food;
import com.project.Entity.Food.Status;
import com.project.Repository.FoodRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class FoodService {
    FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    // Lấy tất cả món
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    // Tìm món phổ biến nhất (theo số lượng đơn đã đặt)
    public List<FoodDTO> getTopPopularFoods(int limit) {
        List<Food> f = foodRepository.findTopPopularFoods(Pageable.ofSize(limit));
        List<FoodDTO> foodDTOs = new ArrayList<FoodDTO>();
        for (Food food : f) {
            foodDTOs.add(mapToDTO(food));
        }
        return foodDTOs;
    }

    // Tìm món theo danh mục
    public List<FoodDTO> getFoodsByCategory(int categoryId) {
        List<Food> f = foodRepository.findByCategoryId(categoryId);
        List<FoodDTO> foodDTOs = new ArrayList<FoodDTO>();
        for (Food food : f) {
            foodDTOs.add(mapToDTO(food));
        }
        return foodDTOs;
    }

    // Tìm món theo tên (có thể chứa một phần tên)
    public List<FoodDTO> getFoodsByName(String name) {
        List<Food> f = foodRepository.findByNameContainingIgnoreCase(name);
        List<FoodDTO> foodDTOs = new ArrayList<FoodDTO>();
        for (Food food : f) {
            foodDTOs.add(mapToDTO(food));
        }
        return foodDTOs;
    }

    // Tìm món theo trạng thái
    public List<FoodDTO> getFoodsByStatus(String status) {
        List<Food> f = foodRepository.findByStatus(status);
        List<FoodDTO> foodDTOs = new ArrayList<FoodDTO>();
        for (Food food : f) {
            foodDTOs.add(mapToDTO(food));
        }
        return foodDTOs;
    }

    // Tìm kiếm theo khoảng giá
    public List<FoodDTO> getFoodsByPriceRange(double minPrice, double maxPrice) {
        BigDecimal min = BigDecimal.valueOf(minPrice);
        BigDecimal max = BigDecimal.valueOf(maxPrice);
        List<Food> f = foodRepository.findByPriceBetween(min, max);
        List<FoodDTO> foodDTOs = new ArrayList<FoodDTO>();
        for (Food food : f) {
            foodDTOs.add(mapToDTO(food));
        }
        return foodDTOs;
    }

    // Thêm món mới
    public Food addFood(Food food) {
        return foodRepository.save(food);
    }

    // Lấy món theo ID
    public Food getFoodById(int foodId) {
        return foodRepository.findById(foodId).orElse(null);
    }

    // Cập nhật món
    public Food updateFood(Food food) {
        return foodRepository.save(food);
    }

    // Xóa món theo ID
    public boolean deleteFood(int foodId) {
        Food existingFood = foodRepository.findById(foodId).orElse(null);
        if (existingFood != null) {
            foodRepository.deleteById(foodId);
            return true;
        } else {
            return false;
        }
    }

    // Nhập dữ liệu từ file Excel
    public Map<String, Object> importFromExcel(InputStream inp, CategoryService categoryService) throws IOException {
        Map<String, Object> response = new HashMap<>();
        List<Food> validFoods = new ArrayList<>();
        List<List<String>> errorRows = new ArrayList<>();
        int insertedCount = 0;
        int skippedCount = 0;

        try (Workbook workbook = new XSSFWorkbook(inp)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Lấy header
            List<String> headers = new ArrayList<>();
            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                for (Cell cell : headerRow) {
                    headers.add(getCellStringValue(cell));
                }
                headers.add("Lỗi"); // Thêm cột lỗi
            }

            @SuppressWarnings("unused")
			int rowNum = 1;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                rowNum++;

                Food food = new Food();
                boolean hasError = false;
                StringBuilder errorMessage = new StringBuilder();

                // Tên món
                String name = getCellStringValue(row.getCell(0));
                if (name == null || name.trim().isEmpty()) {
                    errorMessage.append("Tên món không được để trống; ");
                    hasError = true;
                } else {
                    food.setName(name);
                }

                // Mô tả
                String description = getCellStringValue(row.getCell(1));
                if (description == null || description.trim().isEmpty()) {
                    errorMessage.append("Mô tả không được để trống; ");
                    hasError = true;
                } else {
                    food.setDescription(description);
                }

                // Giá
                String priceStr = getCellStringValue(row.getCell(2));
                try {
                    BigDecimal price = new BigDecimal(priceStr);
                    if (price.compareTo(BigDecimal.ZERO) <= 0) {
                        errorMessage.append("Giá phải lớn hơn 0; ");
                        hasError = true;
                    } else {
                        food.setPrice(price);
                    }
                } catch (NumberFormatException e) {
                    errorMessage.append("Giá không hợp lệ; ");
                    hasError = true;
                }

                // Danh mục
                String categoryIdStr = getCellStringValue(row.getCell(3));
                Category category = categoryService.getCategoryByName(categoryIdStr);
                if (category == null) {
                    errorMessage.append("Danh mục không tồn tại; ");
                    hasError = true;
                }else {
                	food.setCategory(category);
                }
                

                // Trạng thái
                String statusStr = getCellStringValue(row.getCell(4));
                try {
                    Status status = Status.valueOf(statusStr.toUpperCase());
                    food.setStatus(status);
                } catch (IllegalArgumentException e) {
                    errorMessage.append("Trạng thái phải là AVAILABLE hoặc OUT_OF_STOCK; ");
                    hasError = true;
                }

                // Khuyến mãi
                String isPromotionStr = getCellStringValue(row.getCell(5));
                boolean isPromotion = "Có".equalsIgnoreCase(isPromotionStr) || "true".equalsIgnoreCase(isPromotionStr);
                food.setIsPromotion(isPromotion);
                
                Cell cell = row.getCell(6);
                if (cell == null || cell.getCellType() == CellType.BLANK) {
                    errorMessage.append("Số lượng đã bán bị trống; ");
                    hasError = true;
                } else {
                    try {
                        int orderCount;
                        if (cell.getCellType() == CellType.NUMERIC) {
                            // nếu trong Excel là số, lấy thẳng số nguyên
                            orderCount = (int) cell.getNumericCellValue();
                        } else {
                            // nếu trong Excel là text thì trim rồi parse
                            orderCount = Integer.parseInt(cell.getStringCellValue().trim());
                        }

                        if (orderCount < 0) {
                            errorMessage.append("Số lượng đã bán không được âm; ");
                            hasError = true;
                        } else {
                            food.setOrderCount(orderCount);
                        }
                    } catch (Exception e) {
                        errorMessage.append("Số lượng đã bán không hợp lệ; ");
                        hasError = true;
                    }
                }



                // Ngày tạo
                String createdAt = getCellStringValue(row.getCell(7));
                if (createdAt == null || createdAt.trim().isEmpty()) {
                    food.setCreatedAt(java.time.LocalDateTime.now());
                } else {
                    try {
                        food.setCreatedAt(java.time.LocalDateTime.parse(createdAt));
                    } catch (Exception e) {
                        errorMessage.append("Ngày tạo không hợp lệ; ");
                        hasError = true;
                    }
                }

                // Kiểm tra trùng tên món
                if (!hasError && food.getName() != null) {
                    List<Food> existingFoods = foodRepository.findByNameContainingIgnoreCase(food.getName());
                    if (!existingFoods.isEmpty()) {
                        errorMessage.append("Tên món đã tồn tại: ").append(food.getName()).append("; ");
                        hasError = true;
                    }
                }

                // Nếu có lỗi, thêm dòng vào errorRows
                if (hasError) {
                    List<String> rowData = new ArrayList<>();
                    for (int i = 0; i <= 7; i++) {
                        rowData.add(getCellStringValue(row.getCell(i)));
                    }
                    rowData.add(errorMessage.toString());
                    errorRows.add(rowData);
                    skippedCount++;
                } else {
                    validFoods.add(food);
                    insertedCount++;
                }
            }

            // Lưu các món hợp lệ vào cơ sở dữ liệu
            if (!validFoods.isEmpty()) {
                foodRepository.saveAll(validFoods);
            }

            // Tạo file Excel lỗi nếu có
            byte[] errorFile = null;
            if (!errorRows.isEmpty()) {
                Workbook errorWb = new XSSFWorkbook();
                Sheet errorSheet = errorWb.createSheet("Errors");

                // Ghi header
                Row headerRow = errorSheet.createRow(0);
                for (int i = 0; i < headers.size(); i++) {
                    headerRow.createCell(i).setCellValue(headers.get(i));
                }

                // Ghi dữ liệu lỗi
                int r = 1;
                for (List<String> rowData : errorRows) {
                    Row errRow = errorSheet.createRow(r++);
                    for (int i = 0; i < rowData.size(); i++) {
                        errRow.createCell(i).setCellValue(rowData.get(i));
                    }
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                errorWb.write(bos);
                errorWb.close();
                errorFile = bos.toByteArray();
            }

            response.put("success", errorRows.isEmpty());
            response.put("insertedCount", insertedCount);
            response.put("skippedCount", skippedCount);
            response.put("errorFile", errorFile);
        } catch (Exception e) {
            response.put("success", false);
            response.put("errorMessage", "Lỗi xử lý file Excel: " + e.getMessage());
        }

        return response;
    }

    // Hàm hỗ trợ lấy giá trị ô
    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    // Map to DTO
    private FoodDTO mapToDTO(Food food) {
        FoodDTO foodDTO = new FoodDTO();
        foodDTO.setFoodId(food.getFoodId());
        foodDTO.setName(food.getName());
        foodDTO.setDescription(food.getDescription());
        foodDTO.setPrice(food.getPrice());
        foodDTO.setImageUrl(food.getImageUrl());
        foodDTO.setStatus(food.getStatus()); 
        foodDTO.setIsPromotion(food.getIsPromotion());
        foodDTO.setOrderCount(food.getOrderCount());
        foodDTO.setCreatedAt(food.getCreatedAt());
        foodDTO.setReviewCount(food.getReviewCount());
        foodDTO.setAverageRating(food.getAverageRating());
        if (food.getCategory() != null) {
            foodDTO.setCategoryName(food.getCategory().getCategoryName());
        } else {
            foodDTO.setCategoryName(null);
        }
        return foodDTO;
    }
}
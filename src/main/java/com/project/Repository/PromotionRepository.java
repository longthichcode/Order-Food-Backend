package com.project.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Entity.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

	Optional<Promotion> findByCode(String code);

	//lấy khuyến mãi theo khoảng thời gian
	List<Promotion> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
	
	//lấy khuyến mãi đang diễn ra
	List<Promotion> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate currentDate1, LocalDate currentDate2);
}

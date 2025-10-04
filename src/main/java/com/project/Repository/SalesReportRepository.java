package com.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Entity.SalesReport;

public interface SalesReportRepository extends JpaRepository<SalesReport, Integer> {

}

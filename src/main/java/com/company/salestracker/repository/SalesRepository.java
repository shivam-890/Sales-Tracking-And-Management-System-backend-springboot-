package com.company.salestracker.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.company.salestracker.entity.Sale;
import com.company.salestracker.enums.PaymentStatus;

public interface SalesRepository extends JpaRepository<Sale, String> {
	
      Optional<Sale> findByDealIdAndOwnerUserId(String dealId,String owner);
      Optional<Sale> findBySaleIdAndOwnerUserId(String saleId,String owner);
      Page<Sale> findByOwnerUserId(String userId,Pageable pageable);
      List<Sale> findByOwnerUserIdAndPaymentStatusAndSaleDateBetween(String owner,PaymentStatus paymentstatus,LocalDate start,LocalDate end);

}

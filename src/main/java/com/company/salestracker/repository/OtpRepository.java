package com.company.salestracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.salestracker.entity.Otp;

public interface OtpRepository extends JpaRepository<Otp, String>{

}

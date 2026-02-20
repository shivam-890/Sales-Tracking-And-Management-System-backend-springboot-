package com.company.salestracker.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.PaymentStatusRequest;
import com.company.salestracker.dto.request.SaleRequest;
import com.company.salestracker.dto.response.DealResponse;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.dto.response.SaleResponse;
import com.company.salestracker.dto.response.SaleSummaryResponse;
import com.company.salestracker.entity.Deal;
import com.company.salestracker.entity.Sale;
import com.company.salestracker.entity.User;
import com.company.salestracker.enums.DealStatus;
import com.company.salestracker.enums.PaymentStatus;
import com.company.salestracker.exception.BadRequestException;
import com.company.salestracker.exception.ResourceAlreadyExistsException;
import com.company.salestracker.exception.ResourceNotFoundException;
import com.company.salestracker.repository.DealRepository;
import com.company.salestracker.repository.SalesRepository;
import com.company.salestracker.repository.UserRepository;
import com.company.salestracker.service.SalesService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.Helper;

@Service

public class SaleServiceImpl implements SalesService {

    @Autowired private  SalesRepository saleRepo;
    @Autowired private  DealRepository dealRepo;
    @Autowired private  UserRepository userRepo;
    @Autowired private Helper helper;
    public static final boolean NOT_DELETED = false;

    // ================= CREATE =================

    @Override
    public SaleResponse createSale(SaleRequest saleRequest) {
    	
      	LeadServiceImpl.leadValidations();

    	User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();
    	
         saleRepo.findByDealIdAndOwnerUserId(saleRequest.getDeal(),ownerOfLoggedUser.getUserId())
        		        .ifPresent(u -> {  throw new ResourceAlreadyExistsException(Constants.SALE_ALREADY_EXISTS);});  // same deal id se koi sale hematlb sale exist krti he toh exception
                 	
        
   	    Deal deal = dealRepo.findByDealIdAndOwnerUserIdAndDeleted(saleRequest.getDeal(),ownerOfLoggedUser.getUserId(),NOT_DELETED)
             .orElseThrow(() -> new ResourceNotFoundException(Constants.DEAL_NOT_FOUND));                                // yadi deal id jo sale me convert ho rhi he wo exist krti he ya nah
        		                         
        if (!deal.getDealStage().equals(DealStatus.CLOSED_WON)) {                         // deal shoul be closed Won means success hona chaiye
            throw new BadRequestException(Constants.DEAL_SHOULD_WON);
        }

        if (saleRequest.getSaleAmount().compareTo(BigDecimal.ZERO) <= 0) {                    // sale amount 0 or 0 se kam nhi hona chahiye
            throw new BadRequestException("Sale amount must be greater than zero");
        }
        
        BigDecimal expected = deal.getExpectedAmount();
        BigDecimal twentyFivePercent = expected.multiply(new BigDecimal("0.25"));              // jo sale ka amount he wo deal ke expected amount se 25% kam or jyada ho skta he jyada dif nahi hona chahiye

        BigDecimal minAmount = expected.subtract(twentyFivePercent);
        BigDecimal maxAmount = expected.add(twentyFivePercent);

        if (saleRequest.getSaleAmount().compareTo(minAmount) < 0 ||
            saleRequest.getSaleAmount().compareTo(maxAmount) > 0) {

            throw new BadRequestException("Sale amount must be within ±25% of expected amount");
        }

        Sale savedSales = saleRepo.save(mapToEntity(saleRequest, deal));

        return mapToResponse(savedSales);
    }

    // ================= UPDATE PAYMENT =================

    @Override
    public SaleResponse updatePaymentStatus(PaymentStatusRequest request,String saleId) {
    	
      	LeadServiceImpl.leadValidations();


    	User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();
    	
        Sale sale = saleRepo.findBySaleIdAndOwnerUserId(saleId,ownerOfLoggedUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.SALE_NOT_FOUND));      // sale exist or not 

        if(!sale.getPaymentStatus().canMoveTo(request.getPaymentStatus()))
        	throw new BadRequestException(Constants.YOU_CANNOT_UPDATE_PAYMENT_STATUS);           //kooch validation he kb update kr skte he or kab nahi
        
        sale.setPaymentStatus(request.getPaymentStatus());
        saleRepo.save(sale);

        return mapToResponse(sale);
    }

    // ================= GET ALL WITH PAGINATION =================

    @Override
    public PaginationResponse<SaleResponse> getAllSales() {
    	
      	LeadServiceImpl.leadValidations();

    	User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("saleId").descending());

        Page<Sale> listOfSale = saleRepo.findByOwnerUserId(ownerOfLoggedUser.getUserId(),pageable);
        
        if(listOfSale.isEmpty()) throw new ResourceNotFoundException(Constants.SALE_NOT_FOUND);

   	    List<SaleResponse> dtoPage = listOfSale.map(this::mapToResponse).toList();

   	  return new PaginationResponse<>(
         		dtoPage,
         		listOfSale.getNumber(),
         		listOfSale.getSize(),
         		listOfSale.getTotalElements(),
         		listOfSale.getTotalPages(),
         		listOfSale.isLast());
    }

    // ================= MONTHLY SUMMARY =================

    @Override
    public SaleSummaryResponse getMonthlySales(int month, int year) {
    	
      	LeadServiceImpl.leadValidations();
    	User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();

        YearMonth ym = YearMonth.of(year, month);

        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Sale> successSales = saleRepo.findByOwnerUserIdAndPaymentStatusAndSaleDateBetween(ownerOfLoggedUser.getUserId(),PaymentStatus.SUCCESS,start, end);      
        List<Sale> pendingSales = saleRepo.findByOwnerUserIdAndPaymentStatusAndSaleDateBetween(ownerOfLoggedUser.getUserId(),PaymentStatus.PENDING,start, end);      

        BigDecimal successSalesAmount = successSales.stream()
                .map(Sale::getSaleAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal pendingSaleAmount = pendingSales.stream()
        		.map(Sale::getSaleAmount)
        		.reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return SaleSummaryResponse.builder()
                .totalAmount(successSalesAmount)
                .totalSales(Long.valueOf(successSales.size()))
                .pendingAmount(pendingSaleAmount)
                .month(month)
                .year(year)
                .build();        
    }

    // ================= YEARLY SUMMARY =================

    @Override
    public SaleSummaryResponse getYearlySales(int year) {
      	LeadServiceImpl.leadValidations();

    	User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<Sale> successSales = saleRepo.findByOwnerUserIdAndPaymentStatusAndSaleDateBetween(ownerOfLoggedUser.getUserId(),PaymentStatus.SUCCESS,start, end);      
        List<Sale> pendingSales = saleRepo.findByOwnerUserIdAndPaymentStatusAndSaleDateBetween(ownerOfLoggedUser.getUserId(),PaymentStatus.PENDING,start, end);      

        BigDecimal successSalesAmount = successSales.stream()
                .map(Sale::getSaleAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal pendingSaleAmount = pendingSales.stream()
        		.map(Sale::getSaleAmount)
        		.reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return SaleSummaryResponse.builder()
                .totalAmount(successSalesAmount)
                .totalSales(Long.valueOf(successSales.size()))
                .pendingAmount(pendingSaleAmount)
                .year(year)
                .build();       
    }

    // ================= GET BY ID =================

    @Override
    public SaleResponse getSaleById(String saleId) {
      	LeadServiceImpl.leadValidations();
      	
    	User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();

        Sale sale = saleRepo.findBySaleIdAndOwnerUserId(saleId,ownerOfLoggedUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.SALE_NOT_FOUND));

        return mapToResponse(sale);
    }

    // ================= MAP TO RESPONSE =================
    private Sale mapToEntity(SaleRequest saleRequest,Deal deal) {
    	User loggedUser =helper.getLoggedUser();
    	
    return Sale.builder()
            .deal(deal)
            .saleAmount(saleRequest.getSaleAmount())
            .invoiceNumber(saleRequest.getInvoiceNumber())
            .saleDate(saleRequest.getSaleDate() != null ? saleRequest.getSaleDate() : LocalDate.now())
            .paymentStatus(PaymentStatus.PENDING)
            .createdBy(loggedUser)
            .owner(loggedUser.getOwner())
            .build();
    }
    // ================= MAP TO RESPONSE =================

    private SaleResponse mapToResponse(Sale sale) {

        return SaleResponse.builder()
                .saleId(sale.getSaleId())
                .deal(sale.getDeal().getDealId())
                .saleAmount(sale.getSaleAmount())
                .paymentStatus(sale.getPaymentStatus())
                .invoiceNumber(sale.getInvoiceNumber())
                .saleDate(sale.getSaleDate())
                .createdBy(sale.getCreatedBy().getUserName())
                .createdByEmail(sale.getCreatedBy().getUserEmail())
                .owner(sale.getOwner().getUserName())
                .ownerEmail(sale.getOwner().getUserEmail())
                .build();
    }


}
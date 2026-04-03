package com.company.salestracker.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.GetMonthlyRequest;
import com.company.salestracker.dto.request.GetYearlySalesRequest;
import com.company.salestracker.dto.request.PaymentStatusRequest;
import com.company.salestracker.dto.request.SaleRequest;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.dto.response.SaleResponse;
import com.company.salestracker.dto.response.SaleSummaryResponse;
import com.company.salestracker.entity.AuditLog;
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
import com.company.salestracker.service.AuditService;
import com.company.salestracker.service.SalesService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.Helper;

@Service
public class SaleServiceImpl implements SalesService {

    @Autowired private  SalesRepository saleRepo;
    @Autowired private  DealRepository dealRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private Helper helper;
    @Autowired private AuditService auditService;
    public static final boolean NOT_DELETED = false;

    // ================= CREATE =================

    @Override
    public SaleResponse createSale(SaleRequest saleRequest) {
    	
      	LeadServiceImpl.leadValidations();

    	User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser =helper.getLoggedUser();

         saleRepo.findByDealDealIdAndOwnerUserId(saleRequest.getDeal(),ownerOfLoggedUser.getUserId())
        		        .ifPresent(u -> {  throw new ResourceAlreadyExistsException(Constants.SALE_ALREADY_EXISTS);});  // same deal id se koi sale hematlb sale exist krti he toh exception
                 	
        
   	    Deal deal = dealRepo.findByDealIdAndOwnerUserIdAndDeleted(saleRequest.getDeal(),ownerOfLoggedUser.getUserId(),NOT_DELETED)
             .orElseThrow(() -> new ResourceNotFoundException(Constants.DEAL_NOT_FOUND));                                // yadi deal id jo sale me convert ho rhi he wo exist krti he ya nah
        		                         
        if (!deal.getDealStage().equals(DealStatus.CLOSED_WON)) {                         // deal should be closed Won means success hona chaiye
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

        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Create sale").entityName("Sale").entityId(savedSales.getSaleId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());

        return mapToResponse(savedSales);
    }

    // ================= UPDATE PAYMENT =================

    @Override
    public SaleResponse updatePaymentStatus(PaymentStatusRequest request,String saleId) {
    	
      	LeadServiceImpl.leadValidations();


    	User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser =helper.getLoggedUser();

        Sale sale = saleRepo.findBySaleIdAndOwnerUserId(saleId,ownerOfLoggedUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.SALE_NOT_FOUND));      // sale exist or not 

        if(!sale.getPaymentStatus().canMoveTo(request.getPaymentStatus()))
        	throw new BadRequestException(Constants.YOU_CANNOT_UPDATE_PAYMENT_STATUS);           //kooch validation he kb update kr skte he or kab nahi
        
        sale.setPaymentStatus(request.getPaymentStatus());
        saleRepo.save(sale);
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Update sale").entityName("Sale").entityId(sale.getSaleId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());


        return mapToResponse(sale);
    }

    // ================= GET ALL WITH PAGINATION =================

    @Override
    public PaginationResponse<SaleResponse> getAllSales(int pageNumber,int pageSize) {
    	
      	LeadServiceImpl.leadValidations();

    	User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser =helper.getLoggedUser();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("saleId").descending());

        Page<Sale> listOfSale = saleRepo.findByOwnerUserId(ownerOfLoggedUser.getUserId(),pageable);
        
     //   if(listOfSale.isEmpty()) throw new ResourceNotFoundException(Constants.SALE_NOT_FOUND);

   	    List<SaleResponse> dtoPage = listOfSale.map(this::mapToResponse).toList();
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get All sales sale").entityName("Sale").entityId(null).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());


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
    public SaleSummaryResponse getMonthlySales(GetMonthlyRequest getMonthlySaleRequest) {
    	
      	LeadServiceImpl.leadValidations();
    	User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser =helper.getLoggedUser();

        YearMonth ym = YearMonth.of(getMonthlySaleRequest.getYear(), getMonthlySaleRequest.getMonth());  // pehele humare pas month year int me arhe toh use month or year ki help se hum YearMonth ka object leke jisse humare pr use jo month rahega uski tart and end date nikal sake

        LocalDate start = ym.atDay(1);                      // then ym se hum month start date nikalenge
        LocalDate end = ym.atEndOfMonth();                  // the end end date kyon ki hume monthly sale chahiye

        List<Sale> successSales = saleRepo.findByOwnerUserIdAndPaymentStatusAndSaleDateBetween(ownerOfLoggedUser.getUserId(),PaymentStatus.SUCCESS,start, end);      // ab us month ki hum success deal ya jiska payment ho chuka he wo nikalneg 
        List<Sale> pendingSales = saleRepo.findByOwnerUserIdAndPaymentStatusAndSaleDateBetween(ownerOfLoggedUser.getUserId(),PaymentStatus.PENDING,start, end);      // or kitni pending he

        BigDecimal successSalesAmount = successSales.stream()
                .map(Sale::getSaleAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);       // overoll month ka suucess sale ka total amount 
        
        BigDecimal pendingSaleAmount = pendingSales.stream()
        		.map(Sale::getSaleAmount)
        		.reduce(BigDecimal.ZERO, BigDecimal::add);          // overoll month ka pending sale ka totol amount
        
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get monthly sale").entityName("Sale").entityId(null).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());

        
        return SaleSummaryResponse.builder()
                .totalAmount(successSalesAmount)
                .totalSales(Long.valueOf(successSales.size()))
                .pendingAmount(pendingSaleAmount)
                .month(getMonthlySaleRequest.getMonth())
                .year(getMonthlySaleRequest.getYear())
                .build();        
    }

    // ================= YEARLY SUMMARY =================

    @Override
    public SaleSummaryResponse getYearlySales(GetYearlySalesRequest getYearlySalesRequest) {     //same as monthly
      	LeadServiceImpl.leadValidations();

    	User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser =helper.getLoggedUser();

        LocalDate start = LocalDate.of(getYearlySalesRequest.getYear(), 1, 1);
        LocalDate end = LocalDate.of(getYearlySalesRequest.getYear(), 12, 31);

        List<Sale> successSales = saleRepo.findByOwnerUserIdAndPaymentStatusAndSaleDateBetween(ownerOfLoggedUser.getUserId(),PaymentStatus.SUCCESS,start, end);      
        List<Sale> pendingSales = saleRepo.findByOwnerUserIdAndPaymentStatusAndSaleDateBetween(ownerOfLoggedUser.getUserId(),PaymentStatus.PENDING,start, end);      

        BigDecimal successSalesAmount = successSales.stream()
                .map(Sale::getSaleAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal pendingSaleAmount = pendingSales.stream()
        		.map(Sale::getSaleAmount)
        		.reduce(BigDecimal.ZERO, BigDecimal::add);
        
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get yearly sale").entityName("Sale").entityId(null).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());

        
        return SaleSummaryResponse.builder()
                .totalAmount(successSalesAmount)
                .totalSales(Long.valueOf(successSales.size()))
                .pendingAmount(pendingSaleAmount)
                .year(getYearlySalesRequest.getYear())
                .build();       
    }

    // ================= GET BY ID =================

    @Override
    public SaleResponse getSaleById(String saleId) {
      	LeadServiceImpl.leadValidations();
      	
    	User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser =helper.getLoggedUser();

        Sale sale = saleRepo.findBySaleIdAndOwnerUserId(saleId,ownerOfLoggedUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.SALE_NOT_FOUND));
        
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get sale by id").entityName("Sale").entityId(sale.getSaleId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());


        return mapToResponse(sale);
    }
    
    
    // ================= get Yearly Sales By User =================
	@Override
	public SaleSummaryResponse getYearlySalesByUser(GetYearlySalesRequest getYearlySalesRequest,String commissionUserId) { // ye particular kis bande ne kitni sale ki he ek year me  uska record
		LeadServiceImpl.leadValidations();

    	User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser =helper.getLoggedUser();

		User commissionUser =	userRepo.findByUserIdAndOwnerUserIdAndDeleted(commissionUserId,ownerOfLoggedUser.getUserId(),NOT_DELETED).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));


        LocalDate start = LocalDate.of(getYearlySalesRequest.getYear(), 1, 1);
        LocalDate end = LocalDate.of(getYearlySalesRequest.getYear(), 12, 31);

        List<Sale> successSales = saleRepo.findByOwnerUserIdAndCommissionUserUserIdAndPaymentStatusAndSaleDateBetween(ownerOfLoggedUser.getUserId(),commissionUser.getUserId(),PaymentStatus.SUCCESS,start, end);      
        List<Sale> pendingSales = saleRepo.findByOwnerUserIdAndCommissionUserUserIdAndPaymentStatusAndSaleDateBetween(ownerOfLoggedUser.getUserId(),commissionUser.getUserId(),PaymentStatus.PENDING,start, end);      

        BigDecimal successSalesAmount = successSales.stream()
                .map(Sale::getSaleAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal pendingSaleAmount = pendingSales.stream()
        		.map(Sale::getSaleAmount)
        		.reduce(BigDecimal.ZERO, BigDecimal::add);
        
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get yearly sale by user").entityName("Sale").entityId(null).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());

        
        return SaleSummaryResponse.builder()
                .totalAmount(successSalesAmount)
                .totalSales(Long.valueOf(successSales.size()))
                .pendingAmount(pendingSaleAmount)
                .year(getYearlySalesRequest.getYear())
                .saleUserId(commissionUser.getUserId())
                .saleUserEmail(commissionUser.getUserEmail())
                .build();       
	}
	// ================= get monthly Sales By User =================

	@Override
	public SaleSummaryResponse getMonthlySalesByUser(GetMonthlyRequest getMonthlySaleRequest,String commissionUserId) { // ye particular kis bande ne kitni sale ki he ek month me  uska record
		LeadServiceImpl.leadValidations();
    	User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser =helper.getLoggedUser();

		User commissionUser =	userRepo.findByUserIdAndOwnerUserIdAndDeleted(commissionUserId,ownerOfLoggedUser.getUserId(),NOT_DELETED).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));

    	

        YearMonth ym = YearMonth.of(getMonthlySaleRequest.getYear(), getMonthlySaleRequest.getMonth());

        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Sale> successSales = saleRepo.findByOwnerUserIdAndCommissionUserUserIdAndPaymentStatusAndSaleDateBetween(ownerOfLoggedUser.getUserId(),commissionUser.getUserId(),PaymentStatus.SUCCESS,start, end);      
        List<Sale> pendingSales = saleRepo.findByOwnerUserIdAndCommissionUserUserIdAndPaymentStatusAndSaleDateBetween(ownerOfLoggedUser.getUserId(),commissionUser.getUserId(),PaymentStatus.PENDING,start, end);      

        BigDecimal successSalesAmount = successSales.stream()
                .map(Sale::getSaleAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal pendingSaleAmount = pendingSales.stream()
        		.map(Sale::getSaleAmount)
        		.reduce(BigDecimal.ZERO, BigDecimal::add);
        
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get monthly sale by user").entityName("Sale").entityId(null).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());

        
        return SaleSummaryResponse.builder()
                .totalAmount(successSalesAmount)
                .totalSales(Long.valueOf(successSales.size()))
                .pendingAmount(pendingSaleAmount)
                .month(getMonthlySaleRequest.getMonth())
                .year(getMonthlySaleRequest.getYear())
                .saleUserId(commissionUser.getUserId())
                .saleUserEmail(commissionUser.getUserEmail())
                .build();        
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
            .commissionUser(deal.getAssignedTo())
            .createdBy(loggedUser)
            .owner(loggedUser.getOwner())
            .invoiceNumber("INV-" + UUID.randomUUID().toString().substring(0,8))
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
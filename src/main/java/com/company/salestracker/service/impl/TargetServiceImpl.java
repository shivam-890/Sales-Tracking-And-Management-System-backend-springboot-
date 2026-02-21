package com.company.salestracker.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.GetMonthlyRequest;
import com.company.salestracker.dto.request.TargetRequest;
import com.company.salestracker.dto.response.DealResponse;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.dto.response.PerformanceResponse;
import com.company.salestracker.dto.response.SaleSummaryResponse;
import com.company.salestracker.dto.response.TargetResponse;
import com.company.salestracker.entity.Deal;
import com.company.salestracker.entity.Target;
import com.company.salestracker.entity.User;
import com.company.salestracker.exception.BadRequestException;
import com.company.salestracker.exception.ResourceAlreadyExistsException;
import com.company.salestracker.exception.ResourceNotFoundException;
import com.company.salestracker.repository.TargetRepository;
import com.company.salestracker.repository.UserRepository;
import com.company.salestracker.service.SalesService;
import com.company.salestracker.service.TargetService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.Helper;

@Service
public class TargetServiceImpl implements TargetService {
	
	
	 @Autowired private TargetRepository targetRepo;
	  @Autowired  private  UserRepository userRepo;
	  @Autowired private SalesService salesService;
	  @Autowired private Helper helper;
	  private static final boolean NOT_DELETED = false;

	    // ============================= Assign Target =============================
	    @Override
	    public TargetResponse createTrget(TargetRequest tragetRequest) {
	    	LeadServiceImpl.leadValidations();

              User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();
	    	
	        User user = userRepo.findByUserIdAndOwnerUserIdAndDeleted(tragetRequest.getUserId(),ownerOfLoggedUser.getUserId(),NOT_DELETED)
	                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));

	        targetRepo.findByUserUserIdAndOwnerUserIdAndTargetMonthAndTargetYearAndDeleted(
	        		tragetRequest.getUserId(),
	        		ownerOfLoggedUser.getUserId(),
	        		tragetRequest.getTargetMonth(),
	        		tragetRequest.getTargetYear(),
	        		NOT_DELETED
	        ).ifPresent(t -> {
	            throw new ResourceAlreadyExistsException("Target already assigned for this month and year"); // eek bande ko ek month me ek targe hi assign hoga
	        });

	        Target target = mapToEntity(tragetRequest);
	        target.setUser(user);

	        return mapToResponse(targetRepo.save(target));
	    }

	    // ============================= Update Target =============================
	    @Override
	    public TargetResponse updateTarget(String targetId, TargetRequest tragetRequest) {
	    	LeadServiceImpl.leadValidations();
            User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();


	        Target existing = targetRepo.findByTargetIdAndOwnerUserIdAndDeleted(targetId,ownerOfLoggedUser.getUserId(),NOT_DELETED)
	                .orElseThrow(() -> new ResourceNotFoundException(Constants.TARGET_NOT_FOUND));
	        
	        LocalDate currentDate = LocalDate.now();
	        Integer currentYear = currentDate.getYear();
	        Integer currentMonth = currentDate.getMonthValue();
	        
	        if(existing.getTargetYear() <= currentYear && existing.getTargetMonth() <= currentMonth)
	        	 throw new BadRequestException(Constants.CANNOT_UPDATE_TARGET);                       // curent month or past month ke target ko updatenhi kr skte

	        existing.setTargetMonth(tragetRequest.getTargetMonth());
	        existing.setTargetYear(tragetRequest.getTargetYear());
	        existing.setTargetAmount(tragetRequest.getTargetAmount());

	        return mapToResponse(targetRepo.save(existing));
	    }

	    // ============================= Get By Id =============================
	    @Override
	    public TargetResponse getTargetById(String targetId) {
	    	LeadServiceImpl.leadValidations();
            User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();


	        Target target = targetRepo.findByTargetIdAndOwnerUserIdAndDeleted(targetId,ownerOfLoggedUser.getUserId(),NOT_DELETED)
	                .orElseThrow(() -> new ResourceNotFoundException(Constants.TARGET_NOT_FOUND));

	        return mapToResponse(target);
	    }

	    // ============================= Get All =============================
	    @Override
	    public PaginationResponse<TargetResponse> getAllTargets(int pageNumber,int pageSize) {
	    	LeadServiceImpl.leadValidations();
            User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();

	   	 Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("targetId").descending());
      	 
  	      Page<Target> listOfTarget = targetRepo.findByOwnerUserIdAndDeleted(ownerOfLoggedUser.getUserId(),NOT_DELETED,pageable);
          
  	    List<TargetResponse> dtoPage = listOfTarget.map(this::mapToResponse).toList();


          return new PaginationResponse<>(
          		dtoPage,
          		listOfTarget.getNumber(),
        		listOfTarget.getSize(),
        		listOfTarget.getTotalElements(),
        		listOfTarget.getTotalPages(),
        		listOfTarget.isLast());
	    }

	    // ============================= Delete =============================
	    @Override
	    public void deleteTarget(String targetId) {
	    	LeadServiceImpl.leadValidations();

	    	   User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();


		        Target existing = targetRepo.findByTargetIdAndOwnerUserIdAndDeleted(targetId,ownerOfLoggedUser.getUserId(),NOT_DELETED)
		                .orElseThrow(() -> new ResourceNotFoundException(Constants.TARGET_NOT_FOUND));
	        
	        LocalDate currentDate = LocalDate.now();
	        Integer currentYear = currentDate.getYear();
	        Integer currentMonth = currentDate.getMonthValue();
	        
	        if(existing.getTargetYear() <= currentYear && existing.getTargetMonth() <= currentMonth)
	        	 throw new BadRequestException(Constants.CANNOT_DELETE_TARGET);


	        targetRepo.softDeleteTarget(targetId);
	    }


	    // ============================= Individual Performance =============================
	    @Override
	    public PerformanceResponse getIndividualPerformance(String userId,GetMonthlyRequest getMonthlyRequest) {
	    	
	    	LeadServiceImpl.leadValidations();
            User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();

			User user =	userRepo.findByUserIdAndOwnerUserIdAndDeleted(userId,ownerOfLoggedUser.getUserId(),NOT_DELETED).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));

	        SaleSummaryResponse saleSummaryOfUser = salesService.getMonthlySalesByUser(new GetMonthlyRequest(getMonthlyRequest.getMonth(),getMonthlyRequest.getYear()), user.getUserId());
	        
	        BigDecimal achievedAmount = saleSummaryOfUser.getTotalAmount();
	        
	        Target target = targetRepo.findByUserUserIdAndOwnerUserIdAndTargetMonthAndTargetYearAndDeleted(userId, ownerOfLoggedUser.getUserId(), getMonthlyRequest.getMonth(), getMonthlyRequest.getYear(), NOT_DELETED)
	                .orElseThrow(() -> new ResourceNotFoundException(Constants.TARGET_NOT_FOUND));

	        if (target.getTargetAmount().compareTo(BigDecimal.ZERO) == 0) {
	            throw new BadRequestException("Target amount cannot be zero");
	        }
	        
	        

	        BigDecimal achivedSalePercentCompareToTarget = achievedAmount
	                .divide(target.getTargetAmount(), 4, RoundingMode.HALF_UP)
	                .multiply(BigDecimal.valueOf(100));
	        
	        
	        return PerformanceResponse.builder()
	        		   .commissionUserId(user.getUserId())
	        		   .commissionUserEmail(user.getUserEmail())
	                   .achivedAmount(achievedAmount)
	                   .targetAmount(target.getTargetAmount())
	                   .pendingAmount(saleSummaryOfUser.getPendingAmount())
	                   .month(getMonthlyRequest.getMonth())
	                   .year(getMonthlyRequest.getYear())
	                   .achivedSalePercentCompareToTarget(achivedSalePercentCompareToTarget)
	                   .build();
	    }

	    // ============================= Team Performance =============================
	    @Override
	    public PerformanceResponse getTeamPerformance(GetMonthlyRequest getMonthlyRequest) {
	    	LeadServiceImpl.leadValidations();
            User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();


	        List<Target> targets = targetRepo
	                .findByTargetMonthAndTargetYearAndOwnerUserIdAndDeleted(getMonthlyRequest.getMonth(), getMonthlyRequest.getYear(),ownerOfLoggedUser.getUserId(),NOT_DELETED);

	        BigDecimal totalTarget = targets.stream()
	                .map(Target::getTargetAmount)
	                .reduce(BigDecimal.ZERO, BigDecimal::add);

	        if (totalTarget.compareTo(BigDecimal.ZERO) == 0) {
	            throw new BadRequestException("Total team target is zero");
	        }
	        
             SaleSummaryResponse saleSummaryOfUser = salesService.getMonthlySales(new GetMonthlyRequest(getMonthlyRequest.getMonth(),getMonthlyRequest.getYear()));
	        
	        BigDecimal totalTeamAchieved = saleSummaryOfUser.getTotalAmount();
	        
	        BigDecimal achivedSalePercentCompareToTarget = totalTeamAchieved
	                .divide(totalTarget, 4, RoundingMode.HALF_UP)
	                .multiply(BigDecimal.valueOf(100));
	        
	        return PerformanceResponse.builder()
	                   .achivedAmount(totalTeamAchieved)
	                   .targetAmount(totalTarget)
	                   .pendingAmount(saleSummaryOfUser.getPendingAmount())
	                   .month(getMonthlyRequest.getMonth())
	                   .year(getMonthlyRequest.getYear())
	                   .achivedSalePercentCompareToTarget(achivedSalePercentCompareToTarget)
	                   .build();
	    }

	    // ============================= Mapping Methods =============================
	    private Target mapToEntity(TargetRequest targetRequest) {

	    	 User loggedUser = helper.getLoggedUser();
	    	
	       return Target.builder()
	    		        .targetMonth(targetRequest.getTargetMonth())
	    		        .targetYear(targetRequest.getTargetYear())
	    		        .targetAmount(targetRequest.getTargetAmount())
	    		        .createdBy(loggedUser)
	    		        .owner(loggedUser.getOwner())
	    		        .deleted(NOT_DELETED)
	    		        .build();
	    	
	        
	    }

	    private TargetResponse mapToResponse(Target targetRequest) {

	        return TargetResponse.builder()
	                .targetId(targetRequest.getTargetId())
	                .user(targetRequest.getUser().getUserName())
	                .userEmail(targetRequest.getUser().getUserEmail())
	                .targetMonth(targetRequest.getTargetMonth())
	                .targetYear(targetRequest.getTargetYear())
	                .targetAmount(targetRequest.getTargetAmount())
	                .createdBy(targetRequest.getCreatedBy() != null ? targetRequest.getCreatedBy().getUserId() : null)
	                .createdByEmail(targetRequest.getCreatedBy() != null ? targetRequest.getCreatedBy().getUserEmail() : null)
	                .owner(targetRequest.getOwner() != null ? targetRequest.getOwner().getUserId() : null)
	                .ownerEmail(targetRequest.getOwner() != null ? targetRequest.getOwner().getUserEmail() : null)
	                .build();
	    }
	    
	    
	  

}

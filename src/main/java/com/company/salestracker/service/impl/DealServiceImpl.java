package com.company.salestracker.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.AssignDealRequest;
import com.company.salestracker.dto.request.DealRequest;
import com.company.salestracker.dto.request.UpdateDealRequest;
import com.company.salestracker.dto.request.UpdateDealStatusRequest;
import com.company.salestracker.dto.response.DealResponse;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.entity.AuditLog;
import com.company.salestracker.entity.Deal;
import com.company.salestracker.entity.Lead;
import com.company.salestracker.entity.User;
import com.company.salestracker.enums.DealStatus;
import com.company.salestracker.enums.LeadStatus;
import com.company.salestracker.exception.BadRequestException;
import com.company.salestracker.exception.ResourceNotFoundException;
import com.company.salestracker.repository.DealRepository;
import com.company.salestracker.repository.LeadRepository;
import com.company.salestracker.repository.UserRepository;
import com.company.salestracker.service.AuditService;
import com.company.salestracker.service.DealService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.Helper;

@Service
public class DealServiceImpl implements DealService {

    @Autowired
    private DealRepository dealRepo;
    @Autowired
    private LeadRepository leadRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired 
    private AuditService auditService;
    private static final boolean NOT_DELETED = false;


    @Autowired
    private Helper helper;

    // =========================================== CREATE DEAL ===========================================

    @Override
    public DealResponse createDeal(DealRequest dealRequest) {
    	
    	User ownerOfloggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser = helper.getLoggedUser();
    	LeadServiceImpl.leadValidations();
    	
    	User assignTo = userRepo.findByUserIdAndOwnerUserIdAndDeleted(dealRequest.getAssignedTo(), ownerOfloggedUser.getUserId(), NOT_DELETED)
    			                  .orElseThrow(() -> new ResourceNotFoundException(Constants.ASSIGNED_USER_NOT_FOUND));
    	
    	Lead lead = leadRepo.findByLeadIdAndOwnerUserIdAndDeleted(dealRequest.getLead(), ownerOfloggedUser.getUserId(), NOT_DELETED) 
    	               .orElseThrow(() -> new ResourceNotFoundException(Constants.LEAD_NOT_FOUND));
    	
    	if(lead.getStatus() != LeadStatus.QUALIFIED)
    		throw new BadRequestException("Lead is not qualified");
    	
        
        Deal deal = mapToEntity(dealRequest);
        
        if(lead !=null)
        	deal.setLead(lead);
        
        if(assignTo != null)
        	 deal.setAssignedTo(assignTo);
        
        
        Deal savedDeal = dealRepo.save(deal);
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Create deal").entityName("Deal").entityId(savedDeal.getDealId()).timestamp(LocalDateTime.now()).ownerId(ownerOfloggedUser).build());

        return mapToDto(savedDeal);
    }

    // =========================================== UPDATE DEAL ===========================================

    @Override
    public DealResponse updateDeal(String dealId, UpdateDealRequest updateDealRequest) {
    	User ownerOfloggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser = helper.getLoggedUser();


    	LeadServiceImpl.leadValidations();

        Deal deal = dealRepo.findByDealIdAndOwnerUserIdAndDeleted(dealId,ownerOfloggedUser.getUserId(),NOT_DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found"));

        boolean sameDate = Objects.equals(
                updateDealRequest.getClosingDate(), 
                deal.getClosingDate()
        );

        boolean sameAmount = 
                updateDealRequest.getExpectedAmount() != null &&
                deal.getExpectedAmount() != null &&
                updateDealRequest.getExpectedAmount()
                        .compareTo(deal.getExpectedAmount()) == 0;

        if (sameDate && sameAmount) {
            return mapToDto(deal);
        }
        		  
        	deal.setClosingDate(updateDealRequest.getClosingDate());
        	deal.setExpectedAmount(updateDealRequest.getExpectedAmount());

        Deal updatedDeal = dealRepo.save(deal);
        
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Update deal").entityName("Deal").entityId(updatedDeal.getDealId()).timestamp(LocalDateTime.now()).ownerId(ownerOfloggedUser).build());


        return mapToDto(updatedDeal);
    }

    // =========================================== GET ALL DEALS ===========================================

    @Override
    public PaginationResponse<DealResponse> getAllDeals(int pageNumber,int pageSize) {

    	User loggedUser = helper.getLoggedUser();
    	LeadServiceImpl.leadValidations();
    	
      	 Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("dealId").descending());
      	 
   	      Page<Deal> listOfDeal = dealRepo.findByOwner_UserIdAndDeleted(loggedUser.getOwner().getUserId(),NOT_DELETED,pageable);
           
   	    List<DealResponse> dtoPage = listOfDeal.map(this::mapToDto).toList();


        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get all deal").entityName("Deal").entityId(null).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());

   	    
           return new PaginationResponse<>(
           		dtoPage,
         		listOfDeal.getNumber(),
         		listOfDeal.getSize(),
         		listOfDeal.getTotalElements(),
         		listOfDeal.getTotalPages(),
         		listOfDeal.isLast());
    }

    // =========================================== GET DEAL BY ID ===========================================

    @Override
    public DealResponse getDealById(String dealId) {
    	User loggedUser = helper.getLoggedUser();

    	LeadServiceImpl.leadValidations();

        Deal deal = dealRepo.findByDealIdAndOwnerUserIdAndDeleted(dealId, loggedUser.getOwner().getUserId(), NOT_DELETED)
                .orElseThrow(() -> new RuntimeException("Deal not found"));
        
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get deal by id").entityName("Deal").entityId(deal.getDealId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());


        return mapToDto(deal);
    }

    // =========================================== GET DEAL BY ASSIGNED TO ===========================================

    @Override
    public PaginationResponse<DealResponse> getByAssignTo(String assignTo,int pageNumber,int pageSize) {
    	LeadServiceImpl.leadValidations();

    	User ownerOfloggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser = helper.getLoggedUser();

    	
    	 userRepo.findByUserIdAndOwnerUserIdAndDeleted(assignTo,ownerOfloggedUser.getUserId(),NOT_DELETED)                           
           .orElseThrow(() -> new ResourceNotFoundException(Constants.ASSIGNED_USER_NOT_FOUND)); 
    	
   	 Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("dealId").descending());
	      Page<Deal> listOfDeal=null;
	    
	      listOfDeal = dealRepo.findByAssignedTo_UserIdAndOwnerUserIdAndDeleted(assignTo,ownerOfloggedUser.getUserId(),NOT_DELETED,pageable);
        
	    List<DealResponse> dtoPage = listOfDeal.map(this::mapToDto).toList();
	    
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get deal by assignTo").entityName("Deal").entityId(null).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());



        return new PaginationResponse<>(
        		dtoPage,
      		listOfDeal.getNumber(),
      		listOfDeal.getSize(),
      		listOfDeal.getTotalElements(),
      		listOfDeal.getTotalPages(),
      		listOfDeal.isLast());
    }

    // =========================================== DELETE DEAL ===========================================

    @Override
    public boolean deleteDeal(String dealId) {
    	LeadServiceImpl.leadValidations();
    	User ownerOfloggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser = helper.getLoggedUser();



    	 Deal deal = dealRepo.findByDealIdAndOwnerUserIdAndDeleted(dealId,ownerOfloggedUser.getUserId(),NOT_DELETED)
                 .orElseThrow(() -> new ResourceNotFoundException("Deal not found"));
    	 
    	 if(deal.getDealStage() != DealStatus.NEGOTIATION)
    		   throw new BadRequestException(Constants.CANNOT_DELETE_DEAL);
        
    	 
        
    	deal.setDeleted(true);
    	deal.setDealStage(DealStatus.CLOSED_LOST);
        dealRepo.save(deal);
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Delete deal").entityName("Deal").entityId(dealId).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());

        return true;
    }

    // =========================================== UPDATE STATUS ===========================================

    @Override
    public DealResponse updateStatus(UpdateDealStatusRequest updateDealStatusRequest) {
    	LeadServiceImpl.leadValidations();

    	User ownerOfloggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser = helper.getLoggedUser();

    	
   	 Deal deal = dealRepo.findByDealIdAndOwnerUserIdAndDeleted(updateDealStatusRequest.getDealId(),ownerOfloggedUser.getUserId(),NOT_DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.DEAL_NOT_FOUND));

       
        if (!deal.getDealStage().canMoveTo(updateDealStatusRequest.getDealStage())) 
                     throw new BadRequestException("You cannot update stage");
        
        deal.setDealStage(updateDealStatusRequest.getDealStage());
        Deal updatedDeal = dealRepo.save(deal);

        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Update deal status").entityName("Deal").entityId(deal.getDealId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());

        
        return mapToDto(updatedDeal);
    }
    
    @Override
    public DealResponse assignDeal(AssignDealRequest assignDealRequest, String dealId) {
    	User ownerOfloggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser = helper.getLoggedUser();


    	Deal deal = dealRepo.findByDealIdAndOwnerUserIdAndDeleted(dealId,ownerOfloggedUser.getUserId(),NOT_DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.LEAD_NOT_FOUND));

        User user = userRepo.findByUserIdAndOwnerUserIdAndDeleted(assignDealRequest.getAssignTo(),ownerOfloggedUser.getUserId(),NOT_DELETED)                           
 	           .orElseThrow(() -> new ResourceNotFoundException(Constants.ASSIGNED_USER_NOT_FOUND));

        deal.setAssignedTo(user);

        Deal updatedDeal = dealRepo.save(deal);
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Assign deal").entityName("Deal").entityId(deal.getDealId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());


        return mapToDto(updatedDeal);
    }

    // =========================================== MAP TO ENTITY ===========================================

    private Deal mapToEntity(DealRequest dealRequest) {

        User loggedUser = helper.getLoggedUser();

        return Deal.builder()
                .lead(null) // assuming lead passed
                .dealStage(DealStatus.PROPOSAL)
                .expectedAmount(dealRequest.getExpectedAmount())
                .closingDate(dealRequest.getClosingDate())
                .owner(loggedUser.getOwner())
                .createdBy(loggedUser)
                .assignedTo(null)
                .deleted(false)
                .build();
    }

    // =========================================== MAP TO DTO ===========================================

    private DealResponse mapToDto(Deal deal) {

        return DealResponse.builder()
                .dealId(deal.getDealId())
                .leadName(deal.getLead() != null ? deal.getLead().getLeadName() : null)
                .dealStage(deal.getDealStage())
                .expectedAmount(deal.getExpectedAmount())
                .closingDate(deal.getClosingDate())
                .ownerName(deal.getOwner() != null ? deal.getOwner().getUserName() : null)
                .createdByName(deal.getCreatedBy() != null ? deal.getCreatedBy().getUserName() : null)
                .assignedToName(deal.getAssignedTo() != null ? deal.getAssignedTo().getUserName() : null)
                .build();
    }

	
}

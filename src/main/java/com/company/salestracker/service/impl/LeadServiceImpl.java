package com.company.salestracker.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.AssignLeadRequest;
import com.company.salestracker.dto.request.LeadRequest;
import com.company.salestracker.dto.request.UpdateLeadRequest;
import com.company.salestracker.dto.request.UpdateLeadStatusRequest;
import com.company.salestracker.dto.response.LeadActivityResponse;
import com.company.salestracker.dto.response.LeadResponse;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.entity.AuditLog;
import com.company.salestracker.entity.Lead;
import com.company.salestracker.entity.LeadActivity;
import com.company.salestracker.entity.User;
import com.company.salestracker.enums.LeadStatus;
import com.company.salestracker.exception.AccessDeniedException;
import com.company.salestracker.exception.BadRequestException;
import com.company.salestracker.exception.ResourceAlreadyExistsException;
import com.company.salestracker.exception.ResourceNotFoundException;
import com.company.salestracker.repository.LeadActivityRepository;
import com.company.salestracker.repository.LeadRepository;
import com.company.salestracker.repository.UserRepository;
import com.company.salestracker.service.AuditService;
import com.company.salestracker.service.LeadService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.Helper;
@Service
public class LeadServiceImpl implements LeadService {

    @Autowired
    private LeadRepository leadRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private LeadActivityRepository leadActivityRepository;
    @Autowired
    private AuditService auditService;
//    @Autowired
    private static Helper helper;
    
    public LeadServiceImpl(Helper helper) {
        LeadServiceImpl.helper = helper;
    }
    private static final boolean NOT_DELETED = false;
  
    // ========================================== CREATE LEAD ==================================================

    @Override
    public LeadResponse createLead(LeadRequest leadRequest) {
    	User ownerOfloggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser = helper.getLoggedUser();

    	User assignToUser = null;
    	
    	leadValidations();
    	
    	leadRepo.findByLeadEmailAndDeleted(leadRequest.getLeadEmail(),NOT_DELETED)  // yadi is email se lead already he toh save nahi hogi
    	               .ifPresent( u -> {throw new ResourceAlreadyExistsException(Constants.LEAD_ALREADY_EXIST);});
    	
    	if(!(leadRequest.getAssignedTo()==null || leadRequest.getAssignedTo().isBlank())) {
   		  assignToUser = userRepo.findByUserIdAndOwnerUserIdAndDeleted(leadRequest.getAssignedTo(),ownerOfloggedUser.getUserId(),NOT_DELETED)                           
	           .orElseThrow(() -> new ResourceNotFoundException(Constants.ASSIGNED_USER_NOT_FOUND));          // check assignto user is exist or not and also check assignto user same company ka hona chahiye means assign to user or logged user ke owner same hona chahiye
   		 }

        Lead lead = mapToEntity(leadRequest);
        lead.setAssignedTo(assignToUser);
        Lead savedLead = leadRepo.save(lead);
        
        addLeadActivity(savedLead, "Lead add", "new lead add");
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Create lead").entityName("Lead").entityId(savedLead.getLeadId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());

        
        return mapToDto(savedLead);
    }

    // ========================================== UPDATE LEAD ==================================================

    @Override
    public LeadResponse updateLead(String leadId, UpdateLeadRequest updateLeadRequest) {
    	
    	User ownerOfloggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser = helper.getLoggedUser();

    	
    	leadValidations();
    	if ((updateLeadRequest.getLeadName() == null || updateLeadRequest.getLeadName().isBlank()) &&
    		    (updateLeadRequest.getLeadPhone() == null || updateLeadRequest.getLeadPhone().isBlank()) &&
    		    (updateLeadRequest.getSource() == null || updateLeadRequest.getSource().isBlank())) {

    		    throw new BadRequestException(Constants.ATLEAST_FILL_ONE_FEILD);
    		}
    	Lead lead = leadRepo.findByLeadIdAndOwnerUserIdAndDeleted(leadId,ownerOfloggedUser.getUserId(),NOT_DELETED) 
    			                 .orElseThrow(() -> new ResourceNotFoundException(Constants.LEAD_NOT_FOUND));

        lead.setLeadName(updateLeadRequest.getLeadName());
        lead.setLeadPhone(updateLeadRequest.getLeadPhone());
        lead.setSource(updateLeadRequest.getSource());

        Lead updatedLead = leadRepo.save(lead);
        
        addLeadActivity(updatedLead, "Lead update", "lead updated");
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Update Lead").entityName("Lead").entityId(updatedLead.getLeadId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());


        return mapToDto(updatedLead);
    }

    // ========================================== GET ALL LEADS ==================================================

    @Override
    public PaginationResponse<LeadResponse> getAllLeads(int pageNumber,int pageSize) {

    	User loggedUser = helper.getLoggedUser();
    	leadValidations();
    	
    	 Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("leadId").descending());
 	    
 	    Page<Lead> listOfLead = leadRepo.findByOwner_UserIdAndDeleted(loggedUser.getOwner().getUserId(),pageable,NOT_DELETED);
 	    
  	  //  if(listOfLead.isEmpty()) throw new ResourceNotFoundException(Constants.LEAD_NOT_FOUND);

 	    List<LeadResponse> dtoPage = listOfLead.map(this::mapToDto).toList();

        addLeadActivity(null, "get al leads ", "get all leads");
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get all leads").entityName("Lead").entityId(null).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());


 	    
         return new PaginationResponse<>(
       		  dtoPage,
       		listOfLead.getNumber(),
       		listOfLead.getSize(),
       		listOfLead.getTotalElements(),
       		listOfLead.getTotalPages(),
       		listOfLead.isLast());

    }

    // ========================================== GET LEADS BY ASSIGNED TO ==================================================

    @Override
    public PaginationResponse<LeadResponse> getByAssignTo(String assignTo,int pageNumber,int pageSize) {
    	User ownerOfloggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser = helper.getLoggedUser();

    	
    	 userRepo.findByUserIdAndOwnerUserIdAndDeleted(assignTo,ownerOfloggedUser.getUserId(),NOT_DELETED)                           
  	           .orElseThrow(() -> new ResourceNotFoundException(Constants.ASSIGNED_USER_NOT_FOUND));   
    	
    	leadValidations();

    	  Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("leadId").descending());
  	    
  	    Page<Lead> listOfLead = leadRepo.findByAssignedTo_UserIdAndDeleted(assignTo,pageable,NOT_DELETED);
  	    
  	    if(listOfLead.isEmpty()) throw new ResourceNotFoundException(Constants.LEAD_NOT_FOUND);
  	    	
          
  	    List<LeadResponse> dtoPage = listOfLead.map(this::mapToDto).toList();

        addLeadActivity(null, "get leads by assign to", "get leads by assign to");
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get lead by assign to").entityName("Lead").entityId(null).ownerId(loggedUser.getOwner()).build());


  	    
          return new PaginationResponse<>(
        		  dtoPage,
        		listOfLead.getNumber(),
        		listOfLead.getSize(),
        		listOfLead.getTotalElements(),
        		listOfLead.getTotalPages(),
        		listOfLead.isLast());
    }

    // ========================================== GET LEAD BY ID ==================================================

    @Override
    public LeadResponse getLeadById(String leadId) {
    	User ownerOfloggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser = helper.getLoggedUser();
    	

    	leadValidations();

        Lead lead = leadRepo.findByLeadIdAndOwnerUserIdAndDeleted(leadId,ownerOfloggedUser.getUserId(),NOT_DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.LEAD_NOT_FOUND));
        
        addLeadActivity(lead, "get leads by lead id", "get leads by lead id");
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get lead by id").entityName("Lead").entityId(lead.getLeadId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());


     //  System.out.println(lead.getAssignedTo().getUserName());
        
        return mapToDto(lead);
    }

    // ========================================== DELETE LEAD ==================================================

    @Override
    public boolean deleteLead(String leadId) {
    	User ownerOfloggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser = helper.getLoggedUser();


    	Lead lead = leadRepo.findByLeadIdAndOwnerUserIdAndDeleted(leadId,ownerOfloggedUser.getUserId(),NOT_DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.LEAD_NOT_FOUND));
    	
    	leadValidations();
        
        if(!lead.getStatus().equals(LeadStatus.NEW))
   		 throw new BadRequestException(Constants.CANNOT_DELETE_LEAD); 


        lead.setDeleted(true);
        lead.setStatus(LeadStatus.LOST);
        leadRepo.save(lead);
        
        addLeadActivity(lead, "delete lead", "delete lead");
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Delete lead").entityName("Lead").entityId(leadId).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());
        return true;
    }

    // ========================================== UPDATE STATUS ==================================================

    @Override
    public LeadResponse updateStatus(String leadId,UpdateLeadStatusRequest updateLeadStatusRequest) {
    	
    	User ownerOfloggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser = helper.getLoggedUser();


    	Lead lead = leadRepo.findByLeadIdAndOwnerUserIdAndDeleted(leadId,ownerOfloggedUser.getUserId(),NOT_DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.LEAD_NOT_FOUND));
    	
        LeadStatus newStatus = updateLeadStatusRequest.getLeadStatus();
        
        if(newStatus == lead.getStatus())
			throw new BadRequestException("Invalid Status");
    	
    	if (!lead.getStatus().canMoveTo(newStatus)) {
			throw new BadRequestException("Invalid Status");
		}
    	
        lead.setStatus(updateLeadStatusRequest.getLeadStatus());
        Lead updatedLead = leadRepo.save(lead);
        addLeadActivity(updatedLead, "update lead status", "update lead status");
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Update lead status").entityName("Lead").entityId(lead.getLeadId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());

        
        return mapToDto(updatedLead);
    }

    // ========================================== ASSIGN LEAD ==================================================

    @Override
    public LeadResponse assignLeadById(AssignLeadRequest assignLeadRequest, String leadId) {

    	User ownerOfloggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser = helper.getLoggedUser();


    	Lead lead = leadRepo.findByLeadIdAndOwnerUserIdAndDeleted(leadId,ownerOfloggedUser.getUserId(),NOT_DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.LEAD_NOT_FOUND));

        User user = userRepo.findByUserIdAndOwnerUserIdAndDeleted(assignLeadRequest.getAssignTo(),ownerOfloggedUser.getUserId(),NOT_DELETED)                           
 	           .orElseThrow(() -> new ResourceNotFoundException(Constants.ASSIGNED_USER_NOT_FOUND));

        lead.setAssignedTo(user);

        Lead updatedLead = leadRepo.save(lead);
        
        addLeadActivity(updatedLead, "assign lead", "assign lead to employe");
        auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Assign lead").entityName("Lead").entityId(lead.getLeadId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());


        return mapToDto(updatedLead);
    }

    // ========================================== MAP TO ENTITY ==================================================

    private Lead mapToEntity(LeadRequest leadRequest) {

        User loggedUser = helper.getLoggedUser();

        return Lead.builder()
                .leadName(leadRequest.getLeadName())
                .leadEmail(leadRequest.getLeadEmail())
                .leadPhone(leadRequest.getLeadPhone())
                .source(leadRequest.getSource())
                .createdBy(loggedUser)
                .owner(loggedUser.getOwner())
                .status(LeadStatus.NEW)
                .assignedTo(null)
                .deleted(false)
                .build();
    }

    // ========================================== MAP TO DTO ==================================================

    private LeadResponse mapToDto(Lead lead) {

        return LeadResponse.builder()
                .leadId(lead.getLeadId())
                .leadName(lead.getLeadName())
                .leadEmail(lead.getLeadEmail())
                .leadPhone(lead.getLeadPhone())
                .source(lead.getSource())
                .createdByName(lead.getCreatedBy().getUserName())
                .ownerName(lead.getOwner().getUserName())
                .status(lead.getStatus())
                .assignedToName(lead.getAssignedTo() != null ? 
                        lead.getAssignedTo().getUserName() : null)
                .createdAt(lead.getCreatedAt())
                .build();
    }
    
    //======================================== LEAD VALIDATIONS =====================================================
    
    public static void leadValidations() {
    	
        User loggedUser = helper.getLoggedUser();

    	if(loggedUser.getOwner()==null)                                       // logged user yadi super admin he wo manage nahi kra skta
   		 throw new AccessDeniedException(Constants.YOU_CANNOT_MANAGE);
    }
    
    
    
    //======================================== ADD LEAD ACTIVITY =====================================================
    
    public void addLeadActivity(Lead lead,String activityType,String notes) {
    	User loggedUser = helper.getLoggedUser();
    	

    LeadActivity activity =	leadActivityRepository.save(LeadActivity.builder()
	            .lead(lead)
	            .activityType(activityType)
	            .notes(notes)
	            .createdBy(loggedUser)
	            .owner(loggedUser.getOwner())
	            .deleted(NOT_DELETED)
	            .build());
    auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Add lead Activity").entityName("LeadActivity").entityId(activity.getLeadActivityid()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());
	}
    
    //======================================== GET LEAD ACTIVITY =====================================================

	@Override
	public PaginationResponse<LeadActivityResponse> getLeadActivity(int pageNumber, int pageSize) {
	
    	User ownerOfloggedUser = helper.getOwnerOfLoggedUser();
    	User loggedUser = helper.getLoggedUser();


	leadValidations();

	  Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("transactionId").descending());
    
    Page<LeadActivity> listOfLead = leadActivityRepository.findByOwnerUserIdAndDeleted(ownerOfloggedUser.getUserId(),NOT_DELETED,pageable);
    
    if(listOfLead.isEmpty()) throw new ResourceNotFoundException(Constants.LEAD_NOT_FOUND);
    	
    
    List<LeadActivityResponse> dtoPage = listOfLead.map(this::mapToActivityDto).toList();
    

    auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("get lead Activity").entityName("LeadActivity").entityId(null).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());

    return new PaginationResponse<>(
  		  dtoPage,
  		listOfLead.getNumber(),
  		listOfLead.getSize(),
  		listOfLead.getTotalElements(),
  		listOfLead.getTotalPages(),
  		listOfLead.isLast());
		
	}
	
	private LeadActivityResponse mapToActivityDto(LeadActivity leadActivity)
	{
	            return LeadActivityResponse.builder()
	            		                   .leadActivityid(leadActivity.getLeadActivityid())
	            		                   .leadId(leadActivity.getLead().getLeadId())
	            		                   .leadEmail(leadActivity.getLead().getLeadEmail())
	            		                   .activityType(leadActivity.getActivityType())
	            		                   .notes(leadActivity.getNotes())
	            		                   .createdByEmail(leadActivity.getCreatedBy().getUserEmail())
	            		                   .ownerId(leadActivity.getOwner().getUserId())
	            		                   .ownerEmail(leadActivity.getOwner().getUserEmail())
	            		                   .build();
	}
    
    
    
    
}

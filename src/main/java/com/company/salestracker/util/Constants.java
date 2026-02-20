package com.company.salestracker.util;

public class Constants {
	
	 // ===================VALIDATION REGEX===============
	 public static final String VALID_USERNAME_REGEX = "^[A-Za-z][A-Za-z]*\\s?[A-Za-z]+$";
	 public static final String VALID_EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
	 public static final String VALID_PASSWORD_REGEX = "^[^\\s]{6}$";
	 public static final String VALID_CATEGORY_REGEX = "^[A-Za-z]+$";
	 public static final String VALID_PHONE_REGEX = "^\\+?[6-9][0-9]{9,14}$";
	 public static final String DESCRIPTION_MIN_10_WORDS_REGEX = "^(\\s*\\S+){10,}\\s*$";
	 public static final String COMPLAINT_STATUS_REGEX = "^(PENDING|IN_PROGRESS|RESOLVED|CLOSED)$";
	 // ======================User Request Validation Constants =============

	 public final static String USERNAME_ERROR = "Username invalid special character and more than one space in middile not allowed and no space allowed in start and end";
	 public final static String USERNAME_NOT_BLANK = "User name cannot be empty";
	 public final static String EMAIL_ERROR = "Email invalid , enter valid email id ";
	 public final static String EMAIL_NOT_BLANK = "Email cannot be empty";
	 public final static String PASSWORD_ERROR = "Invalid Password, Password must be at least 6 characters and no space allowed";
	 public final static String PASSWORD_NOT_BLANK = "Password cannot be empty";
	 public static final String PHONE_NOT_BLANK = "Phone number must not be empty";
	 public static final String PHONE_ERROR = "Invalid phone number format";
	 public static final String USER_NOT_FOUND = "User not found !";
	 public static final String OWNER_NOT_FOUND = "Owner not found !";	 
	 public static final String EMAIL_ALREADY_EXIST = "Email already exist";
	 public static final String PHONE_ALREADY_EXIST = "Phone number already exist";
	 public static final String USER_DELETE = "User deleted successfully";
	 public static final String USER_IS_ACTIVATE = "Usern is activate";
	 public static final String USER_IS_DEACTIVATE= "User is deactivate";
	 public static final String USER_ALREADY_ACTIVATE = "User is already activate";
	 public static final String USER_ALREADY_DEACTIVATE = "User is already deactivate";
	 public static final String USER_ASSIGNED_ROLES = "Assigned roles to user successfully";
	 public static final String USER_REMOVE_ROLES = "Remove roles from user successfully";
	 public static final String USERID_NOT_BLANKS = "User id cannot be empty!";
	 public static final String YOU_CANNOT_UPDATE_USER = "You cannot update user";
	 public static final String YOU_CANNOT_DELETE_USER = "You cannot delete user";
	 public static final String YOU_CANNOT_DELETE_ITSELF = "You cannot delete your account";
	 public static final String ACTIVATE = "ACTIVATE";
	 public static final String DEACTIVATE = "DEACTIVATE";
	 public static final String YOU_CANNOT_ACTIVATE_USER = "You have no permission to activate this user";
	 public static final String YOU_CANNOT_DEACTIVATE_USER = "You have no permission to de-activate this user";
	 public static final String YOU_CANNOT_ACTIVATE_ITSELF = "You have no permission to activate itself";
	 public static final String YOU_CANNOT_DEACTIVATE_ITSELF = "You have no permission to de-activate itself";
	 public static final String YOU_CANNOT_MANAGE = "You have no permission to manage Organization";
	 public static final String ASSIGNED_USER_NOT_FOUND = "Assign user not found !";	 

	 public static final String PENDING = "PENDING";
	 

	 public static final String INVALID_CREDENTIAL = "Invalid Creadiantial";
	 
	 // ========================User related general constants ==============
	 
	 public static final String USER_REGISTERED = "User is successfully registered";
	 public static final String USER_LOGIN = "User is successfully login";
	 public static final String LOGGED_USER_DEACTIVATED = "Your id is deactivate!";
	 
	 // ============================== Roles Related ==============================
	 
	 public static final String ROLES_NOT_BLANK = "Roles must not be empty";
	 public static final String ROLES_INVALID = "One or more roles invalid";
	 public static final String ROLE_NOT_FOUND= "Role not found !";
	 public static final String ROLE_ADD_SUCCESS= "Role is added successfully";
	 public static final String ROLE_ALREADY_EXIST= "Role already exist !";
	 public static final String ROLE_NOT_ASSIGN= "You cannot assign those roles to the user";
	 public static final String CANNOT_UPDATE_ROLE= "You cannot update role";
	 public static final String CANNOT_DELETE_ROLE= "You cannot delete role";
	 public static final String ROLE_UPDATE_SUCCESS= "Role successfully updated";
	 public static final String ROLE_REMOVE_SUCCESS= "Role successfully removed";
	 public static final String ROLE_DELETE_SUCCESS= "Role successfully deleted";
	 public static final String CANNOT_GET_ROLE= "You have no permissions to get those roles";
	 public static final String NOT_ASSIGN_SOMEROLES= "You have no permissions to assign some roles";
	 public static final String NOT_REMOVE_SOMEROLES= "You have no permissions to remove some roles";
	 
	 
//	 public static final String ROLE_REGEX= "^[A-Z]+(_[A-Z]+)*$";
//	 public static final String ROLE_ERROR= "Role invalid, role must be uppercase, and use underscore between two words SALES_MANAGER";
	 

	 // ============================== Permission Related ==============================
	 
	 public static final String PERMISSIONS_NOT_BLANK = "Permission must not be empty";
	 public static final String PERMISSION_REGEX= "^[A-Z]+(_[A-Z]+)*$";
	 public static final String PERMISSION_ERROR= "Permission invalid, permission must be uppercase,and use underscore between two words like CREATE_USER";
	 public static final String PERMISSION_ALREADY_EXIST= "Permission already exist";
	 public static final String PERMISSION_ADD_SUCCESS= "Permission add successfully";
	 public static final String PERMISSION_NOT_FOUND= "Permission not found !";
	 public static final String PERMISSION_NOT_ASSIGN= "You cannot assign those permissions to the role ";
	 public static final String PERMISSION_NOT_REMOVE= "You cannot remove those permissions to the role ";
	 
	 
	 // =========================== Validation releted ======================================
	 
	 public static final String ACCESS_DENIED= "You don’t have permission to do what you want ";
	 
	 // ============================= Admin related =====================================
	 
	 public static final String ADMIN_NOT_BLANK= "Admin id cannot beempty ";
	 public static final String ADMIN= "ADMIN";
	 public static final String ROLE_SUPER_ADMIN= "ROLE_SUPER_ADMIN";
	 public static final String OTHER= "OTHER";
	 
	 
	 // ============================= LEAD RELATED =====================================
	 public final static String LEADNAME_ERROR = "Lead name invalid special character and more than one space in middile not allowed and no space allowed in start and end";
	 public final static String LEADNAME_NOT_BLANK = "Lead name cannot be empty";
	 public final static String LEAD_NOT_FOUND = "Lead not found";
	 public final static String LEAD_DELETE = "Lead successfully deleted";
	 public final static String LEAD_CREATED = "Lead successfully created";
	 public final static String LEAD_REQUIRED = "Lead is required";
	 public final static String EXPECTED_AMOUNT_REQUIRED = "Expected Amount required";
	 public final static String FUTURE_PRESENT = "Date cannot be past";
	 public final static String ASSIGNEDTO_REQUIRED = "Assign to is required";
	 public final static String SOURCE_REQUIRED = "Source is required";
	 public final static String ATLEAST_FILL_ONE_FEILD = "Atleast fill one feild";
	 public final static String CANNOT_UPDATE_LEAD = "You cannot update lead because lead in the any process";
	 public final static String CANNOT_DELETE_LEAD = "You cannot delete lead because lead in the any process";
	 public final static String STATUS_NOT_BLANK = "Status is required";
	
	 // ============================= DEAL RELATED =====================================
	 
	 public final static String CANNOT_DELETE_DEAL = "You canno delete deal beacuse deal in any process";
	 public final static String DEAL_STAGE_REQUIRED = "Deal stage required";
	 public final static String DEAL_REQUIRED = "Deal required";
	 public final static String DEAL_DELETE = "Deal delete successfully";
	 public final static String DEAL_CREATED = "Deal created successfully";
	 public final static String DEAL_NOT_FOUND= "Deal not found";
	 public final static String DEAL_SHOULD_WON = "Sale can only be created for CLOSED_WON deals";
	 
	 // ============================= SALE RELATED =====================================
	 
	 public final static String SALE_AMOUNT_REQUIRED = "Sale amount required";
	 public final static String INVOICE_REQUIRED = "Invoice number required";
	 public final static String PAYMENT_STATUS_REQUIRED = "Payment status required";
	 public final static String SALE_ALREADY_EXISTS = "Sale already exist";
	 public final static String SALE_NOT_FOUND = "Sale not found";
	 public final static String YOU_CANNOT_UPDATE_PAYMENT_STATUS = "You cannot update payment status";
	 
	 
	 
}

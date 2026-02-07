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
	 public static final String EMAIL_ALREADY_EXIST = "Email already exist";
	 public static final String PHONE_ALREADY_EXIST = "Phone number already exist";

	 public static final String INVALID_CREDENTIAL = "Invalid Creadiantial";
	 
	 // ========================User related general constants ==============
	 
	 public static final String USER_REGISTERED = "User is successfully registered";
	 public static final String USER_LOGIN = "User is successfully login";
	 
	 // ============================== Roles Related ==============================
	 
	 public static final String ROLES_NOT_BLANK = "Roles must not be empty";
	 public static final String ROLES_INVALID = "One or more roles invalid";
	 public static final String ROLE_NOT_FOUND= "Role not found !";
	 public static final String ROLE_ADD_SUCCESS= "Role is added successfully";
	 public static final String ROLE_ALREADY_EXIST= "Role already exist !";
	 public static final String ROLE_REGEX= "^[A-Z]+(_[A-Z]+)*$";
	 public static final String ROLE_ERROR= "Role invalid, role must be uppercase, and use underscore between two words like SALES_MANAGER";
	 

	 // ============================== Permission Related ==============================
	 
	 public static final String PERMISSIONS_NOT_BLANK = "Permission must not be empty";
	 public static final String PERMISSION_REGEX= "^[A-Z]+(_[A-Z]+)*$";
	 public static final String PERMISSION_ERROR= "Permission invalid, permission must be uppercase,and use underscore between two words like CREATE_USER";
	 public static final String PERMISSION_ALREADY_EXIST= "Permission already exist";
	 public static final String PERMISSION_ADD_SUCCESS= "Permission add successfully0";
	 public static final String PERMISSION_NOT_FOUND= "Permission not found !";
}

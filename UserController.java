package com.genai.codeiumapp.controller;  //NOSONAR not used in secure contexts

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.genai.codeiumapp.dto.AllDetailsDTO;
import com.genai.codeiumapp.dto.AllStoresDto;
import com.genai.codeiumapp.dto.AuthRequest;
import com.genai.codeiumapp.dto.BarGraphDto;
import com.genai.codeiumapp.dto.ChallengeDetailsDto;
import com.genai.codeiumapp.dto.EmployeeDTO;
import com.genai.codeiumapp.dto.ExcelDto;
import com.genai.codeiumapp.dto.LeaderboardDTO;
import com.genai.codeiumapp.dto.MapDto;
import com.genai.codeiumapp.dto.MyProgressCompanyDetailsDto;
import com.genai.codeiumapp.dto.NotificationDTO;
import com.genai.codeiumapp.dto.OverviewDto;
import com.genai.codeiumapp.dto.PersonalDetailsDto;
import com.genai.codeiumapp.dto.ProfileImageDTO;
import com.genai.codeiumapp.dto.SavingsDto;
import com.genai.codeiumapp.dto.TokenDto;
import com.genai.codeiumapp.dto.UploadHistoryDto;
import com.genai.codeiumapp.dto.UserSavingsDto;
import com.genai.codeiumapp.exceptions.EmployeeIdExistsException;
import com.genai.codeiumapp.exceptions.UserNotFoundException;
import com.genai.codeiumapp.model.Challenge;
import com.genai.codeiumapp.model.ChallengeAttribute;
import com.genai.codeiumapp.model.ChallengeDto;
import com.genai.codeiumapp.model.Company;
import com.genai.codeiumapp.model.Constants;
import com.genai.codeiumapp.model.ExcelData;
import com.genai.codeiumapp.model.FileDetails;
import com.genai.codeiumapp.model.MyProgressDailyData;
import com.genai.codeiumapp.model.ParticipateDto;
import com.genai.codeiumapp.model.User;
import com.genai.codeiumapp.model.UserSavings;
import com.genai.codeiumapp.repository.ChallengeRepository;
import com.genai.codeiumapp.repository.CompanyRepository;
import com.genai.codeiumapp.repository.UserRepository;
import com.genai.codeiumapp.service.OtpService;
import com.genai.codeiumapp.serviceimpl.JwtService;
import com.genai.codeiumapp.serviceimpl.UserServiceImpl;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")//NOSONAR not used in secure contexts
public class UserController {

	private UserServiceImpl userService; 
	private JwtService jwtService;
	private AuthenticationManager authenticationManager;
	private UserRepository userRepository;
	private final OtpService otpService;
	private ChallengeRepository challengeRepository;
	private CompanyRepository companyRepository;
	public UserController(UserServiceImpl userService, JwtService jwtService, AuthenticationManager authenticationManager, UserRepository userRepository,ChallengeRepository challengeRepository,OtpService otpService,
			CompanyRepository companyRepository) {
	    this.userService = userService;
	    this.jwtService = jwtService;
	    this.authenticationManager = authenticationManager;
	    this.userRepository = userRepository;
	    this.otpService = otpService;
	    
	    this.challengeRepository= challengeRepository;
	    this.companyRepository= companyRepository;
	}
	
	

	@PostMapping("userRegister")
	public String registerManager(@RequestBody User user)
	{
		user.setRegistrationDate(LocalDate.now());
		return userService.registerManager(user);
	}
	
	@PostMapping("companyRegister")
	public String companyRegister(@RequestBody Company company)
	{
		company.setRegistrationDate(LocalDate.now());
		return userService.registerCompany(company);
	}
	
	@PostMapping("/verify")
	public ResponseEntity<String> verifyOtp(@RequestBody Map<String, Object> requestData) {
	    String userEmpId = String.valueOf(requestData.get("userEmpId")); 
	    long enteredOtp = ((Number) requestData.get("enteredOtp")).longValue();

	    return userService.verifyOtp(userEmpId, enteredOtp);
	}
	

	@PostMapping("/resendOtp")
	public ResponseEntity<String> resendOtp(@RequestBody Map<String, Object> requestData) {
	    String userEmpId = String.valueOf(requestData.get("userEmpId"));
 
	    try {
	        // Retrieve user information from the database
	        Optional<User> userOptional = userRepository.findByUserEmpId(userEmpId);
	        if (!userOptional.isPresent()) {
	            throw new UserNotFoundException("User with employee ID " + userEmpId + " not found");
	        }
 
	        // Generate a new OTP
	        long newOtp = otpService.generateOtp();
 
	        // Send the new OTP to the user's email
	        otpService.sendOtp(userOptional.get().getEmail(), newOtp);
 
	        // Update the user's OTP in the database
	        User user = userOptional.get();
	        user.setOtp(newOtp);
	        userRepository.save(user);
 
	        return new ResponseEntity<>("OTP resent successfully", HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>("Error resending OTP: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	
	
	@PostMapping("/userLogin")
	public ResponseEntity<TokenDto> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
	    Optional<User> user = userRepository.findByUserEmpId(authRequest.getUsername());
        Optional<Company> company = companyRepository.findByUserEmpId(authRequest.getUsername());
	    try {
	        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
	        if (authentication.isAuthenticated()) {            
	           
	            if (user.isPresent() && user.get().isVerified()) {
	                String role = user.get().getRole();
	                String token = jwtService.generateToken(authRequest.getUsername());
	                return ResponseEntity.ok(new TokenDto(role, token));
	            } else if (company.isPresent()) {
	                String role = company.get().getRole();
	                String token = jwtService.generateToken(authRequest.getUsername());
	                return ResponseEntity.ok(new TokenDto(role, token));
	            } else {
	                throw new UsernameNotFoundException(Constants.CHECK_CREDENTIALS);//NOSONAR not used in secure contexts
	            }
	        } else {
	            throw new UsernameNotFoundException(Constants.BAD_CREDENTIALS);//NOSONAR not used in secure contexts
	        }
	    } catch (BadCredentialsException e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenDto("Invalid credentials", "Invalid credentials"));
	    } 
	}
	@PostMapping("/send-otp")
	public ResponseEntity<String> sendOtpToEmail(@RequestBody Map<String, String> requestData) {
	    String email = requestData.get("email");
 
	    try {
	        // Retrieve user information from the database
	        Optional<User> user = userRepository.findByEmail(email);
	        if (user.isPresent()) {
	            // Generate OTP
	            long otp = otpService.generateOtp();
 
	            // Send OTP to the user's email
	            otpService.sendOtp(email, otp);
 
	            // Update user's OTP in the database
	            user.get().setOtp(otp);
	            userRepository.save(user.get());
 
	            return ResponseEntity.ok("OTP sent successfully to the provided email.");
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP: " + e.getMessage());
	    }
	}
	
	
	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtpByEmail(@RequestBody Map<String, String> requestData) {
	    String email = requestData.get("email");
	    long enteredOtp = Long.parseLong(requestData.get("enteredOtp"));
	    return userService.verifyOtpByEmail(email, enteredOtp);
	}

	@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_COMPANY')")
	@GetMapping("/getUserByEmail/{email}")
	public Object getUserByEmail(@PathVariable String email) {
		return userService.getUserByEmail(email);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_COMPANY')")
	@PostMapping("/changePassword/{email}/{password}/{reEnterPassword}")
	public ResponseEntity<String> changePassword(@PathVariable String email, @PathVariable String password,
			@PathVariable String reEnterPassword) {
		try {
			userService.changePassword(email, password, reEnterPassword);
			return ResponseEntity.ok(Constants.PASSWORD_CHANGE_SUCCESS);
		} catch (EmployeeIdExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_COMPANY')")
	@PostMapping("/publish/{employeeId}")
	public ResponseEntity<Object> createPublishedChallenge(@RequestBody Challenge challenge, @PathVariable String employeeId) {
	    challenge.setStatus(Constants.PUBLISHED);
	    List<ChallengeAttribute> attributes = new ArrayList<>();
	    attributes.add(new ChallengeAttribute(null, "Attribute 1", "Reference Data 1", "Conversion Factor 1", challenge));
	    challenge.setChallengeAttributes(attributes);
	    userService.createChallenge(challenge, employeeId);	    
	    return ResponseEntity.status(HttpStatus.OK).body(Constants.PUBLISH_SUCCESS);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_COMPANY')")
	@GetMapping("/challengeDetails/{id}")
	public ResponseEntity<Challenge>  challengeDetails(@PathVariable Long id) {
		Optional<Challenge> findById = challengeRepository.findById(id);
		Challenge cl=new Challenge();
		if(findById.isPresent()) {
			return new ResponseEntity<>(findById.get(),HttpStatus.OK);
		}
		return new ResponseEntity<>(cl,HttpStatus.NOT_FOUND);
		
	}

	@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_COMPANY')")
	@PostMapping("/savedraft/{employeeId}")
	public ResponseEntity<Object> createDraftChallenge(@RequestBody Challenge challenge, @PathVariable String employeeId) {

	    challenge.setStatus(Constants.SAVEDRAFT);
	    List<ChallengeAttribute> attributes = new ArrayList<>();
	    attributes.add(new ChallengeAttribute(null, "Attribute 1", "Reference Data 1", "Conversion Factor 1", challenge));
	    challenge.setChallengeAttributes(attributes);
	    userService.createChallenge(challenge, employeeId);
	    return ResponseEntity.status(HttpStatus.OK).body(Constants.SAVEDRAFT_SUCCESS);
	}


	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/enrollChallenge/{challengeId}/{employeeId}")
	public ResponseEntity<String> enrollChallenge(@PathVariable Long challengeId, @PathVariable String employeeId) {
		try {
			String enrollmentMessage = userService.enrollChallenge(challengeId, employeeId);
			return ResponseEntity.ok(enrollmentMessage);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
	

	@PreAuthorize("hasAnyRole('ROLE_COMPANY','ROLE_MANAGER')")
	@GetMapping("/myChallenges/{required}/{employeeId}")
	public ResponseEntity<List<ChallengeDto>> getMyChallenges(@PathVariable String required,
			@PathVariable String employeeId) {
		List<ChallengeDto> myChallenges = userService.getMyChallenges(required, employeeId);
		if (myChallenges != null) {
			return ResponseEntity.ok(myChallenges);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/participateChallenges/{required}/{employeeId}")
	public ResponseEntity<List<ParticipateDto>> getParticipateChallenges(@PathVariable String required,
			@PathVariable String employeeId) {
		List<ParticipateDto> participateChallenges = userService.getParticipateChallenges(required, employeeId);
		if (participateChallenges != null) {
			return ResponseEntity.ok(participateChallenges);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/saveUserSavings")
	public ResponseEntity<String> saveUserSavings(@RequestBody UserSavings userSavings)
	{
		 
	        LocalDate date = LocalDate.parse(userSavings.getSavingsAddedOn().toString());  //NOSONAR not used in secure contexts
	        userSavings.setSavingsAddedOn(date);
		return userService.saveUserSavings(userSavings);
	}

	// if a company logs in -> retrieves all the manager savings under a company 
	// if a manager logs in -> retrieves all his/her savings for all the challenges
	@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_COMPANY')")
	@GetMapping("/getAllUserSavings/{employeeId}")
	public UserSavingsDto getUserSavings(@PathVariable String employeeId)
	{
		return userService.getUserSavings(employeeId);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_COMPANY')")
	@GetMapping("/getAllUserSavingsForChart/{employeeId}/{field}")
	public List<? extends SavingsDto> getUserSavingsInProfileForChart(@PathVariable String employeeId, @PathVariable String field)
	{
		return userService.getUserSavingsInProfileForChart(employeeId,field);
	}

	@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_COMPANY')")
	@GetMapping("/getChallengeDetails/{employeeId}")
	public ChallengeDetailsDto getChallengeDetails(@PathVariable String employeeId)
	{
		return userService.getChallengeDetails(employeeId);
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("getUserSavingForAChallenge/{employeeId}/{challengeId}")
	public List<UserSavingsDto> getUserSavingForAChallenge(@PathVariable String employeeId, @PathVariable long challengeId)
	{		
		return userService.getUserSavingForAChallenge(employeeId,challengeId);		
	}


	@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_COMPANY')")
	@GetMapping("/getPersonalDetails/{employeeId}")
	public PersonalDetailsDto getPersonalDetails(@PathVariable String employeeId,Principal principal)
	{
		
		if(principal.getName().equals(employeeId))
		{
			return userService.getPersonalDetails(employeeId);
		}
		return null;
		
	}
	
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/{employeeId}/employees")
    public ResponseEntity<List<Map<String, Object>>> getEmployeesByUserId(@PathVariable String employeeId) {
        List<Map<String, Object>> employees = userService.getEmployeesByUserId(employeeId);
        if (employees == null) {
            // Handle the case where user with given ID is not found
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employees);
    }

	
	//retrieves all the managers from the db irrespective of the company
	@PreAuthorize("hasRole('ROLE_COMPANY')")
	@GetMapping("/dashboard/company")
    public ResponseEntity<Map<String, Object>>getManagersAndEmployees() {
        Map<String, Object> managersAndEmployees = userService.getManagersAndEmployees();
        return new ResponseEntity<>(managersAndEmployees, HttpStatus.OK);
    }

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/allDetails/{employeeId}")
    public ResponseEntity<EmployeeDTO> getEmployeeDetails(@PathVariable("employeeId") String employeeId) {
        EmployeeDTO employeeDto = userService.getEmployeeDetails(employeeId);  //NOSONAR not used in secure contexts
        
        if (employeeDto != null) {
            return ResponseEntity.ok(employeeDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

	@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_COMPANY')")
	@PostMapping("/excel")
	public void generateExcel(HttpServletResponse response,@RequestBody ExcelDto excelDto) throws IOException
	{
		response.setContentType("application/octet-stream");
		String headerKey = "Content-Disposition";   //NOSONAR not used in secure contexts
		String headerValue = "attachment;filename=users.xls";   //NOSONAR not used in secure contexts
		response.setHeader(headerKey, headerValue);
		userService.generateExcel(response,excelDto);
	}

	@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_COMPANY')")
	@PutMapping("/markAllAsRead/{employeeId}")
    public ResponseEntity<String> markAllNotificationsAsRead(@PathVariable String employeeId) {
       return userService.markAllNotificationsAsRead(employeeId);
    }

	@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_COMPANY')")
	@GetMapping("/all/{employeeId}")
    public ResponseEntity<List<NotificationDTO>> getAllNotificationsByUserId(@PathVariable String employeeId) {
        List<NotificationDTO> notificationDTOs = userService.getAllNotificationsByUserId(employeeId);

        if (notificationDTOs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(notificationDTOs, HttpStatus.OK);
    }

	@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_COMPANY')")
	@GetMapping("/users/{employeeId}")
    public AllDetailsDTO getUserDetails(@PathVariable String employeeId) {
        return userService.getUserDetails(employeeId);
    }

	@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_COMPANY')")
	@PutMapping("/{Id}/{userEmpId}/approve")
    public ResponseEntity<String> approveUser(@PathVariable Long id,@PathVariable String userEmpId) {
        try {
            userService.approveUser(id,userEmpId);
            return ResponseEntity.ok("User approved successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

	@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_COMPANY')")
	@PutMapping("/{Id}/{userEmpId}/decline")
    public ResponseEntity<String> declineUser(@PathVariable Long id,@PathVariable String userEmpId) {
        try {
            userService.declineUser(id,userEmpId);
            return ResponseEntity.ok("User declined!");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    } 
  
    @GetMapping("/overview/{challengeId}/{employeeId}")
    public ResponseEntity<List<OverviewDto>> employeesOverview(@PathVariable Long challengeId, @PathVariable String employeeId) {
       List<OverviewDto> employeesOverview = userService.employeesOverview(challengeId, employeeId);
            return ResponseEntity.ok(employeesOverview);
        }

    @PostMapping("/upload/{userEmpId}")
    public ResponseEntity<ProfileImageDTO> uploadProfileImage(@PathVariable String userEmpId, @RequestParam("image") MultipartFile file) throws IOException {
        ProfileImageDTO profileImageDTO = userService.uploadProfileImage(userEmpId, file);
        return ResponseEntity.ok(profileImageDTO);
    }

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/getMySavingsCompanyDetails/{employeeId}/{challengeId}")
	public ResponseEntity<MyProgressCompanyDetailsDto> getMySavingsCompanyDetails(@PathVariable String employeeId, @PathVariable long challengeId)
	{

		return userService.getMySavingsCompanyDetails(employeeId,challengeId);
	}
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/saveTodaySavingsDetails")
	public ResponseEntity<String> saveTodaySavingsDetails(@RequestBody MyProgressDailyData myProgressDailyData)
	{

		return userService.saveDailyMyProgress(myProgressDailyData);
	}
	@GetMapping("/weekly-savings/{employeeId}/{challengeId}/{savingsType}")
	public ResponseEntity<List<? extends SavingsDto>> getWeeklySavings(@PathVariable String employeeId,
																	   @PathVariable long challengeId, @PathVariable String savingsType) {
		List<? extends SavingsDto> savingsDtoList = userService.getWeeklySavingsForAChallenge(employeeId, challengeId, savingsType);
		return ResponseEntity.ok(savingsDtoList);
	}
	@GetMapping("/challengeOverviewBarGraph/{employeeId}/{challengeId}/{field}/{xAxis}/{yAxis}")
	public ResponseEntity<List<BarGraphDto>> challengeOverviewBarGraph(@PathVariable String employeeId, 
			@PathVariable long challengeId, 
			@PathVariable String field,
			@PathVariable String xAxis, @PathVariable String yAxis, @RequestBody AllStoresDto allStoresDto)
	{
		return userService.challengeOverviewBarGraph(employeeId,challengeId,field,xAxis,yAxis,allStoresDto);
	}
	@GetMapping("/challengeOverviewDonutGraph/{employeeId}/{challengeId}/{field}/{dataType}")
	public List<List<? extends SavingsDto>> challengeOverviewDonutGraph(@PathVariable String employeeId, 
			@PathVariable long challengeId, 
			@PathVariable String field,
			@PathVariable String dataType, @RequestBody AllStoresDto allStoresDto)
	{
		
		return userService.challengeOverviewDonutGraph(employeeId,challengeId,field,dataType,allStoresDto);
	}
	
	 @GetMapping("/leaderboard/{companyEmpId}/{challengeId}/{weekNumber}")
	    public ResponseEntity<List<LeaderboardDTO>> getAllUsersCo2SavingsForChallenge(
	            @PathVariable String companyEmpId,
	            @PathVariable long challengeId,
	            @PathVariable int weekNumber) {
	        try {
	            List<LeaderboardDTO> savings = userService.getAllUsersCo2SavingsForChallenge(companyEmpId, challengeId, weekNumber);
	            return new ResponseEntity<>(savings, HttpStatus.OK);
	        } catch (EmployeeIdExistsException e) {
	            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
	        } catch (Exception e) {
	            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
		
	   @PreAuthorize("hasRole('ROLE_COMPANY')")
	   @GetMapping("/map/{employeeId}/{challengeId}")
	    public ResponseEntity<List<MapDto>> getMapDetails(@PathVariable("employeeId") String employeeId, @PathVariable("challengeId") long challengeId)
	    {
		
	    	return userService.getMapDetails(employeeId,challengeId);
	    }
	   
	   
	   @DeleteMapping("delete/{employeeId}/{challengeId}")
	    public ResponseEntity<String> deleteChallenge(@PathVariable String employeeId, @PathVariable long challengeId) {
	        return userService.deleteChallenge(employeeId, challengeId);
	    }
	   @PreAuthorize("hasRole('ROLE_COMPANY')")
	    @PostMapping("/convertToJson/{empId}")
	    public ResponseEntity<List<FileDetails>> convertExcelListToJson(@RequestParam("files") List<MultipartFile> files,@PathVariable String empId) {
	    	List<FileDetails> jsonList = userService.convertExcelListToJson(files,empId);
	        return new ResponseEntity<>(jsonList, HttpStatus.OK);
	    }
	   @PreAuthorize("hasRole('ROLE_COMPANY')")
	    @DeleteMapping("/delete-data/{empId}")
	    public ResponseEntity<String> deleteExcelDataByIds(@RequestBody List<Long> rowId,@PathVariable String empId) {
	        try {
	            String deletionMessage = userService.deleteExcelDataByIds(rowId,empId);
	            return ResponseEntity.ok(deletionMessage);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	        }
	    }
	   @PreAuthorize("hasRole('ROLE_COMPANY')")
	   @GetMapping("/uploadHistory/{employeeId}")
	   public ResponseEntity<List<UploadHistoryDto>> getUploadHistoryByEmpId(@PathVariable String employeeId) {
		   List<UploadHistoryDto> historyList = userService.getUploadHistoryByEmpId(employeeId);
		   return new ResponseEntity<>(historyList, HttpStatus.OK);
	   }
	   @PreAuthorize("hasRole('ROLE_COMPANY')")
	   @GetMapping("/getAllDetailsById/{employeeId}")
	   public ResponseEntity<List<ExcelData>> viewAllDataByEmployeeId(@PathVariable String employeeId) {
	       List<ExcelData> excelDataList = userService.viewAllDataByEmployeeId(employeeId);
	       
	       if (excelDataList != null && !excelDataList.isEmpty()) {
	           return ResponseEntity.ok(excelDataList);
	       } else {
	           return ResponseEntity.notFound().build();
	       }
	   }
	   
	   @PreAuthorize("hasRole('ROLE_COMPANY')")
	   @GetMapping("/viewByFileId/{fileId}")
	   public ResponseEntity<List<ExcelData>> viewByFileId(@PathVariable Long fileId) {
		   List<ExcelData> historyList = userService.viewByFileId(fileId);
		   return new ResponseEntity<>(historyList, HttpStatus.OK);
		   
	   }
	    
	   
	   @PutMapping("/editChallenge/{employeeId}/{challengeId}")
	   public ResponseEntity<String> editChallenge(@PathVariable String employeeId, @PathVariable long challengeId, @RequestBody Challenge challenge)
	   {
		   return userService.editChallenge(employeeId,challengeId,challenge);
	   }
	    
	   
	   
}


 
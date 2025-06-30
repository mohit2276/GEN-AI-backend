package com.genai.codeiumapp.serviceimpl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.genai.codeiumapp.constants.UserConstants;
import com.genai.codeiumapp.dto.AllDetailsDTO;
import com.genai.codeiumapp.dto.AllStoresDto;
import com.genai.codeiumapp.dto.BarGraphDto;
import com.genai.codeiumapp.dto.ChallengeDetailsDto;
import com.genai.codeiumapp.dto.EmployeeDTO;
import com.genai.codeiumapp.dto.ExcelDto;
import com.genai.codeiumapp.dto.LeaderboardDTO;
import com.genai.codeiumapp.dto.MapDto;
import com.genai.codeiumapp.dto.MonthlyCo2SavingsDto;
import com.genai.codeiumapp.dto.MonthlyDollarSavingsDto;
import com.genai.codeiumapp.dto.MonthlyWasteSavingsDto;
import com.genai.codeiumapp.dto.MyProgressCompanyDetailsDto;
import com.genai.codeiumapp.dto.NotificationDTO;
import com.genai.codeiumapp.dto.OverviewDto;
import com.genai.codeiumapp.dto.PersonalDetailsDto;
import com.genai.codeiumapp.dto.ProfileImageDTO;
import com.genai.codeiumapp.dto.SavingsDto;
import com.genai.codeiumapp.dto.UploadHistoryDto;
import com.genai.codeiumapp.dto.UploadHistoryFileDto;
import com.genai.codeiumapp.dto.UserSavingsDto;
import com.genai.codeiumapp.dto.WeeklyCo2DonutGraphDto;
import com.genai.codeiumapp.dto.WeeklyCo2SavingsDto;
import com.genai.codeiumapp.dto.WeeklyDollarDonutGraphDto;
import com.genai.codeiumapp.dto.WeeklyDollarSavingsDto;
import com.genai.codeiumapp.dto.WeeklyWasteDonutGraphDto;
import com.genai.codeiumapp.dto.WeeklyWasteSavingsDto;
import com.genai.codeiumapp.exceptions.EmployeeIdExistsException;
import com.genai.codeiumapp.exceptions.EmployeeNotFoundException;
import com.genai.codeiumapp.exceptions.UserNotFoundException;
import com.genai.codeiumapp.model.Challenge;
import com.genai.codeiumapp.model.ChallengeAttribute;
import com.genai.codeiumapp.model.ChallengeDto;
import com.genai.codeiumapp.model.ChallengeEnrollment;
import com.genai.codeiumapp.model.Company;
import com.genai.codeiumapp.model.Constants;
import com.genai.codeiumapp.model.Employee;
import com.genai.codeiumapp.model.ExcelData;
import com.genai.codeiumapp.model.FileDetails;
import com.genai.codeiumapp.model.MyProgressDailyData;
import com.genai.codeiumapp.model.Notification;
import com.genai.codeiumapp.model.ParticipateDto;
import com.genai.codeiumapp.model.StoreDetails;
import com.genai.codeiumapp.model.User;
import com.genai.codeiumapp.model.UserSavings;
import com.genai.codeiumapp.repository.ChallengeEnrollmentRepo;
import com.genai.codeiumapp.repository.ChallengeRepository;
import com.genai.codeiumapp.repository.CompanyRepository;
import com.genai.codeiumapp.repository.EmployeeRepository;
import com.genai.codeiumapp.repository.ExcelDataRepo;
import com.genai.codeiumapp.repository.FileDetailsRepo;
import com.genai.codeiumapp.repository.MyProgressDailyDataRepository;
import com.genai.codeiumapp.repository.NotificationRepository;
import com.genai.codeiumapp.repository.UserRepository;
import com.genai.codeiumapp.repository.UserSavingsRepository;
import com.genai.codeiumapp.service.OtpService;
import com.genai.codeiumapp.service.UserService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Service

public class UserServiceImpl implements UserService {

	@Autowired
	private MyProgressDailyDataRepository myProgressDailyDataRepository;
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private ExcelDataRepo excelRepo;
	
	@Autowired
	private FileDetailsRepo detailsRepo;

	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private ChallengeRepository challengeRepository;
	private UserSavingsRepository userSavingsRepository;
	private ChallengeEnrollmentRepo enrollmentRepository;
	private EmployeeRepository empRepository;
	private NotificationRepository notificationRepository;
	private ModelMapper modelMapper;
	private OtpService otpService;

	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM");
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
			ChallengeRepository challengeRepository, UserSavingsRepository userSavingsRepository,
			ChallengeEnrollmentRepo enrollmentRepository, EmployeeRepository empRepository,
			NotificationRepository notificationRepository, ModelMapper modelMapper, OtpService otpService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.challengeRepository = challengeRepository;
		this.userSavingsRepository = userSavingsRepository;
		this.enrollmentRepository = enrollmentRepository;
		this.empRepository = empRepository;
		this.notificationRepository = notificationRepository;
		this.modelMapper = modelMapper;
		this.otpService = otpService;
	}

	@Override
	public String registerManager(User user) {
		String empId = user.getUserEmpId();
		if (userRepository.existsByUserEmpIdOrEmail(empId, user.getEmail())) {
			throw new EmployeeIdExistsException(UserConstants.EMP_ID_EXISTS);
		}
		if (companyRepository.existsByUserEmpIdOrEmail(empId, user.getEmail())) {
			throw new EmployeeIdExistsException(UserConstants.EMP_ID_EXISTS);
		}

		List<Company> companies = companyRepository.findAll();
		int numCompanies = companies.size();
		int companyIndex = (int) (userRepository.count() % numCompanies);
		Company company = companies.get(companyIndex);
		long otp = otpService.generateOtp();
		user.setOtp(otp);
		otpService.sendOtp(user.getEmail(), otp);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole("ROLE_MANAGER");
		user.setCompany(company);
		userRepository.save(user);
		LocalDateTime now = LocalDateTime.now();
		String date = now.format(dateFormatter);
		String time = now.format(timeFormatter);
		Notification notification = new Notification();
		notification.setName(user.getName());
		notification.setUserEmpId(empId);
		notification.setIsRead(false);
		notification.setRegistrationDate(date);
		notification.setRegistrationTime(time);
		notification.setCompany(company);

		notificationRepository.save(notification);
		return "User has been successfully saved to the db with the id: " + user.getUserEmpId();
	}


	public String registerCompany(Company company) {
		String empId = company.getUserEmpId();
		if (userRepository.existsByUserEmpIdOrEmail(empId, company.getEmail())) {
			throw new EmployeeIdExistsException(UserConstants.EMP_ID_EXISTS);
		}
		if (companyRepository.existsByUserEmpIdOrEmail(empId, company.getEmail())) {
			throw new EmployeeIdExistsException(UserConstants.EMP_ID_EXISTS);
		}
		company.setPassword(passwordEncoder.encode(company.getPassword()));
		company.setRole("ROLE_COMPANY");
		companyRepository.save(company);
		return "Company has been successfully saved to the db with the id : " + company.getUserEmpId();
	}

	@Override
	public ResponseEntity<String> verifyOtp(String userEmpId, long enteredOtp) {
		Optional<User> user = userRepository.findByUserEmpId(userEmpId);
		if (user.isPresent() && user.get().getOtp() == enteredOtp) {
			user.get().setOtpVerified(true);
			userRepository.save(user.get());
			return new ResponseEntity<>("User verified successfully", HttpStatus.OK);
		}

		else {
			return new ResponseEntity<>("Invalid OTP", HttpStatus.BAD_REQUEST);
		}
	}

	public Object getUserByEmail(String email) {
		Optional<User> user = userRepository.findByEmail(email);
		if (user.isPresent()) {
			return user.get();
		}

		Optional<Company> company = companyRepository.findByEmail(email);
		if (company.isPresent()) {
			return company.get();
		}

		throw new EmployeeIdExistsException(Constants.EMAIL_NOT_EXISTS);
	}

	@Override
	public ResponseEntity<String> sendOtpToEmail(String email) {
	    Optional<User> user = userRepository.findByEmail(email);
	    if (user.isPresent()) {
	        long otp = otpService.generateOtp();
	        user.get().setOtp(otp);
	        otpService.sendOtp(email, otp);
	        userRepository.save(user.get());
	        return ResponseEntity.ok().body("OTP sent successfully to the provided email.");
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
	    }
	}

	@Override
	public ResponseEntity<String> verifyOtpByEmail(String email, long enteredOtp) {
	    Optional<User> user = userRepository.findByEmail(email);
	    if (user.isPresent() && user.get().getOtp() == enteredOtp) {
	        user.get().setOtpVerified(true);
	        userRepository.save(user.get());
	        return ResponseEntity.ok().body("OTP verified successfully");
	    } else {
	        return ResponseEntity.badRequest().body("Invalid OTP or email");
	    }
	}


	@Override
	public String changePassword(String email, String password, String reEnterPassword) {
		Optional<User> user = userRepository.findByEmail(email);
		Optional<Company> company = companyRepository.findByEmail(email);
		if (user.isPresent()) {
			if (password.equals(reEnterPassword)) {
				String encryptedNewPassword = passwordEncoder.encode(password);
				user.get().setPassword(encryptedNewPassword);
				userRepository.save(user.get());
				return Constants.PASSWORD_CHANGE_SUCCESS;
			} else {
				throw new EmployeeIdExistsException(Constants.PASSWORD_MISMATCH);
			}
		}
		if (company.isPresent()) {
			if (password.equals(reEnterPassword)) {
				String encryptedNewPassword = passwordEncoder.encode(password);
				company.get().setPassword(encryptedNewPassword);
				companyRepository.save(company.get());
				return Constants.PASSWORD_CHANGE_SUCCESSFUL;
			} else {
				throw new EmployeeIdExistsException(Constants.PASSWORD_MISMATCH);
			}
		}

		throw new EmployeeIdExistsException(Constants.PASSWORD_MISMATCH);

	}

	// 40 to 50%
	@Override
	public Challenge createChallenge(Challenge challenge, String employeeId) {
		Optional<User> user1 = userRepository.findByUserEmpId(employeeId);
		Optional<Company> company = companyRepository.findByUserEmpId(employeeId);
		long createdByUserId = 0;
		if (user1.isPresent()) {
			createdByUserId = user1.get().getUserId();

			List<Employee> findAll = user1.get().getEmployees();
			challenge.setCreatedBy("ROLE_MANAGER");
			if (challenge.getStatus().equals("published") && !findAll.isEmpty()) {

				challenge.setCreatedByUserId(createdByUserId);
				challenge.setUser(user1.get());
				challengeRepository.save(challenge);

			}
			if (challenge.getStatus().equals(Constants.SAVEDRAFT)) {

				challenge.setCreatedByUserId(createdByUserId);
				challenge.setUser(user1.get());
				challengeRepository.save(challenge);
			}
		}

		else if (company.isPresent()) {
			createdByUserId = company.get().getCompanyId();
			List<User> users = userRepository.findAllByCompanyCompanyId(createdByUserId);
			challenge.setCreatedBy("ROLE_COMPANY");

			if (challenge.getStatus().equals("published") && !users.isEmpty()) {

				challenge.setCreatedByUserId(company.get().getCompanyId());
				challenge.setCompany(company.get());
				for (ChallengeAttribute attribute : challenge.getChallengeAttributes()) {
					attribute.setChallenge(challenge);
				}
				challengeRepository.save(challenge);
				for (User user : users) {
					ChallengeEnrollment challengeEnrollment = new ChallengeEnrollment();
					challengeEnrollment.setMyProgress(0);
					challengeEnrollment.setChallenge(challenge);
					challengeEnrollment.setUser(user);
					challengeEnrollment.setChallengeStatus(Constants.UPCOMING_STATUS);
					enrollmentRepository.save(challengeEnrollment);
					// Add any notifications related to this specific challenge/user here
					Notification notification = new Notification();
					LocalDateTime now = LocalDateTime.now();
					String date = now.format(dateFormatter);
					String time = now.format(timeFormatter);

					notification.setChallengeName(challenge.getChallengeName());
					notification.setLaunchDate(date);
					notification.setLaunchTime(time);
					notification.setName(user.getName());
					notification.setUser(user);
					notification.setIsRead(false);
					notification.setUserEmpId(user.getUserEmpId());
					notificationRepository.save(notification);
				}
			}
			if (challenge.getStatus().equals(Constants.SAVEDRAFT)) {
				challenge.setCreatedByUserId(createdByUserId);
				challenge.setCompany(company.get());
				challengeRepository.save(challenge);
			}

		}else {
			throw new EmployeeIdExistsException("Either company or User is not found");
			}
			return null;
	}

	// half was given for this method and changes done by me
	@Override
	public String enrollChallenge(Long challengeId, String employeeId) {
		Challenge challenge = challengeRepository.findById(challengeId)
				.orElseThrow(() -> new IllegalArgumentException(Constants.CHALLENGENOTFOUND + challengeId));

		Optional<User> user = userRepository.findByUserEmpId(employeeId);
		if (user.isEmpty()) {
			throw new EmployeeNotFoundException(Constants.EMP_NOT_FOUND);
		}
		Optional<ChallengeEnrollment> enrollment = enrollmentRepository.findByChallengeAndUser(challenge, user.get());

		if (enrollment.isPresent()&&enrollment.get().getChallengeStatus().equalsIgnoreCase(Constants.UPCOMING_STATUS)
				&& challenge.getEndDate().isAfter(ChronoLocalDate.from(LocalDate.now()))
				&& enrollment.get().getUser().getUserId().equals(user.get().getUserId())) {

			enrollment.get().setChallengeStatus(Constants.ONGOING_STATUS);
			enrollment.get().setEnrollmentStatus(Constants.ENROLLED_STATUS);
			enrollment.get().setMyProgress(0);
			enrollmentRepository.save(enrollment.get());

			Notification notification = new Notification();
			LocalDateTime now = LocalDateTime.now();
			String date = now.format(dateFormatter);
			String time = now.format(timeFormatter);
			notification.setName(user.get().getName()); // Assuming user has a name property
			notification.setChallengeName(challenge.getChallengeName());
			notification.setEnrollDate(date);
			notification.setEnrollTime(time);
			notification.setIsRead(false);
			notification.setCompany(user.get().getCompany());
			notificationRepository.save(notification);
			return Constants.ENROLLMENT + challenge.getChallengeName();
		} else {
			throw new IllegalArgumentException(Constants.NOTABLETOENROLL);
		}

	}

	@Override
	public List<ChallengeDto> getMyChallenges(String required, String employeeId) {
		Optional<User> user = userRepository.findByUserEmpId(employeeId);
		Optional<Company> company = companyRepository.findByUserEmpId(employeeId);
		if (user.isPresent()) {
			List<Challenge> challenges = null;
			if ("all".equalsIgnoreCase(required)) {
				challenges = challengeRepository.findAllByUserUserId(user.get().getUserId());
			} else if (Constants.PUBLISHED_STATUS.equalsIgnoreCase(required)) {
				challenges = challengeRepository.findAllByStatusAndUserUserId(required, user.get().getUserId());
			} else if (Constants.SAVEDRAFT.equalsIgnoreCase(required)) {
				challenges = challengeRepository.findAllByStatusAndUserUserId(required, user.get().getUserId());
			}

			if (challenges == null || challenges.isEmpty()) {
				throw new IllegalStateException(Constants.NO_CHALLENGES_FOUND);
			}

			return challenges.stream().map(challenge -> modelMapper.map(challenge, ChallengeDto.class)).toList();
		}
		if (company.isPresent()) {
			List<Challenge> challenges = null;
			if ("all".equalsIgnoreCase(required)) {
				challenges = challengeRepository.findAllByCompanyCompanyId(company.get().getCompanyId());
			} else if (Constants.PUBLISHED_STATUS.equalsIgnoreCase(required)) {
				challenges = challengeRepository.findAllByStatusAndCompanyCompanyId(required,
						company.get().getCompanyId());
			} else if (Constants.SAVEDRAFT.equalsIgnoreCase(required)) {
				challenges = challengeRepository.findAllByStatusAndCompanyCompanyId(required,
						company.get().getCompanyId());
			}

			if (challenges == null || challenges.isEmpty()) {
				throw new IllegalStateException(Constants.NO_CHALLENGES_FOUND);
			}

			return challenges.stream().map(challenge -> modelMapper.map(challenge, ChallengeDto.class)).toList();

		}
		return Collections.emptyList();
	}

	@Override
	public List<ParticipateDto> getParticipateChallenges(String required, String employeeId) {
		// Retrieving user information
		User user = userRepository.findByUserEmpId(employeeId)
				.orElseThrow(() -> new IllegalArgumentException(Constants.USERNOTEXISTS));

		// Fetching challenge enrollments for the user
		List<ChallengeEnrollment> userEnrollments = enrollmentRepository.findByUser(user);
		List<ParticipateDto> challenges ;

		switch (required.toLowerCase()) {
		// For enrolled challenges
		case Constants.ENROLLED_STATUS:
			challenges = userEnrollments.stream()
					.filter(enrollment -> Constants.ENROLLED_STATUS.equalsIgnoreCase(enrollment.getEnrollmentStatus())
							&& enrollment.getChallenge().getEndDate().isAfter(ChronoLocalDate.from(LocalDate.now())))
					.map(enrollment -> {
						ParticipateDto dto = modelMapper.map(enrollment.getChallenge(), ParticipateDto.class);
						dto.setChallengeStatus(enrollment.getChallengeStatus());
						dto.setEnrollmentStatus(enrollment.getEnrollmentStatus());
						return dto;
					}).toList();
			break;

		// For upcoming challenges
		case "upcoming":
			challenges = userEnrollments.stream()
					.filter(enrollment -> "upcoming".equalsIgnoreCase(enrollment.getChallengeStatus())
							&& enrollment.getChallenge().getEndDate().isAfter(ChronoLocalDate.from(LocalDate.now())))
					.map(enrollment -> {
						ParticipateDto dto = modelMapper.map(enrollment.getChallenge(), ParticipateDto.class);
						dto.setChallengeStatus(enrollment.getChallengeStatus());
						dto.setEnrollmentStatus(enrollment.getEnrollmentStatus());
						return dto;
					}).filter(challengeDto -> challengeDto.getEndDate().isAfter(LocalDate.now()))
					.toList();
			break;

		// For historical challenges
		case "historical":
			challenges = userEnrollments.stream()
					.filter(enrollment -> enrollment.getChallenge().getEndDate().isBefore(LocalDate.now()))
					.map(enrollment -> {
						ParticipateDto dto = modelMapper.map(enrollment.getChallenge(), ParticipateDto.class);
						dto.setChallengeStatus(enrollment.getChallengeStatus());
						dto.setEnrollmentStatus(enrollment.getEnrollmentStatus());
						return dto;
					}).toList();
			break;

		// For ongoing challenges
		case Constants.ONGOING_STATUS:
			challenges = userEnrollments.stream()
					.filter(enrollment -> Constants.ONGOING_STATUS.equalsIgnoreCase(enrollment.getChallengeStatus())
							&& enrollment.getChallenge().getEndDate().isAfter(ChronoLocalDate.from(LocalDate.now())))
					.map(enrollment -> {
						ParticipateDto dto = modelMapper.map(enrollment.getChallenge(), ParticipateDto.class);
						dto.setChallengeStatus(enrollment.getChallengeStatus());
						dto.setEnrollmentStatus(enrollment.getEnrollmentStatus());
						return dto;
					}).filter(challengeDto -> challengeDto.getEndDate().isAfter(LocalDate.now()))
					.toList();
			break;

		// For all challenges
		case "all":
			challenges = userEnrollments.stream().map(enrollment -> {
				ParticipateDto dto = modelMapper.map(enrollment.getChallenge(), ParticipateDto.class);
				dto.setChallengeStatus(enrollment.getChallengeStatus());
				dto.setEnrollmentStatus(enrollment.getEnrollmentStatus());
				return dto;
			}).toList();
			break;

		default:
			throw new IllegalArgumentException(Constants.INVALID_REQUIREMENT);
		}
		return challenges;
	}

	public PersonalDetailsDto getPersonalDetails(String employeeId) {
		Optional<User> user = userRepository.findByUserEmpId(employeeId);
		Optional<Company> company = companyRepository.findByUserEmpId(employeeId);
		if (user.isPresent()) {
			return new PersonalDetailsDto(user.get().getName(), user.get().getEmail(), user.get().getUserEmpId(),
					user.get().getStoreDetails().getStoreId(), user.get().getStoreDetails().getCity(),
					user.get().getRole(),user.get().getImageUrl());
		}
		if (company.isPresent()) {
			return new PersonalDetailsDto(company.get().getCompanyName(), company.get().getEmail(),
					company.get().getUserEmpId(), 2, "Store name under company", company.get().getRole(),company.get().getImageUrl());
		}
		throw new EmployeeIdExistsException(Constants.EMP_NOT_FOUND);
	}

	public ResponseEntity<String> saveUserSavings(UserSavings userSavings) {
		String employeeId = userSavings.getUser().getUserEmpId();
		Optional<User> user = userRepository.findByUserEmpId(employeeId);

		if (user.isEmpty()) {
			throw new EmployeeIdExistsException(Constants.EMP_NOT_FOUND);
		}

		Optional<Challenge> challenge = challengeRepository.findById(userSavings.getChallenge().getId());
		if(challenge.isPresent()) {
		Optional<ChallengeEnrollment> challengeEnrollment = enrollmentRepository.findByChallengeAndUser(challenge.get(),
				user.get());


		if (challengeEnrollment.isPresent()
				&& challengeEnrollment.get().getChallengeStatus().equalsIgnoreCase(Constants.ONGOING_STATUS)
				&& challengeEnrollment.get().getUser().getUserId().equals(user.get().getUserId())
				&& (challenge.get().getStartDate().isBefore(userSavings.getSavingsAddedOn())
						|| challenge.get().getStartDate().equals(userSavings.getSavingsAddedOn()))
				&& (challenge.get().getEndDate().isAfter(userSavings.getSavingsAddedOn())
						|| challenge.get().getEndDate().equals(userSavings.getSavingsAddedOn()))) {
			userSavings.setUser(user.get());
			userSavings.setChallenge(challenge.get());
			userSavingsRepository.save(userSavings);
			return ResponseEntity.ok().body("User Savings has been saved successfully" + user.toString());
		}}
		return new ResponseEntity<>("Savings are not saved", HttpStatus.NOT_IMPLEMENTED);
	}

	public UserSavingsDto getUserSavings(String employeeId) {
		Optional<User> user = userRepository.findByUserEmpId(employeeId);
		Optional<Company> company = companyRepository.findByUserEmpId(employeeId);

		if (user.isPresent()) {
			long savingsId = user.get().getUserId();
			List<UserSavings> list = userSavingsRepository.findAllByUserUserId(savingsId);
			double co2Savings = list.stream().mapToDouble(UserSavings::getCo2Savings).sum();
			DecimalFormat df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.HALF_UP);
			String co2SavingsFormatted = df.format(co2Savings);

			double dollarSavings = list.stream().mapToDouble(UserSavings::getDollarSavings).sum();
			String dollarSavingsFormatted = df.format(dollarSavings);

			double wasteSavings = list.stream().mapToDouble(UserSavings::getWasteSavings).sum();
			String wasteSavingsFormatted = df.format(wasteSavings);

			double roundedCo2Savings = Double.parseDouble(co2SavingsFormatted);
			double roundedDollarSavings = Double.parseDouble(dollarSavingsFormatted);
			double roundedWasteSavings = Double.parseDouble(wasteSavingsFormatted);
			return  new UserSavingsDto(roundedDollarSavings, roundedWasteSavings,
					roundedCo2Savings, LocalDate.now());

		} else if (company.isPresent()) {
			List<User> users = userRepository.findAllByCompanyCompanyId(company.get().getCompanyId());
			double totalCo2Savings = 0.0;
			double totalDollarSavings = 0.0;
			double totalWasteSavings = 0.0;

			for (User user2 : users) {
				long savingsId = user2.getUserId();
				List<UserSavings> userSavingsList = userSavingsRepository.findAllByUserUserId(savingsId);
				double co2Savings = userSavingsList.stream().mapToDouble(UserSavings::getCo2Savings).sum();
				double dollarSavings = userSavingsList.stream().mapToDouble(UserSavings::getDollarSavings).sum();
				double wasteSavings = userSavingsList.stream().mapToDouble(UserSavings::getWasteSavings).sum();

				totalCo2Savings += co2Savings;
				totalDollarSavings += dollarSavings;
				totalWasteSavings += wasteSavings;
			}

			DecimalFormat df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.HALF_UP);
			String totalCo2SavingsFormatted = df.format(totalCo2Savings);
			String totalDollarSavingsFormatted = df.format(totalDollarSavings);
			String totalWasteSavingsFormatted = df.format(totalWasteSavings);

			double roundedTotalCo2Savings = Double.parseDouble(totalCo2SavingsFormatted);
			double roundedTotalDollarSavings = Double.parseDouble(totalDollarSavingsFormatted);
			double roundedTotalWasteSavings = Double.parseDouble(totalWasteSavingsFormatted);

			return  new UserSavingsDto(roundedTotalDollarSavings, roundedTotalWasteSavings,
					roundedTotalCo2Savings, LocalDate.now());
		}

		return null;
	}

	@Override
	public List<? extends SavingsDto> getUserSavingsInProfileForChart(String employeeId, String savingsType) {
		Optional<User> user = userRepository.findByUserEmpId(employeeId);
		Optional<Company> company = companyRepository.findByUserEmpId(employeeId);
		if (user.isPresent()) {
			LocalDate startDateAsDate = user.get().getRegistrationDate();
			LocalDate userSavingsDate = startDateAsDate;
			LocalDateTime today = LocalDateTime.now();
			LocalDate todayAsDate = today.toLocalDate();
			long days = ChronoUnit.DAYS.between(startDateAsDate, todayAsDate);
			long userId = user.get().getUserId();

			List<SavingsDto> savingsDtoList = new ArrayList<>();

			int weekNumber = 1;
			for (int i = 0; i <= days; i += 7) {
				List<UserSavings> list = userSavingsRepository.findAllByUserUserId(userId);
				if (list.isEmpty()) {
					throw new EmployeeIdExistsException(
							"either employee id or challenge id is invalid / must there won't be any savings added by you!, please check");
				}

				double savings = 0.0;
				for (UserSavings savingsObj : list) {
					if (savingsObj.getSavingsAddedOn().isAfter(userSavingsDate)
							&& savingsObj.getSavingsAddedOn().isBefore(userSavingsDate.plusDays(7))) {
						switch (savingsType) {
						case "co2":
							savings += savingsObj.getCo2Savings();
							break;
						case Constants.WASTE_SAVING :
							savings += savingsObj.getWasteSavings();
							break;
						case Constants.DOLLAR_SAVING:
							savings += savingsObj.getDollarSavings();
							break;
						default:
							throw new IllegalArgumentException(Constants.INVALID_SAVINGS_TYPE + savingsType);
						}
					}
				}

				SavingsDto savingsDto = null;
				switch (savingsType) {

				case Constants.WASTE_SAVING:
					savingsDto = new WeeklyWasteSavingsDto(savings, userSavingsDate, userSavingsDate.plusDays(6),
							weekNumber);
					break;
				case Constants.DOLLAR_SAVING:
					savingsDto = new WeeklyDollarSavingsDto(savings, userSavingsDate, userSavingsDate.plusDays(6),
							weekNumber);
					break;
				case "co2":
					savingsDto = new WeeklyCo2SavingsDto(savings, userSavingsDate, userSavingsDate.plusDays(6),
							weekNumber);
					break;
				default:
					throw new IllegalArgumentException(Constants.INVALID_SAVINGS_TYPE + savingsType);
				}

				savingsDtoList.add(savingsDto);
				userSavingsDate = userSavingsDate.plusWeeks(1);
				weekNumber++;
			}

			return savingsDtoList;
		} else if (company.isPresent()) {
			// Get the company's registered date
			LocalDate companyRegisteredDate = company.get().getRegistrationDate();

			List<User> users = userRepository.findAllByCompanyCompanyId(company.get().getCompanyId());
			LocalDate todayAsDate = LocalDate.now();
			int weekNumber = 1;

			List<SavingsDto> savingsDtoList = new ArrayList<>();

			// Calculate the user savings based on the company's registered date
			LocalDate userSavingsDate = companyRegisteredDate;
			while (!userSavingsDate.isAfter(todayAsDate)) {
				double savings = 0.0;
				for (User userObj : users) {
					List<UserSavings> list = userSavingsRepository.findAllByUserUserId(userObj.getUserId());
					for (UserSavings savingsObj : list) {
						if (savingsObj.getSavingsAddedOn().isAfter(userSavingsDate)
								&& savingsObj.getSavingsAddedOn().isBefore(userSavingsDate.plusDays(7))) {
							switch (savingsType) {

							case Constants.DOLLAR_SAVING:
								savings += savingsObj.getDollarSavings();
								break;
							case "co2":
								savings += savingsObj.getCo2Savings();
								break;
							case Constants.WASTE_SAVING:
								savings += savingsObj.getWasteSavings();
								break;
							default:
								throw new IllegalArgumentException(Constants.INVALID_SAVINGS_TYPE + savingsType);
							}
						}
					}
				}

				SavingsDto savingsDto = null;
				switch (savingsType) {

				case Constants.DOLLAR_SAVING:
					savingsDto = new WeeklyDollarSavingsDto(savings, userSavingsDate, userSavingsDate.plusDays(6),
							weekNumber);
					break;
				case Constants.WASTE_SAVING:
					savingsDto = new WeeklyWasteSavingsDto(savings, userSavingsDate, userSavingsDate.plusDays(6),
							weekNumber);
					break;
				case "co2":
					savingsDto = new WeeklyCo2SavingsDto(savings, userSavingsDate, userSavingsDate.plusDays(6),
							weekNumber);
					break;

				default:
					throw new IllegalArgumentException(Constants.INVALID_SAVINGS_TYPE  + savingsType);
				}

				savingsDtoList.add(savingsDto);
				userSavingsDate = userSavingsDate.plusWeeks(1);
				weekNumber++;
			}

			return savingsDtoList;
		}
		return Collections.emptyList();

	}

	public ChallengeDetailsDto getChallengeDetails(String employeeId) {
		Optional<User> user = userRepository.findByUserEmpId(employeeId);
		if (user.isEmpty()) {
			throw new EmployeeIdExistsException(Constants.EMP_NOT_FOUND);
		}
		long userId = user.get().getUserId();
		List<Challenge> listOfOwned = challengeRepository.findAllByCreatedByUserId(userId);
		List<ChallengeEnrollment> listOfOngoing = enrollmentRepository.findAllByUserUserIdAndChallengeStatus(userId,
				Constants.ONGOING_STATUS);
		List<ChallengeEnrollment> listOfCompleted = enrollmentRepository
				.findAllByUserUserIdAndChallengeStatusAndMyProgress(userId, Constants.COMPLETED_STATUS, 100);
		List<ChallengeEnrollment> listOfHistorical = enrollmentRepository.findAllByUserUserId(userId);
		AtomicInteger historicalChallenges = new AtomicInteger(0);
		listOfHistorical.forEach(historical -> {
			if (historical.getChallenge().getEndDate().isBefore(LocalDate.now())) {
				historicalChallenges.incrementAndGet();
			}
		});
		AtomicInteger completedChallenges = new AtomicInteger(0);
		listOfCompleted.forEach(completed -> 
				completedChallenges.incrementAndGet()
		);
		ChallengeDetailsDto challengeDetailsDto = new ChallengeDetailsDto();
		challengeDetailsDto.setOwnedChallenges(listOfOwned.size());
		challengeDetailsDto.setOngoingChallenges(listOfOngoing.size());
		challengeDetailsDto.setCompletedChallenges(completedChallenges.get() + historicalChallenges.get());
		return challengeDetailsDto;
	}

	public List<UserSavingsDto> getUserSavingForAChallenge(String employeeId, long challengeId) {

		Optional<User> user = userRepository.findByUserEmpId(employeeId);
		if (user.isEmpty()) {
			throw new EmployeeIdExistsException(Constants.EMP_NOT_FOUND);
		}
		Challenge challenge = challengeRepository.findById(challengeId)
				.orElseThrow(() -> new IllegalArgumentException(Constants.CHALLENGE_NOT_FOUND + challengeId));
		LocalDate startDateAsDate = challenge.getStartDate();
		LocalDate userSavingsDate = startDateAsDate;
		LocalDateTime today = LocalDateTime.now();
		LocalDate todayAsDate = today.toLocalDate();
		long days = ChronoUnit.DAYS.between(startDateAsDate, todayAsDate);
		long userId = user.get().getUserId();
		DoubleAdder co2SavingsAdder = new DoubleAdder();
		DoubleAdder dollarSavingsAdder = new DoubleAdder();
		DoubleAdder wasteSavingsAdder = new DoubleAdder();
		UserSavingsDto userSavingsDto;
		List<UserSavingsDto> userSavingsDtoList = new ArrayList<>();
		for (int i = 0; i <= days; i++) {
			List<UserSavings> list = userSavingsRepository.findAllByUserUserIdAndChallengeId(userId, challengeId);
			if (list.isEmpty()) {
				throw new EmployeeIdExistsException(
						"either employee id or challenge id is invalid / must there wont be any savings added by you!, please check");
			}
			for (int j = 0; j < list.size(); j++) {
				boolean isInBetween = (list.get(j).getSavingsAddedOn().isEqual(userSavingsDate));
				if (isInBetween) {
					co2SavingsAdder.add(list.get(j).getCo2Savings());
					dollarSavingsAdder.add(list.get(j).getDollarSavings());
					wasteSavingsAdder.add(list.get(j).getWasteSavings());
				}
			}

			double co2Savings = co2SavingsAdder.sum();
			double dollarSavings = dollarSavingsAdder.sum();
			double wasteSavings = wasteSavingsAdder.sum();

			userSavingsDto = new UserSavingsDto(dollarSavings, wasteSavings, co2Savings, userSavingsDate);
			userSavingsDtoList.add(userSavingsDto);
			userSavingsDate = userSavingsDate.plusDays(1);
		}
		return userSavingsDtoList;

	}

	@Override
	public List<Map<String, Object>> getEmployeesByUserId(String employeeId) {
		User user = userRepository.findByUserEmpId(employeeId).orElse(null);
		if (user == null) {
			// Handle the case where the user with the given ID is not found
			return Collections.emptyList();
		}

		List<Employee> employees = user.getEmployees();
		return employees.stream().map(employee -> {
			Map<String, Object> employeeData = new HashMap<>();
			employeeData.put("Name", employee.getEmployeeName());
			employeeData.put("UserempId", employee.getEmpId());
			employeeData.put(Constants.EMAIL_KEYWORD, employee.getEmpEmail());
			employeeData.put("StoreName", employee.getEmpStoreName());
			employeeData.put(Constants.STATE_KEYWORD, employee.getEmpState());
			employeeData.put("RegistrationDate", employee.getEmpRegDate());
			return employeeData;
		}).toList();
	}

	@Override
	public Map<String, Object> getManagersAndEmployees() {
		List<User> users = userRepository.findAll();

		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> managers = new ArrayList<>();

		for (User user : users) {
			Map<String, Object> managerData = new LinkedHashMap<>();
			managerData.put("Name", user.getName());
			managerData.put(Constants.EMAIL_KEYWORD, user.getEmail());
			managerData.put("StoreName", user.getStoreDetails().getStoreName());
			managerData.put("RegistrationDate", user.getRegistrationDate().toString());
			managerData.put(Constants.STATE_KEYWORD, user.getStoreDetails().getState());
			managerData.put("UserempId", user.getUserEmpId());

			List<Employee> employees = user.getEmployees();
			List<Map<String, Object>> employeesData = new ArrayList<>();
			for (Employee employee : employees) {
				Map<String, Object> employeeData = new HashMap<>();
				employeeData.put("totalChallenge", employee.getTotalChallenge());
				employeeData.put("carbonSaving", employee.getCarbonSaving());
				employeeData.put("dollarSaving", employee.getDollarSaving());
				employeeData.put("winChallenge", employee.getWinChallenge());
				employeeData.put("EmpName", employee.getEmployeeName());
				employeeData.put("enrolledChallenge", employee.getEnrolledChallenge());
				employeesData.add(employeeData);
			}
			managerData.put("employees", employeesData);
			managers.add(managerData);
		}
		result.put("managers", managers);
		return result;
	}

	@Override
	public EmployeeDTO getEmployeeDetails(String employeeId) {
		Optional<Employee> optionalEmployee = empRepository.findByEmpId(employeeId);

		if (optionalEmployee.isPresent()) {
			Employee employee = optionalEmployee.get();

			EmployeeDTO employeeDTO = new EmployeeDTO(); // NOSONAR not used in secure contexts
			employeeDTO.setName(employee.getEmployeeName());
			employeeDTO.setEmail(employee.getEmpEmail());
			employeeDTO.setStoreName(employee.getEmpStoreName());
			employeeDTO.setAddress(employee.getAddress());
			employeeDTO.setKPI(employee.getKPI());
			employeeDTO.setTotalChallenge(employee.getTotalChallenge());
			employeeDTO.setWinningChallenge(employee.getWinChallenge());
			employeeDTO.setEnrolledChallenge(employee.getEnrolledChallenge());
			employeeDTO.setCo2Savings(employee.getCarbonSaving());
			employeeDTO.setDollarSavings(employee.getDollarSaving());
			employeeDTO.setWasteSavings(employee.getWasteSavings());
			return employeeDTO;
		} else {
			throw new EmployeeNotFoundException(Constants.EMP_NOT_FOUND + employeeId);
		}
	}

	@Override
	public void generateExcel(HttpServletResponse response, ExcelDto excelDto) throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook(); // NOSONAR not used in secure contexts
		ServletOutputStream ops = response.getOutputStream();
		HSSFSheet sheet = workbook.createSheet("user details");
		int dataRowIndex = 1;
		HSSFRow row = sheet.createRow(0);

		row.createCell(0).setCellValue("Manager's Name");
		row.createCell(1).setCellValue("Employee Id");
		row.createCell(2).setCellValue(Constants.EMAIL_KEYWORD);
		row.createCell(3).setCellValue("Store name");
		row.createCell(4).setCellValue(Constants.STATE_KEYWORD);
		row.createCell(5).setCellValue("Reg.Date");

		List<String> listOfEmployees1 = excelDto.getListOfEmployees();
		for (String employeeId : listOfEmployees1) {
			Optional<User> user = userRepository.findByUserEmpId(employeeId);
			if (user.isEmpty()) {
				throw new EmployeeNotFoundException(Constants.EMP_NOT_FOUND);
			}
			List<Employee> listOfEmployees = user.get().getEmployees();

			// NOSONAR not used in secure contexts

			HSSFRow dataRow = sheet.createRow(dataRowIndex);
			dataRow.createCell(0).setCellValue(user.get().getName());
			dataRow.createCell(1).setCellValue(user.get().getUserEmpId());
			dataRow.createCell(2).setCellValue(user.get().getEmail());
			dataRow.createCell(3).setCellValue(user.get().getStoreDetails().getStoreName());
			dataRow.createCell(4).setCellValue(user.get().getStoreDetails().getState());
			dataRow.createCell(5).setCellValue(user.get().getRegistrationDate().toString());
			dataRowIndex++;
			HSSFRow dataRow1 = sheet.createRow(dataRowIndex);
			dataRow1.createCell(0).setCellValue("");
			dataRow1.createCell(1).setCellValue("");
			dataRow1.createCell(2).setCellValue("");
			dataRow1.createCell(3).setCellValue("");
			dataRow1.createCell(4).setCellValue("");
			dataRow1.createCell(5).setCellValue("");
			dataRow1.createCell(6).setCellValue("Employee name");
			dataRow1.createCell(7).setCellValue("Total challenges");
			dataRow1.createCell(8).setCellValue("Win challenges");
			dataRow1.createCell(9).setCellValue("Enrolled challenges");
			dataRow1.createCell(10).setCellValue("Carbon saving");
			dataRow1.createCell(11).setCellValue("Dollar saving");
			for (Employee employee : listOfEmployees) {
				dataRowIndex++;
				HSSFRow dataRow2 = sheet.createRow(dataRowIndex);
				dataRow2.createCell(0).setCellValue("");
				dataRow2.createCell(1).setCellValue("");
				dataRow2.createCell(2).setCellValue("");
				dataRow2.createCell(3).setCellValue("");
				dataRow2.createCell(4).setCellValue("");
				dataRow2.createCell(5).setCellValue("");
				dataRow2.createCell(6).setCellValue(employee.getEmployeeName());
				dataRow2.createCell(7).setCellValue(employee.getTotalChallenge());
				dataRow2.createCell(8).setCellValue(employee.getWinChallenge());
				dataRow2.createCell(9).setCellValue(employee.getEnrolledChallenge());
				dataRow2.createCell(10).setCellValue(employee.getCarbonSaving());
				dataRow2.createCell(11).setCellValue(employee.getDollarSaving());
				dataRow2.createCell(12).setCellValue(employee.getWasteSavings());

			}
			dataRowIndex++;
		}
		workbook.write(ops);
	}

	@Override
	public AllDetailsDTO getUserDetails(String employeeId) {
		User user = userRepository.findByUserEmpId(employeeId).orElseThrow(() -> new EmployeeIdExistsException(Constants.USERNOTEXISTS + employeeId));
		UserSavingsDto userSavingsDto = getUserSavings(employeeId);
		StoreDetails storeDetails = user.getStoreDetails();
		if (userSavingsDto == null || storeDetails == null) {
			return null;
		}
		long totalChallenges = Math.toIntExact(enrollmentRepository.countByUser(user));
		List<ChallengeEnrollment> enrolledChallenge = enrollmentRepository
				.findByUserUserIdAndEnrollmentStatus(user.getUserId(), Constants.ENROLLED_STATUS);
		long enrolledChallenges = enrolledChallenge.size();

		AllDetailsDTO allDetailsDTO = new AllDetailsDTO();
		allDetailsDTO.setName(user.getName());
		allDetailsDTO.setEmail(user.getEmail());
		allDetailsDTO.setStoreName(storeDetails.getStoreName());
		allDetailsDTO.setAddress(storeDetails.getAddress());
		allDetailsDTO.setCo2Savings(userSavingsDto.getCo2Savings());
		allDetailsDTO.setDollarSavings(userSavingsDto.getDollarSavings());
		allDetailsDTO.setWasteSavings(userSavingsDto.getWasteSavings());
		allDetailsDTO.setTotalChallenge(totalChallenges);
		allDetailsDTO.setEnrolledChallenge(enrolledChallenges);

		return allDetailsDTO;
	}

	@Override
	public ResponseEntity<String> markAllNotificationsAsRead(String employeeId) {
		Optional<User> user = userRepository.findByUserEmpId(employeeId);
		Optional<Company> company = companyRepository.findByUserEmpId(employeeId);
		if (user.isPresent()) {
			List<Notification> notifications = notificationRepository.findByUser(user.get());
			for (Notification notification : notifications) {
				if (notification.getRegistrationDate() == null) {
					notification.setIsRead(true);
				}
			}
			// Manually save the changes to the database
			notificationRepository.saveAll(notifications);
			return new ResponseEntity<>("Marked as all read", HttpStatus.OK);
		} else if (company.isPresent()) {
			List<Notification> notifications = notificationRepository.findByCompany(company.get());
			for (Notification notification : notifications) {
				if (notification.getRegistrationDate() == null) {
					notification.setIsRead(true);
				}
			}
			// Manually save the changes to the database
			notificationRepository.saveAll(notifications);
			return new ResponseEntity<>("Marked as all read", HttpStatus.OK);
		} else {
			throw new EmployeeNotFoundException(Constants.EMP_NOT_FOUND + employeeId);
		}
	}

	@Override
	public List<NotificationDTO> getAllNotificationsByUserId(String employeeId) {
		Optional<User> user = userRepository.findByUserEmpId(employeeId);
		Optional<Company> company = companyRepository.findByUserEmpId(employeeId);
		if (user.isPresent()) {
			List<Notification> notifications = notificationRepository.findByUserUserId(user.get().getUserId());

			return notifications.stream().map(notification -> {
				NotificationDTO dto = new NotificationDTO();
				dto.setId(notification.getId());
				dto.setChallengeName(notification.getChallengeName());
				dto.setLaunchDate(notification.getLaunchDate());
				dto.setLaunchTime(notification.getLaunchTime());
				dto.setName(notification.getName());
				dto.setUserEmpId(notification.getUserEmpId());
				dto.setRegistrationDate(notification.getRegistrationDate());
				dto.setRegistrationTime(notification.getRegistrationTime());
				dto.setEnrollDate(notification.getEnrollDate());
				dto.setEnrollTime(notification.getEnrollTime());
				dto.setIsRead(notification.getIsRead());
				dto.setUserId(user.get().getUserId());
				return dto;
			}).toList();
		} else if (company.isPresent()) {
			List<Notification> notifications = notificationRepository
					.findByCompanyCompanyId(company.get().getCompanyId());
			return notifications.stream().map(notification -> {
				NotificationDTO dto = new NotificationDTO();
				dto.setId(notification.getId());
				dto.setChallengeName(notification.getChallengeName());
				dto.setLaunchDate(notification.getLaunchDate());
				dto.setLaunchTime(notification.getLaunchTime());
				dto.setName(notification.getName());
				dto.setUserEmpId(notification.getUserEmpId());
				dto.setRegistrationDate(notification.getRegistrationDate());
				dto.setRegistrationTime(notification.getRegistrationTime());
				dto.setEnrollDate(notification.getEnrollDate());
				dto.setEnrollTime(notification.getEnrollTime());
				dto.setIsRead(notification.getIsRead());
				dto.setUserId(company.get().getCompanyId());
				return dto;
			}).toList();
		} else {
			throw new EmployeeNotFoundException(Constants.EMP_NOT_FOUND + employeeId);
		}

	}

	@Override
	public void approveUser(Long id, String userEmpId) {
		Optional<User> userOptional = userRepository.findByUserEmpId(userEmpId);
		Optional<Notification> notify = notificationRepository.findById(id);

		if (notify.isPresent()) {
			Notification not = notify.get();
			not.setIsRead(true);
			notificationRepository.save(not);
		}
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			user.setVerified(true);
			userRepository.save(user);
		} else {
			throw new UserNotFoundException("User not found with id: " + id);
		}
	}

	@Override
	public void declineUser(Long id, String userEmpId) {
		Optional<User> userOptional = userRepository.findByUserEmpId(userEmpId);
		Optional<Notification> notify = notificationRepository.findById(id);

		if (notify.isPresent()) {
			Notification not = notify.get();
			not.setIsRead(true);
			notificationRepository.save(not);
		}
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			user.setVerified(false);
			userRepository.save(user);
		} else {
			throw new UserNotFoundException("User not found with id: " + id);
		}
	}

	@Override
	public List<OverviewDto> employeesOverview(Long challengeId, String employeeId) {
		Optional<User> user = userRepository.findByUserEmpId(employeeId);
		if (user.isEmpty()) {
			throw new EmployeeIdExistsException(Constants.EMP_NOT_FOUND);
		}
		Optional<Challenge> challenge = challengeRepository.findById(challengeId);
		if (challenge.isEmpty()) {
			throw new EmployeeIdExistsException(Constants.CHALLENGE_NOT_FOUND);
		}
		List<Employee> empList = empRepository.findByUserIdAndChallengeId(user.get().getUserId(), challengeId);
		if (empList.isEmpty()) {
			return Collections.emptyList();
		} else {
			return empList.stream().map(employee -> modelMapper.map(employee, OverviewDto.class))
					.toList();
		}

	}

	@Override
	public ProfileImageDTO uploadProfileImage(String userEmpId, MultipartFile file) throws IOException {
	    Optional<User> userOptional = userRepository.findByUserEmpId(userEmpId);
	    Optional<Company> company = companyRepository.findByUserEmpId(userEmpId);
	    ProfileImageDTO profileImageDTO = new ProfileImageDTO();
	    if(userOptional.isPresent()) {
	    User user = userOptional.orElseThrow(() -> new IllegalArgumentException("Invalid user Employee ID"));
	    profileImageDTO.setUserEmpId(String.valueOf(userEmpId));
	    byte[] bytes = file.getBytes();

	    // Store the image data as a BLOB in the database
	    user.setImageUrl(bytes);
	    userRepository.save(user);
	    profileImageDTO.setImageUrl(bytes);
	    }
	    else if(company.isPresent()) {
	    	 Company user = company.orElseThrow(() -> new IllegalArgumentException("Invalid user Company Employee ID"));
	 	    profileImageDTO.setUserEmpId(String.valueOf(userEmpId));
	 	    byte[] bytes = file.getBytes();
	 	    // Store the image data as a BLOB in the database
	 	    user.setImageUrl(bytes);
	 	    companyRepository.save(user);
	 	   profileImageDTO.setImageUrl(bytes);
	    }
	    return profileImageDTO;
	}
	public ResponseEntity<MyProgressCompanyDetailsDto> getMySavingsCompanyDetails(String employeeId, long challengeId) {
		Optional<User> user = userRepository.findByUserEmpId(employeeId);

		if (user.isEmpty()) {
			throw new EmployeeIdExistsException(Constants.EMP_NOT_FOUND);
		}
		Optional<Challenge> challenge = challengeRepository.findById(challengeId);
		if (challenge.isEmpty()) {
			throw new EmployeeIdExistsException(Constants.CHALLENGE_NOT_FOUND);
		}

		Optional<ChallengeEnrollment> challengeEnrollment = enrollmentRepository
				.findByUserUserIdAndChallengeIdAndEnrollmentStatus(user.get().getUserId(), challengeId, Constants.ENROLLED_STATUS);

		if (challengeEnrollment.isPresent()) {
			return ResponseEntity.ok()
					.body(new MyProgressCompanyDetailsDto(user.get().getStoreDetails().getStoreName(),
							user.get().getStoreDetails().getStreet(), user.get().getStoreDetails().getCity(),
							user.get().getStoreDetails().getState(), user.get().getStoreDetails().getCountry(),
							user.get().getStoreDetails().getZipCode(), challengeEnrollment.get().getMyProgress(),
							challenge.get().getEndDate()));
		}
		throw new EmployeeIdExistsException("Enrollment not found for the user");
	}

	public ResponseEntity<String> saveDailyMyProgress(MyProgressDailyData myProgressDailyData) {
		Optional<User> user = userRepository.findByUserEmpId(myProgressDailyData.getUser().getUserEmpId());

		if (user.isEmpty()) {
			throw new EmployeeIdExistsException(Constants.EMP_NOT_FOUND);
		}
		Optional<Challenge> challenge = challengeRepository.findById(myProgressDailyData.getChallenge().getId());
		if (challenge.isEmpty()) {
			throw new EmployeeIdExistsException(Constants.CHALLENGE_NOT_FOUND);
		}

		Optional<ChallengeEnrollment> challengeEnrollment = enrollmentRepository
				.findByUserUserIdAndChallengeIdAndEnrollmentStatus(user.get().getUserId(), challenge.get().getId(),
						Constants.ENROLLED_STATUS);
		if (challengeEnrollment.isPresent()) {
			myProgressDailyData.setUser(user.get());
			myProgressDailyData.setChallenge(challenge.get());
			myProgressDailyData.setDate(LocalDate.now());
			myProgressDailyDataRepository.save(myProgressDailyData);
			return new ResponseEntity<>("Added Successfully", HttpStatus.CREATED);
		}
		throw new EmployeeIdExistsException("Enrollment not found for the user");
	}

	public List<? extends SavingsDto> getWeeklySavingsForAChallenge(String employeeId, long challengeId,
			String savingsType) {
		Optional<User> user = userRepository.findByUserEmpId(employeeId);
		if (user.isEmpty()) {
			throw new EmployeeIdExistsException(Constants.EMP_NOT_FOUND);
		}
		Challenge challenge = challengeRepository.findById(challengeId)
				.orElseThrow(() -> new EmployeeIdExistsException(Constants.CHALLENGE_NOT_FOUND + challengeId));
		LocalDate startDateAsDate = challenge.getStartDate();
		LocalDate userSavingsDate = startDateAsDate;
		LocalDateTime today = LocalDateTime.now();
		LocalDate todayAsDate = today.toLocalDate();
		long days = ChronoUnit.DAYS.between(startDateAsDate, todayAsDate);
		long userId = user.get().getUserId();
		List<SavingsDto> savingsDtoList = new ArrayList<>();
		int weekNumber = 1;
		for (int i = 0; i <= days; i += 7) {
			List<UserSavings> list = userSavingsRepository.findAllByUserUserIdAndChallengeId(userId, challengeId);
			if (list.isEmpty()) {
				throw new EmployeeIdExistsException(
						"either employee id or challenge id is invalid / must there wont be any savings added by you!, please check");
			}
			double savings = 0.0;
			for (int j = 0; j < list.size(); j++) {
				boolean isInBetween = (list.get(j).getSavingsAddedOn().isAfter(userSavingsDate)
						&& list.get(j).getSavingsAddedOn().isBefore(userSavingsDate.plusDays(7)));
				if (isInBetween) {
					switch (savingsType) {
					case "co2":
						savings += list.get(j).getCo2Savings();
						break;
					case Constants.WASTE_SAVING:
						savings += list.get(j).getWasteSavings();
						break;
					case Constants.DOLLAR_SAVING:
						savings += list.get(j).getDollarSavings();
						break;
					default:
						throw new IllegalArgumentException(Constants.INVALID_SAVINGS_TYPE  + savingsType);
					}
				}
			}
			SavingsDto savingsDto = null;
			switch (savingsType) {
			case "co2":
				savingsDto = new WeeklyCo2SavingsDto(savings, userSavingsDate, userSavingsDate.plusDays(6), weekNumber);
				break;
			case Constants.WASTE_SAVING:
				savingsDto = new WeeklyWasteSavingsDto(savings, userSavingsDate, userSavingsDate.plusDays(6),
						weekNumber);
				break;
			case Constants.DOLLAR_SAVING:
				savingsDto = new WeeklyDollarSavingsDto(savings, userSavingsDate, userSavingsDate.plusDays(6),
						weekNumber);
				break;
			default:
				throw new IllegalArgumentException(Constants.INVALID_SAVINGS_TYPE  + savingsType);
			}
			savingsDtoList.add(savingsDto);
			userSavingsDate = userSavingsDate.plusWeeks(1);
			weekNumber++;
		}
		return savingsDtoList;
	}

	@Override
	public ResponseEntity<List<BarGraphDto>> challengeOverviewBarGraph(String employeeId, long challengeId,
			String field, String xAxis, String yAxis, AllStoresDto allStoresDto)
	{
		Optional<Company> companyFromDb = companyRepository.findByUserEmpId(employeeId);
		Optional<Challenge> challenge = challengeRepository.findById(challengeId);
		if (companyFromDb.isEmpty()) {
			throw new EmployeeIdExistsException(Constants.EMP_NOT_FOUND);
		}
		if (challenge.isEmpty()) {
			throw new EmployeeIdExistsException(Constants.CHALLENGE_NOT_FOUND);
		}

			List<User> usersUnderCompany = userRepository.findAllByCompanyCompanyId(companyFromDb.get().getCompanyId());
			List<StoreDetails> storeDetailsListUnderCompany = new ArrayList<>();
			for (User user : usersUnderCompany) {
				storeDetailsListUnderCompany.add(user.getStoreDetails());
			}
			List<Integer> storesIncoming = allStoresDto.getStoreDetailsList();

			List<StoreDetails> storeDetailsListFromIncoming = new ArrayList<>();
			for(Integer i : storesIncoming)
			{
				storeDetailsListFromIncoming.add(userRepository.findById((long)i).get().getStoreDetails());
			}
			if(storeDetailsListFromIncoming.isEmpty())
			{
				throw new EmployeeNotFoundException(Constants.ID_NOT_PRESENT_IN_COMPANY_STRING);

			}
			boolean allStoresPresent = storeDetailsListFromIncoming.stream()
					.allMatch(storeDetails -> storeDetailsListUnderCompany.contains(storeDetails));
			if(allStoresPresent)
			{
				List<BarGraphDto> barGraphDtos = new ArrayList<>();
				double amount = 10;
				for(StoreDetails storeDetails : storeDetailsListFromIncoming)
				{
					long userId = storeDetails.getStoreId();
					List<UserSavings> userSavingsList = userSavingsRepository.findAllByUserUserIdAndChallengeId(userId, challengeId);
					LocalDate startDate = challenge.get().getStartDate();
					LocalDate today = LocalDate.now();
					int days = (int) ChronoUnit.DAYS.between(startDate, today);
					int weekNumber=1;
					for (int i = 0; i <= days; i += 7) {
						final LocalDate finalStartDate = startDate.plusDays(i);
						double co2Savings = userSavingsList.stream()
								.filter(savings -> savings.getSavingsAddedOn().isAfter(finalStartDate.minusDays(1))
										&& savings.getSavingsAddedOn().isBefore(finalStartDate.plusDays(7)))
								.mapToDouble(UserSavings::getCo2Savings).sum();

						double dollarSavings = userSavingsList.stream()
								.filter(savings -> savings.getSavingsAddedOn().isAfter(finalStartDate.minusDays(1))
										&& savings.getSavingsAddedOn().isBefore(finalStartDate.plusDays(7)))
								.mapToDouble(UserSavings::getDollarSavings).sum();
						double wasteSavings = userSavingsList.stream()
								.filter(savings -> savings.getSavingsAddedOn().isAfter(finalStartDate.minusDays(1))
										&& savings.getSavingsAddedOn().isBefore(finalStartDate.plusDays(7)))
								.mapToDouble(UserSavings::getWasteSavings).sum();

						BarGraphDto barGraphDto = new BarGraphDto(weekNumber,
								storeDetails.getStoreId(),
								amount,
								co2Savings,
								wasteSavings,
								dollarSavings);
						barGraphDtos.add(barGraphDto);
						weekNumber++;
						amount+=10;
					}
					amount=10;
				}
				return ResponseEntity.ok(barGraphDtos);
			}
			else {
				throw new EmployeeNotFoundException(Constants.ID_NOT_PRESENT_IN_COMPANY_STRING);
			}


	}



	public List<List<? extends SavingsDto>> challengeOverviewDonutGraph(String employeeId, long challengeId,
			String field, String dataType, AllStoresDto allStoresDto) {
		Optional<Company> companyFromDb = companyRepository.findByUserEmpId(employeeId);
		Optional<Challenge> challenge = challengeRepository.findById(challengeId);
		if (companyFromDb.isEmpty()) {
			throw new EmployeeIdExistsException(Constants.EMP_NOT_FOUND);
		}
		if (challenge.isEmpty()) {
			throw new EmployeeIdExistsException(Constants.CHALLENGE_NOT_FOUND);
		}
		if (companyFromDb.isPresent()) {
			List<User> usersUnderCompany = userRepository.findAllByCompanyCompanyId(companyFromDb.get().getCompanyId());

			List<StoreDetails> storeDetailsListFromUsers = getStoreDetailsListFromUsers(usersUnderCompany);

			List<Integer> storeIds = allStoresDto.getStoreDetailsList();
			List<StoreDetails> storeDetailsToStore = getStoreDetailsToStore(storeIds);

			switch (field) {
			case "carbonSavings","dollarSavings","wasteSavings":
				return processSavingsData(field, dataType, storeDetailsListFromUsers, storeDetailsToStore, challengeId);
			}
		}
		return Collections.emptyList();
	}

	private List<StoreDetails> getStoreDetailsListFromUsers(List<User> usersUnderCompany) {
		List<StoreDetails> storeDetailsListFromUsers = new ArrayList<>();
		for (User user : usersUnderCompany) {
			storeDetailsListFromUsers.add(user.getStoreDetails());
		}
		return storeDetailsListFromUsers;
	}

	private List<StoreDetails> getStoreDetailsToStore(List<Integer> storeIds) {
		List<StoreDetails> storeDetailsToStore = new ArrayList<>();
		for (int i : storeIds) {
			Optional<User> userOptional = userRepository.findByStoreDetailsStoreId(i);
			if (userOptional.isPresent()) {
				storeDetailsToStore.add(userOptional.get().getStoreDetails());
			} else {
				throw new EmployeeIdExistsException("Store details not found");
			}
		}
		return storeDetailsToStore;
	}

	private List<List<? extends SavingsDto>> processSavingsData(String field, String dataType,
			List<StoreDetails> storeDetailsListFromUsers, List<StoreDetails> storeDetailsToStore, long challengeId) {

		List<Integer> firstListIds = storeDetailsListFromUsers.stream().map(StoreDetails::getStoreId)
				.toList();

		if (storeDetailsToStore.isEmpty()) {
			throw new EmployeeNotFoundException(Constants.ID_NOT_PRESENT_IN_COMPANY_STRING);
			} else {
			boolean allIdsPresent = storeDetailsToStore.stream()
					.allMatch(storeDetails -> firstListIds.contains(storeDetails.getStoreId()));

			if (allIdsPresent) {
				List<List<? extends SavingsDto>> responses = new ArrayList<>();
				for (StoreDetails storeDetails : storeDetailsToStore) {

					long storeId = storeDetails.getStoreId();
					List<? extends SavingsDto> savingsData = getSpecificSavingsData(field, dataType, storeId,
							challengeId);

					responses.add(savingsData);
				}


				return responses;
			} else {
				throw new EmployeeNotFoundException(Constants.ID_NOT_PRESENT_IN_COMPANY_STRING);
				}
		}
	}

	private List<? extends SavingsDto> getSpecificSavingsData(String field, String dataType, long storeId,
			long challengeId) {
		switch (field) {
		case "carbonSavings":
			return Constants.WEEKLY_KEYWORD.equals(dataType) ? getChallengeOverviewDonutGraphWeeklyCo2Data(storeId, challengeId)
					: getChallengeOverviewDonutGraphMonthlyCo2Data(storeId, challengeId);
		case "dollarSavings":
			return Constants.WEEKLY_KEYWORD.equals(dataType) ? getChallengeOverviewDonutGraphWeeklyDollarData(storeId, challengeId)
					: getChallengeOverviewDonutGraphMonthlyDollarData(storeId, challengeId);
		case "wasteSavings":
			return Constants.WEEKLY_KEYWORD.equals(dataType)
					? getChallengeOverviewDonutGraphWeeklyWasteSavingsData(storeId, challengeId)
					: getChallengeOverviewDonutGraphMonthlyWasteSavingsData(storeId, challengeId);
		default:
			throw new IllegalArgumentException("Invalid field type");
		}
	}

	public List<? extends SavingsDto> getChallengeOverviewDonutGraphWeeklyCo2Data(Long userId, long challengeId) {
		Optional<Challenge> challenge = challengeRepository.findById(challengeId);
		if (challenge.isEmpty()) {
			throw new EmployeeNotFoundException(Constants.CHALLENGE_NOT_FOUND_KEYWORD);
		}

		List<UserSavings> userSavingsList = userSavingsRepository.findAllByUserUserIdAndChallengeId(userId,
				challengeId);

		LocalDate startDate = challenge.get().getStartDate();
		LocalDate today = LocalDate.now();
		int days = (int) ChronoUnit.DAYS.between(startDate, today);

		List<WeeklyCo2DonutGraphDto> weeklyCo2SavingsList = new ArrayList<>();
		for (int i = 0; i <= days; i += 7) {
			final LocalDate finalStartDate = startDate.plusDays(i);
			double co2Savings = userSavingsList.stream()
					.filter(savings -> savings.getSavingsAddedOn().isAfter(finalStartDate.minusDays(1))
							&& savings.getSavingsAddedOn().isBefore(finalStartDate.plusDays(7)))
					.mapToDouble(UserSavings::getCo2Savings).sum();

			WeeklyCo2DonutGraphDto weeklyCo2Savings = new WeeklyCo2DonutGraphDto(co2Savings, finalStartDate,
					finalStartDate.plusDays(6));
			weeklyCo2SavingsList.add(weeklyCo2Savings);
		}

		return weeklyCo2SavingsList;
	}

	public List<? extends SavingsDto> getChallengeOverviewDonutGraphWeeklyDollarData(Long userId, long challengeId) {
		Optional<Challenge> challenge = challengeRepository.findById(challengeId);
		if (challenge.isEmpty()) {
			throw new EmployeeNotFoundException(Constants.CHALLENGE_NOT_FOUND_KEYWORD);
		}

		List<UserSavings> userSavingsList = userSavingsRepository.findAllByUserUserIdAndChallengeId(userId,
				challengeId);

		LocalDate startDate = challenge.get().getStartDate();
		LocalDate today = LocalDate.now();
		int days = (int) ChronoUnit.DAYS.between(startDate, today);

		List<WeeklyDollarDonutGraphDto> weeklyDollarSavingsList = new ArrayList<>();
		for (int i = 0; i <= days; i += 7) {
			final LocalDate finalStartDate = startDate.plusDays(i);
			double dollarSavings = userSavingsList.stream()
					.filter(savings -> savings.getSavingsAddedOn().isAfter(finalStartDate.minusDays(1))
							&& savings.getSavingsAddedOn().isBefore(finalStartDate.plusDays(7)))
					.mapToDouble(UserSavings::getDollarSavings).sum();

			WeeklyDollarDonutGraphDto weeklyDollarSavings = new WeeklyDollarDonutGraphDto(dollarSavings, finalStartDate,
					finalStartDate.plusDays(6));
			weeklyDollarSavingsList.add(weeklyDollarSavings);
		}

		return weeklyDollarSavingsList;
	}

	public List<? extends SavingsDto> getChallengeOverviewDonutGraphWeeklyWasteSavingsData(Long userId,
			long challengeId) {
		Optional<Challenge> challenge = challengeRepository.findById(challengeId);
		if (challenge.isEmpty()) {
			throw new EmployeeNotFoundException(Constants.CHALLENGE_NOT_FOUND_KEYWORD);
		}

		List<UserSavings> userSavingsList = userSavingsRepository.findAllByUserUserIdAndChallengeId(userId,
				challengeId);

		LocalDate startDate = challenge.get().getStartDate();
		LocalDate today = LocalDate.now();
		int days = (int) ChronoUnit.DAYS.between(startDate, today);

		List<WeeklyWasteDonutGraphDto> weeklyWasteSavingsList = new ArrayList<>();
		for (int i = 0; i <= days; i += 7) {
			final LocalDate finalStartDate = startDate.plusDays(i);
			double wasteSavings = userSavingsList.stream()
					.filter(savings -> savings.getSavingsAddedOn().isAfter(finalStartDate.minusDays(1))
							&& savings.getSavingsAddedOn().isBefore(finalStartDate.plusDays(7)))
					.mapToDouble(UserSavings::getWasteSavings).sum();

			WeeklyWasteDonutGraphDto weeklyWasteSavings = new WeeklyWasteDonutGraphDto(wasteSavings, finalStartDate,
					finalStartDate.plusDays(6));
			weeklyWasteSavingsList.add(weeklyWasteSavings);
		}

		return weeklyWasteSavingsList;
	}

	// creating this method to call this in donut graph for co2 savings monthly
	// basis
	public List<MonthlyCo2SavingsDto> getChallengeOverviewDonutGraphMonthlyCo2Data(Long userId, long challengeId) {
		Optional<Challenge> challenge = challengeRepository.findById(challengeId);
		if (challenge.isEmpty()) {
			throw new EmployeeNotFoundException(Constants.CHALLENGE_NOT_FOUND_KEYWORD);
		}

		List<UserSavings> userSavingsList = userSavingsRepository.findAllByUserUserIdAndChallengeId(userId,
				challengeId);

		LocalDate startDate = challenge.get().getStartDate();
		LocalDate today = LocalDate.now();
		int months = (int) ChronoUnit.MONTHS.between(startDate, today);

		List<MonthlyCo2SavingsDto> monthlyCo2SavingsList = new ArrayList<>();
		for (int i = 0; i <= months; i++) {
			final LocalDate finalStartDate = startDate.plusMonths(i);
			final LocalDate startOfMonth = finalStartDate.withDayOfMonth(1);
			final LocalDate endOfMonth = finalStartDate.withDayOfMonth(finalStartDate.lengthOfMonth());

			double co2Savings = userSavingsList.stream()
					.filter(savings -> savings.getSavingsAddedOn().isAfter(startOfMonth.minusDays(1))
							&& savings.getSavingsAddedOn().isBefore(endOfMonth.plusDays(1)))
					.mapToDouble(UserSavings::getCo2Savings).sum();

			MonthlyCo2SavingsDto monthlyCo2Savings = new MonthlyCo2SavingsDto(co2Savings, startOfMonth, endOfMonth);
			monthlyCo2SavingsList.add(monthlyCo2Savings);
		}

		return monthlyCo2SavingsList;
	}

	public List<MonthlyDollarSavingsDto> getChallengeOverviewDonutGraphMonthlyDollarData(Long userId,
			long challengeId) {
		Optional<Challenge> challenge = challengeRepository.findById(challengeId);
		if (challenge.isEmpty()) {
			throw new EmployeeNotFoundException(Constants.CHALLENGE_NOT_FOUND_KEYWORD);
		}

		List<UserSavings> userSavingsList = userSavingsRepository.findAllByUserUserIdAndChallengeId(userId,
				challengeId);

		LocalDate startDate = challenge.get().getStartDate();
		LocalDate today = LocalDate.now();
		int months = (int) ChronoUnit.MONTHS.between(startDate, today);

		List<MonthlyDollarSavingsDto> monthlyDollarSavingsList = new ArrayList<>();
		for (int i = 0; i <= months; i++) {
			final LocalDate finalStartDate = startDate.plusMonths(i);
			final LocalDate startOfMonth = finalStartDate.withDayOfMonth(1);
			final LocalDate endOfMonth = finalStartDate.withDayOfMonth(finalStartDate.lengthOfMonth());

			double dollarSavings = userSavingsList.stream()
					.filter(savings -> savings.getSavingsAddedOn().isAfter(startOfMonth.minusDays(1))
							&& savings.getSavingsAddedOn().isBefore(endOfMonth.plusDays(1)))
					.mapToDouble(UserSavings::getDollarSavings).sum();

			MonthlyDollarSavingsDto monthlyDollarSavings = new MonthlyDollarSavingsDto(dollarSavings, startOfMonth,
					endOfMonth);
			monthlyDollarSavingsList.add(monthlyDollarSavings);
		}

		return monthlyDollarSavingsList;
	}

	public List<MonthlyWasteSavingsDto> getChallengeOverviewDonutGraphMonthlyWasteSavingsData(Long userId,
			long challengeId) {
		Optional<Challenge> challenge = challengeRepository.findById(challengeId);
		if (challenge.isEmpty()) {
			throw new EmployeeNotFoundException(Constants.CHALLENGE_NOT_FOUND_KEYWORD);
		}

		List<UserSavings> userWasteSavingsList = userSavingsRepository.findAllByUserUserIdAndChallengeId(userId,
				challengeId);

		LocalDate startDate = challenge.get().getStartDate();
		LocalDate today = LocalDate.now();
		int months = (int) ChronoUnit.MONTHS.between(startDate, today);

		List<MonthlyWasteSavingsDto> monthlyWasteSavingsList = new ArrayList<>();
		for (int i = 0; i <= months; i++) {
			final LocalDate finalStartDate = startDate.plusMonths(i);
			final LocalDate startOfMonth = finalStartDate.withDayOfMonth(1);
			final LocalDate endOfMonth = finalStartDate.withDayOfMonth(finalStartDate.lengthOfMonth());

			double wasteSavings = userWasteSavingsList.stream()
					.filter(savings -> savings.getSavingsAddedOn().isAfter(startOfMonth.minusDays(1))
							&& savings.getSavingsAddedOn().isBefore(endOfMonth.plusDays(1)))
					.mapToDouble(UserSavings::getWasteSavings).sum();

			MonthlyWasteSavingsDto monthlyWasteSavings = new MonthlyWasteSavingsDto(wasteSavings, startOfMonth,
					endOfMonth);
			monthlyWasteSavingsList.add(monthlyWasteSavings);
		}

		return monthlyWasteSavingsList;
	}

	public List<LeaderboardDTO> getAllUsersCo2SavingsForChallenge(String companyEmpId, long challengeId, int weekNumber) {
	    Company company = companyRepository.findByUserEmpId(companyEmpId)
	            .orElseThrow(() -> new EmployeeIdExistsException(UserConstants.EMP_ID_COMPANY));
	    List<User> users = userRepository.findAllByCompanyCompanyId(company.getCompanyId());
	    if (users.isEmpty()) {
	        throw new EmployeeIdExistsException(UserConstants.EMP_ID_EXC);
	    }
	    List<LeaderboardDTO> leaderboard = new ArrayList<>();
	    for (User user : users) {
	        String employeeId = user.getUserEmpId();
	        String storeName = user.getStoreDetails().getStoreName();
	        List<? extends SavingsDto> userSavings = getWeeklySavingsForAChallenge(employeeId, challengeId, "co2");
	        for (SavingsDto savingsDto : userSavings) {
	            WeeklyCo2SavingsDto co2SavingsDto = (WeeklyCo2SavingsDto) savingsDto;
	            if (co2SavingsDto.getWeekNumber() == weekNumber) {
	                LeaderboardDTO leaderboardDTO = new LeaderboardDTO(storeName, co2SavingsDto.getCo2Savings(), co2SavingsDto.getWeekNumber());
	                leaderboard.add(leaderboardDTO);
	            }
	        }
	    }
	    leaderboard.sort((a, b) -> Double.compare(b.getCo2Savings(), a.getCo2Savings()));
	    AtomicInteger rank = new AtomicInteger(1);
	    leaderboard.forEach(dto -> dto.setRank(rank.getAndIncrement()));
	    return leaderboard;
	}
	@Override
	public ResponseEntity<List<MapDto>> getMapDetails(String employeeId,long challengeId) {
		List<MapDto> allStores = new ArrayList<>();
		Optional<Company> company = companyRepository.findByUserEmpId(employeeId);
		if (company.isPresent()) {
			List<User> allUsers = userRepository.findAllByCompanyCompanyId(company.get().getCompanyId());
			for (User user : allUsers) {
				UserSavingsDto userSavingsDto = getUserSavings(user.getUserEmpId());
				Optional<Challenge> challenge = challengeRepository.findById(challengeId);
				if (challenge.isPresent()) {
					Optional<ChallengeEnrollment> challengeEnrollment = enrollmentRepository.findByUserUserIdAndChallengeId(user.getUserId(), challengeId);

					if (challengeEnrollment.isPresent() && challengeEnrollment.get().getEnrollmentStatus().equals(Constants.ENROLLED_STATUS)) {
						MapDto mapDto = new MapDto(
								user.getStoreDetails().getLatitude(),
								user.getStoreDetails().getLongitude(),
								userSavingsDto.getCo2Savings(),
								user.getStoreDetails().getStoreName(),
								challengeEnrollment.get().getMyProgress(),
								user.getName(),
								user.getStoreDetails().getCountry(),
								user.getStoreDetails().getState(),
								user.getStoreDetails().getStreet(),
								user.getStoreDetails().getCity(),
								user.getStoreDetails().getZipCode());
						allStores.add(mapDto);
					}
				} else {
					throw new UsernameNotFoundException(Constants.CHALLENGE_NOT_FOUND);//NOSONAR not used in secure contexts
				}

			}
			return ResponseEntity.ok(allStores);
		}
		throw new EmployeeIdExistsException(Constants.EMP_NOT_FOUND);
		
		
	}
	
	 @Override
	    public ResponseEntity<String> deleteChallenge(String employeeId, long challengeId) {
	        Optional<Company> company = companyRepository.findByUserEmpId(employeeId);
	        Optional<User> user = userRepository.findByUserEmpId(employeeId);
	        Optional<Challenge> challengeOpt = challengeRepository.findById(challengeId);
	        
	        if (challengeOpt.isEmpty()) {
	            throw new UsernameNotFoundException(Constants.CHALLENGE_NOT_FOUND );
	        }
	        
	        Challenge challenge = challengeOpt.get();
	        
	        if ((company.isPresent() && company.get().getCompanyId().equals( challenge.getCompany().getCompanyId()))
	                || (user.isPresent() && user.get().getUserId().equals(challenge.getUser().getUserId()))) {

	        	enrollmentRepository.deleteByChallengeId(challengeId);
	            
	            empRepository.deleteByChallengeId(challengeId);
	            
	            myProgressDailyDataRepository.deleteByChallengeId(challengeId);

	            userSavingsRepository.deleteAllByChallengeId(challengeId);
	            
	            challengeRepository.deleteById(challengeId);
	            
	            return ResponseEntity.ok("Deleted successfully");
	        }
	        
	        return ResponseEntity.ok("No user found with the employee id");
	    }
	@Override
	public List<FileDetails> convertExcelListToJson(List<MultipartFile> excelFiles, String empId) {
	    List<FileDetails> excelDataList = new ArrayList<>();
	    Optional<Company> company = companyRepository.findByUserEmpId(empId);
	    if (company.isPresent()) {
	        for (MultipartFile excelFile : excelFiles) {
	            FileDetails fileDetails = new FileDetails();
	            fileDetails.setComments("Requested By");
	            fileDetails.setUploadDate(LocalDate.now());
	            fileDetails.setTime(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
	            fileDetails.setUploader(company.get().getCompanyName());
	            String filename = excelFile.getOriginalFilename();
	            fileDetails.setFileName(filename);
	 
	            try (Workbook workbook = WorkbookFactory.create(excelFile.getInputStream())) {
	                Sheet sheet = workbook.getSheetAt(0);
	 
	                Row headerRow = sheet.getRow(0);
	                List<String> expectedNames = List.of("upload Date", "market", "store Name", "store Id", "manager Name", "email Id", "address", "master Franchise");
	                if (!validateColumnNames(headerRow, expectedNames)) {
	                    fileDetails.setFileStatus(Constants.FAILED_STATUS);
	                    detailsRepo.save(fileDetails);
	                    excelDataList.add(fileDetails);
	                    continue;  
	                }
	 
	                boolean fileProcessedSuccessfully = true;
	                for (Row row : sheet) {
	                    int rowNumber = row.getRowNum();
	                    if (rowNumber == 0) {
	                        continue; 
	                    }
	                    ExcelData excelData = new ExcelData();
	                    int cellIndex = 0;
	                    boolean validRow = true;
	                    for (Cell cell : row) {
	                        try {
	                            switch (cell.getCellType()) {
	                                case STRING:
	                                    setExcelDataField(excelData, cellIndex, cell.getStringCellValue(), expectedNames);
	                                    break;
	                                case NUMERIC:
	                                    setExcelDataField(excelData, cellIndex, String.valueOf(cell.getNumericCellValue()), expectedNames);
	                                    break;
	                                default:
	                                    setExcelDataField(excelData, cellIndex, "", expectedNames);
	                            }
	                        } catch (Exception e) {
	                            validRow = false;
	                            break;
	                        }
	                        cellIndex++;
	                    }
	                    if (validRow) {
	                        excelData.setFileDetails(fileDetails);
	                        excelRepo.save(excelData);
	                        
	                    } else {
	                        fileProcessedSuccessfully = false;
	                        break;
	                    }
	                }
	 
	                if (fileProcessedSuccessfully) {
	                    fileDetails.setFileStatus(Constants.COMPLETED_STATUS);
	                } else {
	                    fileDetails.setFileStatus(Constants.FAILED_STATUS);
	                }
	                detailsRepo.save(fileDetails);
	                excelDataList.add(fileDetails);
	            } catch (IOException | EncryptedDocumentException e) {
	                fileDetails.setFileStatus(Constants.FAILED_STATUS);
	                detailsRepo.save(fileDetails);
	                excelDataList.add(fileDetails);
	            }
	        }
	    } else {
	        throw new EmployeeIdExistsException(Constants.EMP_NOT_FOUND);
	    }
	    return excelDataList;
	}
	 
	public boolean validateColumnNames(Row headerRow, List<String> expectedNames) {
	    for (int i = 0; i < expectedNames.size(); i++) {
	        Cell cell = headerRow.getCell(i);
	        if (cell == null || !expectedNames.get(i).trim().equalsIgnoreCase(cell.getStringCellValue().trim())) {
	            return false;
	        }
	    }
	    return true;
	}
	 
	public void setExcelDataField(ExcelData excelData, int cellIndex, String cellValue, List<String> columnHeaders) throws NoSuchFieldException, IllegalAccessException {
	    String columnHeader = columnHeaders.get(cellIndex);
	    String fieldName = columnHeader.replace(" ", "");
	    Field field = ExcelData.class.getDeclaredField(fieldName);
	    field.setAccessible(true);
	    Object value = null;
	    if (field.getType() == String.class) {
	        value = cellValue;
	    } else if (field.getType() == Integer.class) {
	        value = Integer.valueOf(cellValue);
	    } else if (field.getType() == Double.class) {
	        value = Double.valueOf(cellValue);
	    } else if (field.getType() == LocalDate.class) {
	        long numericDate = (long) Double.parseDouble(cellValue);
	        LocalDate epochStart = LocalDate.of(1899, 12, 30);
	        LocalDate date = epochStart.plusDays(numericDate);
	        value = date;
	    }
	    if (value != null) {
	        field.set(excelData, value);
	    }
	}

	@Override
	public String deleteExcelDataByIds(List<Long> rowId,String empId) {
	    Optional<Company> company = companyRepository.findByUserEmpId(empId);
	    if (company.isPresent()) {
		  List<Long> missingIds = rowId.stream()
	                .filter(id -> excelRepo.findById(id).isEmpty())
	                .toList();
	        if (!missingIds.isEmpty()) {
	            throw new IllegalArgumentException("ExcelData with IDs " + missingIds + " not found");
	        }
	      for(Long id : rowId) {
	    	  excelRepo.deleteById(id);
	      }
	      return "Successfully deleted " + rowId.size() + " ExcelData entities";
	    }else {
	        throw new EmployeeIdExistsException(Constants.EMP_NOT_FOUND);
	    }
	}
	
	@Override
	public List<UploadHistoryDto> getUploadHistoryByEmpId(String empId) {
	    Optional<Company> company = companyRepository.findByUserEmpId(empId);
	    if (company.isEmpty()) {
	        throw new EmployeeIdExistsException(Constants.EMP_NOT_FOUND);
	    }
	    String companyName = company.get().getCompanyName();
	    List<FileDetails> fileDetailsList = detailsRepo.findByUploader(companyName);
	    if (fileDetailsList.isEmpty()) {
	        return Collections.emptyList();
	    }
	    Map<String, List<FileDetails>> groupedFileDetails = fileDetailsList.stream()
	            .collect(Collectors.groupingBy(fd -> fd.getUploadDate() + fd.getTime()));
	    List<UploadHistoryDto> uploadHistoryDtos = new ArrayList<>();
	    for (Map.Entry<String, List<FileDetails>> entry : groupedFileDetails.entrySet()) {
	        List<FileDetails> group = entry.getValue();
	        UploadHistoryDto uploadHistoryDto = new UploadHistoryDto();
	        uploadHistoryDto.setUploadDate(group.get(0).getUploadDate().toString());
	        uploadHistoryDto.setTime(group.get(0).getTime());
	        uploadHistoryDto.setUploader(group.get(0).getUploader());
	        uploadHistoryDto.setComments(group.get(0).getComments());
	        boolean hasFailedFile = group.stream().anyMatch(fd -> Constants.FAILED_STATUS.equalsIgnoreCase(fd.getFileStatus()));
	        uploadHistoryDto.setStatus(hasFailedFile ? Constants.FAILED_STATUS : Constants.COMPLETED_STATUS);
	        List<UploadHistoryFileDto> fileDtos = group.stream()
	                .map(fd -> new UploadHistoryFileDto(fd.getId(), fd.getId(), fd.getFileName(), fd.getFileStatus(), null))
	                .toList();
	        uploadHistoryDto.setFilesList(fileDtos);
	        uploadHistoryDtos.add(uploadHistoryDto);
	    }
	    return uploadHistoryDtos;
	}

	@Override
	public List<ExcelData> viewAllDataByEmployeeId(String empId) {
	    try {
	        Optional<Company> company = companyRepository.findByUserEmpId(empId);
	        if (company.isPresent()) {
	            List<FileDetails> fileDetails = detailsRepo.findByUploader(company.get().getCompanyName());
	            List<ExcelData> excelDataList = new ArrayList<>();
	            for (FileDetails fileDetail : fileDetails) {
	                List<ExcelData> excelData = excelRepo.findByFileDetails(fileDetail);
	                excelDataList.addAll(excelData);
	            }
	            return excelDataList;
	        } else {
	            throw new UserNotFoundException("Company not found for the given employee id: " + empId);
	        }
	    } catch (Exception e) {
	    	return Collections.emptyList();
	    }
	}

	@Override
	public List<ExcelData> viewByFileId(Long fileId) {
		Optional<FileDetails> findById = detailsRepo.findById(fileId);
		if(findById.isPresent()) {
			return excelRepo.findByFileDetails(findById.get());
		}
		else {
            throw new UserNotFoundException("File is not present with given id");
        }
		
	}
	
	
	
	@Override
	public ResponseEntity<String> editChallenge(String employeeId, long challengeId, Challenge challenge) {
	    Optional<Company> company = companyRepository.findByUserEmpId(employeeId);
	    Optional<User> user = userRepository.findByUserEmpId(employeeId);
	    Optional<Challenge> challenge1 = challengeRepository.findById(challengeId);
	    
	    if(challenge1.isEmpty()) {
	        throw new UsernameNotFoundException(Constants.CHALLENGE_NOT_FOUND );
	    }
	    
	    if ((company.isPresent() && company.get().getCompanyId().equals( challenge1.get().getCompany().getCompanyId())) 
	    		|| (user.isPresent() && user.get().getUserId() .equals( challenge1.get().getUser().getUserId()))){
	        
	        Challenge existingChallenge = challenge1.get();
	        existingChallenge.setChallengeName(challenge.getChallengeName());
	        existingChallenge.setDescription(challenge.getDescription());
	        existingChallenge.setEvaluationKpi(challenge.getEvaluationKpi());
	        existingChallenge.setEligibility(challenge.getEligibility());
	        //createdBy is not set because it is not changed
	        existingChallenge.setDataRequired(challenge.getDataRequired());
	        existingChallenge.setWinner(challenge.getWinner());
	        existingChallenge.setFirstRunnerUp(challenge.getFirstRunnerUp());
	        existingChallenge.setSecondRunnerUp(challenge.getSecondRunnerUp());
	        existingChallenge.setStartDate(challenge.getStartDate());
	        existingChallenge.setEndDate(challenge.getEndDate());
	        existingChallenge.setWinnerAnnouncementDate(challenge.getWinnerAnnouncementDate());
	        existingChallenge.setStatus(challenge.getStatus());
	        //company and user is not set because it is not changed.
	        //saving the updated challenge.
	        challengeRepository.save(existingChallenge);
	        return ResponseEntity.ok(Constants.CHALLENGE_UPDATED_SUCCESSFULLY);
	    }
	    return ResponseEntity.ok("No user found with the employee id");
	}
	

}
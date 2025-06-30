package com.genai.codeiumapp.service;

import com.genai.codeiumapp.dto.AllDetailsDTO;
import com.genai.codeiumapp.dto.AllStoresDto;
import com.genai.codeiumapp.dto.BarGraphDto;
import com.genai.codeiumapp.dto.ChallengeDetailsDto;
import com.genai.codeiumapp.dto.EmployeeDTO;
import com.genai.codeiumapp.dto.ExcelDto;
import com.genai.codeiumapp.dto.MapDto;
import com.genai.codeiumapp.dto.NotificationDTO;
import com.genai.codeiumapp.dto.OverviewDto;
import com.genai.codeiumapp.dto.PersonalDetailsDto;
import com.genai.codeiumapp.dto.ProfileImageDTO;
import com.genai.codeiumapp.dto.SavingsDto;
import com.genai.codeiumapp.dto.UploadHistoryDto;
import com.genai.codeiumapp.dto.UserSavingsDto;
import com.genai.codeiumapp.model.Challenge;
import com.genai.codeiumapp.model.ChallengeDto;
import com.genai.codeiumapp.model.ExcelData;
import com.genai.codeiumapp.model.FileDetails;
import com.genai.codeiumapp.model.ParticipateDto;
import com.genai.codeiumapp.model.User;
import com.genai.codeiumapp.model.UserSavings;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserService {

	String registerManager(User user);

	Object getUserByEmail(String email);

	String changePassword(String email, String oldPassword, String newPassword);


	PersonalDetailsDto getPersonalDetails(String employeeId);

	ResponseEntity<String> saveUserSavings(UserSavings userSavings);

	UserSavingsDto getUserSavings(String employeeId);

	ChallengeDetailsDto getChallengeDetails(String employeeId);

	List<Map<String, Object>> getEmployeesByUserId(String employeeId);

	Map<String, Object> getManagersAndEmployees();

	EmployeeDTO getEmployeeDetails(String employeeId);
	
	ResponseEntity<String> markAllNotificationsAsRead(String employeeId);

	List<NotificationDTO> getAllNotificationsByUserId(String employeeId);

	AllDetailsDTO getUserDetails(String employeeId);
	
	List<OverviewDto> employeesOverview(Long challengeId ,String employeeId);

	void approveUser(Long id, String userEmpId);

	void declineUser(Long id, String userEmpId);
	
	ResponseEntity<String> verifyOtp(String userEmpId, long enteredOtp);

	void generateExcel(HttpServletResponse response, ExcelDto excelDto) throws IOException;

	Challenge createChallenge(Challenge challenge, String employeeId);

	String enrollChallenge(Long challengeId, String employeeId);

	List<ChallengeDto> getMyChallenges(String required, String employeeId);

	List<ParticipateDto> getParticipateChallenges(String required, String employeeId);

	List<? extends SavingsDto> getUserSavingsInProfileForChart(String employeeId, String savingsType);
	ResponseEntity<String> verifyOtpByEmail(String email, long enteredOtp);
	ResponseEntity<String> sendOtpToEmail(String email);

	ProfileImageDTO uploadProfileImage(String userEmpId, MultipartFile file) throws IOException;
	ResponseEntity<List<MapDto>> getMapDetails(String employeeId,long challengeId);
	ResponseEntity<List<BarGraphDto>> challengeOverviewBarGraph(String employeeId, long challengeId,
			String field, String xAxis, String yAxis, AllStoresDto allStoresDto);

	ResponseEntity<String> deleteChallenge(String employeeId, long challengeId);
	
	List<FileDetails> convertExcelListToJson(List<MultipartFile> excelFiles,String empId) ;
	 String deleteExcelDataByIds(List<Long> ids,String empId);
	 
	 List<UploadHistoryDto> getUploadHistoryByEmpId(String empId);
	 List<ExcelData> viewAllDataByEmployeeId(String empId);
	 List<ExcelData> viewByFileId(Long fileId);
	
	ResponseEntity<String> editChallenge(String employeeId, long challengeId, Challenge challenge);
}

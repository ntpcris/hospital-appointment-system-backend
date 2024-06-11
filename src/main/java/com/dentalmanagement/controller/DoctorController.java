package com.dentalmanagement.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.dentalmanagement.dto.*;
import com.dentalmanagement.entity.Specialist;
import com.dentalmanagement.service.SpecialistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import com.dentalmanagement.entity.User;
import com.dentalmanagement.service.UserService;
import com.dentalmanagement.utility.Constants.ResponseCode;
import com.dentalmanagement.utility.Constants.UserRole;
import com.dentalmanagement.utility.Constants.UserStatus;
import com.dentalmanagement.utility.StorageService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/doctor/")
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorController {
	
	Logger LOG = LoggerFactory.getLogger(DoctorController.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserService userService;

	@Autowired
	private StorageService storageService;

	@Autowired
	private SpecialistService specialistService;

	@PostMapping("register")
	@ApiOperation(value = "Api to register doctor")
	public ResponseEntity<?> registerDoctor(DoctorRegisterDto doctorRegisterDto) {
		LOG.info("Recieved request for doctor register");

		User user = DoctorRegisterDto.toEntity(doctorRegisterDto);

		boolean EmailExists = userService.checkEmailExists(user.getEmailId());
		if (EmailExists) {
			CommanApiResponse responseCheckEmail = new CommanApiResponse();
			responseCheckEmail.setResponseCode(ResponseCode.FAILED.value());
			responseCheckEmail.setResponseMessage("Email already exists. Please register with a different email.");
			return new ResponseEntity<>(responseCheckEmail, HttpStatus.BAD_REQUEST);
		}

		String image = storageService.store(doctorRegisterDto.getImage());
		user.setDoctorImage(image);

		String encodedPassword = passwordEncoder.encode(user.getPassword());

		user.setPassword(encodedPassword);
		user.setStatus(UserStatus.ACTIVE.value());

		String specialistName = doctorRegisterDto.getSpecialistName();
		Specialist specialist = specialistService.findByName(specialistName);
		user.setSpecialist(specialist);

		User registerUser = userService.registerUser(user);
		CommanApiResponse response = new CommanApiResponse();
		if (registerUser != null) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage(user.getRole() + " Doctor Registered Successfully");

			return new ResponseEntity<>(response, HttpStatus.OK);
		}

		else {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to Register Doctor");
			return  new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all")
	public ResponseEntity<?> getAllDoctor() {
		LOG.info("recieved request for getting ALL Customer!!!");
		
		List<User> doctors = this.userService.getAllUserByRole(UserRole.DOCTOR.value());
		
		LOG.info("response sent!!!");
		return ResponseEntity.ok(doctors);
	}
	@GetMapping("/id")
	public ResponseEntity<?> getDoctorById(@RequestParam("id") int id) {
		LOG.info("received request for getting info Customer!!!");

		User doctor = this.userService.getUserById(id);

		LOG.info("response sent!!!");
		return ResponseEntity.ok(doctor);
	}


	@GetMapping(value = "/{doctorImageName}", produces = "image/*")
	@ApiOperation(value = "Api to fetch doctor image by using image name")
	public void fetchProductImage(@PathVariable("doctorImageName") String doctorImageName, HttpServletResponse resp) {
		LOG.info("request came for fetching doctor pic");
		LOG.info("Loading file: " + doctorImageName);
		Resource resource = storageService.load(doctorImageName);
		if (resource != null) {
			try (InputStream in = resource.getInputStream()) {
				ServletOutputStream out = resp.getOutputStream();
				FileCopyUtils.copy(in, out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		LOG.info("response sent!");
	}

	@PostMapping("/updateDoctor")
	public ResponseEntity<?> updatePatient(@RequestBody UpdateDoctorDto updateDoctorDto) {
		LOG.info("received request for updating info Doctor!!!");

		User doctor = this.userService.getUserById(updateDoctorDto.getId());

		doctor.setFirstName(updateDoctorDto.getFirstName());
		doctor.setLastName(updateDoctorDto.getLastName());
		doctor.setContact(updateDoctorDto.getContact());
		doctor.setAge(updateDoctorDto.getAge());
		doctor.setSex(updateDoctorDto.getSex());
		doctor.setExperience(updateDoctorDto.getExperience());
		doctor.setStreet(updateDoctorDto.getStreet());
		doctor.setCity(updateDoctorDto.getCity());

		User updatePatient = this.userService.updateUser(doctor);

		if (updatePatient != null) {
			return ResponseEntity.ok(updatePatient);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("specialist/all")
	public ResponseEntity<?> getAllSpecialist() {

		LOG.info("Received the request for getting as Specialist");
		
		List<Specialist> specialists = specialistService.getAllSpecialist();
		
		LOG.info("Response sent!!!");

		return new ResponseEntity<>(specialists, HttpStatus.OK);
	}

	@GetMapping("getDoctorBySpecialist/{specialist}")
	public ResponseEntity<?> getDoctorBySpecialist(@PathVariable String specialist) {

		LOG.info("Received the request for getting as Doctor by Specialist");

		List<User> doctors = userService.getDoctorBySpecialist(specialist);

		LOG.info("Response sent!!");

		return new ResponseEntity<>(doctors, HttpStatus.OK);

	}

	@PostMapping("/changePassword")
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
		LOG.info("received request for changing password!!!");

		User doctor = userService.getUserByEmailId(changePasswordRequest.getEmailId());

		// Kiểm tra mật khẩu hiện tại có khớp hay không
		if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), doctor.getPassword())) {
			return ResponseEntity.badRequest().body("Invalid current password");
		}
		// Mã hóa mật khẩu mới
		String newPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());

		// Cập nhật mật khẩu trong database
		doctor.setPassword(newPassword);
		userService.updateUser(doctor);

		// Gửi phản hồi thành công
		return ResponseEntity.ok().build();

	}

}

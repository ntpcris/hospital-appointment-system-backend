package com.dentalmanagement.controller;

import java.util.ArrayList;
import java.util.List;

import com.dentalmanagement.dto.ChangePasswordRequest;
import com.dentalmanagement.dto.CommanApiResponse;
import com.dentalmanagement.dto.UpdatePatientDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.dentalmanagement.entity.User;
import com.dentalmanagement.service.UserService;
import com.dentalmanagement.utility.Constants.BloodGroup;
import com.dentalmanagement.utility.Constants.UserRole;

@RestController
@RequestMapping("api/patient/")
@CrossOrigin(origins = "http://localhost:3000")
public class PatientController {
	
	Logger LOG = LoggerFactory.getLogger(PatientController.class);

	@Autowired
	private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

	@GetMapping("/bloodgroup/all")
	public ResponseEntity<?> getAllBloodGroups() {
		
		LOG.info("Received the request for getting all the Blood Groups");
		
		List<String> bloodGroups = new ArrayList<>();

		for (BloodGroup bg : BloodGroup.values()) {
			bloodGroups.add(bg.value());
		}
		
		LOG.info("Response Sent!!!");

		return new ResponseEntity(bloodGroups, HttpStatus.OK);
	}
	
	@GetMapping("all")
	public ResponseEntity<?> getAllPatient() {
		LOG.info("recieved request for getting ALL Customer!!!");
		
		List<User> patients = this.userService.getAllUserByRole(UserRole.PATIENT.value());
		
		LOG.info("response sent!!!");
		return ResponseEntity.ok(patients);
	}

	@GetMapping("/id")
	public ResponseEntity<?> getPatientById(@RequestParam("id") int id) {
		LOG.info("recieved request for getting info Customer!!!");

		User patient = this.userService.getUserById(id);

		LOG.info("response sent!!!");
		return ResponseEntity.ok(patient);
	}

	@PostMapping("/updatePatient")
	public ResponseEntity<?> updatePatient(@RequestBody UpdatePatientDto updatePatientDto) {
		LOG.info("received request for updating info Customer!!!");

		User patient = this.userService.getUserById(updatePatientDto.getId());

		patient.setFirstName(updatePatientDto.getFirstName());
		patient.setLastName(updatePatientDto.getLastName());
		patient.setContact(updatePatientDto.getContact());
		patient.setAge(updatePatientDto.getAge());
		patient.setSex(updatePatientDto.getSex());
		patient.setStreet(updatePatientDto.getStreet());
		patient.setCity(updatePatientDto.getCity());
		patient.setRole(updatePatientDto.getRole());

		User updatePatient = this.userService.updateUser(patient);

		if (updatePatient != null) {
			return ResponseEntity.ok(updatePatient);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/changePassword")
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
		LOG.info("received request for changing password!!!");

		User patient = userService.getUserByEmailId(changePasswordRequest.getEmailId());

		// Kiểm tra mật khẩu hiện tại có khớp hay không
		if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), patient.getPassword())) {
			return ResponseEntity.badRequest().body("Invalid current password");
		}
		// Mã hóa mật khẩu mới
		String newPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());

		// Cập nhật mật khẩu trong database
		patient.setPassword(newPassword);
		userService.updateUser(patient);

		// Gửi phản hồi thành công
		return ResponseEntity.ok().build();

	}


}

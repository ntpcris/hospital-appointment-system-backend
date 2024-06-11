package com.dentalmanagement.dto;

import com.dentalmanagement.entity.Specialist;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;
import com.dentalmanagement.entity.User;

@Data
public class DoctorRegisterDto {

	private String firstName;

	private String lastName;

	private int age;

	private String sex;

	private String emailId;

	private String contact;

	private String street;

	private String city;

	private String pincode;

	private String password;

	private String role;

	private String specialistName;

	private int status;

	private int experience;

	private MultipartFile image;

	public static User toEntity(DoctorRegisterDto doctorRegisterDto) {
		User user = new User();
		BeanUtils.copyProperties(doctorRegisterDto, user, "image", "specialistName");
		return user;
	}

}

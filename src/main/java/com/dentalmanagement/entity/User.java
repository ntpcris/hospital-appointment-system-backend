package com.dentalmanagement.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import com.dentalmanagement.dto.DoctorRegisterDto;
import com.dentalmanagement.dto.UserLoginResponse;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String firstName;

	private String lastName;

	private int age;

	private String sex;

	private String bloodGroup;

	private String emailId;

	private String contact;

	private String street;

	private String city;

	private String pincode;

	private String password;

	private String role;

	@ManyToOne
	@JoinColumn(name = "specialist_id")
	//@JsonBackReference//mới thêm
	private Specialist specialist;

	private int status;

	private String doctorImage;

	private int experience;

//	@ManyToMany(mappedBy = "doctors")
//	private Set<TimeSchedule> timeSchedules = new HashSet<>();
	@OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL) // OneToMany with SlotBooking
	@JsonIgnore
	private Set<SlotBooking> slotBookings = new HashSet<>();


	public static UserLoginResponse toUserLoginResponse(User user) {
		UserLoginResponse userLoginResponse = new UserLoginResponse();
		BeanUtils.copyProperties(user, userLoginResponse, "password");
		return userLoginResponse;
	}

	public static DoctorRegisterDto toUserDto(User user) {
		DoctorRegisterDto userDto = new DoctorRegisterDto();
		BeanUtils.copyProperties(user, userDto, "password");
		return userDto;
	}

}

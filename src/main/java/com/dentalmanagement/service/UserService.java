package com.dentalmanagement.service;

import java.util.List;

import com.dentalmanagement.dao.SpecialistDao;
import com.dentalmanagement.entity.Specialist;
import com.dentalmanagement.utility.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dentalmanagement.dao.UserDao;
import com.dentalmanagement.entity.User;
import com.dentalmanagement.utility.Constants.UserStatus;


@Service
public class UserService {
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private SpecialistDao specialistDao;
	
	public User registerUser(User user) {
		User registeredUser = null;
		if(user != null) {
			registeredUser = this.userDao.save(user);
		}
		
		return registeredUser;
	}
	
	public User getUserByEmailIdAndPassword(String emailId, String password) {
		return this.userDao.findByEmailIdAndPassword(emailId, password);
	}
	
	public User getUserByEmailIdAndPasswordAndRole(String emailId, String password, String role) {
		return this.userDao.findByEmailIdAndPasswordAndRole(emailId, password, role);
	}
	
	public User getUserByEmailIdAndRole(String emailId, String role) {
		return this.userDao.findByEmailIdAndRole(emailId, role);
	}
	
	public User getUserByEmailId(String emailId) {
		return this.userDao.findByEmailId(emailId);
	}
	
	public User getUserById(int userId) {
		return this.userDao.findById(userId).get();
	}
	
	public User updateUser(User user) {
		return this.userDao.save(user);
	}
	
	public List<User> getAllUserByRole(String role) {
		return this.userDao.findByRoleAndStatus(role, UserStatus.ACTIVE.value());
	}
	public boolean checkEmailExists(String emailId) {
		return this.userDao.findByEmailId(emailId) != null;
	}

	public void deleteUser(User user) {
		this.userDao.delete(user);
	}

	public List<User> getDoctorBySpecialist(String specialistName){
		Specialist specialist = specialistDao.findByName(specialistName);

		return userDao.findBySpecialistIdAndRole(specialist.getId(), Constants.UserRole.DOCTOR.value());
	}
}

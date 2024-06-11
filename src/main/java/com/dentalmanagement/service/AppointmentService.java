package com.dentalmanagement.service;

import java.util.ArrayList;
import java.util.List;

import com.dentalmanagement.dao.SlotBookingDao;
import com.dentalmanagement.entity.SlotBooking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dentalmanagement.dao.AppointmentDao;
import com.dentalmanagement.entity.Appointment;

@Repository
public class AppointmentService {
	
	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private SlotBookingService slotBookingService;
	
	public Appointment addAppointment(Appointment appointment) {
		return appointmentDao.save(appointment);
	}
	
	public Appointment getAppointmentById(int id) {
		return appointmentDao.findById(id).get();
	}

	public List<Appointment> getAllAppointment() {
		return appointmentDao.findAll();
	}
	
	public List<Appointment> getAppointmentByPatientId(int patiendId) {
		return appointmentDao.findByPatientId(patiendId);
	}
	
	public List<Appointment> getAppointmentByDoctorId(int doctorId) {
		return appointmentDao.findAppointmentsByDoctorId(doctorId);
	}
//	public List<Appointment> getAppointmentByDoctorId(int doctorId) {
//
//		List<Appointment> appointments = new ArrayList<Appointment>();
//		List<SlotBooking> slotBookings = slotBookingService.getSlotsByDoctorId(doctorId);
//		for(SlotBooking slotBooking : slotBookings) {
//			appointments.add(appointmentDao.g)
//		}
//	}
	
	
}

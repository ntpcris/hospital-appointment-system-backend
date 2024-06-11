package com.dentalmanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dentalmanagement.entity.Appointment;

@Repository
public interface AppointmentDao extends JpaRepository<Appointment, Integer>  {
	
	List<Appointment> findByPatientId(int patientId);

	@Query("SELECT a FROM Appointment a JOIN a.slotBooking sb WHERE sb.doctor.id = :doctorId")
	List<Appointment> findAppointmentsByDoctorId(@Param("doctorId") int doctorId);

}

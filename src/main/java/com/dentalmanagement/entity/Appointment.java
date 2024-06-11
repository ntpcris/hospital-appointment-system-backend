package com.dentalmanagement.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private int patientId;

	private String appointmentDate;

	private String date;

	private String specialist;

	@OneToOne
	@JoinColumn( name = "slot_Booking_Id", referencedColumnName = "id")
	private SlotBooking slotBooking;

	private String problem;

	private String prescription;

	private String result;

	private String status;

}



package com.dentalmanagement.dto;

import lombok.Data;

@Data
public class AppointmentResponseDto {

	private int id;

	private int patientId;

	private int doctorId;

	private String patientName;

	private String patientContact;

	private String problem;

	private String doctorName;

	private String doctorContact;

	private String specialist;

	private String date;

	private String appointmentDate;

	private String startTime;

	private String endTime;

	private String prescription;

	private String result;

	private String status;

	private String bloodGroup;



}

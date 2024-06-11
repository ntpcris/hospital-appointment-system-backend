package com.dentalmanagement.dto;

import lombok.Data;

@Data
public class UpdateAppointmentRequest {

	private int appointmentId;

	private int slotId;

	private String specialist;
}

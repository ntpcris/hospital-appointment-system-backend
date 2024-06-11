package com.dentalmanagement.dto;

import lombok.Data;

@Data
public class UpdateAppointmentRequestByPatient {

    private int appointmentId;

    private String status;
}

package com.dentalmanagement.dto;

import lombok.Data;

@Data
public class UpdateAppointmentRequestByDoctor {

    private int appointmentId;

    private String result;

    private String prescription;

    private String status;
}

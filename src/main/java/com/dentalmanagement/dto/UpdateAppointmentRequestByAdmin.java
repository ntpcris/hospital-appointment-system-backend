package com.dentalmanagement.dto;

import lombok.Data;

@Data
public class UpdateAppointmentRequestByAdmin {

    private int appointmentId;

    private int slotId;

    private String specialist;

}

package com.dentalmanagement.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {

    private String emailId;

    private String currentPassword;

    private String newPassword;
}

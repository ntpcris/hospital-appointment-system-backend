package com.dentalmanagement.dto;

import lombok.Data;

@Data
public class UpdatePatientDto {

    private int id;

    private String firstName;

    private String lastName;

    private String emailId;

    private String contact;

    private int age;

    private String sex;

    private String street;

    private String city;

    private String role;


}

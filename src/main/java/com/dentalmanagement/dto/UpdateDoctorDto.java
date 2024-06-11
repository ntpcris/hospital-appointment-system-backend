package com.dentalmanagement.dto;

import lombok.Data;

@Data
public class UpdateDoctorDto {
    private int id;

    private String firstName;

    private String lastName;

    private String contact;

    private int age;

    private String sex;

    private int experience;

    private String street;

    private String city;


}

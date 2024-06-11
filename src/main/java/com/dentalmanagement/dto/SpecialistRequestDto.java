package com.dentalmanagement.dto;

import com.dentalmanagement.entity.Specialist;
import com.dentalmanagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialistRequestDto {

    private String name;

    private MultipartFile image;

    private String createDate;


    public static Specialist toEntity(SpecialistRequestDto specialistRequestDto) {
        Specialist specialist = new Specialist();
        BeanUtils.copyProperties(specialistRequestDto, specialist, "image");
        return specialist;
    }

//    public static User toEntity(DoctorRegisterDto doctorRegisterDto) {
//        User user = new User();
//        BeanUtils.copyProperties(doctorRegisterDto, user, "image", "specialistName");
//        return user;
//    }
}

package com.dentalmanagement.dto;

import lombok.Data;

import java.util.Date;
@Data
public class TimeScheduleRequestDto {

    private String date;

    private String startTime;

    private String endTime;


}

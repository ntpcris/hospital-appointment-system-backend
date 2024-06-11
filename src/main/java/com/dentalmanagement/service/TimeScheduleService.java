package com.dentalmanagement.service;

import com.dentalmanagement.dao.TimeScheduleDao;
import com.dentalmanagement.dto.TimeScheduleRequestDto;
import com.dentalmanagement.entity.TimeSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TimeScheduleService {
    @Autowired
    private TimeScheduleDao timeScheduleDao;

    public List<TimeSchedule> getScheduleTimesByDate(String date) {
        return timeScheduleDao.findByDate(date);
    }

    public List<TimeSchedule> addTimeSchedule(List<TimeScheduleRequestDto> timeWorks){

        List<TimeSchedule> createdTimeSchedules = new ArrayList<>();
        for (TimeScheduleRequestDto timeWork : timeWorks) {
            createdTimeSchedules.addAll(calculateTimeSchedule(timeWork.getDate(),
                    timeWork.getStartTime(), timeWork.getEndTime()));
        }
        timeScheduleDao.saveAll(createdTimeSchedules);
        return createdTimeSchedules;
    }
    private List<TimeSchedule> calculateTimeSchedule(String date, String startTime, String endTime) {
        List<TimeSchedule> timeSchedules = new ArrayList<>();

        LocalTime startTimeLocalTime = LocalTime.parse(startTime);
        LocalTime endTimeLocalTime = LocalTime.parse(endTime);

        while (startTimeLocalTime.isBefore(endTimeLocalTime.plusMinutes(1)) || startTimeLocalTime.equals(endTimeLocalTime)) {
            TimeSchedule timeSchedule = new TimeSchedule();
            timeSchedule.setDate(date);
            timeSchedule.setStartTime(startTimeLocalTime.toString());

            // Set end time 30 minutes after start time
            LocalTime endTimeForSlot = startTimeLocalTime.plusMinutes(30);
            timeSchedule.setEndTime(endTimeForSlot.toString());

            timeSchedules.add(timeSchedule);

            // Update start time for the next slot
            startTimeLocalTime = startTimeLocalTime.plusMinutes(30);
        }

        return timeSchedules;

    }

//    private LocalDate convertStringToDate(String dateString) {
//        // Parse the date string (assuming format YYYY-MM-DD)
//        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//    }


}

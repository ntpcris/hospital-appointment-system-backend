package com.dentalmanagement.controller;

import com.dentalmanagement.dto.CommanApiResponse;
import com.dentalmanagement.dto.TimeScheduleRequestDto;
import com.dentalmanagement.entity.TimeSchedule;
import com.dentalmanagement.service.SlotBookingService;
import com.dentalmanagement.service.TimeScheduleService;
import com.dentalmanagement.utility.Constants;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/timeschedule")
@CrossOrigin(origins = "http://localhost:3000")
public class TimeScheduleController {

    Logger LOG = Logger.getLogger(TimeScheduleController.class.getName());

    @Autowired
    private TimeScheduleService timeScheduleService;

    @Autowired
    private SlotBookingService slotBookingService;

    @PostMapping("admin/addSchedule")
    @ApiOperation(value = "Api to add schedule")
    public ResponseEntity<?> addSchedule(@RequestBody List<TimeScheduleRequestDto> request) {
        LOG.info("Receive request to add Schedule");

        CommanApiResponse response = new CommanApiResponse();

        if (request == null || request.isEmpty()) {
            response.setResponseCode(Constants.ResponseCode.FAILED.value());
            response.setResponseMessage("Failed to add TimeSchedule - Empty data");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<TimeSchedule> addTimeSchedule = timeScheduleService.addTimeSchedule(request);
        if (addTimeSchedule != null) {
            slotBookingService.addSlotBooking(addTimeSchedule);
            response.setResponseCode(Constants.ResponseCode.SUCCESS.value());
            response.setResponseMessage("Successfully added TimeSchedule");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        else {
            response.setResponseCode(Constants.ResponseCode.FAILED.value());
            response.setResponseMessage("Failed to add TimeSchedule");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("admin/getScheduleByDate")
    @ApiOperation(value = "Api to get schedule by date")
    public ResponseEntity<?> getScheduleTimesByDate(@RequestParam String date) {

        LOG.info("Receive request to get TimeSchedule by date");

        List<TimeSchedule> timeSchedules = timeScheduleService.getScheduleTimesByDate(date);

        LOG.info("Response sent!!!");

        return new ResponseEntity<>(timeSchedules, HttpStatus.OK);
    }

    


}

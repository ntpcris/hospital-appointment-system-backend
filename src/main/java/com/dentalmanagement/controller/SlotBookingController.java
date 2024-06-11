package com.dentalmanagement.controller;

import com.dentalmanagement.dto.CommanApiResponse;
import com.dentalmanagement.entity.SlotBooking;
import com.dentalmanagement.entity.User;
import com.dentalmanagement.service.SlotBookingService;
import com.dentalmanagement.service.SpecialistService;
import com.dentalmanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/slotBooking/")
@CrossOrigin(origins = "http://localhost:3000")
public class SlotBookingController {

    Logger LOG = LoggerFactory.getLogger(SlotBooking.class);

    @Autowired
    private SlotBookingService slotBookingService;
    @Autowired
    private SpecialistService specialistService;
    @Autowired
    private UserService userService;

    @GetMapping("getAllSLotByDoctorAndDate")
    public ResponseEntity<?> getAllSLotByDoctorAndDate(@RequestParam("doctorId") int doctorId, @RequestParam("date") String date){
        LOG.info("Received request get list SlotBooking by doctor and date");

        List<SlotBooking> slotBookings = slotBookingService.getSlotBookingByDoctorAndDate(doctorId,date);

        LOG.info("response sent!!!");

        return new ResponseEntity<>(slotBookings, HttpStatus.OK);
    }

    @GetMapping("getSlotBySpecialist")
    public ResponseEntity<?> getSlotBySpecialist(@RequestParam("specialist") String specialist, @RequestParam("date") String date){
        LOG.info("Received request get list SlotBooking by specialist");

        SlotBooking slotBooking = slotBookingService.getSlotBookingBySpecialist(specialist,date);

        if(slotBooking == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        LOG.info("response sent!!!");
        return new ResponseEntity<>(slotBooking, HttpStatus.OK);
    }

}

package com.dentalmanagement.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.dentalmanagement.dto.*;
import com.dentalmanagement.entity.SlotBooking;
import com.dentalmanagement.entity.TimeSchedule;
import com.dentalmanagement.service.SlotBookingService;
import com.dentalmanagement.service.TimeScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dentalmanagement.entity.Appointment;
import com.dentalmanagement.entity.User;
import com.dentalmanagement.exception.AppointmentNotFoundException;
import com.dentalmanagement.service.AppointmentService;
import com.dentalmanagement.service.UserService;
import com.dentalmanagement.utility.Constants.AppointmentStatus;
import com.dentalmanagement.utility.Constants.BloodGroup;
import com.dentalmanagement.utility.Constants.DoctorSpecialist;
import com.dentalmanagement.utility.Constants.ResponseCode;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/appointment/")
@CrossOrigin(origins = "http://localhost:3000")
public class AppointmentController {

	Logger LOG = LoggerFactory.getLogger(AppointmentController.class);

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private UserService userService;
    @Autowired
    private SlotBookingService slotBookingService;
    @Autowired
    private TimeScheduleService timeScheduleService;

	@PostMapping("patient/add")
	@ApiOperation(value = "Api to add patient appointment")
	public ResponseEntity<?> addAppointment(@RequestBody Appointment appointment) {
		LOG.info("Received request to add patient appointment");

		CommanApiResponse response = new CommanApiResponse();

		if (appointment == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to add patient appointment");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		if (appointment.getPatientId() == 0) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to add patient appointment");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		LocalDate today = LocalDate.now();
		LocalDate appointmentDate = LocalDate.parse(appointment.getAppointmentDate());
		if (appointmentDate.isEqual(today)) {
			LocalTime now = LocalTime.now();
			List<TimeSchedule> todaySchedules = timeScheduleService.getScheduleTimesByDate(today.toString());

			boolean isWithinTimeSlot = todaySchedules.stream().anyMatch(schedule ->
					LocalTime.parse(schedule.getEndTime()).isAfter(now)
			);

			if (!isWithinTimeSlot) {
				response.setResponseCode(ResponseCode.FAILED.value());
				response.setResponseMessage("Failed to add patient appointment - Current time is beyond today's operational hours");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		}

		//LocalDate appointmentDate = LocalDate.parse(appointment.getAppointmentDate());
		List<TimeSchedule> appointmentDaySchedules = timeScheduleService.getScheduleTimesByDate(appointmentDate.toString());

		// Kiểm tra nếu lịch khám của ngày đặt hẹn là null hoặc rỗng
		if (appointmentDaySchedules == null || appointmentDaySchedules.isEmpty()) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("No schedule available for the selected date");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		appointment.setDate(LocalDate.now().toString());
		appointment.setStatus(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value()) ;

		Appointment addedAppointment = appointmentService.addAppointment(appointment);

		if (addedAppointment != null) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("Appointment Added");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

		else {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to add Appointment");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("all")
	public ResponseEntity<?> getAllAppointments() {
		LOG.info("Received request for getting ALL Appointments !!!");

		List<Appointment> appointments = this.appointmentService.getAllAppointment();

		List<AppointmentResponseDto> response = new ArrayList();

		for (Appointment appointment : appointments) {

			AppointmentResponseDto a = new AppointmentResponseDto();

			User patient = this.userService.getUserById(appointment.getPatientId());

			a.setPatientContact(patient.getContact());
			a.setPatientId(patient.getId());
			a.setPatientName(patient.getFirstName() + " " + patient.getLastName());

			if (appointment.getSlotBooking() != null) {
				User doctor = this.userService.getUserById(appointment.getSlotBooking().getDoctor().getId());
				a.setDoctorContact(doctor.getContact());
				a.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
				a.setDoctorId(doctor.getId());
				a.setSpecialist(doctor.getSpecialist().getName());
				a.setStartTime(appointment.getSlotBooking().getTimeSchedule().getStartTime());
				a.setEndTime(appointment.getSlotBooking().getTimeSchedule().getEndTime());
				a.setPrescription(appointment.getPrescription());
				a.setResult(appointment.getResult());


//				if (appointment.getStatus().equals(AppointmentStatus.TREATMENT_DONE.value())) {
//					a.setPrice(String.valueOf(appointment.getPrice()));
//				}
//
//				else {
//					a.setPrice(AppointmentStatus.TREATMENT_PENDING.value());
//				}
			}

			else {
				a.setDoctorContact(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setDoctorName(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setDoctorId(0);
				a.setSpecialist(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				//a.setPrice(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setResult(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setPrescription(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setStartTime(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setEndTime(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());

			}

			a.setStatus(appointment.getStatus());
			a.setProblem(appointment.getProblem());
			a.setDate(appointment.getDate());
			a.setAppointmentDate(appointment.getAppointmentDate());
			a.setId(appointment.getId());

			response.add(a);
		}

		LOG.info("response sent!!!");
		return ResponseEntity.ok(response);
	}

	@GetMapping("id")
	public ResponseEntity<?> getAllAppointments(@RequestParam("appointmentId") int appointmentId) {
		LOG.info("recieved request for getting  Appointment by id !!!");

		Appointment appointment = this.appointmentService.getAppointmentById(appointmentId);

		if (appointment == null) {
			throw new AppointmentNotFoundException();
		}

		AppointmentResponseDto a = new AppointmentResponseDto();

		User patient = this.userService.getUserById(appointment.getPatientId());

		a.setPatientContact(patient.getContact());
		a.setPatientId(patient.getId());
		a.setPatientName(patient.getFirstName() + " " + patient.getLastName());

		if (appointment.getSlotBooking() != null) {
			User doctor = this.userService.getUserById(appointment.getSlotBooking().getDoctor().getId());
			a.setDoctorContact(doctor.getContact());
			a.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
			a.setDoctorId(doctor.getId());
			a.setSpecialist(doctor.getSpecialist().getName());
			a.setStartTime(appointment.getSlotBooking().getTimeSchedule().getStartTime());
			a.setEndTime(appointment.getSlotBooking().getTimeSchedule().getEndTime());

			if (appointment.getStatus().equals(AppointmentStatus.TREATMENT_DONE.value())) {
				a.setResult(appointment.getResult());
				a.setPrescription(appointment.getPrescription());

			}

			else {
				a.setResult(AppointmentStatus.TREATMENT_PENDING.value());
				a.setPrescription(AppointmentStatus.TREATMENT_PENDING.value());
			}

		}

		else {
			a.setDoctorContact(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
			a.setDoctorName(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
			a.setDoctorId(0);
			a.setSpecialist(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
			a.setResult(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
			a.setPrescription(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
			a.setStartTime(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
			a.setEndTime(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());

		}

		a.setStatus(appointment.getStatus());
		a.setProblem(appointment.getProblem());
		a.setDate(appointment.getDate());
		a.setAppointmentDate(appointment.getAppointmentDate());
		a.setBloodGroup(patient.getBloodGroup());
		a.setId(appointment.getId());

		LOG.info("response sent!!!");
		return ResponseEntity.ok(a);
	}

	@GetMapping("patient/id")
	public ResponseEntity<?> getAllAppointmentsByPatientId(@RequestParam("patientId") int patientId) {
		LOG.info("recieved request for getting ALL Appointments by patient Id !!!");

		List<Appointment> appointments = this.appointmentService.getAppointmentByPatientId(patientId);

		List<AppointmentResponseDto> response = new ArrayList();

		for (Appointment appointment : appointments) {

			AppointmentResponseDto a = new AppointmentResponseDto();

			User patient = this.userService.getUserById(appointment.getPatientId());

			a.setPatientContact(patient.getContact());
			a.setPatientId(patient.getId());
			a.setPatientName(patient.getFirstName() + " " + patient.getLastName());

			if (appointment.getSlotBooking() != null) {
				User doctor = this.userService.getUserById(appointment.getSlotBooking().getDoctor().getId());
				a.setDoctorContact(doctor.getContact());
				a.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
				a.setDoctorId(doctor.getId());
				a.setSpecialist(doctor.getSpecialist().getName());
				a.setStartTime(appointment.getSlotBooking().getTimeSchedule().getStartTime());
				a.setEndTime(appointment.getSlotBooking().getTimeSchedule().getEndTime());

				if (appointment.getStatus().equals(AppointmentStatus.TREATMENT_DONE.value())) {
					a.setResult(appointment.getResult());
					a.setPrescription(appointment.getPrescription());

				}

				else {
					a.setResult(AppointmentStatus.TREATMENT_PENDING.value());
					a.setPrescription(AppointmentStatus.TREATMENT_PENDING.value());
				}

			}

			else {
				a.setDoctorContact(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setDoctorName(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setDoctorId(0);
				a.setSpecialist(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				//a.setPrice(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setResult(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setPrescription(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setStartTime(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setEndTime(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());

			}

			a.setStatus(appointment.getStatus());
			a.setProblem(appointment.getProblem());
			a.setDate(appointment.getDate());
			a.setAppointmentDate(appointment.getAppointmentDate());
			a.setBloodGroup(patient.getBloodGroup());
			a.setId(appointment.getId());

			response.add(a);

		}

		LOG.info("response sent!!!");
		return ResponseEntity.ok(response);
	}

	@GetMapping("doctor/id")
	public ResponseEntity<?> getAllAppointmentsByDoctorId(@RequestParam("doctorId") int doctorId) {
		LOG.info("recieved request for getting ALL Appointments by doctor Id !!!");

		List<Appointment> appointments = this.appointmentService.getAppointmentByDoctorId(doctorId);

		List<AppointmentResponseDto> response = new ArrayList();

		for (Appointment appointment : appointments) {

			AppointmentResponseDto a = new AppointmentResponseDto();

			User patient = this.userService.getUserById(appointment.getPatientId());
			a.setPatientContact(patient.getContact());

			a.setPatientId(patient.getId());
			a.setPatientName(patient.getFirstName() + " " + patient.getLastName());

			if (appointment.getSlotBooking() != null) {
				User doctor = this.userService.getUserById(appointment.getSlotBooking().getDoctor().getId());
				a.setDoctorContact(doctor.getContact());
				a.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
				a.setDoctorId(doctor.getId());
				a.setSpecialist(doctor.getSpecialist().getName());
				a.setStartTime(appointment.getSlotBooking().getTimeSchedule().getStartTime());
				a.setEndTime(appointment.getSlotBooking().getTimeSchedule().getEndTime());

				if (appointment.getStatus().equals(AppointmentStatus.TREATMENT_DONE.value())) {
					a.setResult(appointment.getResult());
					a.setPrescription(appointment.getPrescription());

				}

				else {
					a.setResult(AppointmentStatus.TREATMENT_PENDING.value());
					a.setPrescription(AppointmentStatus.TREATMENT_PENDING.value());
				}

			}

			else {
				a.setDoctorContact(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setDoctorName(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setDoctorId(0);
				a.setSpecialist(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setResult(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setPrescription(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setStartTime(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
				a.setEndTime(AppointmentStatus.NOT_ASSIGNED_TO_DOCTOR.value());
			}

			a.setStatus(appointment.getStatus());
			a.setProblem(appointment.getProblem());
			a.setDate(appointment.getDate());
			a.setAppointmentDate(appointment.getAppointmentDate());
			a.setBloodGroup(patient.getBloodGroup());
			a.setId(appointment.getId());

			response.add(a);

		}

		LOG.info("response sent!!!");
		return ResponseEntity.ok(response);
	}

	@PostMapping("admin/assign/doctor")
	@ApiOperation(value = "Api to assign appointment to doctor")
	public ResponseEntity<?> updateAppointmentStatus(UpdateAppointmentRequestByAdmin request) {
		LOG.info("Recieved request to assign appointment to doctor");


		CommanApiResponse response = new CommanApiResponse();

		if (request == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to assign appointment");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		Appointment appointment = appointmentService.getAppointmentById(request.getAppointmentId());


		if (request.getSlotId() == 0) {
			// Nếu không có slot, hủy cuộc hẹn bằng cách thay đổi trạng thái thành "Cancelled"
			appointment.setStatus(AppointmentStatus.CANCEL.value());
			appointmentService.addAppointment(appointment); // Lưu thay đổi trạng thái

			// Gửi thông báo về việc hủy cuộc hẹn thành công
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("Appointment cancelled because of no available slot");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}


		SlotBooking slotBooking = slotBookingService.getSlotBookingById(request.getSlotId());

		if (slotBooking == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Slot not found");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getAppointmentId() == 0) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Appointment not found");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		if (appointment == null) {
			throw new AppointmentNotFoundException();
		}

		if (appointment.getStatus().equals(AppointmentStatus.CANCEL.value())) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Appointment is cancel by patient");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		appointment.setSlotBooking(slotBookingService.getSlotBookingById(request.getSlotId()));
		appointment.setSpecialist(request.getSpecialist());

		appointment.setStatus(AppointmentStatus.ASSIGNED_TO_DOCTOR.value());

		Appointment updatedAppointment = this.appointmentService.addAppointment(appointment);


		if (updatedAppointment != null) {

			slotBooking.setSlot(0);
			slotBookingService.saveSlotBooking(slotBooking);
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("Successfully Assigned Appointment to doctor");
			return new ResponseEntity(response, HttpStatus.OK);
		}

		else {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to assign");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("doctor/update")
	@ApiOperation(value = "Api to assign appointment to doctor")
	public ResponseEntity<?> assignAppointmentToDoctor(UpdateAppointmentRequestByDoctor request) {
		LOG.info("Received request to update appointment");

		CommanApiResponse response = new CommanApiResponse();

		if (request == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to assign appointment");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getAppointmentId() == 0) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Appointment not found");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		Appointment appointment = appointmentService.getAppointmentById(request.getAppointmentId());

		if (appointment == null) {
			throw new AppointmentNotFoundException();
		}

		appointment.setStatus(request.getStatus());

		if (request.getStatus().equals(AppointmentStatus.TREATMENT_DONE.value())) {
			appointment.setPrescription(request.getPrescription());
			appointment.setResult(request.getResult());
		}

		Appointment updatedAppointment = this.appointmentService.addAppointment(appointment);

		if (updatedAppointment != null) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("Updated Treatment Status");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

		else {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to update");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("patient/update")
	@ApiOperation(value = "Api to update appointment patient")
	public ResponseEntity<?> udpateAppointmentStatus(@RequestBody UpdateAppointmentRequestByPatient request) {
		LOG.info("Recieved request to update appointment");

		CommanApiResponse response = new CommanApiResponse();

		if (request == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to assign appointment");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getAppointmentId() == 0) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Appointment not found");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		Appointment appointment = appointmentService.getAppointmentById(request.getAppointmentId());

		if (appointment == null) {
			throw new AppointmentNotFoundException();
		}

		appointment.setStatus(request.getStatus());
		Appointment updatedAppointment = this.appointmentService.addAppointment(appointment);

		if (updatedAppointment != null) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("Updated Treatment Status");
			return new ResponseEntity(response, HttpStatus.OK);
		}

		else {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to update");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("cancelByAdmin/{appointmentId}")
	@ApiOperation(value = "Api to cancel an appointment")
	public ResponseEntity<?> cancelAppointment(@PathVariable int appointmentId) {
		LOG.info("Received request to cancel appointment with ID: {}", appointmentId);

		CommanApiResponse response = new CommanApiResponse();

		if (appointmentId == 0) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Invalid appointment ID");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		Appointment appointment = appointmentService.getAppointmentById(appointmentId);

		if (appointment == null) {
			throw new AppointmentNotFoundException();
		}

		appointment.setStatus(AppointmentStatus.CANCEL.value());
		Appointment updatedAppointment = appointmentService.addAppointment(appointment);

		if (updatedAppointment != null) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("Appointment cancelled successfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to cancel appointment");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}

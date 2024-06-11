package com.dentalmanagement.service;

import com.dentalmanagement.dao.SlotBookingDao;
import com.dentalmanagement.dao.SpecialistDao;
import com.dentalmanagement.entity.SlotBooking;
import com.dentalmanagement.entity.Specialist;
import com.dentalmanagement.entity.TimeSchedule;
import com.dentalmanagement.entity.User;
import com.dentalmanagement.utility.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SlotBookingService {

    @Autowired
    private SlotBookingDao slotBookingDao;

    @Autowired
    private UserService userService;
    @Autowired
    private SpecialistService specialistService;
    @Autowired
    private SpecialistDao specialistDao;

    public SlotBooking getSlotBookingById(int id) {
        return slotBookingDao.findById(id);
    }

    public List<SlotBooking> getSlotsByDoctorId(int doctorId) {
        return slotBookingDao.findByDoctorId(doctorId);
    }

    public SlotBooking saveSlotBooking(SlotBooking slotBooking) {
        return slotBookingDao.save(slotBooking);
    }

    public List<SlotBooking> addSlotBooking (List<TimeSchedule> timeScheduleList){

        List<User> doctors = this.userService.getAllUserByRole(Constants.UserRole.DOCTOR.value());
        List<SlotBooking> slotBookingList = new ArrayList<>();

        for (TimeSchedule timeSchedule : timeScheduleList) {
            for (User doctor : doctors) {
                SlotBooking slotBooking = new SlotBooking();
                slotBooking.setTimeSchedule(timeSchedule);
                slotBooking.setDoctor(doctor);
                slotBooking.setSlot(1); // Thiết lập slot mặc định
                slotBookingList.add(slotBooking);
            }
        }

        slotBookingDao.saveAll(slotBookingList);
        return slotBookingList;
    }

    public List<SlotBooking> getSlotBookingByDoctorAndDate(int doctorId, String date){
        return slotBookingDao.findByDoctorIdAndDate(doctorId,date);
    }

    public List<SlotBooking> getAvailableSlotsForToday(List<SlotBooking> slotBookings) {
        LocalTime oneHourLater = LocalTime.now().plusHours(1);

        // Lọc ra các slot chưa được đặt và có thời gian bắt đầu sau thời gian hiện tại cộng thêm 1 giờ
        return slotBookings.stream()
                .filter(slot -> slot.getSlot() == 1 && LocalTime.parse(slot.getTimeSchedule().getStartTime()).isAfter(oneHourLater))
                .sorted(Comparator.comparing(slot -> slot.getTimeSchedule().getStartTime()))
                .collect(Collectors.toList());
    }
    public SlotBooking getSlotBookingBySpecialist(String specialistName, String date) {
        List<SlotBooking> slotBookings = slotBookingDao.findBySpecialistAndDate(specialistName, date);

        // Kiểm tra nếu là ngày hiện tại thì gọi hàm lọc slot cho ngày hiện tại
        if (date.equals(LocalDate.now().format(DateTimeFormatter.ISO_DATE))) {
            slotBookings = getAvailableSlotsForToday(slotBookings);
        } else {
            // Lọc ra các slot chưa được đặt (slot = 1)
            slotBookings = slotBookings.stream()
                    .filter(slot -> slot.getSlot() == 1)
                    .collect(Collectors.toList());
        }

        if (slotBookings.isEmpty()) {
            return null;
        }

        // Sắp xếp danh sách các SlotBooking theo thời gian bắt đầu
        slotBookings.sort(Comparator.comparing(slot -> slot.getTimeSchedule().getStartTime()));

        // Lấy danh sách các slot sớm nhất
        String earliestTime = slotBookings.get(0).getTimeSchedule().getStartTime();
        List<SlotBooking> earliestSlotsFiltered = new ArrayList<>();
        for (SlotBooking slot : slotBookings) {
            if (slot.getTimeSchedule().getStartTime().equals(earliestTime)) {
                earliestSlotsFiltered.add(slot);
            }
        }

        // Nếu chỉ có một slot sớm nhất, trả về slot đó
        if (earliestSlotsFiltered.size() == 1) {
            return earliestSlotsFiltered.get(0);
        }

        // Nếu có nhiều slot sớm nhất, chọn ngẫu nhiên một slot
        Random random = new Random();
        return earliestSlotsFiltered.get(random.nextInt(earliestSlotsFiltered.size()));
    }



}


//public SlotBooking getSlotBookingBySpecialist(String specialistName, String date){
//
//    List<SlotBooking> slotBookings = slotBookingDao.findBySpecialistAndDate(specialistName,date);
//
//    // Lọc ra các slot chưa được đặt (slot = 1)
//    List<SlotBooking> availableSlots = slotBookings.stream()
//            .filter(slot -> slot.getSlot() == 1)
//            .collect(Collectors.toList());
//
//    if (availableSlots.isEmpty()) {
//        return null;
//    }
//
//    // Sắp xếp danh sách các SlotBooking theo thời gian bắt đầu
//    availableSlots.sort(Comparator.comparing(slot -> slot.getTimeSchedule().getStartTime()));
//
//    // Lấy danh sách các slot sớm nhất
//    String earliestTime = availableSlots.get(0).getTimeSchedule().getStartTime();
//    List<SlotBooking> earliestSlotsFiltered = new ArrayList<>();
//    for (SlotBooking slot : slotBookings) {
//        if (slot.getTimeSchedule().getStartTime().equals(earliestTime)) {
//            earliestSlotsFiltered.add(slot);
//        }
//    }
//
//    // Nếu chỉ có một slot sớm nhất, trả về slot đó
//    if (earliestSlotsFiltered.size() == 1) {
//        return earliestSlotsFiltered.get(0);
//    }
//
//    // Nếu có nhiều slot sớm nhất, chọn ngẫu nhiên một slot
//    Random random = new Random();
//    return earliestSlotsFiltered.get(random.nextInt(earliestSlotsFiltered.size()));
//
//
//}

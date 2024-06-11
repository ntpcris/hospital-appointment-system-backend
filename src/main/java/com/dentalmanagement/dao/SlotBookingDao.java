package com.dentalmanagement.dao;

import com.dentalmanagement.entity.SlotBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlotBookingDao extends JpaRepository<SlotBooking, Integer> {

    @Query("SELECT sb\n" +
            "            FROM SlotBooking  sb\n" +
            "            JOIN sb.timeSchedule ts\n" +
            "            WHERE sb.doctor.id = :doctorId AND ts.date = :date")
    List<SlotBooking> findByDoctorIdAndDate(@Param("doctorId") int doctorId, @Param("date") String date);

    @Query("SELECT sb FROM SlotBooking sb " +
            "JOIN sb.timeSchedule ts " +
            "JOIN sb.doctor d " +
            "JOIN d.specialist s " +
            "WHERE s.name = :specialistName AND ts.date = :date")
    List<SlotBooking> findBySpecialistAndDate(@Param("specialistName") String specialistName, @Param("date") String date);

    SlotBooking findById(int id);

    List<SlotBooking> findByDoctorId(int doctorId);
}

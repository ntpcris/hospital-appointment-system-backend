package com.dentalmanagement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String date;

    private String startTime;

    private String endTime;

//    @ManyToMany
//    @JoinTable(name = "doctor_timeShedule_slot", // Tên bảng kết nối
//            joinColumns = @JoinColumn(name = "timeSchedule_id"),
//            inverseJoinColumns = @JoinColumn(name = "doctor_id"))
//    private Set<User> doctors = new HashSet<>();
    @OneToMany(mappedBy = "timeSchedule", cascade = CascadeType.ALL)
    @JsonIgnore //cũ đổi thử cái mowis
    //@JsonManagedReference
    private Set<SlotBooking> slotBookings = new HashSet<>();


}

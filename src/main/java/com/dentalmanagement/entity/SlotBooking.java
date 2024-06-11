package com.dentalmanagement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "timeSchedule_id",referencedColumnName = "id")
    //@JsonBackReference //mới thêm
    private TimeSchedule timeSchedule;

    @ManyToOne
    @JoinColumn(name = "doctor_id",referencedColumnName = "id")
    //@JsonBackReference //mới thêm
    private User doctor;

    private int slot;

}

package com.dentalmanagement.dao;

import com.dentalmanagement.entity.TimeSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeScheduleDao extends JpaRepository<TimeSchedule, Integer> {
    List<TimeSchedule> findByDate(String date);
}

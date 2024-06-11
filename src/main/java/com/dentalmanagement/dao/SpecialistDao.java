package com.dentalmanagement.dao;

import com.dentalmanagement.entity.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialistDao extends JpaRepository<Specialist, Integer> {
    Specialist findByName(String name);
}

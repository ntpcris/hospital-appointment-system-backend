package com.dentalmanagement.service;

import com.dentalmanagement.dao.SpecialistDao;
import com.dentalmanagement.entity.Specialist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialistService {

    @Autowired
    private SpecialistDao specialistDao;

    public Specialist addSpecialist(Specialist specialist) {
        return specialistDao.save(specialist);
    }

    public List<Specialist> getAllSpecialist() {
        return specialistDao.findAll();
    }

    public Specialist findByName (String name) {
        return specialistDao.findByName(name);
    }
}

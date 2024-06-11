package com.dentalmanagement.controller;

import com.dentalmanagement.dto.CommanApiResponse;
import com.dentalmanagement.dto.SpecialistRequestDto;
import com.dentalmanagement.entity.Specialist;
import com.dentalmanagement.service.SpecialistService;
import com.dentalmanagement.utility.Constants;
import com.dentalmanagement.utility.StorageService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("api/specialist/")
@CrossOrigin(origins = "http://localhost:3000")
public class SpecialistController {

    Logger LOG = LoggerFactory.getLogger(SpecialistController.class);

    @Autowired
    private SpecialistService specialistService;

    @Autowired
    private StorageService storageService;

    @PostMapping("addSpecialist")
    @ApiOperation(value = "Api add to Specialist")
    public ResponseEntity<?> addSpecialist(SpecialistRequestDto specialistRequestDto) {

        LOG.info("Received request for add Specialist");


        Specialist specialist = SpecialistRequestDto.toEntity(specialistRequestDto);

        CommanApiResponse response = new CommanApiResponse();

        if(specialistRequestDto == null ) {
            response.setResponseCode(Constants.ResponseCode.FAILED.value());
            response.setResponseMessage("Failed to add Specialist");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String image = storageService.store(specialistRequestDto.getImage());
        specialist.setImage(image);

        Specialist addSpecialist = specialistService.addSpecialist(specialist);

        if(addSpecialist != null) {
            response.setResponseCode(Constants.ResponseCode.SUCCESS.value());
            response.setResponseMessage("Successfully added Specialist");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        else {
            response.setResponseCode(Constants.ResponseCode.FAILED.value());
            response.setResponseMessage("Failed to add Specialist");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("getAllSpecialist")
    @ApiOperation(value = "Api get all specialist")
    public ResponseEntity<?> getAllSpecialist() {

        LOG.info("Received request for all Specialist");

        List<Specialist> specialists = specialistService.getAllSpecialist();

        LOG.info("response sent!!!");
        return new ResponseEntity<>(specialists, HttpStatus.OK);
    }

    @GetMapping(value = "/{image}", produces = "image/*")
    @ApiOperation(value = "Api to fetch specialist image")
    public void fetchSpecialistImage(@PathVariable("image") String image, HttpServletResponse response) {
        LOG.info("request came for fetching specialist pic");
        LOG.info("Loading file: " + image);

        Resource resource = storageService.load(image);
        if(resource != null){
            try (InputStream in = resource.getInputStream()) {
                ServletOutputStream out = response.getOutputStream();
                FileCopyUtils.copy(in, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LOG.info("response sent!");
    }
}

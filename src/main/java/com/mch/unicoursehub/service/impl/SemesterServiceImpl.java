package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.ConflictException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.model.dto.CreateSemesterRequest;
import com.mch.unicoursehub.model.dto.SemesterResponse;
import com.mch.unicoursehub.model.dto.UpdateSemesterRequest;
import com.mch.unicoursehub.model.entity.Semester;
import com.mch.unicoursehub.repository.SemesterRepository;
import com.mch.unicoursehub.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SemesterServiceImpl implements SemesterService {

    private final SemesterRepository semesterRepository;

    @Override
    public SemesterResponse createSemester(CreateSemesterRequest req) {

        if (semesterRepository.existsByName(req.name().trim())) {
            throw new ConflictException("Semester with name '" + req.name() + "' already exists");
        }

        if (req.startDate().isAfter(req.endDate())) {
            throw new BadRequestException("Start date must be before end date");
        }

        if (req.minUnits() > req.maxUnits()) {
            throw new BadRequestException("minUnits cannot be greater than maxUnits");
        }


        Semester semester = Semester.builder()
                .name(req.name().trim())
                .startDate(req.startDate())
                .endDate(req.endDate())
                .minUnits(req.minUnits())
                .maxUnits(req.maxUnits())
                .build();

        Semester saved = semesterRepository.save(semester);

        return toResponse(saved);
    }

    @Override
    public SemesterResponse updateSemester(String name, UpdateSemesterRequest req) {

        Semester semester = semesterRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Semester '" + name + "' not found"));

        if (req.name() != null && !req.name().isBlank()) {
            if (!req.name().equals(semester.getName())
                    && semesterRepository.existsByName(req.name().trim())) {
                throw new ConflictException("Semester with name '" + req.name() + "' already exists");
            }
            semester.setName(req.name().trim());
        }

        if (req.startDate() != null) {
            semester.setStartDate(req.startDate());
        }

        if (req.endDate() != null) {
            semester.setEndDate(req.endDate());
        }

        if (req.minUnits() != null) {
            semester.setMinUnits(req.minUnits());
        }

        if (req.maxUnits() != null) {
            semester.setMaxUnits(req.maxUnits());
        }

        if (semester.getStartDate().isAfter(semester.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }

        if (semester.getMinUnits() > semester.getMaxUnits()) {
            throw new BadRequestException("minUnits cannot be greater than maxUnits");
        }

        return toResponse(semester);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SemesterResponse> getAllSemesters() {

        return semesterRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private SemesterResponse toResponse(Semester semester) {
        return SemesterResponse.builder()
                .name(semester.getName())
                .startDate(semester.getStartDate())
                .endDate(semester.getEndDate())
                .minUnits(semester.getMinUnits())
                .maxUnits(semester.getMaxUnits())
                .build();
    }
}

package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.dto.CreateSemesterRequest;
import com.mch.unicoursehub.model.dto.SemesterResponse;
import com.mch.unicoursehub.model.dto.UpdateSemesterRequest;

import java.util.List;

public interface SemesterService {

    SemesterResponse createSemester(CreateSemesterRequest request);

    SemesterResponse updateSemester(String name, UpdateSemesterRequest request);

    List<SemesterResponse> getAllSemesters();
}

package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.dto.CreateSemesterRequest;
import com.mch.unicoursehub.model.dto.SemesterResponse;
import com.mch.unicoursehub.model.dto.UpdateSemesterRequest;

import java.util.List;

/**
 * Service interface for managing semesters.
 * <p>
 * Provides methods for creating, updating, and retrieving semesters.
 * These operations are typically restricted to admin users.
 * </p>
 */
public interface SemesterService {

    /**
     * Create a new semester.
     *
     * @param request DTO containing semester details.
     * @return The created semester as a response DTO.
     */
    SemesterResponse createSemester(CreateSemesterRequest request);

    /**
     * Update an existing semester identified by its name.
     *
     * @param name    Name of the semester to update.
     * @param request DTO containing updated semester details.
     * @return The updated semester as a response DTO.
     */
    SemesterResponse updateSemester(String name, UpdateSemesterRequest request);

    /**
     * Retrieve all semesters.
     *
     * @return List of semesters as response DTOs.
     */
    List<SemesterResponse> getAllSemesters();
}

package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.dto.CourseOfferingResponse;
import com.mch.unicoursehub.model.dto.CreateCourseOfferingRequest;
import com.mch.unicoursehub.model.dto.UpdateCourseOfferingRequest;

import java.util.List;

public interface CourseOfferingService {
     CourseOfferingResponse createCourseOffering(CreateCourseOfferingRequest req);

     List<CourseOfferingResponse> getCourseOfferings(String semesterName, String professorName, String courseCode, String courseName);

    CourseOfferingResponse updateCourseOffering(
            String semesterName,
            String courseCode,
            int groupNumber,
            UpdateCourseOfferingRequest req
    );

    void deleteCourseOffering(String semesterName, String courseCode, int groupNumber);
}

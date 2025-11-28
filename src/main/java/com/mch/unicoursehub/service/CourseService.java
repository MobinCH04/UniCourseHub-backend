package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.dto.CourseResponse;
import com.mch.unicoursehub.model.dto.CreateCourseRequest;

public interface CourseService {
    CourseResponse createCourse(CreateCourseRequest request);
}

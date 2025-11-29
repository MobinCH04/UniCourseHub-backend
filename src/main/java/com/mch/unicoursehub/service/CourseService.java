package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.dto.AllCoursesResponse;
import com.mch.unicoursehub.model.dto.CourseResponse;
import com.mch.unicoursehub.model.dto.CreateCourseRequest;
import com.mch.unicoursehub.model.dto.UpdateCourseRequest;
import com.mch.unicoursehub.utils.pagination.Pagination;

public interface CourseService {
    CourseResponse createCourse(CreateCourseRequest request);

    Pagination<AllCoursesResponse> getAllCourses(int page, int size, String code, String name, Integer unit);

    CourseResponse updateCourse(String code, UpdateCourseRequest req);
}

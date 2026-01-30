package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.dto.DropCourseRequest;
import com.mch.unicoursehub.model.dto.EnrollCourseRequest;
import com.mch.unicoursehub.model.dto.StudentEnrollmentResponse;
import com.mch.unicoursehub.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface EnrollmentService {

    void enrollStudent(User student, EnrollCourseRequest req);

    List<StudentEnrollmentResponse> getStudentEnrollments(User student, String semesterName);

    void dropCourse(User student, DropCourseRequest req);
}

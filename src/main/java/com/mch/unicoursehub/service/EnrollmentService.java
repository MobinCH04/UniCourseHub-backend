package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.dto.DropCourseRequest;
import com.mch.unicoursehub.model.dto.EnrollCourseRequest;
import com.mch.unicoursehub.model.dto.StudentEnrollmentResponse;
import com.mch.unicoursehub.model.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing student enrollments in course offerings.
 *
 * <p>This service handles operations such as enrolling in a course, dropping a course,
 * and retrieving a student's current enrollments for a specific semester.</p>
 */
public interface EnrollmentService {

    /**
     * Enrolls a student in a specific course offering for a given semester.
     *
     * <p>Validations typically include:
     * <ul>
     *     <li>Checking course capacity</li>
     *     <li>Ensuring the student has not previously dropped the course</li>
     *     <li>Checking for duplicate enrollment in the same semester</li>
     *     <li>Verifying prerequisites are passed</li>
     *     <li>Checking for schedule and exam conflicts</li>
     *     <li>Ensuring total units do not exceed the semester's max limit</li>
     * </ul>
     * </p>
     *
     * @param student the student performing the enrollment
     * @param semesterName the name of the semester (e.g., "1404-1")
     * @param req the enrollment request containing course code and group number
     * @throws com.mch.unicoursehub.exceptions.BadRequestException if enrollment violates any rule
     * @throws com.mch.unicoursehub.exceptions.NotFoundException if the course offering does not exist
     */
    void enrollStudent(User student,String semesterName, EnrollCourseRequest req);

    /**
     * Retrieves all current enrollments of a student for a specific semester.
     *
     * <p>Only enrollments with status other than DROPPED are returned.</p>
     *
     * @param student the student whose enrollments are to be retrieved
     * @param semesterName the name of the semester (e.g., "1404-1")
     * @return a list of {@link StudentEnrollmentResponse} representing the student's enrollments
     * @throws com.mch.unicoursehub.exceptions.NotFoundException if the semester does not exist
     */
    List<StudentEnrollmentResponse> getStudentEnrollments(User student, String semesterName);

    /**
     * Drops a course in which the student is currently enrolled.
     *
     * <p>Only enrollments with status SELECTED can be dropped. The enrollment status
     * will be updated to DROPPED.</p>
     *
     * @param student the student performing the drop
     * @param req the drop request containing course code, group number, and semester name
     * @throws com.mch.unicoursehub.exceptions.BadRequestException if the enrollment is not in SELECTED status
     * @throws com.mch.unicoursehub.exceptions.NotFoundException if the enrollment does not exist
     */
    void dropCourse(User student, DropCourseRequest req);
}

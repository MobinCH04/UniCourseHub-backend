package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.model.dto.CreateCourseOfferingRequest;
import com.mch.unicoursehub.model.dto.CourseOfferingResponse;
import com.mch.unicoursehub.model.dto.UpdateCourseOfferingRequest;
import com.mch.unicoursehub.model.entity.Course;
import com.mch.unicoursehub.model.entity.CourseOffering;
import com.mch.unicoursehub.model.entity.Semester;
import com.mch.unicoursehub.model.entity.TimeSlot;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.model.enums.EnrollmentStatus;
import com.mch.unicoursehub.repository.*;
import com.mch.unicoursehub.service.CourseOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.mch.unicoursehub.ConstErrors.*;

import java.util.List;

/**
 * Service implementation for managing course offerings.
 *
 * <p>This class handles creating course offerings and retrieving them
 * with optional filtering by professor name, course code, or course name.</p>
 *
 * <p>It uses repositories for {@link Course}, {@link User}, {@link Semester},
 * {@link TimeSlot}, and {@link CourseOffering} to persist and query data.</p>
 */
@Service
@RequiredArgsConstructor
public class CourseOfferingServiceImpl implements CourseOfferingService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final SemesterRepository semesterRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * Creates a new course offering with the specified details.
     *
     * @param req the request DTO containing course code, professor, semester,
     *            capacity, exam date, classroom, and time slot IDs
     * @return a {@link CourseOfferingResponse} representing the newly created offering
     * @throws NotFoundException if the course, professor, or semester is not found
     * @throws BadRequestException if one or more time slots are not found
     */
    @Transactional
    public CourseOfferingResponse createCourseOffering(CreateCourseOfferingRequest req) {

        Course course = courseRepository.findByCode(req.courseCode().trim())
                .orElseThrow(() -> new NotFoundException("Course not found"));

        User professor = userRepository.findByUserNumber(req.professorUserNumber().trim())
                .orElseThrow(() -> new NotFoundException("Professor not found"));

        Semester semester = semesterRepository.findByName(req.semesterName().trim())
                .orElseThrow(() -> new NotFoundException("Semester not found"));


        int nextGroupNumber = courseOfferingRepository.countByCourseAndSemester(course, semester) + 1;


        List<TimeSlot> timeSlots = timeSlotRepository.findAllById(req.timeSlotIds());
        if (timeSlots.size() != req.timeSlotIds().size()) {
            throw new BadRequestException("One or more time slots not found");
        }


        CourseOffering offering = CourseOffering.builder()
                .course(course)
                .professor(professor)
                .semester(semester)
                .capacity(req.capacity())
                .examDate(req.examDate())
                .classRoom(req.classroomNumber())
                .section(nextGroupNumber)
                .timeSlots(timeSlots)
                .build();

        courseOfferingRepository.save(offering);


        return CourseOfferingResponse.builder()
                .courseCode(offering.getCourse().getCode())
                .courseName(offering.getCourse().getName())
                .professorName(offering.getProfessor().fullName())
                .capacity(offering.getCapacity())
                .examDate(offering.getExamDate())
                .classroomNumber(Integer.parseInt(offering.getClassRoom()))
                .groupNumber(offering.getSection())
                .timeSlotIds(offering.getTimeSlots().stream().map(TimeSlot::getId).toList())
                .build();
    }

    /**
     * Retrieves all course offerings, optionally filtered by professor name, course code, or course name.
     *
     * @param professorName optional substring of professor's full name to filter
     * @param courseCode optional exact course code to filter
     * @param courseName optional substring of course name to filter
     * @return a list of {@link CourseOfferingResponse} representing the filtered course offerings
     */
    @Transactional(readOnly = true)
    public List<CourseOfferingResponse> getCourseOfferings(String semesterName, String professorName, String courseCode, String courseName) {

        Semester semester = semesterRepository.findByName(semesterName.trim())
                .orElseThrow(() -> new NotFoundException(notFoundSemester));

        List<CourseOffering> allOfferings = courseOfferingRepository.findBySemester(semester);

        List<CourseOffering> filtered = allOfferings.stream()
                .filter(co -> professorName == null ||
                        (co.getProfessor().getFirstName() + " " + co.getProfessor().getLastName())
                                .toLowerCase()
                                .contains(professorName.trim().toLowerCase()))
                .filter(co -> courseCode == null || co.getCourse().getCode().equalsIgnoreCase(courseCode.trim()))
                .filter(co -> courseName == null || co.getCourse().getName().toLowerCase().contains(courseName.trim().toLowerCase()))
                .toList();

        return filtered.stream()
                .map(co -> CourseOfferingResponse.builder()
                        .courseCode(co.getCourse().getCode())
                        .courseName(co.getCourse().getName())
                        .professorName(co.getProfessor().fullName())
                        .capacity(co.getCapacity())
                        .examDate(co.getExamDate())
                        .classroomNumber(Integer.parseInt(co.getClassRoom()))
                        .groupNumber(co.getSection())
                        .timeSlotIds(co.getTimeSlots().stream().map(ts -> ts.getId()).toList())
                        .build())
                .toList();
    }

    @Transactional
    public CourseOfferingResponse updateCourseOffering(
            String semesterName,
            String courseCode,
            int groupNumber,
            UpdateCourseOfferingRequest req
    ) {
        Semester semester = semesterRepository.findByName(semesterName)
                .orElseThrow(() -> new NotFoundException(notFoundSemester));

        CourseOffering offering = courseOfferingRepository.findBySemester(semester)
                .stream()
                .filter(o -> o.getCourse().getCode().equalsIgnoreCase(courseCode))
                .filter(o -> o.getSection() == groupNumber)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(courseOfferingNotFound));

        if (req.professorUserNumber() != null) {
            User professor = userRepository.findByUserNumber(req.professorUserNumber().trim())
                    .orElseThrow(() -> new NotFoundException("Professor not found"));
            offering.setProfessor(professor);
        }

        if (req.capacity() != null) {
            offering.setCapacity(req.capacity());
        }

        if (req.examDate() != null) {
            offering.setExamDate(req.examDate());
        }

        if (req.classroomNumber() != null) {
            offering.setClassRoom(req.classroomNumber());
        }

        if (req.timeSlotIds() != null) {
            List<TimeSlot> timeSlots = timeSlotRepository.findAllById(req.timeSlotIds());
            if (timeSlots.size() != req.timeSlotIds().size()) {
                throw new BadRequestException("One or more time slots not found");
            }
            offering.setTimeSlots(timeSlots);
        }

        courseOfferingRepository.save(offering);

        return CourseOfferingResponse.builder()
                .courseCode(offering.getCourse().getCode())
                .courseName(offering.getCourse().getName())
                .professorName(offering.getProfessor().fullName())
                .capacity(offering.getCapacity())
                .examDate(offering.getExamDate())
                .classroomNumber(Integer.parseInt(offering.getClassRoom()))
                .groupNumber(offering.getSection())
                .timeSlotIds(offering.getTimeSlots().stream().map(TimeSlot::getId).toList())
                .build();
    }

    @Transactional
    public void deleteCourseOffering(String semesterName, String courseCode, int groupNumber) {

        Semester semester = semesterRepository.findByName(semesterName.trim())
                .orElseThrow(() -> new NotFoundException(notFoundSemester));

        CourseOffering offering = courseOfferingRepository.findBySemester(semester)
                .stream()
                .filter(o -> o.getCourse().getCode().equalsIgnoreCase(courseCode.trim()))
                .filter(o -> o.getSection() == groupNumber)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(courseOfferingNotFound));

        boolean hasActiveEnrollment =
                enrollmentRepository.existsByCourseOfferingAndStatusNot(
                        offering,
                        EnrollmentStatus.DROPPED
                );

        if (hasActiveEnrollment) {
            throw new BadRequestException("Cannot delete course offering with active students");
        }

        enrollmentRepository.deleteByCourseOffering(offering);

        courseOfferingRepository.delete(offering);
    }

}

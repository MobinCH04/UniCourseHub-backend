package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.model.dto.CreateCourseOfferingRequest;
import com.mch.unicoursehub.model.dto.CourseOfferingResponse;
import com.mch.unicoursehub.model.entity.Course;
import com.mch.unicoursehub.model.entity.CourseOffering;
import com.mch.unicoursehub.model.entity.Semester;
import com.mch.unicoursehub.model.entity.TimeSlot;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.repository.CourseOfferingRepository;
import com.mch.unicoursehub.repository.CourseRepository;
import com.mch.unicoursehub.repository.SemesterRepository;
import com.mch.unicoursehub.repository.TimeSlotRepository;
import com.mch.unicoursehub.repository.UserRepository;
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
public class CourseOfferingServiceImpl {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final SemesterRepository semesterRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final TimeSlotRepository timeSlotRepository;

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

        // تولید شماره گروه
        int nextGroupNumber = courseOfferingRepository.countByCourseAndSemester(course, semester) + 1;

        // گرفتن تایم‌اسلات‌ها
        List<TimeSlot> timeSlots = timeSlotRepository.findAllById(req.timeSlotIds());
        if (timeSlots.size() != req.timeSlotIds().size()) {
            throw new BadRequestException("One or more time slots not found");
        }

        // ایجاد CourseOffering
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

        // ساخت Response مستقیماً داخل سرویس
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
                .orElseThrow(() -> new NotFoundException(semesterNotFound));

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
}

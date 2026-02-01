package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.model.dto.DropCourseRequest;
import com.mch.unicoursehub.model.dto.EnrollCourseRequest;
import com.mch.unicoursehub.model.dto.StudentEnrollmentResponse;
import com.mch.unicoursehub.model.entity.*;
import com.mch.unicoursehub.model.enums.EnrollmentStatus;
import com.mch.unicoursehub.repository.CourseOfferingRepository;
import com.mch.unicoursehub.repository.EnrollmentRepository;
import com.mch.unicoursehub.repository.PrerequisiteRepository;
import com.mch.unicoursehub.repository.SemesterRepository;
import com.mch.unicoursehub.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static com.mch.unicoursehub.ConstErrors.*;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final PrerequisiteRepository prerequisiteRepository;
    private final SemesterRepository semesterRepository;

    public void enrollStudent(User student, String semesterName, EnrollCourseRequest req) {

        CourseOffering offering = courseOfferingRepository
                .findByCourse_CodeAndSectionAndSemester_Name(
                        req.courseCode().trim(),
                        req.groupNumber(),
                        semesterName.trim()
                )
                .orElseThrow(() -> new NotFoundException(courseOfferingNotFound));

        Semester semester = offering.getSemester();
        Course course = offering.getCourse();

        /* 1️⃣ ظرفیت */
        long enrolledCount = enrollmentRepository.countByCourseOffering(offering);
        if (enrolledCount >= offering.getCapacity()) {
            throw new BadRequestException(fullCapacity);
        }

        /* 2️⃣ عدم اخذ مجدد در صورت Drop شدن */
        boolean droppedBefore = enrollmentRepository
                .existsByStudentAndCourseOffering_SemesterAndCourseOffering_CourseAndStatus(
                        student,
                        semester,
                        course,
                        EnrollmentStatus.DROPPED
                );

        if (droppedBefore) {
            throw new BadRequestException(droppedCourse);
        }

        /* 2️⃣ عدم اخذ تکراری درس در ترم */
        boolean alreadyTaken = enrollmentRepository
                .existsByStudentAndCourseOffering_SemesterAndCourseOffering_Course(
                        student, semester, course
                );

        if (alreadyTaken) {
            throw new BadRequestException(taken);
        }

        /* 3️⃣ بررسی پیش‌نیاز */
        List<Prerequisite> prerequisites = prerequisiteRepository.findByCourse(course);

        List<Enrollment> passedCourses =
                enrollmentRepository.findByStudentAndStatus(student, EnrollmentStatus.PASSED);

        for (Prerequisite p : prerequisites) {
            boolean passed = passedCourses.stream()
                    .anyMatch(e -> e.getCourseOffering()
                            .getCourse()
                            .equals(p.getPrerequisite()));

            if (!passed) {
                throw new BadRequestException(
                        "Prerequisite not passed: " + p.getPrerequisite().getCode()
                );
            }
        }

        /* 4️⃣ تداخل زمانی و امتحان + محاسبه واحد */
        List<Enrollment> currentEnrollments =
                enrollmentRepository.findByStudentAndCourseOffering_SemesterAndStatus(student, semester,EnrollmentStatus.SELECTED);

        int totalUnits = course.getUnit();

        for (Enrollment e : currentEnrollments) {

            CourseOffering co = e.getCourseOffering();

            // امتحان
            if (co.getExamDate().equals(offering.getExamDate())) {
                throw new BadRequestException(examDateConflict);
            }

            // تایم‌اسلات
            boolean timeConflict = co.getTimeSlots().stream()
                    .anyMatch(ts -> offering.getTimeSlots().contains(ts));

            if (timeConflict) {
                throw new BadRequestException(classTimeConflict);
            }

            totalUnits += co.getCourse().getUnit();
        }

        /* 5️⃣ محدودیت واحد */
        if (totalUnits > semester.getMaxUnits()) {
            throw new BadRequestException(maxUnit);
        }

        /* 6️⃣ ثبت Enrollment */
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourseOffering(offering);
        enrollment.setStatus(EnrollmentStatus.SELECTED);

        enrollmentRepository.save(enrollment);
    }

        @Transactional(readOnly = true)
        public List<StudentEnrollmentResponse> getStudentEnrollments(
                User student,
                String semesterName
        ) {

            Semester semester = semesterRepository.findByName(semesterName.trim())
                    .orElseThrow(() -> new NotFoundException(notFoundSemester));

            return enrollmentRepository
                    .findByStudentAndCourseOffering_Semester(student, semester)
                    .stream()
                    .filter(e -> e.getStatus() != EnrollmentStatus.DROPPED)
                    .map(this::toResponse)
                    .toList();
        }

    private StudentEnrollmentResponse toResponse(Enrollment e) {

        CourseOffering co = e.getCourseOffering();

        List<String> timeSlots = co.getTimeSlots()
                .stream()
                .sorted(Comparator
                        .comparing(TimeSlot::getDayOfWeek)
                        .thenComparing(TimeSlot::getStartTime)
                )
                .map(ts ->
                        ts.getDayOfWeek().name() + " " +
                                ts.getStartTime().toString().substring(0, 5) +
                                "-" +
                                ts.getEndTime().toString().substring(0, 5)
                )
                .toList();

        return new StudentEnrollmentResponse(
                co.getCourse().getCode(),
                co.getCourse().getName(),
                co.getCourse().getUnit(),
                co.getProfessor().fullName(),
                co.getSection(),
                timeSlots,
                co.getExamDate()
        );
    }


    public void dropCourse(
            User student,
            DropCourseRequest req
    ) {

        Enrollment enrollment = enrollmentRepository
                .findByStudentAndCourseOffering_Course_CodeAndCourseOffering_SectionAndCourseOffering_Semester_Name(
                        student,
                        req.courseCode().trim(),
                        req.groupNumber(),
                        req.semesterName().trim()
                )
                .orElseThrow(() ->
                        new NotFoundException(notFoundEnrollment)
                );

        if (enrollment.getStatus() != EnrollmentStatus.SELECTED) {
            throw new BadRequestException(
                    nonSelectedStatus
            );
        }

        // روش پیشنهادی: Drop منطقی
        enrollment.setStatus(EnrollmentStatus.DROPPED);

    }
}

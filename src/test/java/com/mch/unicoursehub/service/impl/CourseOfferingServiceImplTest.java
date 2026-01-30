package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.model.dto.CreateCourseOfferingRequest;
import com.mch.unicoursehub.model.dto.CourseOfferingResponse;
import com.mch.unicoursehub.model.entity.*;
import com.mch.unicoursehub.model.enums.DayOfWeek;
import com.mch.unicoursehub.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CourseOfferingServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private CourseOfferingRepository courseOfferingRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @InjectMocks
    private CourseOfferingServiceImpl service;

    private Course course;
    private User professor;
    private Semester semester;
    private List<TimeSlot> timeSlots;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        course = Course.builder()
                .cid(UUID.randomUUID())
                .code("CS101")
                .name("Algorithms")
                .unit(3)
                .build();

        professor = User.builder()
                .uid(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .userNumber("U123")
                .password("pass")
                .nationalCode("1234567890")
                .phoneNumber("09120000000")
                .isAccountLocked(false)
                .build();

        semester = Semester.builder()
                .id(UUID.randomUUID())
                .name("1404-1")
                .minUnits(12)
                .maxUnits(24)
                .startDate(java.time.LocalDate.of(2025,2,1))
                .endDate(java.time.LocalDate.of(2025,6,30))
                .build();

        TimeSlot ts = TimeSlot.builder()
                .id(UUID.randomUUID())
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(java.time.LocalTime.of(8,0))
                .endTime(java.time.LocalTime.of(10,0))
                .build();
        timeSlots = List.of(ts);
    }

    // ---------- createCourseOffering ----------

    @Test
    void createCourseOffering_shouldReturnResponse() {
        CreateCourseOfferingRequest req = new CreateCourseOfferingRequest(
                "CS101", "U123", "1404-1",
                30, LocalDateTime.now(), "101",
                List.of(timeSlots.get(0).getId())
        );

        when(courseRepository.findByCode("CS101")).thenReturn(Optional.of(course));
        when(userRepository.findByUserNumber("U123")).thenReturn(Optional.of(professor));
        when(semesterRepository.findByName("1404-1")).thenReturn(Optional.of(semester));
        when(courseOfferingRepository.countByCourseAndSemester(course, semester)).thenReturn(0);
        when(timeSlotRepository.findAllById(req.timeSlotIds())).thenReturn(timeSlots);
        when(courseOfferingRepository.save(any(CourseOffering.class))).thenAnswer(inv -> inv.getArgument(0));

        CourseOfferingResponse response = service.createCourseOffering(req);

        assertThat(response).isNotNull();
        assertThat(response.courseCode()).isEqualTo("CS101");
        assertThat(response.professorName()).isEqualTo("John Doe");
        assertThat(response.classroomNumber()).isEqualTo(101); // parse String -> int
        assertThat(response.groupNumber()).isEqualTo(1);
        assertThat(response.timeSlotIds()).containsExactly(timeSlots.get(0).getId());

        verify(courseOfferingRepository, times(1)).save(any(CourseOffering.class));
    }

    @Test
    void createCourseOffering_whenCourseNotFound_shouldThrow() {
        CreateCourseOfferingRequest req = new CreateCourseOfferingRequest(
                "CS999", "U123", "1404-1", 30, LocalDateTime.now(), "101", List.of(UUID.randomUUID())
        );

        when(courseRepository.findByCode("CS999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createCourseOffering(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Course not found");
    }

    @Test
    void createCourseOffering_whenTimeSlotMissing_shouldThrow() {
        CreateCourseOfferingRequest req = new CreateCourseOfferingRequest(
                "CS101", "U123", "1404-1", 30, LocalDateTime.now(), "101", List.of(UUID.randomUUID())
        );

        when(courseRepository.findByCode("CS101")).thenReturn(Optional.of(course));
        when(userRepository.findByUserNumber("U123")).thenReturn(Optional.of(professor));
        when(semesterRepository.findByName("1404-1")).thenReturn(Optional.of(semester));
        when(courseOfferingRepository.countByCourseAndSemester(course, semester)).thenReturn(0);
        when(timeSlotRepository.findAllById(req.timeSlotIds())).thenReturn(List.of()); // خالی

        assertThatThrownBy(() -> service.createCourseOffering(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("One or more time slots not found");
    }

    // ---------- getCourseOfferings ----------

    @Test
    void getCourseOfferings_shouldFilterCorrectly() {
        CourseOffering offering = CourseOffering.builder()
                .course(course)
                .professor(professor)
                .semester(semester)
                .capacity(30)
                .examDate(LocalDateTime.now())
                .classRoom("101")
                .section(1)
                .timeSlots(timeSlots)
                .build();

        when(semesterRepository.findByName("1404-1")).thenReturn(Optional.of(semester));
        when(courseOfferingRepository.findBySemester(semester)).thenReturn(List.of(offering));

        List<CourseOfferingResponse> results = service.getCourseOfferings(
                "1404-1", "John", "CS101", "Algorithms"
        );

        assertThat(results).hasSize(1);
        CourseOfferingResponse resp = results.get(0);
        assertThat(resp.courseCode()).isEqualTo("CS101");
        assertThat(resp.professorName()).isEqualTo("John Doe");
        assertThat(resp.classroomNumber()).isEqualTo(101);
    }

    @Test
    void getCourseOfferings_whenSemesterNotFound_shouldThrow() {
        when(semesterRepository.findByName("1404-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCourseOfferings(
                "1404-1", null, null, null
        )).isInstanceOf(NotFoundException.class);
    }
}

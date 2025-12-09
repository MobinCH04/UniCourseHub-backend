package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.ConflictException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.model.dto.CourseResponse;
import com.mch.unicoursehub.model.dto.CreateCourseRequest;
import com.mch.unicoursehub.model.dto.UpdateCourseRequest;
import com.mch.unicoursehub.model.entity.Course;
import com.mch.unicoursehub.model.entity.Prerequisite;
import com.mch.unicoursehub.repository.CourseRepository;
import com.mch.unicoursehub.repository.PrerequisiteRepository;
import com.mch.unicoursehub.utils.pagination.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.Default.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private PrerequisiteRepository prerequisiteRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course courseA;
    private Course courseB;

    @BeforeEach
    void setup() {
        courseA = Course.builder()
                .cid(UUID.randomUUID())
                .code("CS101")
                .name("Algorithms")
                .unit(3)
                .prerequisites(new ArrayList<>())
                .dependentCourses(new ArrayList<>())
                .build();

        courseB = Course.builder()
                .cid(UUID.randomUUID())
                .code("CS50")
                .name("Data Structures")
                .unit(3)
                .prerequisites(new ArrayList<>())
                .dependentCourses(new ArrayList<>())
                .build();
    }

    @Test
    void testCreateCourse_successNoPrereq() {
        CreateCourseRequest req = new CreateCourseRequest("CS102", "Databases", 3, null);

        when(courseRepository.existsByCode("CS102")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));
        when(courseRepository.findAll()).thenReturn(List.of(courseA, courseB));

        CourseResponse response = courseService.createCourse(req);

        assertEquals("CS102", response.code());
        assertEquals("Databases", response.name());
        assertEquals(3, response.unit());
        assertTrue(response.prerequisites().isEmpty());

        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void testCreateCourse_withPrerequisites_success() {
        CreateCourseRequest req = new CreateCourseRequest("CS103", "AI", 3, List.of("CS101", "CS50"));

        when(courseRepository.existsByCode("CS103")).thenReturn(false);
        when(courseRepository.findByCodeIn(List.of("CS101", "CS50"))).thenReturn(List.of(courseA, courseB));
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));
        when(courseRepository.findAll()).thenReturn(List.of(courseA, courseB));

        CourseResponse response = courseService.createCourse(req);

        assertEquals("CS103", response.code());
        assertEquals(2, response.prerequisites().size());

        verify(prerequisiteRepository).saveAll(anyList());
    }

    @Test
    void testCreateCourse_duplicateCode_throwsConflict() {
        CreateCourseRequest req = new CreateCourseRequest("CS101", "Algorithms", 3, null);

        when(courseRepository.existsByCode("CS101")).thenReturn(true);

        assertThrows(ConflictException.class, () -> courseService.createCourse(req));
    }

    @Test
    void testUpdateCourse_success() {
        UpdateCourseRequest req = new UpdateCourseRequest("Advanced Algo", 4, null);

        when(courseRepository.findByCode("CS101")).thenReturn(Optional.of(courseA));
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));
        when(courseRepository.findAll()).thenReturn(List.of(courseA, courseB));

        CourseResponse response = courseService.updateCourse("CS101", req);

        assertEquals("CS101", response.code());
        assertEquals("Advanced Algo", response.name());
        assertEquals(4, response.unit());
    }

    @Test
    void testDeleteCourse_success() {
        String code = "CS101";
        when(courseRepository.findByCode(code)).thenReturn(Optional.of(courseA));
        when(prerequisiteRepository.findByPrerequisiteCid(courseA.getCid())).thenReturn(List.of());

        doNothing().when(courseRepository).delete(courseA);

        courseService.deleteCourse(code);

        verify(courseRepository, times(1)).delete(courseA);
        verify(courseRepository).delete(courseA);
    }



    @Test
    void testDeleteCourse_notFoundException() {

        String code = "CS101";
        when(courseRepository.findByCode(code)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> courseService.deleteCourse(code));
        assertEquals("Course with code '" + code + "' not found", exception.getMessage());
    }

    @Test
    void testGetAllCourses_filterByCode() {
        when(courseRepository.findAll()).thenReturn(List.of(courseA, courseB));

        Pagination<CourseResponse> page = courseService.getAllCourses(1, 10, "CS101", null, null);

        assertEquals(1, page.getData().size());
        assertEquals("CS101", page.getData().get(0).code());
    }

}
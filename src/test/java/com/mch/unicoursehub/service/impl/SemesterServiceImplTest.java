package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.ConflictException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.model.dto.CreateSemesterRequest;
import com.mch.unicoursehub.model.dto.SemesterResponse;
import com.mch.unicoursehub.model.dto.UpdateSemesterRequest;
import com.mch.unicoursehub.model.entity.Semester;
import com.mch.unicoursehub.repository.SemesterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SemesterServiceImplTest {

    private SemesterRepository semesterRepository;
    private SemesterServiceImpl service;

    @BeforeEach
    void setUp() {
        semesterRepository = mock(SemesterRepository.class);
        service = new SemesterServiceImpl(semesterRepository);
    }

    // ------------------ createSemester ------------------

    @Test
    void createSemester_shouldSaveSemesterSuccessfully() {
        CreateSemesterRequest req = new CreateSemesterRequest(
                "1404-1",
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 6, 30),
                12,
                18
        );

        when(semesterRepository.existsByName("1404-1")).thenReturn(false);

        Semester savedSemester = Semester.builder()
                .name(req.name())
                .startDate(req.startDate())
                .endDate(req.endDate())
                .minUnits(req.minUnits())
                .maxUnits(req.maxUnits())
                .build();

        when(semesterRepository.save(any())).thenReturn(savedSemester);

        SemesterResponse response = service.createSemester(req);

        assertThat(response.name()).isEqualTo("1404-1");
        assertThat(response.startDate()).isEqualTo(req.startDate());
        assertThat(response.endDate()).isEqualTo(req.endDate());
        assertThat(response.minUnits()).isEqualTo(req.minUnits());
        assertThat(response.maxUnits()).isEqualTo(req.maxUnits());

        ArgumentCaptor<Semester> captor = ArgumentCaptor.forClass(Semester.class);
        verify(semesterRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("1404-1");
    }

    @Test
    void createSemester_whenNameExists_shouldThrowConflict() {
        CreateSemesterRequest req = new CreateSemesterRequest(
                "1404-1",
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 6, 30),
                12,
                18
        );

        when(semesterRepository.existsByName("1404-1")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.createSemester(req));
    }

    @Test
    void createSemester_whenStartDateAfterEndDate_shouldThrowBadRequest() {
        CreateSemesterRequest req = new CreateSemesterRequest(
                "1404-1",
                LocalDate.of(2025, 7, 1),
                LocalDate.of(2025, 6, 30),
                12,
                18
        );

        when(semesterRepository.existsByName("1404-1")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> service.createSemester(req));
    }

    @Test
    void createSemester_whenMinUnitsGreaterThanMax_shouldThrowBadRequest() {
        CreateSemesterRequest req = new CreateSemesterRequest(
                "1404-1",
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 6, 30),
                20,
                18
        );

        when(semesterRepository.existsByName("1404-1")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> service.createSemester(req));
    }

    // ------------------ updateSemester ------------------

    @Test
    void updateSemester_shouldUpdateFieldsSuccessfully() {
        UpdateSemesterRequest req = new UpdateSemesterRequest(
                "1404-2",
                LocalDate.of(2025, 2, 5),
                LocalDate.of(2025, 6, 28),
                10,
                20
        );

        Semester existing = Semester.builder()
                .name("1404-1")
                .startDate(LocalDate.of(2025, 2, 1))
                .endDate(LocalDate.of(2025, 6, 30))
                .minUnits(12)
                .maxUnits(18)
                .build();

        when(semesterRepository.findByName("1404-1")).thenReturn(Optional.of(existing));
        when(semesterRepository.existsByName("1404-2")).thenReturn(false);

        SemesterResponse response = service.updateSemester("1404-1", req);

        assertThat(response.name()).isEqualTo("1404-2");
        assertThat(response.startDate()).isEqualTo(req.startDate());
        assertThat(response.endDate()).isEqualTo(req.endDate());
        assertThat(response.minUnits()).isEqualTo(req.minUnits());
        assertThat(response.maxUnits()).isEqualTo(req.maxUnits());
    }

    @Test
    void updateSemester_whenSemesterNotFound_shouldThrowNotFound() {
        UpdateSemesterRequest req = new UpdateSemesterRequest(null, null, null, null, null);

        when(semesterRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateSemester("nonexistent", req));
    }

    @Test
    void updateSemester_whenNewNameExists_shouldThrowConflict() {
        UpdateSemesterRequest req = new UpdateSemesterRequest("1404-2", null, null, null, null);

        Semester existing = Semester.builder()
                .name("1404-1")
                .startDate(LocalDate.of(2025, 2, 1))
                .endDate(LocalDate.of(2025, 6, 30))
                .minUnits(12)
                .maxUnits(18)
                .build();

        when(semesterRepository.findByName("1404-1")).thenReturn(Optional.of(existing));
        when(semesterRepository.existsByName("1404-2")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.updateSemester("1404-1", req));
    }

    // ------------------ getAllSemesters ------------------

    @Test
    void getAllSemesters_shouldReturnAll() {
        Semester s1 = Semester.builder().name("1404-1").minUnits(12).maxUnits(18).startDate(LocalDate.of(2025,2,1)).endDate(LocalDate.of(2025,6,30)).build();
        Semester s2 = Semester.builder().name("1404-2").minUnits(10).maxUnits(20).startDate(LocalDate.of(2025,2,5)).endDate(LocalDate.of(2025,6,28)).build();

        when(semesterRepository.findAll()).thenReturn(List.of(s1, s2));

        List<SemesterResponse> result = service.getAllSemesters();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("1404-1");
        assertThat(result.get(1).name()).isEqualTo("1404-2");
    }
}


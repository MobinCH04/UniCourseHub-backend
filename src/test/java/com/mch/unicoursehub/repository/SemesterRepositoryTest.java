package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.Semester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SemesterRepositoryTest {

    @Autowired
    private SemesterRepository semesterRepository;

    private Semester createSemester(String name) {
        return Semester.builder()
                .name(name)
                .startDate(LocalDate.of(2025, 2, 1))
                .endDate(LocalDate.of(2025, 6, 30))
                .minUnits(12)
                .maxUnits(24)
                .build();
    }

    @Test
    @DisplayName("existsByName should return true when semester exists")
    void existsByName_whenSemesterExists_shouldReturnTrue() {
        // given
        Semester semester = createSemester("1404-1");
        semesterRepository.save(semester);

        // when
        boolean exists = semesterRepository.existsByName("1404-1");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByName should return false when semester does not exist")
    void existsByName_whenSemesterDoesNotExist_shouldReturnFalse() {
        // when
        boolean exists = semesterRepository.existsByName("1404-2");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findByName should return semester when it exists")
    void findByName_whenSemesterExists_shouldReturnSemester() {
        // given
        Semester semester = createSemester("1404-1");
        semesterRepository.save(semester);

        // when
        Optional<Semester> result = semesterRepository.findByName("1404-1");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("1404-1");
    }

    @Test
    @DisplayName("findByName should return empty when semester does not exist")
    void findByName_whenSemesterDoesNotExist_shouldReturnEmpty() {
        // when
        Optional<Semester> result = semesterRepository.findByName("1404-2");

        // then
        assertThat(result).isEmpty();
    }
}

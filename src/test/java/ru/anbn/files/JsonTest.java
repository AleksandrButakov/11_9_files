package ru.anbn.files;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import ru.anbn.files.domain.Student;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JsonTest {
    String pathFileJson = "src/test/resources/files/file.json";

    @Test
    void jsonTest() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File file = new File(pathFileJson);
            assertTrue(file.exists());
            Student student = objectMapper.readValue(file, Student.class);
            assertThat(student.name).isEqualTo("Ivan");
            assertThat(student.surname).isEqualTo("Ivanov");
            assertThat(student.address.street).isEqualTo("Mira");
            assertThat(student.address.house).isEqualTo(10);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

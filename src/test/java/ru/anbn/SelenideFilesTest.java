package ru.anbn;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static com.codeborne.selenide.Selenide.*;

public class SelenideFilesTest {

    @Test
    void selenideDownloadTest() {
        open("https://github.com/junit-team/junit5/blob/main/README.md");
        try {
            File downloadedFile = $("#raw-url").download();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("444");

    }

}

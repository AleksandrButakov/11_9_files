package ru.anbn;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SelenideFilesTest {

    @Disabled
    @Test
    // чтение файла и поиск в нем предложения
    void selenideDownloadTest() throws Exception {
        open("https://github.com/junit-team/junit5/blob/main/README.md");
        /* базовый механизм как в java читаются файлы. Есть еще метод Files из более новой версии java, но мы используем
           приведенный ниже пример. Stream требует закрытия после использования.
           Такой вариант скачивания работает когда в коде кнопки по которой кликаем есть ссылка на файл href.
           Если href нет (вызывается какой-нибудь скрипт котрый загружает файл) то в selenide есть возможность выполнить
           загрузку через прокси. Но это встречается крайне редко и прокси нестабильный механизм, поэтому лучше так не
           делать.
         */
        File downloadedFile = $("#raw-url").download();
        try (InputStream is = new FileInputStream(downloadedFile)) {
            assertThat(new String(is.readAllBytes(), UTF_8)).contains("This repository is the home " +
                    "of the next generation of JUnit");
        }
        // второй вариант скачивания файла из новой версии java который делает то же самое но используем первый вариант
        String readString = Files.readString(downloadedFile.toPath(), UTF_8);
    }

    @Test
    // загрузка файла на страницу
    void uploadSelenideTest() {
        open("https://the-internet.herokuapp.com/upload");
        // bad practice
        /* селектор загрузки файла как правило input с типом file. Можно так загрузить файл, но так делать не надо,
           потому что в Jenkins путь будет другим и такой номер не прокатит. Если в коде есть такой статический путь,
           значит это не правильно
        Selenide.$("input[type='file']")
                .uploadFile(new File("D:\\Docum\\Java_IDEA\\QaGuru\\11_9_files\\src\\test\\resources\\files\\1.txt"));
        */
        /* файлы размещаем в директории resources. Существует класс classLoader который выполняет поиск файлов в
           директории ресурсы.
         */

        // для Classpath путь указываем из resources
        $("input[type='file']")
                .uploadFromClasspath("files/1.txt");

        $("#file-submit").click();
        $("div.example").shouldHave(Condition.text("File Uploaded!"));
        $("#uploaded-files").shouldHave(Condition.text("1.txt"));

    }

}

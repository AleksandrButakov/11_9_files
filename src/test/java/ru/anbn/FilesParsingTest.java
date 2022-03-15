package ru.anbn;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;
import ru.anbn.domain.Teacher;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FilesParsingTest {

    @Test
    void parsePdfTest() throws Exception {
        open("https://junit.org/junit5/docs/current/user-guide/");
        File pdfDownload = $(byText("PDF download")).download();
        /* подключаем в Grable библиотеку 'com.codeborne:pdf-test:1.7.0' которая позволяет работать с pdf файлами
           используя её можем обращаться к полям файла (pdf.author) содержит 'Marc Philipp'
         */
        PDF pdf = new PDF(pdfDownload);
        assertThat(pdf.author).contains("Marc Philipp");
    }

    @Test
    void parseXlsTest() throws Exception {
        open("http://romashka2008.ru/price");
        // a[href*= где *= значит содержит

        /*
        <div class="site-main__inner">
            <div class="site-path"><a href="/"><span>Главная</span></a> <span>Прайс-лист Excel</span></div>
	        <h1>Скачать Прайс-лист Excel</h1>
            <p>
                <a href="/f/prajs_ot_1503.xls">Скачать Прайс-лист Excel</a>
            </p>
            <p>&nbsp;</p>
            <p>&nbsp;</p>
        </div>

        Селектор к этому блоку:
        .site-main__inner a[href*='prajs_ot']
         */

        File xlsDownload = $(".site-main__inner a[href*='prajs_ot']").download();
        XLS xls = new XLS(xlsDownload);

        // обратились адресно к строке excel и проверили что она содержит contains текст ...
        assertThat(xls.excel
                .getSheetAt(0)
                .getRow(11)
                .getCell(1)
                .getStringCellValue()).contains("693010,");

        System.out.println("");
    }

    @Test
    // читаем csv файл из resources и проверяем первую строку на соответствие
    void parseCsvTest() throws Exception {
        // добавили файл csv себе в ресурсы, значит будем его читать с помощью classLoader
        // способ №1
        ClassLoader classLoader1 = getClass().getClassLoader();
        // если находимся внутри статического класса то первый способ не пройдет, нужен второй
        // способ №1
        ClassLoader classLoader2 = FilesParsingTest.class.getClassLoader();

        try (InputStream is = classLoader1.getResourceAsStream("files/Machine_readable_file_bdc_sf_2021_q4.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is))) {
            List<String[]> content = reader.readAll();
            /* первая строка выглядит следующим образом:
               Series_reference,Period,Data_value,Suppressed,STATUS,UNITS,Magnitude,Subject,Group,Series_title_1......
               для корректного сравнения заменяем , на ", "
             */
            assertThat(content.get(0)).contains(
                    "Series_reference",
                    "Period",
                    "Data_value",
                    "Suppressed",
                    "STATUS",
                    "UNITS",
                    "Magnitude",
                    "Subject",
                    "Group",
                    "Series_title_1",
                    "Series_title_2",
                    "Series_title_3",
                    "Series_title_4",
                    "Series_title_5");
        }
    }

    @Test
    // работа с zip файлами
    void parseZipTest() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        /* в java есть класс для работы с zip файлами zipInputStream
           так как в блоке try переменные is and zis находятся до открывающийся фигурной скобки то в ручную
           их закрывать не нужно, они будут закрыты автоматически когда станут ненужны.
           Если бы например ZipInputStream zis = new ... распологалась после фигурной скобки, то после её использования
           необходимо было бы выполнить команду zis.close();
         */
        try (InputStream is = classLoader.getResourceAsStream("files/sample-zip-file.zip");
            ZipInputStream zis = new ZipInputStream(is)) {
            // все крутится вокруг ZipEntry
            ZipEntry entry; // объявили переменную
            /* работать с этим придется через цикл. В цикле while проверяем что entry присваиваем zis.getNextEntry()
               и проверяем что результат этого присвоения не равен null
             */
            while ((entry = zis.getNextEntry()) != null) {
                // если в zip архиве несколько файлов, то каждый раз будем попадать в тело цикла и что-то проверять
                assertThat(entry.getName()).isEqualTo("sample.txt");
            }
        }
    }

    @Test
    void jsonCommonTest() throws Exception {
        Gson gson = new Gson();
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("files/simple.json")) {
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            // любой json пожно положить в JsonObject. JsonObject универсальный объект из библиотеки для работы с json
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            // проверим что name = Ivan
            assertThat(jsonObject.get("name").getAsString()).isEqualTo("Ivan");
            // можно получить вложенный объект
            assertThat(jsonObject.get("address").getAsJsonObject().get("street").getAsString()).isEqualTo("Mira");

            // в большинстве случаев это не потребуется, можно создать свой класс который будет соответствовать Json-у
            // есть плагины, которые это делают
        }
    }

    @Test
    /* проверяем содержимое Json файла через класс Teacher. Тест выполняет тот же самый функционал что и тест выше,
       только используя классический подход
     */
    void jsonTypeTest() throws Exception {
        Gson gson = new Gson();
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("files/simple.json")) {
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            Teacher jsonObject = gson.fromJson(json, Teacher.class);
            // проверим что name = Ivan
            assertThat(jsonObject.name).isEqualTo("Ivan");
            // проверим вложенный объект
            assertThat(jsonObject.address.street).isEqualTo("Mira");

            // в большинстве случаев это не потребуется, можно создать свой класс который будет соответствовать Json-у
            // есть плагины, которые это делают
        }
    }

}

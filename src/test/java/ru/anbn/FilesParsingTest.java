package ru.anbn;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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

}

package ru.anbn.files;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FilesParsingTest {
    /* в архиве содержаться файлы:
       test_csv.csv
       test_pdf.pdf
       test_xlsx.xlsx
    */
    private static final String
            NAMEFILEXLSX = "test_xlsx.xlsx",
            NAMEFILECSV = "test_csv.csv",
            NAMEFILEPDF = "test_pdf.pdf";

    // переменные необходимо для проверки того что все три файла найдены
    private static boolean
            bFileXlsx = false,
            bFileCsv = false,
            bFilePdf = false;

    @Test
        // проверяем наличие файлов в ZIP архиве
    void parseZipTest() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("files/archiv.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                if (entry.getName().contains("xlsx")) {
                    assertThat(entry.getName()).isEqualTo(NAMEFILEXLSX);
                    System.out.println(".xlsx file found");
                    bFileXlsx = true;
                    checkingXlsxFile(zis);
                }
                if (entry.getName().contains("csv")) {
                    assertThat(entry.getName()).isEqualTo(NAMEFILECSV);
                    System.out.println(".csv file found");
                    bFileCsv = true;
                    //checkingCsvFile(zis);
                }
                if (entry.getName().contains("pdf")) {
                    assertThat(entry.getName()).isEqualTo(NAMEFILEPDF);
                    System.out.println(".pdf file found");
                    bFilePdf = true;
                    checkingPdfFile(zis);
                }
            }
            // проверим что все файлы были найдены
            assertThat(bFileCsv);
            assertThat(bFilePdf);
            assertThat(bFileXlsx);
        }
    }

    // проверим содержимое .csv файла на наличие ожидаемого текста
    void checkingCsvFile(InputStream file) throws Exception {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file))) {
            List<String[]> content = reader.readAll();
            assertThat(content.get(2)).contains("1997,Ford,E350, Yam");
            System.out.println("Checking the contents of a csv file...");
        }
    }

    // проверим содержимое .csv файла на наличие ожидаемого текста
    void checkingPdfFile(InputStream file) throws Exception {
        PDF pdf = new PDF(file);
        assertThat(pdf.text).contains("Checking a pdf file");
        System.out.println("Checking the contents of a pdf file...");
    }

    // проверим содержимое .csv файла на наличие ожидаемого текста
    void checkingXlsxFile(InputStream file) throws Exception {
        XLS xls= new XLS(file);
        assertThat(xls.excel
                .getSheetAt(0)
                .getRow(2)
                .getCell(0)
                .getStringCellValue()).contains("Text file for verification");
        System.out.println("Checking the contents of a xlsx file...");
    }


}

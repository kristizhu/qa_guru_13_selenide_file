package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

public class FileParseTest {
    ClassLoader classLoader = FileParseTest.class.getClassLoader();

    @DisplayName("PDF in Zip")
    @Test
    void PDFTest() throws Exception {
        ZipFile zipFiles = new ZipFile(new File("src/test/resources/files.zip"));
        ZipEntry zipEntry = zipFiles.getEntry("pdf-test.pdf");
        InputStream inputStream = zipFiles.getInputStream(zipEntry);
        PDF pdf = new PDF(inputStream);
        assertThat(pdf.text).contains("Canada");

    }
    @DisplayName("XLS in Zip")
    @Test
    void xlsxTest() throws Exception {
        ZipFile zipFiles = new ZipFile(new File("src/test/resources/files.zip"));
        ZipEntry zipEntry = zipFiles.getEntry("xlsx-test.xlsx");
        InputStream inputStream = zipFiles.getInputStream(zipEntry);
        XLS xls = new XLS(inputStream);
        assertThat(
                xls.excel.getSheetAt(0)
                .getRow(0)
                .getCell(0)
                .getStringCellValue()).contains("PDF Test File");
    }

    @DisplayName("CSV in Zip test")
    @Test
    void parseCsvTest() throws Exception {
        ZipFile zipFiles = new ZipFile(new File("src/test/resources/files.zip"));
        ZipEntry zipEntry = zipFiles.getEntry("csv-test.csv");
        try (InputStream inputStream = zipFiles.getInputStream(zipEntry);
             CSVReader csv = new CSVReader(new InputStreamReader(inputStream))) {
            List<String[]> content = csv.readAll();
            assertThat(content.get(9)).contains("Canada");
        }
    }

    @DisplayName("Json with Jackson test")

    @Test
    void JsonTest() throws Exception{
        InputStream inputStream = classLoader.getResourceAsStream("student.json");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new InputStreamReader(inputStream));
        assertThat(jsonNode.get("age").asInt()).isEqualTo(26);
        assertThat(jsonNode.get("name").asText()).isEqualTo("kris");
        assertThat(jsonNode.get("rating").asText()).isEqualTo("5");
        assertThat(jsonNode.findValue("teacher").findValue("name").asText()).isEqualTo("Sam");
    }
}

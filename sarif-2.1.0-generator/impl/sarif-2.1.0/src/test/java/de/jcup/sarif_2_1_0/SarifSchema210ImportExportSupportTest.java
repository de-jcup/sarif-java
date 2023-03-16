package de.jcup.sarif_2_1_0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.jcup.sarif_2_1_0.model.SarifSchema210;

class SarifSchema210ImportExportSupportTest {

    private SarifSchema210ImportExportSupport supportToTest;
    private static SarifSchema210TestReportFactory testReportFactory;

    @BeforeAll
    static void beforeAll() {
        testReportFactory = new SarifSchema210TestReportFactory();
    }

    @BeforeEach
    void beforeEach() {
        supportToTest = new SarifSchema210ImportExportSupport();
    }

    @Test
    void toFile_works() throws IOException {
        /* prepare */
        Path tempFile = Files.createTempFile("test_sarif_save", ".json");
        File file = tempFile.toFile();

        SarifSchema210 sarif = testReportFactory.createTestSarifSchema();

        /* execute */
        supportToTest.toFile(sarif, file);

        /* test */
        assertTrue(file.exists());
    }

    @Test
    void fromFile_works() throws IOException {
        /* execute */
        SarifSchema210 sarif = supportToTest.fromFile(new File("./src/test/resources/sarif_2_1_0_testfile1.json"));
        
        /* test */
        assertNotNull(sarif);
        assertEquals(1, sarif.getRuns().size());
    }
    
    @Test
    void to_json_works() throws IOException {
        /* prepar */
        SarifSchema210 sarif = testReportFactory.createTestSarifSchema();
        
        /* execute */
        String json = supportToTest.toJSON(sarif);
        
        /* test */
        assertNotNull(json);
        assertTrue(json.contains("runs"));
        assertTrue(json.contains("12346-rule-1"));
        
    }
    
    @Test
    void from_json_works_empty_json() throws IOException {
        /* prepar */
        String json = "{}";
        
        /* execute */
        SarifSchema210 sarif = supportToTest.fromJSON(json);
        
        System.out.println(supportToTest.toJSON(sarif));
        
        /* test */
        assertNotNull(sarif);
        assertEquals(0, sarif.getRuns().size());
        
    }
    
    @Test
    void from_json_works_only_run_defined() throws IOException {
        /* prepar */
        /* @formatter:off */
        String json = "{\n"
                + "  \"runs\" : [ {\n"
                + "    \"tool\" : {\n"
                + "      \"driver\" : {\n"
                + "        \"guid\" : \"1234-guid-test-tool-driver-id\",\n"
                + "        \"fullName\" : \"Only-Test\"\n"
                + "      }\n"
                + "    }\n"
                + "  } ]\n"
                + "}";
        /* @formatter:on */
        
        /* execute */
        SarifSchema210 sarif = supportToTest.fromJSON(json);
        
        /* test */
        assertNotNull(sarif);
        assertEquals(1, sarif.getRuns().size());
        
    }

}

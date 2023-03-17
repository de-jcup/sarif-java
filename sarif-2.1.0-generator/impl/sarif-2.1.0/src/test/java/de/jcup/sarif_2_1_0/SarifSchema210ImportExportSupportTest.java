package de.jcup.sarif_2_1_0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.jcup.sarif_2_1_0.model.CodeFlow;
import de.jcup.sarif_2_1_0.model.MultiformatMessageString;
import de.jcup.sarif_2_1_0.model.PropertyBag;
import de.jcup.sarif_2_1_0.model.ReportingConfiguration;
import de.jcup.sarif_2_1_0.model.ReportingDescriptor;
import de.jcup.sarif_2_1_0.model.Result;
import de.jcup.sarif_2_1_0.model.Run;
import de.jcup.sarif_2_1_0.model.SarifSchema210;
import de.jcup.sarif_2_1_0.model.ToolComponent;

class SarifSchema210ImportExportSupportTest {

    private SarifSchema210ImportExportSupport supportToTest;
    private static SarifSchema210TestReportFactory testReportFactory;
    private static TestFileData testData;

    @BeforeAll
    static void beforeAll() {
        testReportFactory = new SarifSchema210TestReportFactory();

        testData = new TestFileData();
    }

    @BeforeEach
    void beforeEach() {
        supportToTest = new SarifSchema210ImportExportSupport();
    }

    @Test
    void microsoft_sarif_tutorial_codeflow_example() throws IOException {
        /* prepare */
        File codeFlowSarifSchema210File = new File(testData.sarifTutorialSamplesFolder, "CodeFlows.sarif");

        /* execute */
        SarifSchema210 SarifSchema210 = supportToTest.fromFile(codeFlowSarifSchema210File);

        /* test */
        List<Run> runs = SarifSchema210.getRuns();
        assertEquals(1, runs.size(), "there must be ONE run!");
        Run run = runs.iterator().next();
        List<Result> results = run.getResults();
        assertEquals(1, results.size(), "there must be ONE result!");
        Result result = results.iterator().next();
        assertEquals("TUT1001", result.getRuleId());
        assertEquals("Use of uninitialized variable.", result.getMessage().getText());

        List<CodeFlow> codeFlows = result.getCodeFlows();
        assertEquals(2, codeFlows.size());
    }

    @Test
    void microsoft_sarif_tutorial_taxonomies_example__result_messages() throws IOException {
        /* prepare */
        File codeFlowSarifSchema210File = new File(testData.sarifTutorialSamplesFolder, "Taxonomies.sarif");

        /* execute */
        SarifSchema210 SarifSchema210 = supportToTest.fromFile(codeFlowSarifSchema210File);

        /* test */
        List<Run> runs = SarifSchema210.getRuns();
        assertEquals(1, runs.size(), "there must be ONE run!");
        Run run = runs.iterator().next();

        List<Result> results = run.getResults();
        assertEquals(2, results.size(), "there must be two result!");
        Iterator<Result> iterator = results.iterator();

        // sort results by tree map, so we can fetch wanted ones
        Map<String, Result> sortedMap = new TreeMap<>();
        Result result = iterator.next();
        sortedMap.put(result.getRuleId(), result);
        result = iterator.next();
        sortedMap.put(result.getRuleId(), result);

        Result result1 = sortedMap.get("TUT1001");
        assertNotNull(result1);
        assertEquals("TUT1001", result1.getRuleId());
        assertEquals("This result violates a rule that is classified as 'Required'.", result1.getMessage().getText());

        Result result2 = sortedMap.get("TUT1002");
        assertNotNull(result2);
        assertEquals("TUT1002", result2.getRuleId());
        assertEquals("This result violates a rule that is classified as 'Recommended'.",
                result2.getMessage().getText());
    }

    @Test
    void microsoft_sarif_tutorial_taxonomies_example__result_defaultocnfiguraiton_level() throws IOException {
        /* prepare */
        File codeFlowSarifSchema210File = new File(testData.sarifTutorialSamplesFolder, "Taxonomies.sarif");

        /* execute */
        SarifSchema210 SarifSchema210 = supportToTest.fromFile(codeFlowSarifSchema210File);

        /* test */
        List<Run> runs = SarifSchema210.getRuns();
        assertEquals(1, runs.size(), "there must be ONE run!");
        Run run = runs.iterator().next();

        Set<ReportingDescriptor> rules = run.getTool().getDriver().getRules();
        Map<String, ReportingDescriptor> sortedMap = new TreeMap<>();
        for (ReportingDescriptor rule : rules) {
            sortedMap.put(rule.getId(), rule);
        }
        ReportingDescriptor rule1 = sortedMap.get("TUT0001");
        assertNotNull(rule1);
        ReportingConfiguration defaultConfig1 = rule1.getDefaultConfiguration();
        assertNotNull(defaultConfig1);
        assertEquals(ReportingConfiguration.Level.ERROR, defaultConfig1.getLevel());

        ReportingDescriptor rule2 = sortedMap.get("TUT0002");
        assertNotNull(rule2);
        ReportingConfiguration defaultConfig2 = rule2.getDefaultConfiguration();
        assertNotNull(defaultConfig2);
        assertEquals(ReportingConfiguration.Level.WARNING, defaultConfig2.getLevel());
    }

    @Test
    void microsoft_sarif_tutorial_taxonomies_example_taxonomies_deserialized_correclty() throws IOException {
        /* prepare */
        File codeFlowSarifSchema210File = new File(testData.sarifTutorialSamplesFolder, "Taxonomies.sarif");

        /* execute */
        SarifSchema210 SarifSchema210 = supportToTest.fromFile(codeFlowSarifSchema210File);

        /* test */
        List<Run> runs = SarifSchema210.getRuns();
        assertEquals(1, runs.size(), "there must be ONE run!");

        Run run = runs.iterator().next();
        Set<ToolComponent> taxonomies = run.getTaxonomies();
        Map<String, ToolComponent> sortedTaxonomiesMap = new TreeMap<>();

        for (ToolComponent taxonomy : taxonomies) {
            sortedTaxonomiesMap.put(taxonomy.getGuid(), taxonomy);
        }

        ToolComponent taxonomy1 = sortedTaxonomiesMap.get("1A567403-868F-405E-92CF-771A9ECB03A1");
        assertEquals("Requirement levels", taxonomy1.getName());
        MultiformatMessageString shortDescription = new MultiformatMessageString();
        shortDescription.setText(
                "This taxonomy classifies rules according to whether their use is required or recommended by company policy.");
        assertEquals(shortDescription, taxonomy1.getShortDescription());

        Map<String, ReportingDescriptor> sortedTaxaMap = new TreeMap<>();
        for (ReportingDescriptor taxon : taxonomy1.getTaxa()) {
            sortedTaxaMap.put(taxon.getId(), taxon);
        }
        ReportingDescriptor taxon1 = sortedTaxaMap.get("RQL1001");
        assertNotNull(taxon1);
        assertEquals("Required", taxon1.getName());
        MultiformatMessageString shortDescription2 = new MultiformatMessageString();
        shortDescription2.setText(
                "Rules in this category are required by company policy. All violations must be fixed unless an exemption is granted.");
        assertEquals(shortDescription2, taxon1.getShortDescription());

        ReportingDescriptor taxon2 = sortedTaxaMap.get("RQL1002");
        assertNotNull(taxon2);
        assertEquals("Recommended", taxon2.getName());
        MultiformatMessageString shortDescription3 = new MultiformatMessageString();
        shortDescription3.setText(
                "Rules in this category are recommended but not required by company policy. Violations should be fixed but an exemption is not required to suppress a result.");
        assertEquals(shortDescription3, taxon2.getShortDescription());
    }

    @Test
    void brakeman_sarif_example_with_tags_can_be_loaded() throws IOException {
        /* prepare */
        File folder = testData.sarifBrakemanFolder;

        /* execute +test */
        testSarifSchema210s(folder, 1, "2.1.0");
    }

    @Test
    void specification_examples_can_all_be_loaded() throws IOException {
        /* prepare */
        File folder = testData.sarifSpecificationSnippetsFolder;

        /* execute +test */
        testSarifSchema210s(folder, 1, "2.1.0");

    }

    @Test
    void specification_properties_snippet_properties_contains_tags() throws IOException {
        /* prepare */
        File folder = testData.sarifSpecificationSnippetsFolder;

        /* execute */
        SarifSchema210 SarifSchema210 = supportToTest
                .fromFile(new File(folder, "specification-properties-snippet.sarif.json"));

        /* test */
        List<Result> results = SarifSchema210.getRuns().iterator().next().getResults();
        Result result = results.iterator().next();
        PropertyBag properties = result.getProperties();
        assertNotNull(properties);
        assertEquals(Collections.singleton("openSource"), properties.getTags());

    }

    @Test
    void specification_properties_snippet_properties_contains_opensource_key_and_map_value() throws IOException {
        /* prepare */
        File folder = testData.sarifSpecificationSnippetsFolder;

        /* execute */
        SarifSchema210 SarifSchema210 = supportToTest
                .fromFile(new File(folder, "specification-properties-snippet.sarif.json"));

        /* test */
        List<Result> results = SarifSchema210.getRuns().iterator().next().getResults();
        Result result = results.iterator().next();
        PropertyBag properties = result.getProperties();
        assertNotNull(properties);
        Object openSourceData = properties.getAdditionalProperties().get("openSource");
        if (openSourceData instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) openSourceData;
            String informationUri = (String) map.get("informationUri");
            assertEquals("http://www.example.com/procedures/usingOpenSource.html", informationUri);
        } else {
            fail("expected map but found:" + openSourceData);
        }

    }

    @Test
    void specification_properties_snippet_properties_contains_opensource_key_and_map_value_and_can_be_written()
            throws IOException {
        /* prepare */
        File folder = testData.sarifSpecificationSnippetsFolder;

        /* execute */
        SarifSchema210 SarifSchema210 = supportToTest
                .fromFile(new File(folder, "specification-properties-snippet.sarif.json"));

        /* test */
        List<Result> results = SarifSchema210.getRuns().iterator().next().getResults();
        Result result = results.iterator().next();
        PropertyBag properties = result.getProperties();
        assertNotNull(properties);
        Object openSourceData = properties.getAdditionalProperties().get("openSource");
        if (openSourceData instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) openSourceData;
            String informationUri = (String) map.get("informationUri");
            assertEquals("http://www.example.com/procedures/usingOpenSource.html", informationUri);
        } else {
            fail("expected map but found:" + openSourceData);
        }

    }

    @Test
    void microsoft_sarif_tutorial_samples_can_all_be_loaded() throws IOException {
        /* prepare */
        File folder = testData.sarifTutorialSamplesFolder;

        /* execute +test */
        testSarifSchema210s(folder, 14, "2.1.0");

    }

    @Test
    void microsoft_sarif_tutorial_samples_1_introduction_can_all_be_loaded() throws IOException {

        /* prepare */
        File folder = new File(testData.sarifTutorialSamplesFolder, "1-Introduction");

        /* execute +test */
        testSarifSchema210s(folder, 1, "2.1.0");

    }

    @Test
    void microsoft_sarif_tutorial_samples_2_basics_can_all_be_loaded() throws IOException {

        /* prepare */
        File folder = new File(testData.sarifTutorialSamplesFolder, "2-Basics");

        /* execute +test */
        testSarifSchema210s(folder, 1, "2.1.0");

    }

    @Test
    void microsoft_sarif_tutorial_samples_3_beyond_basics_can_all_be_loaded() throws IOException {

        /* prepare */
        File folder = new File(testData.sarifTutorialSamplesFolder, "3-Beyond-basics");

        /* execute +test */
        testSarifSchema210s(folder, 8, "2.1.0");

    }

    private void testSarifSchema210s(File folder, int expectedCount, String expectedSarifVersion) throws IOException {
        int count = 0;
        for (File file : folder.listFiles(testData.sarifFileEndingFilter)) {
            /* prepare */
            count++;

            /* execute */
            try {
                SarifSchema210 SarifSchema210 = supportToTest.fromFile(file);

                /* test */
                assertNotNull(SarifSchema210);
                assertEquals(expectedSarifVersion, SarifSchema210.getVersion().toString());
            } catch (IOException e) {
                throw new IOException("Failure on file:" + file.getAbsolutePath(), e);
            }

        }
        /* sanity check */
        assertEquals(expectedCount, count, "Not amount of expected files were read as sarif SarifSchema210!");
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
        SarifSchema210 sarif = supportToTest.fromFile(testData.sarif_2_1_0_testfile1);

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

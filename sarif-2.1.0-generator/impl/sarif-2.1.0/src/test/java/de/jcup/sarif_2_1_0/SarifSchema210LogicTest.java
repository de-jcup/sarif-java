package de.jcup.sarif_2_1_0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.jcup.sarif_2_1_0.model.ReportingDescriptor;
import de.jcup.sarif_2_1_0.model.Result;
import de.jcup.sarif_2_1_0.model.Result.Level;
import de.jcup.sarif_2_1_0.model.Run;
import de.jcup.sarif_2_1_0.model.SarifSchema210;

class SarifSchema210LogicTest {

    private SarifSchema210LogicSupport supportToTest;
    private TestFileData testData;
    private static SarifSchema210ImportExportSupport importExportSupport;
    private static SarifSchema210TestReportFactory testSchemaFactory;

    @BeforeAll
    static void beforeAll() {
        testSchemaFactory = new SarifSchema210TestReportFactory();
        importExportSupport = new SarifSchema210ImportExportSupport();

    }

    @BeforeEach
    void beforeEach() {
        supportToTest = new SarifSchema210LogicSupport();
        testData = new TestFileData();
    }

    @Test
    void brakeman_sarif_example_with_tags__tags_can_be_fetched() throws IOException {
        /* prepare */
        File codeFlowSarifSchema210File = new File(testData.sarifBrakemanFolder,
                "sarif_2_1_0__brakeman_testfile_with_tags.sarif.json");

        /* execute - 1 */
        SarifSchema210 SarifSchema210 = importExportSupport.fromFile(codeFlowSarifSchema210File);

        /* test 1- check it can be loaded */
        List<Run> runs = SarifSchema210.getRuns();
        assertEquals(1, runs.size(), "there must be ONE run!");
        Run run = runs.iterator().next();
        List<Result> results = run.getResults();
        assertEquals(32, results.size(), "there must be 32 results!");
        Result result = results.iterator().next();

        /* execute - 2 */
        ReportingDescriptor rule = supportToTest.fetchRuleForResult(result, run);

        /* test 2 : here we test the logic! */
        Set<String> tags = rule.getProperties().getTags();
        assertNotNull(tags);

        Set<String> expected = new LinkedHashSet<>();
        expected.add("ContentTag");
        expected.add("Tag2");
        expected.add("Tag3");
        assertEquals(expected, tags);
    }

    @Test
    void fetchRuleForResult() {
        /* prepare */
        SarifSchema210 sarif = testSchemaFactory.createTestSarifSchema();

        /* check preconditions */
        Run run1 = sarif.getRuns().iterator().next();
        List<Result> results = run1.getResults();
        assertEquals(5, results.size());

        Iterator<Result> it = results.iterator();
        Result result1 = it.next();
        Result result2 = it.next();
        Result result3 = it.next();
        Result result4 = it.next();
        Result result5 = it.next();

        /* execute */
        ReportingDescriptor rule1 = supportToTest.fetchRuleForResult(result1, run1);
        ReportingDescriptor rule2 = supportToTest.fetchRuleForResult(result2, run1);
        ReportingDescriptor rule3 = supportToTest.fetchRuleForResult(result3, run1);
        ReportingDescriptor rule4 = supportToTest.fetchRuleForResult(result4, run1);
        ReportingDescriptor rule5 = supportToTest.fetchRuleForResult(result5, run1);

        /* test */
        assertNotNull(rule1);
        assertNotNull(rule2);
        assertNotNull(rule3);
        assertNotNull(rule4);
        assertNotNull(rule5);

        assertTrue(rule1.getGuid().endsWith("rule-1"));
        assertTrue(rule2.getGuid().endsWith("rule-2"));
        assertTrue(rule3.getGuid().endsWith("rule-3"));
        assertTrue(rule4.getGuid().endsWith("rule-4"));
        assertTrue(rule5.getGuid().endsWith("rule-5"));

    }

    @Test
    void resolveLevel_when_levels_are_explicit_null_set_in_model() {
        /* prepare */
        SarifSchema210 sarif = testSchemaFactory.createTestSarifSchema();

        /* test */
        commonLevelCheck(sarif);

    }

    @Test
    void resolveLevel_when_levels_are_explicit_null_set_from_file() throws IOException {

        /* prepare */
        SarifSchema210 sarif = importExportSupport.fromFile(testData.sarif_2_1_0_testfile1);

        /* test */
        commonLevelCheck(sarif);

    }

    /**
     * This test case checks if the default mechanism for level resolution works as
     * expected, when the json file does NOT contain level settings with null
     * inside. Means the jackson setup is not used (nothing defined). Here we test 2
     * things: import + level resolution. But the main focus is the level resolution
     * problem. <br>
     * <br>
     * This tests if https://github.com/de-jcup/sarif-java/issues/11 has been fixed
     * 
     * @throws IOException
     */
    @Test
    void resolveLevel_when_levels_are_NOT_explicit_null_set_from_file() throws IOException {
        /* prepare */
        SarifSchema210 sarif = importExportSupport.fromFile(testData.sarif_2_1_0_testfile2_no_explicit_level_null_set);

        /* test */
        commonLevelCheck(sarif);

    }

    private void commonLevelCheck(SarifSchema210 sarif) {
        /* check preconditions */
        Run run1 = sarif.getRuns().iterator().next();
        List<Result> results = run1.getResults();
        assertEquals(5, results.size());

        Iterator<Result> it = results.iterator();
        Result result1 = it.next();
        Result result2 = it.next();
        Result result3 = it.next();
        Result result4 = it.next();
        Result result5 = it.next();

        /* execute */
        Level level1 = supportToTest.resolveLevel(result1, run1);
        Level level2 = supportToTest.resolveLevel(result2, run1);
        Level level3 = supportToTest.resolveLevel(result3, run1);
        Level level4 = supportToTest.resolveLevel(result4, run1);
        Level level5 = supportToTest.resolveLevel(result5, run1);

        /* test */
        assertEquals(Level.NOTE, level1);
        assertEquals(Level.ERROR, level2);
        assertEquals(Level.WARNING, level3);
        assertEquals(Level.WARNING, level4);
        assertEquals(Level.NOTE, level5);
    }

}

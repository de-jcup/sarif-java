package de.jcup.sarif_2_1_0;

import de.jcup.sarif_2_1_0.model.MultiformatMessageString;
import de.jcup.sarif_2_1_0.model.ReportingConfiguration;
import de.jcup.sarif_2_1_0.model.ReportingDescriptor;
import de.jcup.sarif_2_1_0.model.ReportingDescriptorReference;
import de.jcup.sarif_2_1_0.model.Result;
import de.jcup.sarif_2_1_0.model.Result.Level;
import de.jcup.sarif_2_1_0.model.Run;
import de.jcup.sarif_2_1_0.model.SarifSchema210;
import de.jcup.sarif_2_1_0.model.Tool;
import de.jcup.sarif_2_1_0.model.ToolComponent;
import de.jcup.sarif_2_1_0.model.ToolComponentReference;

public class SarifSchema210TestReportFactory {

    /**
     * Creates a test schema.
     * 
     * We have 3 rules inside
     * 
     * <ol>
     * <li>rule1</li>with default level:note
     * <li>rule2</li>with default level:none
     * <li>rule3</li>with default level:null
     * <li>rule4</li>without default configuration
     * <li>rule5</li>without default configuration
     * </ol>
     * 
     * We have 3 results inside
     * 
     * <ol>
     * <li>result1</li>with level:null --> so rule1 default will be used: note
     * <li>result2</li>with level:error --> so rule2 default will be overridden:
     * error
     * <li>result3</li>with level: null --> so rule3 default, but still null, so
     * SARIF default must be applied: warning
     * <li>result4</li>with level: null --> no default rule, so SARIF default
     * applied: warning
     * <li>result5</li>with level: note --> no default rule: note
     * </ol>
     *
     * @return
     */
    public SarifSchema210 createTestSarifSchema() {
        SarifSchema210 sarif = new SarifSchema210();
        Run run1 = new Run();
        Tool tool1 = new Tool();
        ToolComponent driver = new ToolComponent();

        String driverGuid = "1234-guid-test-tool-driver-id";

        driver.setGuid(driverGuid);
        driver.setFullName("Only-Test");

        tool1.setDriver(driver);
        run1.setTool(tool1);
        sarif.getRuns().add(run1);

        String rule1Id = createRuleWithDefaultLevel(driver, de.jcup.sarif_2_1_0.model.ReportingConfiguration.Level.NOTE,
                "rule-1", true);
        String rule2Id = createRuleWithDefaultLevel(driver, de.jcup.sarif_2_1_0.model.ReportingConfiguration.Level.NONE,
                "rule-2", true);
        String rule3Id = createRuleWithDefaultLevel(driver, null, "rule-3", true);
        String rule4Id = createRuleWithDefaultLevel(driver, null, "rule-4", false);
        String rule5Id = createRuleWithDefaultLevel(driver, null, "rule-5", false);

        createResultAtRun(run1, driverGuid, rule1Id, null);
        createResultAtRun(run1, driverGuid, rule2Id, Level.ERROR);
        createResultAtRun(run1, driverGuid, rule3Id, null);
        createResultAtRun(run1, driverGuid, rule4Id, null);
        createResultAtRun(run1, driverGuid, rule5Id, Level.NOTE);

        return sarif;
    }

    private void createResultAtRun(Run run1, String driverGuid, String ruleId, Level level) {
        Result result1 = new Result();
        run1.getResults().add(result1);

        result1.setGuid("12354-guid-result-1");
        result1.setLevel(level);
        result1.setRuleId(ruleId);

        ReportingDescriptorReference ruleReference1 = new ReportingDescriptorReference();
        ToolComponentReference toolReference1 = new ToolComponentReference();
        toolReference1.setGuid(driverGuid);

        ruleReference1.setToolComponent(toolReference1);
        ruleReference1.setGuid(ruleId);

        result1.setRule(ruleReference1);
    }

    private String createRuleWithDefaultLevel(ToolComponent driver,
            de.jcup.sarif_2_1_0.model.ReportingConfiguration.Level level, String givenRuleId,
            boolean withDefaultConfiguration) {
        ReportingDescriptor rule = new ReportingDescriptor();
        String rule1Guid = "12346-" + givenRuleId;
        String ruleId = givenRuleId;
        rule.setGuid(rule1Guid);
        rule.setId(ruleId);

        MultiformatMessageString fullDescription = new MultiformatMessageString();
        fullDescription.setText("full-description of " + givenRuleId);
        rule.setFullDescription(fullDescription);

        MultiformatMessageString shortDescription = new MultiformatMessageString();
        shortDescription.setText("short-description of " + givenRuleId);
        rule.setShortDescription(shortDescription);

        if (withDefaultConfiguration) {
            ReportingConfiguration defaultConfiguration = new ReportingConfiguration();
            rule.setDefaultConfiguration(defaultConfiguration);
            rule.getDefaultConfiguration().setLevel(level);

        }
        driver.getRules().add(rule);
        return ruleId;
    }
}

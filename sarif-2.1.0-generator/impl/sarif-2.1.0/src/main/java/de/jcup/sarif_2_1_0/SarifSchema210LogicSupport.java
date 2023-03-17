package de.jcup.sarif_2_1_0;

import java.util.Set;

import de.jcup.sarif_2_1_0.model.ReportingConfiguration;
import de.jcup.sarif_2_1_0.model.ReportingDescriptor;
import de.jcup.sarif_2_1_0.model.Result;
import de.jcup.sarif_2_1_0.model.Run;
import de.jcup.sarif_2_1_0.model.Tool;
import de.jcup.sarif_2_1_0.model.ToolComponent;
import de.jcup.sarif_2_1_0.model.Result.Level;

/**
 * This class provides some special SARIF logic which is necessary and cannot be
 * generated (for Example: resolveLevel method is an implementation for the
 * pseudo code found in the official SARIF documentation at
 * https://docs.oasis-open.org/sarif/sarif/v2.1.0/os/sarif-v2.1.0-os.html#_Toc34317648
 * 
 * @author Albert Tregnaghi
 *
 */
public class SarifSchema210LogicSupport {

    /**
     * Fetch rule for result. Each run has ONE tool, multiple results and taxonomies
     * 
     * @param result - the result
     * @param run    - the run whose tool contains the rule whose id is referenced
     *               by the given result object
     * @return
     */
    public ReportingDescriptor fetchRuleForResult(Result result, Run run) {
        //
        Tool tool = run.getTool();
        ToolComponent driver = tool.getDriver();

        Set<ReportingDescriptor> rules = driver.getRules();
        if (rules == null) {
            return null;
        }

        String ruleId = result.getRuleId();
        if (ruleId == null) {
            return null;
        }
       /* @formatter:off */
       for (ReportingDescriptor rule: rules){
           if (rule==null) {
               continue;
           }
           
           if (ruleId.equals(rule.getId())){
               return rule;
           }
       }
       /* @formatter:on */
        return null;
    }

    /**
     * Resolves the result level.
     * 
     * This is the implementation of the specification pseudo code see
     * https://docs.oasis-open.org/sarif/sarif/v2.1.0/os/sarif-v2.1.0-os.html#_Toc34317648
     * 
     * Tries first the result level. If not set, the level will be obtained by
     * default configuration if available. If not found {@link Level#WARNING} is
     * returned.
     * 
     * @param result
     * @param run
     * @return level, never null
     */
    public Level resolveLevel(Result result, Run run) {
        Level level = result.getLevel();
        if (level != null) {
            return level;
        }
        ReportingDescriptor rule = fetchRuleForResult(result, run);
        if (rule != null) {

            ReportingConfiguration defaultConfiguration = rule.getDefaultConfiguration();
            if (defaultConfiguration != null) {
                /* convert to result level */
                de.jcup.sarif_2_1_0.model.ReportingConfiguration.Level configLevel = defaultConfiguration.getLevel();
                if (configLevel != null) {
                    level = Level.fromValue(configLevel.value());
                }
            }
        }
        if (level == null) {
            level = Level.WARNING;
        }
        return level;
    }
}

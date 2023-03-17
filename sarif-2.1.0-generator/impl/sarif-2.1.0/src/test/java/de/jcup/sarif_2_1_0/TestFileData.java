package de.jcup.sarif_2_1_0;

import java.io.File;
import java.io.FilenameFilter;

public class TestFileData {

    FilenameFilter sarifFileEndingFilter;
    File sarifTutorialSamplesFolder;
    File sarifSpecificationSnippetsFolder;
    File sarifBrakemanFolder;

    File sarif_2_1_0_testfile1;
    File sarif_2_1_0_testfile2_no_explicit_level_null_set;

    TestFileData() {
        String pathToUse = System.getProperty("sarif.testdata.folder");
        if (pathToUse == null) {
            pathToUse = "./";
        }
        File parentFile = new File(pathToUse);

        sarifFileEndingFilter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".sarif") || name.endsWith(".sarif.json");
            }
        };

        sarifTutorialSamplesFolder = new File(parentFile,
                "src/test/resources/examples/microsoft/sarif-tutorials/samples");
        sarifSpecificationSnippetsFolder = new File(parentFile, "src/test/resources/examples/specification");
        sarifBrakemanFolder = new File(parentFile, "src/test/resources/examples/brakeman");

        sarif_2_1_0_testfile1 = new File(parentFile, "src/test/resources/sarif_2_1_0_testfile1.json");
        sarif_2_1_0_testfile2_no_explicit_level_null_set = new File(parentFile,
                "src/test/resources/sarif_2_1_0_testfile2_no_explicit_level_null_set.json");
    }
}
// SPDX-License-Identifier: MIT
package de.jcup.sarif_2_1_0;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.jcup.sarif_2_1_0.model.SarifSchema210;

/**
 * A support class for SARIF which provides some convenient methods to read and
 * write SARIF reports.
 * 
 * @author Albert Tregnaghi
 *
 */
public class SarifSchema210ImportExportSupport {

    private ObjectMapper mapper;
    private static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");
    
    public SarifSchema210ImportExportSupport() {
        JsonFactoryBuilder builder = new JsonFactoryBuilder();
        JsonFactory factory = new JsonFactory(builder);
        mapper = new ObjectMapper(factory).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    public void toFile(SarifSchema210 sarifReport, Path targetFile) throws IOException {
        Objects.requireNonNull(targetFile, "targetFile may not be null!");
        
        toFile(sarifReport, targetFile.toFile());
    }

    public void toFile(SarifSchema210 sarifReport, File targetFile) throws IOException {
        Objects.requireNonNull(targetFile, "targetFile may not be null!");
        
        mapper.writerWithDefaultPrettyPrinter().writeValue(targetFile, sarifReport);
    }

    public SarifSchema210 fromFile(Path path) throws IOException {
        Objects.requireNonNull(path, "path may not be null!");
        
        return fromFile(path.toFile());
    }

    public SarifSchema210 fromFile(File file) throws IOException {
        Objects.requireNonNull(file, "file may not be null!");
        
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist:" + file.getAbsolutePath());
        }

        try (FileInputStream inputStream = new FileInputStream(file)) {
            SarifSchema210 schema = mapper.readValue(inputStream, SarifSchema210.class);
            return schema;
        }

    }

    public String toJSON(SarifSchema210 sarifReport) throws IOException {
        if (sarifReport == null) {
            return null;
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sarifReport);
    }
    
    public SarifSchema210 fromJSON(String json) throws IOException {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(json.getBytes(UTF_8_CHARSET))) {
            SarifSchema210 schema = mapper.readValue(byteStream, SarifSchema210.class);
            return schema;
        }
    }

}

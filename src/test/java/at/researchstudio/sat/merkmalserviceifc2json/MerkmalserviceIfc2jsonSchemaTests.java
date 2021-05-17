package at.researchstudio.sat.merkmalserviceifc2json;

import at.researchstudio.sat.merkmalserviceifc2json.utils.BaseJsonSchemaValidatorTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MerkmalserviceIfc2jsonSchemaTests extends BaseJsonSchemaValidatorTest {
    @Test
    void loadBooleanAndValidate() throws IOException {
        JsonNode schemaNode = getJsonNodeFromClasspath("schema.json");
        JsonSchema schema = getJsonSchemaFromJsonNodeAutomaticVersion(schemaNode);
        JsonNode node = getJsonNodeFromClasspath("ft_boolean.json");
        Set<ValidationMessage> errors = schema.validate(node);
        assertTrue(errors.size() == 0);
    }

    @Test
    void loadStringAndValidate() throws IOException {
        JsonNode schemaNode = getJsonNodeFromClasspath("schema.json");
        JsonSchema schema = getJsonSchemaFromJsonNodeAutomaticVersion(schemaNode);
        JsonNode node = getJsonNodeFromClasspath("ft_string.json");
        Set<ValidationMessage> errors = schema.validate(node);
        assertTrue(errors.size() == 0);
    }

    @Test
    void loadReferenceAndValidate() throws IOException {
        JsonNode schemaNode = getJsonNodeFromClasspath("schema.json");
        JsonSchema schema = getJsonSchemaFromJsonNodeAutomaticVersion(schemaNode);
        JsonNode node = getJsonNodeFromClasspath("ft_reference.json");
        Set<ValidationMessage> errors = schema.validate(node);
        assertTrue(errors.size() == 0);
    }

    @Test
    void loadNumericAndValidate() throws IOException {
        JsonNode schemaNode = getJsonNodeFromClasspath("schema.json");
        JsonSchema schema = getJsonSchemaFromJsonNodeAutomaticVersion(schemaNode);
        JsonNode node = getJsonNodeFromClasspath("ft_numeric.json");
        Set<ValidationMessage> errors = schema.validate(node);
        assertTrue(errors.size() == 0);
    }

    @Test
    void loadInvalidNumericAndValidate() throws IOException {
        JsonNode schemaNode = getJsonNodeFromClasspath("schema.json");
        JsonSchema schema = getJsonSchemaFromJsonNodeAutomaticVersion(schemaNode);
        JsonNode node = getJsonNodeFromClasspath("ft_numeric_invalid.json");
        Set<ValidationMessage> errors = schema.validate(node);
        assertTrue(errors.size() == 2);
    }

    @Test
    void loadEnumerationAndValidate() throws IOException {
        JsonNode schemaNode = getJsonNodeFromClasspath("schema.json");
        JsonSchema schema = getJsonSchemaFromJsonNodeAutomaticVersion(schemaNode);
        JsonNode node = getJsonNodeFromClasspath("ft_enumeration.json");
        Set<ValidationMessage> errors = schema.validate(node);
        assertTrue(errors.size() == 0);
    }
}

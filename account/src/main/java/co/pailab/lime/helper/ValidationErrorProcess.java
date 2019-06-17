package co.pailab.lime.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

public class ValidationErrorProcess {
    public static JsonNode run(BindingResult bindingResult) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();

            for (ObjectError oe : errors) {
                String str = oe.getCodes()[0];
                String errorField = str.substring(str.lastIndexOf(".") + 1);
                String errorMessage = oe.getDefaultMessage();
                ((ObjectNode) rootNode).put(errorField, errorMessage);
            }

            return rootNode;
        }

        return null;
    }
}

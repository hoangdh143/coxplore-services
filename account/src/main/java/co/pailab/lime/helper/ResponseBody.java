package co.pailab.lime.helper;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResponseBody {
    private ObjectNode rootNode;
    private ObjectMapper mapper;

    public ResponseBody() {
        this.mapper = new ObjectMapper();
        this.rootNode = this.mapper.createObjectNode();
    }

    public void put(String message, int number) {
        rootNode.put(message, number);
    }

    public void put(String message, Object object) {
        JsonNode node = mapper.valueToTree(object);
        this.rootNode.put(message, node);
    }

    public <DO, DTO extends DO> void put(String message, DO object, Function<DO, DTO> fn) {
        DTO objectBasicInfos = fn.apply(object);
        JsonNode node = mapper.valueToTree(objectBasicInfos);
        this.rootNode.put(message, node);
    }

    public <DO, DTO extends DO> void putList(String message, Collection<DO> listOfObject, Function<DO, DTO> fn) {
        List<DTO> objectBasicInfos = listOfObject.stream().map(fn).collect(Collectors.toList());
        JsonNode node = mapper.valueToTree(objectBasicInfos);
        this.rootNode.put(message, node);
    }

    public JsonNode get() {
        return this.rootNode;
    }
}

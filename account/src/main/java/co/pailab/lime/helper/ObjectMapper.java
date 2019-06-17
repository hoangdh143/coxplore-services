package co.pailab.lime.helper;

import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public class ObjectMapper {
    public static void copyProperties(Object src, Object dest) {
        Condition notEmpty = ctx -> ctx.getSource() != "";
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setPropertyCondition(notEmpty)
                .setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.map(src, dest);
    }
}

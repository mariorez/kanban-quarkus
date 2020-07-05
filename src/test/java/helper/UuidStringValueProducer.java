package helper;

import com.github.jsontemplate.jsonbuild.JsonStringNode;
import com.github.jsontemplate.valueproducer.StringValueProducer;

import java.util.UUID;

public class UuidStringValueProducer extends StringValueProducer {

    public static final String TYPE_NAME = "uuid";

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public JsonStringNode produce() {
        return new JsonStringNode(this::produceString);
    }

    public String produceString() {
        return UUID.randomUUID().toString();
    }
}

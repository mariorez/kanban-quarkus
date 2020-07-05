package helper;

import com.github.jsontemplate.jsonbuild.JsonStringNode;
import com.github.jsontemplate.valueproducer.StringValueProducer;

public class BlankStringValueProducer extends StringValueProducer {

    public static final String TYPE_NAME = "blank";

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public JsonStringNode produce() {
        return new JsonStringNode(this::produceString);
    }

    public String produceString() {
        return "   ";
    }
}

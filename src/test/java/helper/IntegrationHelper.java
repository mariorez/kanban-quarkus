package helper;

import org.junit.jupiter.api.Tag;

@Tag("integration")
public abstract class IntegrationHelper extends TestHelper {

    protected static String[] args(String... items) {
        return items;
    }
}

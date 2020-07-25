package helper;

import org.junit.jupiter.api.Tag;

import javax.inject.Inject;

@Tag("integration")
public abstract class IntegrationHelper extends TestHelper {

    @Inject
    protected DataSourceMock dataSource;

    protected static String[] args(String... items) {
        return items;
    }
}

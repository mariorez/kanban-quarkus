package helper;

import io.quarkus.test.junit.QuarkusTestProfile;

import javax.enterprise.inject.Alternative;
import java.util.Collections;
import java.util.Set;

public class ReadProfile implements QuarkusTestProfile {

    @Override
    public Set<Class<?>> getEnabledAlternatives() {
        return Collections.singleton(DataSourceAlternative.class);
    }

    @Alternative
    public static class DataSourceAlternative extends DataSourceMock {
    }
}

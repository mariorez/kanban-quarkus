package org.seariver.kanbanboard.write.adapter;

import io.quarkus.test.Mock;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.enterprise.context.ApplicationScoped;

@Mock
@ApplicationScoped
public class DataSourceMock extends BasicDataSource {

    private static final String DEFAULT_FIXTURE = "dataset";

    public DataSourceMock() {
        configDataSource(DEFAULT_FIXTURE);
    }

    public DataSourceMock(String fixture) {
        configDataSource(fixture);
    }

    private void configDataSource(String fixture) {

        String url = "jdbc:h2:mem:UNIT_TEST;" +
            "MODE=PostgreSQL;" +
            "INIT=RUNSCRIPT FROM 'src/main/resources/db/migration/V001__Initial_setup.sql'\\;" +
            "RUNSCRIPT FROM 'classpath:fixture/" + fixture + ".sql'\\;";

        this.setUrl(url);
        this.setMaxTotal(1);
    }
}

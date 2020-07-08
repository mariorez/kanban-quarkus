package org.seariver.kanbanboard.write.adapter;

import io.quarkus.test.Mock;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.inject.Singleton;

@Mock
@Singleton
public class DataSourceMock extends BasicDataSource {

    public DataSourceMock() {

        String url = "jdbc:h2:mem:DATABASE_TEST;" +
            "MODE=PostgreSQL;" +
            "INIT=RUNSCRIPT FROM 'src/main/resources/db/migration/V001__Initial_setup.sql'\\;" +
            "RUNSCRIPT FROM 'classpath:fixture/dataset.sql'\\;";

        this.setUrl(url);
    }
}

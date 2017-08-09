package logic;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import de.lehmann.lehmannorm.examples.entities.ExampleEntity;
import de.lehmann.lehmannorm.logic.Dao;

public class DaoUnitTest {

    ExampleEntity               entity;
    Dao<ExampleEntity, Integer> unitToTest;

    @Before
    public void createExampleEntity() throws InstantiationException, IllegalAccessException, SQLException {

        entity = new ExampleEntity();
        unitToTest = Dao.createInstance(null, ExampleEntity.class);
    }

    @Test
    public void testUnit() {

        unitToTest.insert(entity);

    }

}

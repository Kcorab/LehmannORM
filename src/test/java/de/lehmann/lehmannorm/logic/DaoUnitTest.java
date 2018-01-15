package de.lehmann.lehmannorm.logic;

import java.sql.SQLException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.lehmann.lehmannorm.AConnectionMockUnitTest;
import de.lehmann.lehmannorm.models.PersonTestEntity;
import de.lehmann.lehmannorm.models.ReligionTestEntity;
import de.lehmann.lehmannorm.models.TierTestEntity;

public class DaoUnitTest extends AConnectionMockUnitTest {

    private TierTestEntity               entity;
    private Dao<TierTestEntity, Integer> unitToTest;

    @Test
    @Disabled
    public void testDaoCaching() throws InstantiationException, IllegalAccessException, SQLException {

        final TierTestEntity tierA = createNestedEntities();
        final TierTestEntity tierB = createNestedEntities();
        unitToTest = Dao.createInstance(connection, TierTestEntity.class);

    }

    private TierTestEntity createNestedEntities() {

        final ReligionTestEntity religion = new ReligionTestEntity();

        final PersonTestEntity person = new PersonTestEntity();
        person.setColumnValue(PersonTestEntity.RELIGION, religion);

        final TierTestEntity tier = new TierTestEntity();
        tier.setColumnValue(TierTestEntity.BESITZER, person);

        return tier;
    }
}

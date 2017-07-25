package de.lehmann.lehmannorm.test;

import org.junit.Test;

import de.lehmann.lehmannorm.examples.entities.ExampleEntity;

/**
 *
 * @author Tim Lehmann
 */
public class InitUnitTest {

    @Test
    public void helloWorld() {

        System.out.println("LehmannORM");

        final ExampleEntity exampleEntity = new ExampleEntity();
        exampleEntity.setColumnValue(ExampleEntity.DESCRIPTION, "Eine Beschreibung.");
    }
}

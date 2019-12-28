package de.micromata.merlin.importer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PojoDiffTest {

    @Test
    void importTest() {
        Person person1 = new Person();
        person1.surname = "Kai";
        person1.name = "Reinhard";
        person1.city = "Kassel";
        person1.number = "1";
        Person person2 = new Person();
        person2.surname = "Jan";
        person2.name = "Reinhard";
        person2.number = "2";
        List<PropertyDelta> deltas = PojoDiff.getPropertyChanges(person1, person2, "number");
        assertEquals(2, deltas.size());
        testDelta(deltas, "surname", "Kai", "Jan");
        testDelta(deltas, "city", "Kassel", null);
    }

    private void testDelta(List<PropertyDelta> deltas, String property, String oldValue, String newValue) {
        for (PropertyDelta delta : deltas) {
            if (property.equals(delta.property)) {
                assertEquals(property, delta.property);
                assertEquals(oldValue, delta.oldValue);
                assertEquals(newValue, delta.newValue);
                return;
            }
        }
    }

    public class Person {
        String surname, name, city, number;

        public String getSurname() {
            return this.surname;
        }

        public String getName() {
            return this.name;
        }

        public String getCity() {
            return this.city;
        }

        public String getNumber() {
            return this.number;
        }
    }
}

package com.cit.models;

import com.cit.UnitTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pl.pojo.tester.api.assertion.Method;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

@Category(UnitTests.class)
public class LocationTest {

    @Test
    public void Should_Pass_All_Pojo_Tests_Using_All_Testers() {
        // given
        final Class<?> classUnderTest = Location.class;


        // then
        assertPojoMethodsFor(classUnderTest).quickly()
                .areWellImplemented();

        // then
        assertPojoMethodsFor(classUnderTest).testing(Method.GETTER, Method.SETTER, Method.TO_STRING)
                .testing(Method.EQUALS)
                .testing(Method.HASH_CODE)
                .testing(Method.CONSTRUCTOR)
                .areWellImplemented();

    }


}
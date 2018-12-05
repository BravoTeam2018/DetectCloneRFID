package com.cit.transfer;

import com.cit.UnitTests;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Test;
import pl.pojo.tester.api.assertion.Method;


import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

@Category(UnitTests.class)
public class ValidationServiceRestResponseDTOTest {


    @Test
    public void Should_Pass_All_Pojo_Tests_Using_All_Testers() {
        // given
        final Class<?> classUnderTest = ValidationServiceRestResponseDTO.class;


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
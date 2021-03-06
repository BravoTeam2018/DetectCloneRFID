package com.cit.transfer;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import com.cit.UnitTests;
import com.cit.services.validation.ValidationRulesResult;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Test;
import pl.pojo.tester.api.assertion.Method;

@Category(UnitTests.class)
public class ValidationRulesResultTest {

    @Test
    public void should_Pass_All_Pojo_Tests_Using_All_Testers() {
        // given
        final Class<?> classUnderTest = ValidationRulesResult.class;


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
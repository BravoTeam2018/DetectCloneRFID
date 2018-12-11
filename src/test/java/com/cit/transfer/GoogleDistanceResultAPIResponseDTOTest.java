package com.cit.transfer;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import com.cit.UnitTests;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Test;
import pl.pojo.tester.api.assertion.Method;

@Category(UnitTests.class)
public class GoogleDistanceResultAPIResponseDTOTest {

    @Test
    public void Should_Pass_All_Pojo_Tests_Using_All_Testers_GoogleDistanceAPIResponseDTO() {
        // given
        final Class<?> classUnderTest = GoogleDistanceAPIResponseDTO.class;


        // then
        assertPojoMethodsFor(classUnderTest).quickly()
                .areWellImplemented();

        // then
        assertPojoMethodsFor(classUnderTest).testing(Method.GETTER, Method.SETTER, Method.TO_STRING)
                .testing(Method.EQUALS)
                .testing(Method.HASH_CODE)
                .testing(Method.CONSTRUCTOR)
                .testing(Method.CONSTRUCTOR)
                .areWellImplemented();

    }

    @Test
    public void Should_Pass_All_Pojo_Tests_Using_All_Testers_Rows() {
        // given
        final Class<?> classUnderTest = GoogleDistanceAPIResponseDTO.Rows.class;

        // when

        // then
        assertPojoMethodsFor(classUnderTest).quickly()
                .areWellImplemented();
    }

    @Test
    public void Should_Pass_All_Pojo_Tests_Using_All_Testers_Rows_Elements() {
        // given
        final Class<?> classUnderTest = GoogleDistanceAPIResponseDTO.Rows.Elements.class;

        // when

        // then
        assertPojoMethodsFor(classUnderTest).quickly()
                .areWellImplemented();
    }

    @Test
    public void Should_Pass_All_Pojo_Tests_Using_All_Testers_Rows_Elements_Distance() {
        // given
        final Class<?> classUnderTest = GoogleDistanceAPIResponseDTO.Rows.Elements.Distance.class;

        // when

        // then
        assertPojoMethodsFor(classUnderTest).quickly()
                .areWellImplemented();
    }

    @Test
    public void Should_Pass_All_Pojo_Tests_Using_All_Testers_Rows_Elements_Duration() {
        // given
        final Class<?> classUnderTest = GoogleDistanceAPIResponseDTO.Rows.Elements.Duration.class;

        // when

        // then
        assertPojoMethodsFor(classUnderTest).quickly()
                .areWellImplemented();
    }


}

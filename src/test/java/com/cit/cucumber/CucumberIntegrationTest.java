package com.cit.cucumber;


import com.cit.CucumberTests;
import com.cit.UnitTests;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@Category(CucumberTests.class)
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/cucumber")
public class CucumberIntegrationTest extends SpringIntegrationTest{
}
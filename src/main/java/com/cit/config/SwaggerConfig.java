package com.cit.config;

import com.cit.controllers.SwaggerController;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Contact;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    SwaggerController swaggerController() { return new SwaggerController(); }

    @Bean
    public Docket api() {


        Class[] ignoreTheseModels = {};

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .ignoredParameterTypes(ignoreTheseModels)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("/api/.*"))
                .build();
    }


    /**
     * Describe the API
     * @return ApiInfo
     */
    private ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                .title("Clone access card Validation REST API ")
                .description(" This validation API detects use of cloned RFID access cards and notifies about the event in real-time.")
                .version("1.0")
                .termsOfServiceUrl("Terms of service")
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .contact(new Contact("Team Bravo - John Nolan, Eamon Foy", "https://github.com/BravoTeam2018/DetectCloneRFID", ""))
                .build();
    }



}

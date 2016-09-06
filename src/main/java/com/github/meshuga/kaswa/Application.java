package com.github.meshuga.kaswa;

import com.fasterxml.classmate.TypeResolver;
import io.katharsis.spring.boot.KatharsisConfigV2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.Collections;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

@SpringBootApplication
@EnableSwagger2
@Import(KatharsisConfigV2.class)
public class Application {

    @Bean
    public CommandLineRunner commandLineRunner(DocumentationCache documentationCache) {
        return args -> {
            Documentation documentation = documentationCache.documentationByGroup("default");

            Operation operation = new Operation(
                    HttpMethod.GET,
                    "Get tasks",
                    "",
                    new ModelRef("java.lang.String"),
                    null,
                    1,
                    Collections.emptySet(),
                    Collections.singleton("application/vnd.api+json"),
                    Collections.singleton("application/vnd.api+json"),
                    Collections.singleton("HTTP"),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptySet(),
                    "",
                    false,
                    Collections.emptyList()
            );

            ApiDescription apiDescription = new ApiDescription("/api/tasks",
                    "Tasks operations",
                    Collections.singletonList(operation),
                    false);

            Tag tag = new Tag("tasks-repository", "tasks repository endpoint");
            ApiListing tasksListing = new ApiListing(
                    DocumentationType.SWAGGER_2.getVersion(),
                    "/api",
                    "/tasks",
                    Collections.singleton("application/vnd.api+json"),
                    Collections.singleton("application/vnd.api+json"),
                    "localhost",
                    Collections.singleton("HTTP"),
                    Collections.emptyList(),
                    Collections.singletonList(apiDescription),
                    Collections.emptyMap(),
                    "Tasks endpoints",
                    123,
                    Collections.singleton(tag)
            );
            documentation.getApiListings().put("tasks-repository", tasksListing);
            documentation.getTags().add(tag);
        };
    }

    @Bean
    public Docket petApi(TypeResolver typeResolver) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/")
                .directModelSubstitute(LocalDate.class,
                        String.class)
                .genericModelSubstitutes(ResponseEntity.class)
                .alternateTypeRules(
                        newRule(typeResolver.resolve(DeferredResult.class,
                                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                                typeResolver.resolve(WildcardType.class)))
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET,
                        newArrayList(new ResponseMessageBuilder()
                                .code(500)
                                .message("500 message")
                                .responseModel(new ModelRef("Error"))
                                .build()))
                .enableUrlTemplating(true)
                .globalOperationParameters(
                        newArrayList(new ParameterBuilder()
                                .name("someGlobalParameter")
                                .description("Description of someGlobalParameter")
                                .modelRef(new ModelRef("string"))
                                .parameterType("query")
                                .required(true)
                                .build()))
                .tags(new Tag("Pet Service", "All apis relating to pets"));
    }

    @Bean
    UiConfiguration uiConfig() {
        return new UiConfiguration(
                "validatorUrl",// url
                "none",       // docExpansion          => none | list
                "alpha",      // apiSorter             => alpha
                "schema",     // defaultModelRendering => schema
                UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS,
                false,        // enableJsonEditor      => true | false
                true);        // showRequestHeaders    => true | false
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
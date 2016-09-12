package com.github.meshuga.kaswa;

import com.fasterxml.classmate.GenericType;
import com.fasterxml.classmate.TypeResolver;
import com.github.meshuga.kaswa.jsonapi.JsonApiResource;
import com.github.meshuga.kaswa.jsonapi.ManyTopJsonApi;
import com.github.meshuga.kaswa.jsonapi.SingleTopJsonApi;
import com.github.meshuga.kaswa.model.Task;
import com.google.common.base.Optional;
import io.katharsis.resource.registry.RegistryEntry;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.spring.boot.KatharsisConfigV2;
import io.katharsis.spring.boot.KatharsisSpringBootProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

@SpringBootApplication
@EnableSwagger2
@Import(KatharsisConfigV2.class)
@RestController
@EnableConfigurationProperties(KatharsisSpringBootProperties.class)
public class Application {

    @Autowired
    private KatharsisSpringBootProperties properties;

    @RequestMapping(value = "/greeting/{id}", method = RequestMethod.GET)
    public List<Task> greeting(@PathVariable("id") long id) {
        return null;
    }

    @RequestMapping(value = "/greeting", method = RequestMethod.GET)
    public List<Task> greeting2() {
        return null;
    }

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public CommandLineRunner commandLineRunner(@Value("${katharsis.pathPrefix}") String pathPrefix,
                                               ResourceRegistry resourceRegistry,
                                               DocumentationCache documentationCache) {
        return args -> {
            Documentation documentation = documentationCache.documentationByGroup("default");

            for (Map.Entry<Class, RegistryEntry> resourceEntry: resourceRegistry.getResources().entrySet()) {
                Class<?> resourceClass = resourceEntry.getValue().getResourceInformation().getResourceClass();
                String type = resourceEntry.getValue().getResourceInformation().getResourceType();
                addResource(pathPrefix, documentation, resourceClass, type);
            }
        };
    }

    private void addResource(@Value("${katharsis.pathPrefix}") String pathPrefix, Documentation documentation, Class<?> clazz, String type) {
        ApiDescription manyDescription = new ApiDescription(pathPrefix + "/" + type,
                clazz.getSimpleName() + " operations",
                Arrays.asList(buildGetManyOperation(clazz.getSimpleName(), type),
                        buildPostSingleOperation(clazz, type)),
                false);

        ApiDescription singleDescription = new ApiDescription(pathPrefix + "/" + type + "/{id}",
                clazz.getSimpleName() + " operations",
                Arrays.asList(buildGetSingleOperation(clazz.getSimpleName(), type),
                        buildDeleteSingleOperation(clazz.getSimpleName(), type),
                        buildPatchSingleOperation(clazz, type)),
                false);

        Tag tag = new Tag(type + "-repository", type + " repository endpoint");
        ApiListing listing = new ApiListing(
                DocumentationType.SWAGGER_2.getVersion(),
                pathPrefix,
                "/" + type,
                Collections.singleton("application/vnd.api+json"),
                Collections.singleton("application/vnd.api+json"),
                "localhost",
                Collections.singleton("HTTP"),
                Collections.emptyList(),
                Arrays.asList(singleDescription, manyDescription),
                Collections.emptyMap(),
                type + " endpoints",
                0,
                Collections.singleton(tag)
        );
        documentation.getApiListings().put("tasks-repository", listing);
        documentation.getTags().add(tag);
    }

    private Operation buildGetSingleOperation(String className, String type) {
        ModelRef model = new ModelRef("SingleTopJsonApi«JsonApiResource«" + className + "»»");
        ResponseMessage responseMessage = buildResponseMessage(className, model);
        return buildOperation(type, responseMessage, HttpMethod.GET, model, getParameter());
    }

    private Operation buildDeleteSingleOperation(String className, String type) {
        ResponseMessage responseMessage = buildResponseMessage(className, null);
        return buildOperation(type, responseMessage, HttpMethod.DELETE, null, getParameter());
    }

    private Parameter getParameter() {
        return new Parameter("id", "id", null, true, false, new ModelRef("String"),
                Optional.of(typeResolver.resolve(String.class)), null, "path", null);
    }

    private Operation buildGetManyOperation(String className, String type) {
        ModelRef model = new ModelRef("ManyTopJsonApi«JsonApiResource«" + className + "»»");
        ResponseMessage responseMessage = buildResponseMessage(className, model);
        return buildOperation(type, responseMessage, HttpMethod.GET, model, null);
    }

    private Operation buildPostSingleOperation(Class<?> clazz, String type) {
        String className = clazz.getSimpleName();
        ModelRef model = new ModelRef("SingleTopJsonApi«JsonApiResource«" + className + "»»");
        ResponseMessage responseMessage = buildResponseMessage(className, model);
        Parameter parameter = new Parameter(className.toLowerCase(), className.toLowerCase(), null, false, false, model,
                Optional.of(typeResolver.resolve(clazz)), null, "body", null);
        return buildOperation(type, responseMessage, HttpMethod.POST, model, parameter);
    }

    private Operation buildPatchSingleOperation(Class<?> clazz, String type) {
        String className = clazz.getSimpleName();
        ModelRef model = new ModelRef("SingleTopJsonApi«JsonApiResource«" + className + "»»");
        ResponseMessage responseMessage = buildResponseMessage(className, model);
        Parameter parameter = new Parameter(className.toLowerCase(), className.toLowerCase(), null, false, false, model,
                Optional.of(typeResolver.resolve(clazz)), null, "body", null);
        return buildOperation(type, responseMessage, HttpMethod.PATCH, model, parameter);
    }

    private Operation buildOperation(String type, ResponseMessage responseMessage, HttpMethod method, ModelRef model,
                                     Parameter parameter) {
        return new Operation(
                method,
                method + " " + type,
                "",
                model,
                null,
                1,
                Collections.emptySet(),
                Collections.singleton("application/vnd.api+json"),
                Collections.singleton("application/vnd.api+json"),
                Collections.singleton("HTTP"),
                Collections.emptyList(),
                parameter == null ? Collections.emptyList() : Collections.singletonList(parameter),
                Collections.singleton(responseMessage),
                "",
                false,
                Collections.emptyList()
        );
    }

    private ResponseMessage buildResponseMessage(String className, ModelRef model) {
        return new ResponseMessage(200, "OK", model, Collections.emptyMap());
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
                .enableUrlTemplating(true)
                .tags(new Tag("Pet Service", "All apis relating to pets"))
                .additionalModels(typeResolver.resolve(new GenericType<SingleTopJsonApi<JsonApiResource<Task>>>() { }))
                .additionalModels(typeResolver.resolve(new GenericType<ManyTopJsonApi<JsonApiResource<Task>>>() { }));
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
package com.visable.exercise.messagingservice.config;

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@EnableSwagger2
@Configuration
@Import(value = [BeanValidatorPluginsConfiguration::class])
class SwaggerConfiguration {

    @Bean
    fun apiDocket(): Docket = Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build()

    fun apiInfo(): ApiInfo = ApiInfoBuilder().title("Messaging service backend")
        .description("Backend for messaging application")
        .termsOfServiceUrl("www,kvganesh.com")
        .contact(Contact("Ganesh KV", "www.kvganesh.com", "ganeshsreedhar92@gmail.com")).license("MIT licence")
        .version("1.0").build()
}
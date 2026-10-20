package com.utn.fintech.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fintechOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Fintech")
                        .description("API REST para la gestión de usuarios y cuentas bancarias " +
                                "(caja de ahorro y cuenta corriente)." +
                                "Desarrollo de Software FinTech - UTN BA.")
                        .version("1.0.0")
                        .contact(new Contact().name("María Pérez Schmit")));
    }
}
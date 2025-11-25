package com.mch.unicoursehub.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Mobin Cheshmberah",
                        url = "",
                        email = "mobinch142@gmail.com"
                ),
                description = "Uni-Course-Hub api documentation",
                version = "1.0",
                title = "Uni-Course-Hub api documentation"
        ),
        servers = {
                @Server(url = "")
        },
        security = {

        }
)
@SecurityScheme(
        name = "Uni-Course-Hub-AUTH",
        description = "",
        scheme = "bearer",
        type = SecuritySchemeType.APIKEY,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenAPIConfig {

}

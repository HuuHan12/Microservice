package com.ltfullstack.employeeservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Employee Api Specification - LT Fullstack",
                description = "Api documentation for Employee Service",
                version = "1.0",
                contact = @Contact(
                        name = "Huu Han",
                        email = "dnhan.a7.c3tqcap@gmail.com",
                        url = "https://www.linkedin.com/in/%C4%91o%C3%A0n-h%E1%BB%AFu-h%C3%A0n-b44785336/"
                ),
                license = @License(
                        name = "MIT license",
                        url = "https://www.linkedin.com/in/%C4%91o%C3%A0n-h%E1%BB%AFu-h%C3%A0n-b44785336/licenses"
                ),
                termsOfService = "https://www.linkedin.com/in/%C4%91o%C3%A0n-h%E1%BB%AFu-h%C3%A0n-b44785336/terms"

        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:9002"
                ),
                @Server(
                        description = "Dev ENV",
                        url = "https://employee-service.dev.com"
                ),
                @Server(
                        description = "Prod ENV",
                        url = "https://employee-service.prod.com"
                ),
        }

)
public class OpenApiConfig {
}

package bankcards.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityRequirement;

/**
 * <p>
 * Конфигурация OpenAPI / Swagger для проекта Bank Cards API.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Настройка информации о API: название, версия, описание, контактные данные</li>
 *     <li>Добавление схемы безопасности JWT (bearer token)</li>
 *     <li>Обеспечение интеграции с Swagger UI для документирования API</li>
 * </ul>
 */
@Configuration
public class OpenApiConfig {

    /**
     * Создаёт и настраивает объект {@link OpenAPI} для Swagger.
     *
     * @return объект {@link OpenAPI} с информацией о приложении и схемой безопасности
     */
    @Bean
    public OpenAPI bankCardsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Cards API")
                        .description("Backend API для управления банковскими картами")
                        .version("1.0")
                        .contact(new Contact().name("Your Name").email("your.email@example.com"))
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
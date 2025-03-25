package com.banking.multi_threading.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI openAPI() {
                return new OpenAPI()
                        .info(new Info()
                                .title("Banking API")
                                .description("멀티스레딩 환경에서 계좌 입출금 동작을 테스트하는 API")
                                .version("1.0"));
        }
}

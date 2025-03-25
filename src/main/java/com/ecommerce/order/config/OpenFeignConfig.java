package com.ecommerce.order.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("com.ecommerce.order.external.*.client")
public class OpenFeignConfig {
}

package com.example.OEP.Config; // Adjust this to match your folder structure

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // This tells Spring: "If a URL starts with /images/,
        // look for the actual file in the 'uploads' folder on my computer."
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:uploads/");
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://192.168.0.111:3000", // Other devices on network
                        "http://localhost:3000",      // Your local development
                        "http://127.0.0.1:3000"       // Alternative local loopback
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
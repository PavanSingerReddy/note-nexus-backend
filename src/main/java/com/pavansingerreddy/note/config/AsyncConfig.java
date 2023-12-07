package com.pavansingerreddy.note.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

// we are defining a configuration annotation which indicates that we are configuring something and This annotation indicates that the class can be used by the Spring IoC container as a source of bean definitions.
@Configuration
// This annotation is used to enable Spring’s asynchronous method execution
// capability.We are enabling asynchronous execution of spring because it can be
// used in event listeners
@EnableAsync
// This is the declaration of the configuration class. You can add more
// configuration details inside this class if needed.
public class AsyncConfig {

}

package ru.yandex.practicum.filmorate.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MainConfig {

    @Bean
    open fun logger() : Logger {
        return LoggerFactory.getLogger(MainConfig::class.java)
    }
}
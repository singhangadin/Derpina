package com.github.angads25.derpinaserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
class DerpinaServerApplication : SpringBootServletInitializer() {

    fun main(args: Array<String>) {
        runApplication<DerpinaServerApplication>(*args)
    }

    override fun configure(builder: SpringApplicationBuilder?): SpringApplicationBuilder {
        return builder!!.sources(DerpinaServerApplication::class.java)
    }
}
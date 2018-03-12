package com.lx.barragereptile.config;

import org.hyperic.sigar.Sigar;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class SigarConfig {

    @Bean
    public Sigar sigar() {
        Sigar sigar = new Sigar();
        return sigar;
    }
}

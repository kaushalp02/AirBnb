package com.kaushal.projects.airBnbApp.configurations;

import com.kaushal.projects.airBnbApp.auditing.AuditorAwareImpl;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "AuditorAwareImpl")
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class Config {

    @Bean
    public ModelMapper getModelMapper(){
        return new ModelMapper();

    }

    @Bean(name = "AuditorAwareImpl")
    public AuditorAware<String> getAuditorAwareImpl(){
        return new AuditorAwareImpl();
    }

}

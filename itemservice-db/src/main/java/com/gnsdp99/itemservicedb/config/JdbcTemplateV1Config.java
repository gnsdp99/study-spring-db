package com.gnsdp99.itemservicedb.config;

import com.gnsdp99.itemservicedb.repository.ItemRepository;
import com.gnsdp99.itemservicedb.repository.jdbctemplate.JdbcTemplateItemRepositoryV1;
import com.gnsdp99.itemservicedb.service.ItemService;
import com.gnsdp99.itemservicedb.service.ItemServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JdbcTemplateV1Config {

    private final DataSource dataSource;

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new JdbcTemplateItemRepositoryV1(dataSource);
    }
}

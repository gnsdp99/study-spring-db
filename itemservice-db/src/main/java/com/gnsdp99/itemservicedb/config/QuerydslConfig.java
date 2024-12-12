package com.gnsdp99.itemservicedb.config;

import com.gnsdp99.itemservicedb.repository.ItemRepository;
import com.gnsdp99.itemservicedb.repository.jpa.JpaItemRepositoryV3;
import com.gnsdp99.itemservicedb.service.ItemService;
import com.gnsdp99.itemservicedb.service.ItemServiceV1;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class QuerydslConfig {

    private final EntityManager entityManager;

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new JpaItemRepositoryV3(entityManager);
    }
}

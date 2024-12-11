package com.gnsdp99.itemservicedb.config;

import com.gnsdp99.itemservicedb.repository.ItemRepository;
import com.gnsdp99.itemservicedb.repository.jdbctemplate.JdbcTemplateItemRepositoryV3;
import com.gnsdp99.itemservicedb.repository.mybatis.ItemMapper;
import com.gnsdp99.itemservicedb.repository.mybatis.MyBatisItemRepository;
import com.gnsdp99.itemservicedb.service.ItemService;
import com.gnsdp99.itemservicedb.service.ItemServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class MyBatisConfig {

    private final ItemMapper itemMapper;

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new MyBatisItemRepository(itemMapper);
    }
}

package com.gnsdp99.itemservicedb;

import com.gnsdp99.itemservicedb.config.V2Config;
import com.gnsdp99.itemservicedb.repository.ItemRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Import(V2Config.class)
@SpringBootApplication(
        scanBasePackages = "com.gnsdp99.itemservicedb.web"
)
public class ItemserviceDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemserviceDbApplication.class, args);
    }

    @Bean
    @Profile("local")
    public TestDataInit testDataInit(ItemRepository itemRepository) {
        return new TestDataInit(itemRepository);
    }
}

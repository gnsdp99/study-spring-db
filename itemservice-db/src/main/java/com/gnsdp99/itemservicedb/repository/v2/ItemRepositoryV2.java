package com.gnsdp99.itemservicedb.repository.v2;

import com.gnsdp99.itemservicedb.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepositoryV2 extends JpaRepository<Item, Long> {
}

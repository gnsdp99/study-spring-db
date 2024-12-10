package com.gnsdp99.itemservicedb.repository;

import com.gnsdp99.itemservicedb.domain.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    void update(Long itemId, ItemUpdateDto updateParam);

    Optional<Item> findById(Long id);

    List<Item> findAll(ItemSearchCondDto cond);

}

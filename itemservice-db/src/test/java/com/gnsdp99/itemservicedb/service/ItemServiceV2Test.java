package com.gnsdp99.itemservicedb.service;

import com.gnsdp99.itemservicedb.domain.Item;
import com.gnsdp99.itemservicedb.repository.ItemSearchCondDto;
import com.gnsdp99.itemservicedb.repository.ItemUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class ItemServiceV2Test {

    @Autowired
    ItemService itemService;

    @Test
    void save() {
        Item itemA = new Item("itemA", 15000, 10);
        Item saveItem = itemService.save(itemA);

        Item findItem = itemService.findById(itemA.getId())
                .orElseThrow(() -> new NoSuchElementException("Item not found with id=" + itemA.getId()));

        assertThat(saveItem).isEqualTo(findItem);
    }

    @Test
    void update() {
        Item itemA = new Item("itemA", 15000, 10);
        itemService.save(itemA);

        ItemUpdateDto updateParam = new ItemUpdateDto("itemB", 30000, 30);
        itemService.update(itemA.getId(), updateParam);

        Item findItem = itemService.findById(itemA.getId())
                .orElseThrow(() -> new NoSuchElementException("Item not found with id=" + itemA.getId()));

        assertThat(findItem.getItemName()).isEqualTo(updateParam.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateParam.getQuantity());
    }

    @Test
    void findItems() {
        Item itemA = new Item("itemA", 15000, 10);
        Item itemB = new Item("itemB", 20000, 20);
        Item itemC = new Item("itemC", 30000, 30);
        itemService.save(itemA);
        itemService.save(itemB);
        itemService.save(itemC);

        testFindItems("tem", 25000, itemA, itemB);
        testFindItems(null, 15000, itemA);
        testFindItems(null, null, itemA, itemB, itemC);
    }

    void testFindItems(String itemName, Integer maxPrice, Item ...items) {
        List<Item> result = itemService.findItems(new ItemSearchCondDto(itemName, maxPrice));
        assertThat(result).containsExactly(items);
    }
}
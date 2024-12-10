package com.gnsdp99.itemservicedb.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateDto {

    private String itemName;
    private Integer price;
    private Integer quantity;
}

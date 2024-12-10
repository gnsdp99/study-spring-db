package com.gnsdp99.itemservicedb.repository.jdbctemplate;

import com.gnsdp99.itemservicedb.domain.Item;
import com.gnsdp99.itemservicedb.repository.ItemRepository;
import com.gnsdp99.itemservicedb.repository.ItemSearchCondDto;
import com.gnsdp99.itemservicedb.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * NamedParameterJdbcTemplate 적용
 *
 * 파라미터 바인딩 방법
 * - Map
 * - SqlParameterSource
 *  - BeanPropertySqlParameterSource
 *  - MapSqlParameterSource
 */
@Slf4j
public class JdbcTemplateItemRepositoryV2 implements ItemRepository {

    private final NamedParameterJdbcTemplate template;

    public JdbcTemplateItemRepositoryV2(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        String sql = """
                insert into item(item_name, price, quantity)
                values (:itemName, :price, :quantity)
                """;

        SqlParameterSource param = new BeanPropertySqlParameterSource(item);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, param, keyHolder);

        item.setId(keyHolder.getKey().longValue());

        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = """
                update item set
                item_name = :itemName, price = :price, quantity = :quantity
                where id = :itemId
                """;

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("itemName", updateParam.getItemName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("itemId", itemId);

        template.update(sql, param);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = """
                select * from item
                where id = :id
                """;

        Map<String, Object> param = Map.of("id", id);

        Item item = template.queryForObject(sql, param, itemRowMapper());
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> findAll(ItemSearchCondDto cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        String sql = """
                select * from item
                """;

        SqlParameterSource param = new BeanPropertySqlParameterSource(cond);

        // 동적 쿼리
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }

        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%', :itemName, '%')";
            andFlag = true;
        }

        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= :maxPrice";
        }

        return template.query(sql, param, itemRowMapper());
    }

    private RowMapper<Item> itemRowMapper() {
        return BeanPropertyRowMapper.newInstance(Item.class);
    }
}

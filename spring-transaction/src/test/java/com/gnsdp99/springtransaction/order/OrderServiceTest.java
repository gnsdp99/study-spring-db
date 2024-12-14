package com.gnsdp99.springtransaction.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void complete() throws NotEnoughMoneyException {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.ACCEPTED);

        orderService.order(order);

        Order findOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(findOrder.getPayStatus()).isEqualTo(PayStatus.COMPLETED);
    }

    @Test
    void runtimeException() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.EXCEPTION);

        assertThrows(RuntimeException.class, () -> orderService.order(order));

        assertThrows(NoSuchElementException.class, () -> orderRepository.findById(order.getId()).orElseThrow());
    }

    @Test
    void bizException() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.NOT_ENOUGH_BALANCE);

        try {
            orderService.order(order);
        } catch (NotEnoughMoneyException e) {
            log.info("고객에게 잔고 부족을 알리고 별도의 계좌로 입금하도록 안내");
        }

        Order findOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(findOrder.getPayStatus()).isEqualTo(PayStatus.WAINTING);
    }
}
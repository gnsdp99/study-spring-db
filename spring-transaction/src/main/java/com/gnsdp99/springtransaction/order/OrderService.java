package com.gnsdp99.springtransaction.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public void order(Order order) throws NotEnoughMoneyException {
        log.info("call order");
        orderRepository.save(order);

        log.info("결제 프로세스 시작");

        if (order.getOrderStatus().equals(OrderStatus.EXCEPTION)) {
            log.info("시스템 예외 발생");
            throw new RuntimeException("시스템 예외");
        }

        if (order.getOrderStatus().equals(OrderStatus.NOT_ENOUGH_BALANCE)) {
            log.info("비즈니스 예외 발생");
            order.setPayStatus(PayStatus.WAINTING);
            throw new NotEnoughMoneyException("잔고부족");
        }

        if (order.getOrderStatus().equals(OrderStatus.ACCEPTED)) {
            log.info("정상 승인");
            order.setPayStatus(PayStatus.COMPLETED);
        }

        log.info("결제 프로세스 종료");
    }
}

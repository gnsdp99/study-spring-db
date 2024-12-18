package com.gnsdp99.springtransaction.propagation;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MemberRepository {

    private final EntityManager entityManager;

    @Transactional
    public void save(Member member) {
        log.info("회원 저장");
        entityManager.persist(member);
    }

    public Optional<Member> findByUsername(String username) {
        return entityManager.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", username)
                .getResultList().stream().findAny();
    }
}

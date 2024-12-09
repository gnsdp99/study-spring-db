package com.gnsdp99.jdbc.repository;

import com.gnsdp99.jdbc.domain.Member;

public interface MemberRepository {

    Member save(Member member);

    Member findById(String memberId);

    void update(String memberId, int money);

    void delete(String memberId);
}

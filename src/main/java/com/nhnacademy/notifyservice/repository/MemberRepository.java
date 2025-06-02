package com.nhnacademy.notifyservice.repository;

import com.nhnacademy.notifyservice.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMbEmail(String mbEmail);

    // Role의 roleName이 "ROLE_ADMIN"인 멤버 전체 조회
    List<Member> findByRole_RoleName(String roleName);
}

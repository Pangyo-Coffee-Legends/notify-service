package com.nhnacademy.notifyservice.repository;

import com.nhnacademy.notifyservice.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 회원(Member) 엔티티에 대한 데이터 접근을 담당하는 JPA 리포지토리 인터페이스입니다.
 *
 * 이 리포지토리는 실시간 알림 서비스에서 사용자 정보를 관리하고 조회하는 기능을 제공합니다.
 * Spring Data JPA의 기본 CRUD 기능 외에도 이메일 기반 사용자 조회와
 * 역할(Role) 기반 사용자 필터링 기능을 지원합니다[1].
 *
 * <p>주요 사용 사례:</p>
 * <ul>
 * <li>사용자 인증 및 권한 확인</li>
 * <li>알림 대상 사용자 조회</li>
 * <li>역할 기반 알림 전송을 위한 사용자 그룹 조회</li>
 * <li>사용자별 알림 설정 관리</li>
 * </ul>
 *
 * @see Member
 * @see JpaRepository
 * @see org.springframework.data.jpa.repository.Query
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
    /**
     * 이메일 주소를 기반으로 회원 정보를 조회합니다.
     *
     * 실시간 알림 시스템에서 사용자 식별의 핵심 메서드로 사용됩니다.
     * HTTP 헤더의 'X-USER'로 전달받은 이메일을 통해 해당 사용자의 상세 정보를 조회하여
     * 알림 전송, 권한 확인, 세션 관리 등의 작업에 활용됩니다[1].
     *
     * <p>사용 예시:</p>
     * <pre>{@code
     * // NotificationServiceImpl에서 사용자 조회
     * Member member = memberRepository.findByMbEmail(userEmail)
     *     .orElseThrow(() -> new EntityNotFoundException("member cannot be found."));
     * }</pre>
     *
     * @param mbEmail 조회할 회원의 이메일 주소 (고유 식별자)
     * @return Optional&lt;Member&gt; 해당 이메일을 가진 회원 정보, 존재하지 않으면 빈 Optional
     * @throws IllegalArgumentException mbEmail이 null인 경우
     */
    Optional<Member> findByMbEmail(String mbEmail);

    /**
     * 특정 역할(Role)을 가진 모든 회원을 조회합니다.
     *
     * 역할 기반 알림 전송 시스템에서 특정 권한을 가진 사용자 그룹에게
     * 일괄적으로 알림을 전송하기 위해 사용됩니다.
     * 예를 들어, 관리자에게만 시스템 알림을 보내거나,
     * 특정 부서의 사용자들에게 공지사항을 전달할 때 활용됩니다[1].
     *
     * <p>주요 사용 사례:</p>
     * <ul>
     * <li><strong>관리자 알림:</strong> "ROLE_ADMIN" 역할을 가진 모든 사용자에게 시스템 알림 전송</li>
     * <li><strong>부서별 알림:</strong> "ROLE_MANAGER" 역할을 가진 관리자들에게 업무 관련 알림 전송</li>
     * <li><strong>권한별 필터링:</strong> 특정 기능에 접근 권한이 있는 사용자들만 조회</li>
     * </ul>
     *
     * <p>사용 예시:</p>
     * <pre>{@code
     * // 모든 관리자에게 시스템 알림 전송
     * List<Member> admins = memberRepository.findByRole_RoleName("ROLE_ADMIN");
     * for (Member admin : admins) {
     *     notificationService.saveNotificationMessage(admin, admin.getRole(), "시스템 점검 안내");
     * }
     * }</pre>
     *
     * @param roleName 조회할 역할명 (예: "ROLE_ADMIN", "ROLE_USER", "ROLE_MANAGER")
     * @return List&lt;Member&gt; 해당 역할을 가진 회원 목록, 해당하는 회원이 없으면 빈 리스트
     * @throws IllegalArgumentException roleName이 null인 경우
     */
    List<Member> findByRole_RoleName(String roleName);
}

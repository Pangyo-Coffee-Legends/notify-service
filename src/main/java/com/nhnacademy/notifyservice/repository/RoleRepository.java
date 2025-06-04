package com.nhnacademy.notifyservice.repository;

import com.nhnacademy.notifyservice.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 역할(Role) 엔티티에 대한 데이터 접근을 담당하는 JPA 리포지토리 인터페이스입니다.
 *
 * 이 리포지토리는 실시간 알림 시스템에서 사용자 권한 관리와 역할 기반 알림 전송을 위한
 * 역할 정보 조회 기능을 제공합니다[1]. Spring Data JPA의 기본 CRUD 기능과 함께
 * 역할명을 통한 특화된 조회 기능을 지원합니다.
 *
 * <p>주요 사용 사례:</p>
 * <ul>
 * <li>사용자 권한 확인 및 인증</li>
 * <li>역할 기반 알림 전송 대상 결정</li>
 * <li>관리자 권한 검증</li>
 * <li>역할별 알림 필터링</li>
 * </ul>
 *
 * <p>알림 시스템에서의 역할:</p>
 * <ul>
 * <li><strong>권한 기반 알림:</strong> 특정 역할을 가진 사용자들에게만 알림 전송</li>
 * <li><strong>관리자 알림:</strong> 시스템 관련 중요 알림을 관리자에게만 전달</li>
 * <li><strong>부서별 알림:</strong> 역할에 따른 업무 관련 알림 분류</li>
 * </ul>
 *
 * @author NHN Academy
 * @version 1.0
 * @since 1.0
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * 역할명을 기반으로 역할 정보를 조회합니다.
     *
     * 실시간 알림 시스템에서 사용자의 권한을 확인하거나 특정 역할을 가진 사용자들에게
     * 타겟팅된 알림을 전송하기 위해 사용됩니다[1]. 이 메서드는 역할 기반 접근 제어(RBAC)의
     * 핵심 구성 요소로서 시스템의 보안과 알림 전송 로직에 중요한 역할을 담당합니다.
     *
     * <p>일반적인 역할명 예시:</p>
     * <ul>
     * <li><strong>ROLE_ADMIN:</strong> 시스템 관리자 권한</li>
     * <li><strong>ROLE_USER:</strong> 일반 사용자 권한</li>
     * <li><strong>ROLE_MANAGER:</strong> 부서 관리자 권한</li>
     * <li><strong>ROLE_MODERATOR:</strong> 중재자 권한</li>
     * </ul>
     *
     * <p>사용 예시:</p>
     * <pre>{@code
     * // NotificationServiceImpl에서 관리자 역할 조회
     * Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
     *     .orElseThrow(() -> new EntityNotFoundException("role cannot be found."));
     *
     * // 역할 기반 알림 전송
     * List<Member> admins = memberRepository.findByRole_RoleName("ROLE_ADMIN");
     * for (Member admin : admins) {
     *     notificationService.saveNotificationMessage(admin, adminRole, "시스템 점검 안내");
     * }
     * }</pre>
     *
     * <p>알림 시스템에서의 활용:</p>
     * <ul>
     * <li><strong>권한 검증:</strong> 사용자의 역할을 확인하여 알림 수신 권한 결정</li>
     * <li><strong>알림 분류:</strong> 역할별로 다른 유형의 알림 전송</li>
     * <li><strong>보안 강화:</strong> 민감한 정보는 특정 역할에게만 알림 전송</li>
     * <li><strong>업무 효율성:</strong> 역할에 맞는 맞춤형 알림 제공</li>
     * </ul>
     *
     * @param RoleName 조회할 역할의 이름 (예: "ROLE_ADMIN", "ROLE_USER")
     * @return Optional&lt;Role&gt; 해당 이름을 가진 역할 정보, 존재하지 않으면 빈 Optional
     * @throws IllegalArgumentException RoleName이 null인 경우
     */
    Optional<Role> findByRoleName(String RoleName);
}

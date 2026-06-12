package com.guide.portail.repository;

import com.guide.portail.entity.AdminActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long> {
    List<AdminActionLog> findAllByOrderByDateHeureDesc();
}

package com.tcc.gerenciador_projetos_tcc.repository;

import com.tcc.gerenciador_projetos_tcc.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByGroupId(Long groupId);

    List<Message> findByGroupIdOrderByTimestampAsc(Long groupId);

    @Modifying
    @Transactional
    void deleteByGroupId(Long groupId);
}

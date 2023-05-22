package com.example.natifyka.repository;

import com.example.natifyka.config.BotConfiguration;
import com.example.natifyka.model.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {
    List<Paper> findAllBySubscriberId(Long subscriberId);

    Long deleteBySubscriberIdAndId(Long subscriber_id, Long id);

    List<Paper> findAllBySubscriberActive(boolean subscriber_active);
}

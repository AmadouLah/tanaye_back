package com.tanaye.www.repository;

import com.tanaye.www.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByAuteurIdOrderByDateEnvoiDesc(Long auteurId, Pageable pageable);

    Page<Message> findByDestinataireIdOrderByDateEnvoiDesc(Long destinataireId, Pageable pageable);
}

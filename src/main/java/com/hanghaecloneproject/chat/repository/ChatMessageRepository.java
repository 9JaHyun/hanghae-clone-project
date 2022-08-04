package com.hanghaecloneproject.chat.repository;

import com.hanghaecloneproject.chat.domain.ChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);
}

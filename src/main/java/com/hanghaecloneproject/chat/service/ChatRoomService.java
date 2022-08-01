package com.hanghaecloneproject.chat.service;

import com.hanghaecloneproject.chat.domain.ChatMessage;
import com.hanghaecloneproject.chat.domain.ChatRoom;
import com.hanghaecloneproject.chat.domain.ChatRoomJoin;
import com.hanghaecloneproject.chat.dto.ChatRoomForm;
import com.hanghaecloneproject.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;


@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomJoinService chatRoomJoinService;

    public Optional<ChatRoom> findById(Long id) {
        return chatRoomRepository.findById(id);
    }

//    public List<ChatRoomForm> setting(List<ChatRoomJoin> chatRoomJoins, User user) {
//        List<ChatRoomForm> chatRooms = new ArrayList<>();
//        for(ChatRoomJoin tmp : chatRoomJoins){
//            ChatRoomForm chatRoomForm = new ChatRoomForm();
//            ChatRoom chatRoom = tmp.getChatRoom();
//            chatRoomForm.setId(chatRoom.getId());
//            if(chatRoom.getMessages().size() != 0) {
//                Collections.sort(chatRoom.getMessages(), new Comparator<ChatMessage>() {
//                    @Override
//                    public int compare(ChatMessage c1, ChatMessage c2) {
//                        if(c1.getTime().isAfter(c2.getTime())){
//                            return -1;
//                        }
//                        else{
//                            return 1;
//                        }
//                    }
//                });
//                ChatMessage lastMessage = chatRoom.getMessages().get(0);
//                chatRoomForm.makeChatRoomForm(lastMessage.getMessage(),chatRoomJoinService.findAnotherUser(chatRoom, user.getUsername()),lastMessage.getTime());
//                chatRooms.add(chatRoomForm);
//            }
//            else{
//                chatRoomJoinService.delete(tmp);
//            }
//        }
//        Collections.sort(chatRooms, new Comparator<ChatRoomForm>() {
//            @Override
//            public int compare(ChatRoomForm c1, ChatRoomForm c2) {
//                if(c1.getTime().isAfter(c2.getTime())){
//                    return -1;
//                }
//                else{
//                    return 1;
//                }
//            }
//        });
//        return chatRooms;
//    }
}
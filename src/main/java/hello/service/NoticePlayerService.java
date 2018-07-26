package hello.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import hello.entity.Player;
import hello.model.ChatMessage;
import hello.repository.PlayerRepository;

@Service
public class NoticePlayerService {
	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private PlayerRepository playerRepository;
	
	public void noticeAdd(String username) {
		List<Player> players = playerRepository.findAll();
		
		ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.PLAY);
        chatMessage.setContent(players.size()+"");
        chatMessage.setSender(username);
		
		this.template.convertAndSend("/topic/publicChatRoom", chatMessage);
	}
	
	
	public void noticeStart(List<Player> players) {
		ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.START);
		for(Player p:players){
			 chatMessage.setContent(p.getCards());
		     chatMessage.setSender(p.getName());
		     this.template.convertAndSendToUser(p.getName(),"/topic/publicChatRoom", chatMessage);
		}
	}
}

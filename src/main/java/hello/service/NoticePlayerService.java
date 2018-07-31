package hello.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

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
		chatMessage.setContent(players.size() + "");
		chatMessage.setSender(username);

		this.template.convertAndSend("/topic/publicChatRoom", chatMessage);
	}

	public void noticeStart(List<Player> players) {
		Map<String, Object> map = new HashMap<>();
		map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);

		String firstOne = players.get(0).getName();
		
		String rankBoard = ";(**)" + firstOne + ":0,";

		for (int i = 1; i < players.size(); i++) {
			Player p = players.get(i);
			rankBoard += p.getName() + ":0,";
		}

		for (Player p : players) {
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setType(ChatMessage.MessageType.START);
			chatMessage.setContent(p.getCards() + rankBoard+";" + firstOne);
			chatMessage.setSender(p.getName());
			this.template.convertAndSendToUser(p.getName(), "/queue/start-game", chatMessage, map);
		}
	}
}

package hello.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import hello.entity.Viewer;
import hello.model.ChatMessage;
import hello.repository.ViewerRepository;

@Controller
public class WebSocketController {

	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/publicChatRoom")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
		return chatMessage;
	}

	@Autowired
	private ViewerRepository viewerRepository;

	@MessageMapping("/chat.addUser")
	@SendTo("/topic/publicChatRoom")
	public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		// Add username in web socket session
		String username = chatMessage.getSender();
		headerAccessor.getSessionAttributes().put("username", username);
		if (chatMessage.getType().equals(ChatMessage.MessageType.JOIN)) {
			List<Viewer> viewers = viewerRepository.findByName(username);
			if (viewers == null || viewers.size()==0) {
				System.out.println("=============2");
				Viewer v = new Viewer();
				v.setName(username);
				System.out.println("=============1");
				viewerRepository.save(v);
				System.out.println("=============0");
			}
		}
		return chatMessage;
	}

}
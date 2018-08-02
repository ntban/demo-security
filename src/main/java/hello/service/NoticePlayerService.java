package hello.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import hello.entity.CardByTurn;
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

		ChatMessage notice = new ChatMessage();
		notice.setType(ChatMessage.MessageType.PLAY);
		notice.setContent(players.size() + "");
		notice.setSender(username);

		this.template.convertAndSend("/topic/publicChatRoom", notice);
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
			ChatMessage notice = new ChatMessage();
			notice.setType(ChatMessage.MessageType.START);
			notice.setContent(p.getCards() + rankBoard + ";" + firstOne);
			notice.setSender(p.getName());
			this.template.convertAndSendToUser(p.getName(), "/queue/play-game", notice, map);
		}
	}

	public void noticeChoose(List<Player> players, int currentPlayer, String hint) {
		Map<String, Object> map = new HashMap<>();
		map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
		String sender = players.get(currentPlayer).getName();
		
		ChatMessage doneHint = new ChatMessage();
		doneHint.setType(ChatMessage.MessageType.DONE_HINT);
		doneHint.setSender(sender);
		doneHint.setContent(hint);
		
		this.template.convertAndSendToUser(sender, "/queue/play-game", doneHint, map);

		for (int i = 0; i < players.size(); i++) {
			if (i == currentPlayer) {
				continue;
			}

			ChatMessage chooseCard = new ChatMessage();
			chooseCard.setType(ChatMessage.MessageType.CHOOSE);
			chooseCard.setSender(sender);
			chooseCard.setContent(hint);

			this.template.convertAndSendToUser(players.get(i).getName(), "/queue/play-game", chooseCard, map);
		}
	}

	public void noticeShowCard(List<CardByTurn> cardByTurns, List<Player> players, int currentPlayer) {
		String hint = cardByTurns.get(0).getHint();
		Collections.shuffle(cardByTurns);
		
		Map<String, Object> map = new HashMap<>();
		map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
		
		String sender = players.get(currentPlayer).getName();
		
		// TODO: Người gợi ý: chỉ show các bài, hint + k cho chọn
		
		String contentShowOnly = hint+";";
		
		for(CardByTurn card:cardByTurns){
			contentShowOnly += card.getImageCard()+",";
		}
		
		ChatMessage showOnly = new ChatMessage();
		showOnly.setType(ChatMessage.MessageType.SHOW_ONLY);
		showOnly.setSender(sender);
		showOnly.setContent(contentShowOnly+";");
		
		this.template.convertAndSendToUser(sender, "/queue/play-game", showOnly, map);
		
		// TODO: Người chọn: show các bài, hint + có cho chọn
		for(int i = 0; i < players.size(); i++){
			if(i == currentPlayer){
				continue;
			}
			
			ChatMessage showChoose = new ChatMessage();
			showChoose.setType(ChatMessage.MessageType.SHOW_CHOOSE);
			showChoose.setSender(sender);
			
			String contentShowChoose =  hint+";";
			String myCard = "";

			for(CardByTurn card:cardByTurns){
				if(card.getOwner().equals(players.get(i).getName())){
					myCard = card.getImageCard();
				}
				contentShowChoose += card.getImageCard()+",";
			}
			showChoose.setContent(contentShowChoose+";"+myCard);
			
			this.template.convertAndSendToUser(players.get(i).getName(), "/queue/play-game", showChoose, map);
		}
	}
}

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

	public void noticeStart(List<Player> players, int currentPlayer, HashMap<String, Integer> scores) {
		Map<String, Object> map = new HashMap<>();
		map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);

		String hintGiver = players.get(currentPlayer).getName();

		String rankBoard = ";(**)" + hintGiver + ":" + scores.get(hintGiver) + ",";

		for (int i = 0; i < players.size(); i++) {
			if (i == currentPlayer) {
				continue;
			}
			Player p = players.get(i);
			rankBoard += p.getName() + ":" + scores.get(p.getName()) + ",";
		}

		for (Player p : players) {
			ChatMessage notice = new ChatMessage();
			notice.setType(ChatMessage.MessageType.START);
			notice.setContent(p.getCards() + rankBoard + ";" + hintGiver);
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

		String contentShowOnly = hint + ";";

		for (CardByTurn card : cardByTurns) {
			contentShowOnly += card.getImageCard() + ",";
		}
		contentShowOnly = contentShowOnly.substring(0, contentShowOnly.length() - 1);// remove
																						// last
																						// ','

		ChatMessage showOnly = new ChatMessage();
		showOnly.setType(ChatMessage.MessageType.SHOW_ONLY);
		showOnly.setSender(sender);
		showOnly.setContent(contentShowOnly + ";");

		this.template.convertAndSendToUser(sender, "/queue/play-game", showOnly, map);

		// TODO: Người chọn: show các bài, hint + có cho chọn
		for (int i = 0; i < players.size(); i++) {
			if (i == currentPlayer) {
				continue;
			}

			ChatMessage showChoose = new ChatMessage();
			showChoose.setType(ChatMessage.MessageType.SHOW_CHOOSE);
			showChoose.setSender(sender);

			String contentShowChoose = hint + ";";
			String myCard = "";

			for (CardByTurn card : cardByTurns) {
				if (card.getOwner().equals(players.get(i).getName())) {
					myCard = card.getImageCard();
				}
				contentShowChoose += card.getImageCard() + ",";
			}
			contentShowChoose = contentShowChoose.substring(0, contentShowChoose.length() - 1);// remove
																								// last
																								// ','

			showChoose.setContent(contentShowChoose + ";" + myCard);

			this.template.convertAndSendToUser(players.get(i).getName(), "/queue/play-game", showChoose, map);
		}
	}

	public void showResult(List<Player> players, HashMap<String, Integer> scores,
			HashMap<CardByTurn, List<String>> scoreGet, HashMap<String, CardByTurn> cardWithOwner) {
		// TODO: cập nhật điểm
		String messageContent = "";

		for (Player p : players) {
			String username = p.getName();
			messageContent += username + ":" + scores.get(username) + ",";
		}
		messageContent = messageContent.substring(0, messageContent.length() - 1);
		messageContent += ";";

		// TODO: hiện thị kết quả

		for (String owner : cardWithOwner.keySet()) {
			CardByTurn card = cardWithOwner.get(owner);
			messageContent += owner + ":" + card.getImageCard() + ":";

			List<String> choosers = scoreGet.get(card);
			if (choosers != null) {
				for (String chooser : choosers) {
					messageContent += chooser + " ";
				}
			}
			messageContent += ",";
		}
		messageContent = messageContent.substring(0, messageContent.length() - 1);

		Map<String, Object> map = new HashMap<>();
		map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
		for (Player p : players) {
			String username = p.getName();
			ChatMessage showResult = new ChatMessage();
			showResult.setType(ChatMessage.MessageType.SHOW_RESULT);
			showResult.setSender(username);
			showResult.setContent(messageContent);
			this.template.convertAndSendToUser(username, "/queue/play-game", showResult, map);
		}
	}

	public void noticeGameOver(List<Player> players, String messageContent) {
		Map<String, Object> map = new HashMap<>();
		map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
		for (Player p : players) {
			String username = p.getName();
			ChatMessage showResult = new ChatMessage();
			showResult.setType(ChatMessage.MessageType.GAME_OVER);
			showResult.setSender(username);
			showResult.setContent(messageContent);
			this.template.convertAndSendToUser(username, "/queue/play-game", showResult, map);
		}
	}
}

package hello.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import hello.entity.Card;
import hello.entity.CardByTurn;
import hello.entity.CardChoose;
import hello.entity.Player;
import hello.repository.CardRepository;
import hello.repository.PlayerRepository;
import hello.service.NoticePlayerService;

@Controller
public class DixitController {
	List<Card> cards;
	List<Player> players = new ArrayList<>();
	HashMap<String, Integer> scores = new HashMap<>();

	int currentPlayer;

	String started = "";

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private NoticePlayerService noticePlayerService;

	@RequestMapping(path = "/startGame", method = RequestMethod.POST)
	public @ResponseBody String startGame(HttpServletRequest request) {
		Player player = (Player) request.getSession().getAttribute("player");
		if (player == null || player.getFirstPlayer() == null) {
			return "You can't start game!";
		}
		if (players.size() < 3) {
			return "You can't start game when not have 3 players !";
		}

		createCards();
		noticePlayerService.noticeStart(players);
		currentPlayer = 0;
		started = "started";
		for (int i = 0; i < players.size(); i++) {
			scores.put(player.getName(), 0);
		}

		return "Game Started!";
	}

	@RequestMapping(path = "/registerGame", method = RequestMethod.POST)
	public @ResponseBody String registerGame(HttpServletRequest request, Principal principal, Model model) {
		if (players.size() == 6 || started.equals("started")) {
			return "Can't register anymore!";
		}

		String username = principal.getName();

		if (username == null) {
			return "Register Fail!";
		}

		for (Player p : players) {
			if (p.getName().equals(username)) {
				return "Already Register!";
			}
		}

		if (players.size() == 0) {
			cards = cardRepository.findAll();
		}

		Player player = new Player(username);
		player = playerRepository.save(player);

		if (players.size() == 0) {
			player.setFirstPlayer("TRUE");
		}

		request.getSession().setAttribute("player", player);

		players.add(player);

		noticePlayerService.noticeAdd(username);
		model.addAttribute("username", username);

		return "Register OK!";
	}

	List<CardByTurn> cardByTurns = new ArrayList<>();

	@RequestMapping(path = "/hintOrder", method = RequestMethod.POST)
	public @ResponseBody String hintOrder(HttpServletRequest request, Principal principal, Model model) {
		String username = principal.getName();

		if (username == null) {
			return "Register Fail!";
		}

		if (!username.equals(players.get(currentPlayer).getName())) {
			return "Not your turn!";
		}

		String imageCard = request.getParameter("imageCard");
		String hint = request.getParameter("hint");
		CardByTurn card = new CardByTurn();
		card.setHint(hint);
		card.setImageCard(imageCard);
		card.setOwner(username);
		cardByTurns.add(card);

		// gửi event choose bài đến cho các player khác
		noticePlayerService.noticeChoose(players, currentPlayer, hint);

		return "Done!";
	}

	@RequestMapping(path = "/chooseOrder", method = RequestMethod.POST)
	public @ResponseBody String chooseOrder(HttpServletRequest request, Principal principal, Model model) {
		if (cardByTurns.size() == players.size()) {
			return "Can't choose anymore!";
		}

		String username = principal.getName();

		if (username == null) {
			return "Register Fail!";
		}
		String imageCard = request.getParameter("imageCard");

		CardByTurn card = new CardByTurn();
		card.setImageCard(imageCard);
		card.setOwner(username);
		cardByTurns.add(card);

		if (cardByTurns.size() == players.size()) {
			// gửi event show bài cho chọn
			noticePlayerService.noticeShowCard(cardByTurns, players, currentPlayer);
		}

		return "Done!";
	}

	List<CardChoose> cardGetScore = new ArrayList<>();

	@RequestMapping(path = "/chooseGetScore", method = RequestMethod.POST)
	public @ResponseBody String chooseGetScore(HttpServletRequest request, Principal principal, Model model) {
		if (cardGetScore.size() == players.size() - 1) {
			return "Can't choose anymore!";
		}

		String username = principal.getName();

		if (username == null) {
			return "Register Fail!";
		}

		if (username.equals(players.get(currentPlayer).getName())) {
			return "It's your turn! You can't choose!";
		}

		String imageCard = request.getParameter("imageCard");
		// TODO: check tự chọn bài mình
		for (CardByTurn card : cardByTurns) {
			if (card.getOwner().equals(username) && card.getImageCard().equals(imageCard)) {
				return "You can't choose your card!";
			}
		}

		CardChoose cardChoose = new CardChoose();
		cardChoose.setChooser(username);
		cardChoose.setImageCard(imageCard);
		String owner = "";
		for (CardByTurn card : cardByTurns) {
			if (card.getImageCard().equals(imageCard)) {
				owner = card.getOwner();
				break;
			}
		}
		cardChoose.setOwner(owner);
		cardGetScore.add(cardChoose);

		if (cardGetScore.size() == players.size() - 1) {
			// TODO: thông báo điểm và báo kết quả
			calculateScore();
		}

		return "Done!";
	}

	private void calculateScore() {
		String currentPlayerName = players.get(currentPlayer).getName();

		HashMap<CardByTurn, List<String>> scoreGet = new HashMap<>();
		HashMap<String, CardByTurn> cardWithOwner = new HashMap<>();

		for (CardByTurn card : cardByTurns) {
			cardWithOwner.put(card.getOwner(), card);
		}

		for (CardChoose c : cardGetScore) {
			List<String> choosers = scoreGet.get(c.getOwner());
			if (choosers == null) {
				choosers = new ArrayList<>();
			}
			choosers.add(c.getChooser());
			scoreGet.put(cardWithOwner.get(c.getOwner()), choosers);
		}

		List<String> chooserOfCurrent = scoreGet.get(currentPlayerName);
		if (chooserOfCurrent == null) {
			// TODO: không ai chọn đúng
			// người gợi ý: 0 điểm, những người còn lại +2 và + thêm
			// số người chọn bài mình
			for (CardByTurn key : scoreGet.keySet()) {
				String username = key.getOwner();
				Integer score = scores.get(username);
				score += (2 + scoreGet.get(key).size());
				scores.put(username, score);
			}
		} else if (chooserOfCurrent.size() == players.size() - 1) {
			// TODO: tất cả cùng chọn đúng
			// người gợi ý: 0 điểm, tất cả 2 điểm
			for (Player p : players) {
				String username = p.getName();
				if (username.equals(currentPlayerName)) {
					continue;
				}
				Integer score = scores.get(username);
				score += 2;
				scores.put(username, score);
			}
		} else {
			// TODO: không ai chọn đúng
			// người gợi ý: 3 điểm, những người chọn đúng +3

			Integer score = scores.get(currentPlayerName);
			score += 3;
			scores.put(currentPlayerName, score);

			for (String username : chooserOfCurrent) {
				Integer score1 = scores.get(username);
				score1 += 3;
				scores.put(username, score1);
			}

			// những người chơi khác còn + thêm số người chọn bài mình
			for (CardByTurn key : scoreGet.keySet()) {
				String username = key.getOwner();
				if (username.equals(currentPlayerName)) {
					continue;
				}
				Integer score2 = scores.get(username);
				score2 += scoreGet.get(key).size();
				scores.put(username, score2);
			}
		}

		// bắn điểm, bắn kết quả về cho player
		noticePlayerService.showResult(players, scores, scoreGet);

		// xoay vòng người kể chuyện
		currentPlayer = (currentPlayer + 1) % players.size();
		
		// đưa list bài chơi về cho lượt mới
		cardByTurns = new ArrayList<>();
		cardGetScore = new ArrayList<>();
		
		// TODO: check xem đã có ai thắng chưa
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		int max = -1;
		for(String key:scores.keySet()){
			// find max
			if(scores.get(key) > max){
				max = scores.get(key);
			}
		}
		
		// TODO: winner! gamne over!
		if(max >= 30){
			//handle here 
		}
	}

	private void createCards() {
		Collections.shuffle(cards);

		// set for players
		int i = 0;
		List<Card> cardUsed = new ArrayList<>();
		for (Player p : players) {
			String cardOfPlayer = "";
			for (int j = 0; j < 6; j++) {
				Card c = cards.get(i);
				cardOfPlayer += "images/" + c.getImage() + ",";
				c.setUsed("used");
				cardUsed.add(c);
				i++;
			}
			p.setCards(cardOfPlayer);
		}
		cardRepository.save(cardUsed);
	}
}
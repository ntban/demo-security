package hello.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import hello.entity.Card;
import hello.entity.Player;
import hello.repository.CardRepository;
import hello.repository.PlayerRepository;
import hello.service.NoticePlayerService;

@Controller
public class DixitController {
	List<Card> cards;
	ArrayList<Player> players = new ArrayList<>();

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
		if (players.size() < 4) {
			return "You can't start game when not have 4 players !";
		}

		createCards();
		noticePlayerService.noticeStart(players);
		
		return "Game Started!";
	}

	@RequestMapping(path = "/registerGame", method = RequestMethod.POST)
	public @ResponseBody String registerGame(HttpServletRequest request, Principal principal, Model model) {
		if (players.size() == 6) {
			return "Can't register anymore!";
		}

		String username = principal.getName();

		for (Player p : players) {
			if (p.getName().equals(username)) {
				return "Already Register!";
			}
		}

		if (username == null) {
			return "Register Fail!";
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

	private void createCards() {
		Collections.shuffle(cards);

		// set for players
		int i = 0;
		List<Card> cardUsed = new ArrayList<>();
		for (Player p : players) {
			String cardOfPlayer = "";
			for (int j = 0; j < 6; j++) {
				Card c = cards.get(i);
				cardOfPlayer += c.getId() + ",";
				c.setUsed("used");
				cardUsed.add(c);
				i++;
			}
			p.setCards(cardOfPlayer);
		}
		cardRepository.save(cardUsed);
	}
}
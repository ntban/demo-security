package hello.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import hello.entity.Card;
import hello.entity.Player;
import hello.repository.CardRepository;
import hello.repository.PlayerRepository;
import hello.utils.WebUtils;
 
@Controller
public class MainController {
 
    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public String welcomePage(Model model) {
        model.addAttribute("title", "Welcome");
        model.addAttribute("message", "This is welcome page!");
        return "welcomePage";
    }
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String homePage(Model model) {
        return "redirect:/login";
    }
 
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPage(Model model, Principal principal) {
         
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
 
        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("userPrincipal", principal);
         
        return "adminPage";
    }
 
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(Model model) {
 
        return "loginPage";
    }
    
    @RequestMapping(value = "/logoutSuccessful", method = RequestMethod.GET)
    public String logoutSuccessfulPage(Model model) {
        model.addAttribute("title", "Logout");
        return "logoutSuccessfulPage";
    }
    
    @RequestMapping(value = "/logout")
    public void logout(Model model, Principal principal) {
    	playerRepository.deletePlayer(principal.getName());
    }
 
    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
    public String userInfo(Model model, Principal principal) {
 
        // Sau khi user login thanh cong se co principal
        String userName = principal.getName();
 
        System.out.println("User Name: " + userName);
 
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
 
        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);
        
        model.addAttribute("userPrincipal", principal);
 
        return "userInfoPage";
    }
 
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String accessDenied(Model model, Principal principal) {
 
        if (principal != null) {
            User loginedUser = (User) ((Authentication) principal).getPrincipal();
 
            String userInfo = WebUtils.toString(loginedUser);
 
            model.addAttribute("userInfo", userInfo);
 
            String message = "Hi " + principal.getName() //
                    + "<br/s> You do not have permission to access this page!";
            model.addAttribute("message", message);
 
        }
 
        return "403Page";
    }
 
    @Autowired
	private PlayerRepository playerRepository;
    
    @RequestMapping("/chat")
	public String chat(Principal principal, Model model) {
    	if (principal == null ) {
			return "redirect:/login";
		}
    	
		String username = principal.getName();

		model.addAttribute("username", username);
		
		List<Player> players = playerRepository.findAll();
		
		model.addAttribute("playerCount", players.size());

		return "chat";
	}
    
    @Autowired
	CardRepository cardRepository;

	@RequestMapping(path = "/card-page", method = RequestMethod.GET)
	public String showCardPage(Model model) {
		List<Card> cards = cardRepository.getFirstSix();

		List<Card> cards1 = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Card c = cards.get(i);
			c.setShowTipOrd("L"+(i+1));
			cards1.add(c);
			
			cards.get(i).setUsed("used");
			model.addAttribute("image"+(i+1), "/images/"+c.getImage());
		}
		model.addAttribute("highCards", cards1);

		List<Card> cards2 = new ArrayList<>();
		for (int i = 3; i < 6; i++) {
			Card c = cards.get(i);
			c.setShowTipOrd("L"+(i+1));
			cards2.add(c);
			cards.get(i).setUsed("used");
			
			model.addAttribute("image"+(i+1),"/images/"+c.getImage());
		}
		model.addAttribute("lowCards", cards2);

		cardRepository.save(cards);
		return "card-page";
	}
	
	@RequestMapping(path = "/card-page", method = RequestMethod.POST)
	public  @ResponseBody String refreshCardPage(Model model) {
		List<Card> cards = cardRepository.getFirstSix();

		List<Card> cards1 = new ArrayList<>();
		String listImages = "";
		
		for (int i = 0; i < 3; i++) {
			Card c = cards.get(i);
			c.setShowTipOrd("L"+(i+1));
			cards1.add(c);
			
			cards.get(i).setUsed("used");
			model.addAttribute("image"+(i+1), "/images/"+c.getImage());
			listImages+= "/images/"+c.getImage()+",";
		}
		model.addAttribute("highCards", cards1);

		List<Card> cards2 = new ArrayList<>();
		for (int i = 3; i < 6; i++) {
			Card c = cards.get(i);
			c.setShowTipOrd("L"+(i+1));
			cards2.add(c);
			cards.get(i).setUsed("used");
			
			model.addAttribute("image"+(i+1),"/images/"+c.getImage());
			listImages+= "/images/"+c.getImage()+",";
		}
		model.addAttribute("lowCards", cards2);

		cardRepository.save(cards);
		return listImages;
	}
}
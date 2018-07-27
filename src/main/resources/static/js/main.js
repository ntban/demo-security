'use strict';

var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('#connecting');

var stompClient = null;
var username = null;

function connect() {
	username = document.querySelector('#username').innerText.trim();

	var socket = new SockJS('/ws');
	stompClient = Stomp.over(socket);

	stompClient.connect({}, onConnected, onError);
	$("#formStartGame").hide();
	$("#owncard-container-showhide").hide();
	$("#playcard-container-showhide").hide();
}

// Connect to WebSocket Server.
connect();

function onConnected() {
	// Subscribe to the Public Topic
	stompClient.subscribe('/topic/publicChatRoom', onMessageReceived);

	// Tell your username to the server
	stompClient.send("/app/chat.addUser", {}, JSON.stringify({
		sender : username,
		type : 'JOIN'
	}))

	connectingElement.classList.add('hidden');
}

function onError(error) {
	connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
	connectingElement.style.color = 'red';
}

function sendMessage(event) {
	var messageContent = messageInput.value.trim();
	if (messageContent && stompClient) {
		var chatMessage = {
			sender : username,
			content : messageInput.value,
			type : 'CHAT'
		};
		stompClient.send("/app/chat.sendMessage", {}, JSON
				.stringify(chatMessage));
		messageInput.value = '';
	}
	event.preventDefault();
}

function onMessageReceived(payload) {
	var message = JSON.parse(payload.body);

	var messageElement = document.createElement('li');

	if (message.type === 'JOIN') {
		messageElement.classList.add('event-message');
		message.content = message.sender + ' joined!';
	} else if (message.type === 'LEAVE') {
		messageElement.classList.add('event-message');
		message.content = message.sender + ' left!';
	} else if (message.type === 'CHAT') {
		messageElement.classList.add('chat-message');
		var usernameElement = document.createElement('strong');
		usernameElement.classList.add('nickname');
		var usernameText = document.createTextNode(message.sender);
		var usernameText = document.createTextNode(message.sender);
		usernameElement.appendChild(usernameText);
		messageElement.appendChild(usernameElement);
	} else if (message.type === 'PLAY') {
		var messageCountPlayer = document.querySelector('#messageCountPlayer');
		var numberPlayer = parseInt(message.content);

		messageCountPlayer.innerHTML = numberPlayer;

		return;
	} else if (message.type === 'START') {
		$('#formStartGame').hide();
		$('#player-count').hide();
		
		var data = message.content.split(";");
		
		//6 fixed own cards
		var res = data[0].split(",");
		var image1 = res[0], image2=res[1],image3=res[2],image4=res[3],image5=res[4],image6=res[5];
		dw_Tooltip.content_vars = {
				L1: {
					 img: image1,
					 w: 300, 
					 h: 400 
				},
				L2: {
					 img: image2,
					 w: 300, 
					 h: 400 
				},
				L3: {
					 img: image3,
					 w: 300, 
					 h: 400 
				},
				L4: {
					 img: image4,
					 w: 300, 
					 h: 400 
				},
				L5: {
					 img: image5,
					 w: 300, 
					 h: 400 
				},
				L6: {
					 img: image6,
					 w: 300, 
					 h: 400 
				}	
			}
		$("#L1").attr("src",image1);
		$("#L2").attr("src",image2);
		$("#L3").attr("src",image3);
		$("#L4").attr("src",image4);
		$("#L5").attr("src",image5);
		$("#L6").attr("src",image6);
		
		//set rank board
		var players = data[1].split(",");
		for(var i=0;i<players.length; i++){
			if(!players[i]){
				break;
			}
			
			var info = players[i].split(":");
			$("#name-player"+i).html(info[0]);
			$("#score-player"+i).html(info[1]);
		}
		
		//show play area
		$("#owncard-container-showhide").show();
		
		//$("#playcard-container-showhide").show();
	}

	var textElement = document.createElement('span');
	var messageText = document.createTextNode(message.content);
	textElement.appendChild(messageText);

	messageElement.appendChild(textElement);

	messageArea.appendChild(messageElement);
	messageArea.scrollTop = messageArea.scrollHeight;
}

messageForm.addEventListener('submit', sendMessage, true);

function registerGame() {
	$.post("/registerGame", function(data, status) {
		if(data === "Register Fail!"){
			window.location.href = "login";
		}
		
		$("#registerGame").hide();
		$("#formStartGame").show();
		$('#player-count').css('margin-top',"50px");
		alert(data);
	});
}

function startGame() {
	$.post("/startGame", function(data, status) {
		
		alert(data);
	});
}
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
	stompClient.subscribe("/user/queue/play-game", onMessagePlayReceived);

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

	var messageElement = "";

	if (message.type === 'JOIN') {
		messageElement += '<li class="event-message">'; 
		message.content = message.sender + ' đã vào phòng chat!';
	} else if (message.type === 'LEAVE') {
		messageElement += '<li class="event-message">';
		message.content = message.sender + ' đã rời phòng chat!';
	} else if (message.type === 'CHAT') {
		messageElement += '<li class="chat-message">';
		var usernameElement = '<strong class="nickname">' + message.sender +'</strong>';
		messageElement += usernameElement;
	}else if (message.type === 'PLAY') {
		var numberPlayer = parseInt(message.content);
		$('#messageCountPlayer').html(numberPlayer);
		return; 
	}

	var content = message.content;
	for(var i = 0 ; i < hotkeys.length; i++){
		content = content.replace(new RegExp(hotkeys[i], 'g'), emoticons[i]);
	}
	messageElement += content+"</li>";
	$('#messageArea').append(messageElement);
	
	messageArea.scrollTop = messageArea.scrollHeight;
}

messageForm.addEventListener('submit', sendMessage, true);

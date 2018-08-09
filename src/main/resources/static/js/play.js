function onMessagePlayReceived(payload) {
	var message = JSON.parse(payload.body);

	if (message.type === 'START') {
		startGameWithCards(message);
	} else if (message.type === 'CHOOSE') {
		var hint = message.content;

		$("#your-hint").val("Gợi ý: " + hint);
		$("#your-hint").attr('disabled', true);
		$("#play-submit").attr('disabled', false);

		alert("Mời bạn đưa bài!");
	} else if (message.type === 'DONE_HINT') {
		$("input[name='radioOwn']").attr('disabled', true);
		$("#your-hint").attr('disabled', true);
		$("#play-submit").attr('disabled', true);
	} else if (message.type === 'SHOW_ONLY') {
		showImagePlayCards(message.content);
		$("#choose-button").attr('disabled', true);
	} else if (message.type === 'SHOW_CHOOSE') {
		showImagePlayCards(message.content);
		$("#choose-button").attr('disabled', false);
	} else if (message.type === 'SHOW_RESULT') {
		showResultRound(message.content);
	}else if(message.type === 'GAME_OVER'){
		
		$("#choose-button").attr('disabled', true);
		$("#your-hint").attr('disabled', true);
		$("#play-submit").attr('disabled', true);
		$("#choose-button").attr('disabled', true);
		
		alert(message.content);
	}else if(message.type==='VIEWER_START'){
		$('#formStartGame').hide();
		$('#player-count').hide();
		$("#playcard-container-showhide").hide();

		var data = message.content.split(";");
		// set rank board
		var players = data[1].split(",");
		for (var i = 0; i < players.length; i++) {
			if (!players[i]) {
				break;
			}

			var info = players[i].split(":");
			$("#name-player" + i).html(info[0]);
			$("#score-player" + i).html(info[1]);
		}
		
		$("#owncard-container-showhide").show();
		$("#rank-board").show();
		$("#your-hint").hide();
		$("#play-submit").hide();
		$("#owncard-name").hide();
		$("#owncard-board").hide();
	}else if(message.type === 'RESET_GAME'){
		$("#registerGame").show();
		$("#messageCountPlayer").html("0");
		$("#player-count").show();
		$("#formStartGame").hide();
		$("#owncard-container-showhide").hide();
		$("#playcard-container-showhide").hide();
	}
}

function startGameWithCards(message) {
	$('#formStartGame').hide();
	$('#player-count').hide();
	$("#playcard-container-showhide").hide();

	var data = message.content.split(";");

	// 6 fixed own cards
	var res = data[0].split(",");
	var image1 = res[0], image2 = res[1], image3 = res[2], image4 = res[3], image5 = res[4], image6 = res[5];
	dw_Tooltip.content_vars = {
		L1 : {
			img : image1,
			w : 375,
			h : 500
		},
		L2 : {
			img : image2,
			w : 375,
			h : 500
		},
		L3 : {
			img : image3,
			w : 375,
			h : 500
		},
		L4 : {
			img : image4,
			w : 375,
			h : 500
		},
		L5 : {
			img : image5,
			w : 375,
			h : 500
		},
		L6 : {
			img : image6,
			w : 375,
			h : 500
		}
	}
	$("#L1").attr("src", image1);
	$("#L2").attr("src", image2);
	$("#L3").attr("src", image3);
	$("#L4").attr("src", image4);
	$("#L5").attr("src", image5);
	$("#L6").attr("src", image6);

	$("#radioOwn1").val(image1);
	$("#radioOwn2").val(image2);
	$("#radioOwn3").val(image3);
	$("#radioOwn4").val(image4);
	$("#radioOwn5").val(image5);
	$("#radioOwn6").val(image6);

	// set rank board
	var players = data[1].split(",");
	for (var i = 0; i < players.length; i++) {
		if (!players[i]) {
			break;
		}

		var info = players[i].split(":");
		$("#name-player" + i).html(info[0]);
		$("#score-player" + i).html(info[1]);
	}

	// set current player
	var username = $("#username").html();
	var currentPlayer = data[2];
	currentDixitHintPlayer = currentPlayer;
	if (username === currentPlayer) {
		$("input[name='radioOwn']").attr('disabled', true);
		$("#your-hint").prop('disabled', false);
		$("#play-submit").attr('disabled', false);
		$("#your-hint").val("");
		$("#your-hint").attr("placeholder", "Mời nhập gợi ý..");
	} else {
		$("input[name='radioOwn']").attr('disabled', true);
		$("#your-hint").prop('disabled', true);
		$("#play-submit").attr('disabled', true);
		$("#your-hint").val("");
		$("#your-hint").attr("placeholder", "Chờ gợi ý..");
	}

	// show play area
	$("#owncard-container-showhide").show();

}

function showImagePlayCards(content) {
	var data = content.split(";");
	$("#playing-hint").html("Gợi ý: " + data[0]);

	// n-cards
	var res = data[1].split(",");

	dw_Tooltip.content_vars.L11 = {
		img : res[0],
		w : 375,
		h : 500
	};
	$("#L11").attr("src", res[0]);
	$("#radioPlay1").val(res[0]);

	dw_Tooltip.content_vars.L12 = {
		img : res[1],
		w : 375,
		h : 500
	};
	$("#L12").attr("src", res[1]);
	$("#radioPlay2").val(res[1]);

	dw_Tooltip.content_vars.L13 = {
		img : res[2],
		w : 375,
		h : 500
	};
	$("#L13").attr("src", res[2]);
	$("#radioPlay3").val(res[2]);

	if (res.length >= 4) {
		dw_Tooltip.content_vars.L14 = {
			img : res[3],
			w : 375,
			h : 500
		};
		$("#L14").attr("src", res[3]);
		$("#radioPlay4").val(res[3]);
	}else{
		$("#L14").attr("src", "");
		$("#L14").hide();
		$("#checkmarkL14").hide();
	}

	if (res.length >= 5) {
		dw_Tooltip.content_vars.L15 = {
			img : res[4],
			w : 375,
			h : 500
		};
		$("#L15").attr("src", res[4]);
		$("#radioPlay5").val(res[4]);
	}else{
		$("#L15").attr("src", "");
		$("#L15").hide();
		$("#checkmarkL15").hide();
	}

	if (res.length >= 6) {
		dw_Tooltip.content_vars.L16 = {
			img : res[5],
			w : 375,
			h : 500
		};
		$("#L16").attr("src", res[5]);
		$("#radioPlay6").val(res[5]);
	}else{
		$("#L16").attr("src", "");
		$("#L16").hide();
		$("#checkmarkL16").hide();
	}

	currentOwnImageCard = data[2];

	$("#playcard-container-showhide").show();
	$("#result-board").hide();
}

function showResultRound(content){
	$("#choose-button").attr('disabled', true);
	
	var data = content.split(";");
	var res = data[0].split(",");
	
	for(var i=0;i<res.length;i++){
		var playerInfo = res[i].split(":");
		$("#name-player"+i).html(playerInfo[0]);
		$("#score-player"+i).html(playerInfo[1]);
	}
	
	var whichChoose = data[1].split(",");
	for(var i=0;i<whichChoose.length;i++){
		var playerInfo = whichChoose[i].split(":");
		$("#owner-player"+i).html(playerInfo[0]);
		$("#image-playingcard"+i).attr("src", playerInfo[1]);
		$("#choosers-card"+i).html(playerInfo[2]);
	}
	$("#result-board").show();
	
}
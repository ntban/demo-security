
function onMessagePlayReceived(payload) {
	var message = JSON.parse(payload.body);
	
	if (message.type === 'START') {
		startGameWithCards(message);
	}else if(message.type === 'CHOOSE'){
		
	}
}

function startGameWithCards(message){
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

	$("#radioOwn1").val(image1);
	$("#radioOwn2").val(image2);
	$("#radioOwn3").val(image3);
	$("#radioOwn4").val(image4);
	$("#radioOwn5").val(image5);
	$("#radioOwn6").val(image6);
	
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
	
	//set current player
	var username = $("#username").html();
	var currentPlayer = data[2];
	
	if(username === currentPlayer){
		alert("Your turn!");
	}else{
		$("#your-hint").prop('disabled', true);
	}
	
	//show play area
	$("#owncard-container-showhide").show();
	
	//$("#playcard-container-showhide").show();
}
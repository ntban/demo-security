function playSubmit(){
	var username = $("#username").html();
}
	if(currentDixitHintPlayer == username){// give hint and card 
		
		var hint = $('#your-hint').val();
		var imageCard = $("input[name='radioOwn']:checked").val();
		
		$.ajax({
		    type: "POST",
		    url: "/hintOrder",
		    data: {hint: hint, imageCard: imageCard},
		    success: function(data){
		    	if(data === "Register Fail!"){
					window.location.href = "login";
					return;
				}
		    	
		    	alert(data);
		    }
		});
		
	}else{// give card 
		var imageCard = $("input[name='radioOwn']:checked").val();
		$.ajax({
		    type: "POST",
		    url: "/chooseOrder",
		    data: {imageCard: imageCard},
		    success: function(data){
		    	if(data === "Register Fail!"){
					window.location.href = "login";
					return;
				}
		    	alert(data);
		    }
		});
	}
}

function chooseCard(){
	var imageCard = $("input[name='radioPlay']:checked").val();
}
	if(currentOwnImageCard == imageCard){
		alert("You can't choose your card!");
		return;
	}
	
	
}

function checkRadio(idSpan){
	$( "#"+idSpan ).prop( "checked", true );
}

function registerGame() {
	$.post("/registerGame", function(data, status) {
		if(data === "Register Fail!"){
			window.location.href = "login";
			return;
		}
		
		$("#registerGame").hide();
		$("#formStartGame").show();
		$('#player-count').css('margin-top',"50px");
		alert(data);
	});
}

function startGame() {
	$.post("/startGame", function(data, status) {
		if(data != "Game Started!"){
			alert(data);
		}
	});
}

function playSubmit(){
	var username = $("#username").html();
	if(currentDixitHintPlayer == username){// give hint and card 
		
		var hint = $('#your-hint').val();
		var imageCard = $("input[name='radioOwn']:checked").val();
		
		if(!imageCard){
			alert("Bạn chưa chọn lá bài nào!");
			return;
		}
		if(!hint){
			alert("Bạn chưa gợi ý !");
			return;
			
		}
		$('input[name="radioOwn"]').attr('checked', false);
		$.ajax({
		    type: "POST",
		    url: "/hintOrder",
		    data: {hint: hint, imageCard: imageCard},
		    success: function(data){
		    	if(data === "Chưa đăng ký chơi!"){
					window.location.href = "login";
					return;
				}
		    	
		    	alert(data);
		    }
		});
		
	}else{// give card 
		var imageCard = $("input[name='radioOwn']:checked").val();
		if(!imageCard){
			alert("Bạn chưa chọn lá bài nào!");
			return;
		}
		$('input[name="radioOwn"]').attr('checked', false);
		$.ajax({
		    type: "POST",
		    url: "/chooseOrder",
		    data: {imageCard: imageCard},
		    success: function(data){
		    	if(data === "Chưa đăng ký chơi!"){
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
	if(!imageCard){
		alert("Bạn chưa chọn lá bài nào!");
		return;
	}
	if(currentOwnImageCard == imageCard){
		alert("Bạn không thể tự chọn bài mình!");
		return;
	}
	$('input[name="radioPlay"]').attr('checked', false);
	$.ajax({
	    type: "POST",
	    url: "/chooseGetScore",
	    data: {imageCard: imageCard},
	    success: function(data){
	    	if(data === "Chưa đăng ký chơi!"){
				window.location.href = "login";
				return;
			}
	    	
	    	alert(data);
	    }
	});
	
}

function checkRadio(idSpan){
	$( "#"+idSpan ).prop( "checked", true );
}

function registerGame() {
	$.post("/registerGame", function(data, status) {
		if(data === "Chưa đăng ký chơi!"){
			window.location.href = "login";
			return;
		}
		
		$("#registerGame").hide();
		$("#formStartGame").show();
		$('#player-count').css('margin-top',"70px");
		alert(data);
	});
}

function startGame() {
	$.post("/startGame", function(data, status) {
		if(data != "Bắt đầu chơi!"){
			alert(data);
		}
	});
}

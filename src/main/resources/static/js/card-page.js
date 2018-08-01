function playSubmit(){
	if($('#your-hint').is(':disabled')){
		
	}else{
		var hint = $('#your-hint').val();
		var imageCard = $("input[name='radioOwn']:checked").val();
		
		$.ajax({
		    type: "POST",
		    url: "/hintOrder",
		    data: {hint: hint, imageCard: imageCard},
		    success: function(data){
		    	alert(data);
		    }
		});
	}
}

function checkRadio(idSpan){
	$( "#"+idSpan ).prop( "checked", true );
}

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
		if(data != "Game Started!"){
			alert(data);
		}
	});
}

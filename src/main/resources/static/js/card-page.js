function refresh(){
	$.post("/card-page", function(data, status) {
		var res = data.split(",");
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
	});
}
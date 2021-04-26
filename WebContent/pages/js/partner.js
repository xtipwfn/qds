var app=angular.module('myapp',[]);
app.controller('controller',function($scope){
	var user_uuid ="";
	var user_name ="";
	var qds =localStorage.getItem('qds');
	
	$('#loading').show();
	$.ajax({
		type: 'POST',
		data: {
			action: 'queryZsy',
			qds:qds
		},
		url: baseUrl + 'UserServlet',
		dataType: 'json',
		success: function(data) {
			console.log(data);
			if (data.result == 'success') {	
				$('.headUrl').attr("src", data.HEAD_URL);
				$('.username').html(data.XM);
				$('.phone').html(data.PHONE);
				$('.yqyh').html(data.YQYH);
				$('.hhrtd').html(data.HHRTD);
				var ljsy=gettoDecimal(data.LJSY);
				$('.ljsy').html(ljsy);
				var ktx=gettoDecimal(data.TXJE);
				$('.ktx').html(ktx);
				var jrsr=gettoDecimal(data.JRSY);
				$('.jrsr').html(jrsr);
				$('.yf').html(data.YF);
				var zyj=gettoDecimal(data.TDZYJ);
				$('.zyj').html(zyj);
				$('.yhlx').html(data.YHLX);
				localStorage.setItem('user_uuid',data.USER_UUID);
				localStorage.setItem('user_name',data.XM);
				user_uuid = data.USER_UUID;
				user_name = data.XM;		
			}else{
				$('.headUrl').attr("src", "img/default.png");
				$('.username').html("--");
				$('.phone').html("--");
				$('.yqyh').html("--");
				$('.hhrtd').html("--");
				$('.ljsy').html("--");
				$('.ktx').html("--");
				$('.jrsr').html("--");
				$('.yf').html("--");
				$('.zyj').html("");
				$('.yhlx').html("");
				mui.toast(data.msg);
			}
		},
		error: function(data) {
			console.log(data);
		},
		complete: function(){
			$('#loading').hide();
		}
	});
	
	$('.tz').click(function(){
		window.location = "team.html?v="+ Math.random();
	});
	//合伙人招募
	$('#yjtg').click(function(){
		window.location.href = baseUrl + 'ProdServlet?action=getZbmmImg&userUuid='+user_uuid+'&userName='+user_name+'&qds='+qds;
	})
	
	//信贷服务推广
	$('#ljsq').click(function(){
		window.location.href = baseUrl + 'IndexServlet';
	});
	
});
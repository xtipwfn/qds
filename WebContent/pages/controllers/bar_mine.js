app.controller('mineController', function($scope, $route, $routeParams) {
	$scope.$route = $route;
	$scope.thisTxet = "THIS IS mineController!";
	$(function(){
		document.title = localStorage.getItem('qdsTitle');
		var qds =localStorage.getItem('qds');	
		
		$('.zbmm').click(function(){
			var user_uuid = localStorage.getItem('user_uuid');
			var user_name = localStorage.getItem('user_name');
			console.log(user_uuid)
			window.location.href = baseUrl + 'ProdServlet?action=getZbmmImg&userUuid='+user_uuid+'&userName='+user_name+'&qds='+qds
		})
		var user_uuid = localStorage.getItem('user_uuid');
		var user_name = localStorage.getItem('user_name');		
		
		
		$.ajax({
			type: 'POST',
			async: false,
			data: {
				action: 'checkLogin',
				qds:qds
			},
			url: baseUrl + 'LoginServlet',
			dataType: 'json',
			success: function(data) {	
//				console.log("checkLogin:",data)
				if (data.result == 'success') {
					user_uuid=data.USER_UUID;
					user_name=data.XM;
					if(data.USER_TYPE){						
						$('#type').html(data.USER_TYPE);
					}
					if(data.HEAD_URL){
						$(".head_url").css("src",data.HEAD_URL);
					}else{
						$(".head_url").css("src","img/default.png");
					}
					localStorage.setItem('user_uuid',user_uuid);
					localStorage.setItem('user_name',user_name);
					querySrxx();
				} else {
					user_uuid=null;
					user_name=null;
					console.log(data);					
				}
			},
			error: function(data) {
				console.log(data)
			},
			complete: function() {
			}
		})
		
		function querySrxx() {		
			$.ajax({
				type: 'POST',
				async: false,
				data: {
					action: 'querySrxx',
					qds:qds
				},
				url: baseUrl + 'UserServlet',
				dataType: 'json',
				success: function(data) {	
					console.log(data)			
					if (data.result == 'success') {
						$("#zsr").html("￥"+data.ZSR);
						$("#yjsr").html("￥"+data.YJSR);
						$("#ptjl").html("￥"+data.PTJL);				
						$("#byss").html("￥"+data.BYSR);
						$("#ytx").html("￥"+data.YTX);
						$("#ktx").html("￥"+data.KTX);
					} else {
						$("#zsr").html("￥0.00");
						$("#yjsr").html("￥0.00");
						$("#ptjl").html("￥0.00");				
						$("#byss").html("￥0.00");
						$("#ytx").html("￥0.00");
						$("#ktx").html("￥0.00");
					}
					$scope.$apply();
				},
				error: function(data) {
					console.log(data)
				},
				complete: function() {
				}
			})
		}
//		 if( !(user_uuid && user_name)  || "undefined" ==user_name ){
//		 	window.location.href="login.html"
//		 }

		if(user_name){
			$('.username').text(user_name);
		}
	})
	
	$scope.toTeamData = function(){
		window.location.href = "team_data.html?v="+ Math.random();
	}
	
	$scope.toLoanData = function(){
		window.location.href = "loan_data.html?v="+ Math.random();
	}
	
	$('.askInfoMask').hide();
	//平台客服
	$scope.toPlatformService = function(){		
		$('.askInfoMask').show();
	}
	//平台客服 关闭
	$('.clear').click(function(){
		$('.askInfoMask').hide();
	});
	
	//完善信息
	$('.wszl').click(function(){
		window.location.href = "editPersonal.html?v="+ Math.random();
	});
	
	//申请提现
	$('.sqtx').click(function(){
		window.location.href = "destoon_finance_cash.html?v="+ Math.random();
	});
	
});

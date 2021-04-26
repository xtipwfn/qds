var app=angular.module('myapp',[]);
app.controller('controller',function($scope){
	var user_uuid=localStorage.getItem('user_uuid')?localStorage.getItem('user_uuid'):'';
	$scope.list = [];
	var qds =localStorage.getItem('qds');
	//查询总收益
	querySrxx();
	
	$.ajax({
		type: 'POST',
		data: {
			action: 'queryTxmx',
			userUuid:user_uuid,
			qds:qds
		},
		url: baseUrl + 'UserServlet',
		dataType: 'json',
		success: function(data) {
			console.log(data);
			if (data.result == 'success') {
				$scope.list = data.txmx;
			} else {
				$scope.list = [];
			}
			$scope.$apply();
		},
		error: function(data) {
			console.log(data)
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
					$("#zsy").html(data.ZSR);
					$("#ytx").html(data.YTX);
					$("#ktx").html(data.KTX);
					localStorage.setItem('kxt',data.KTX);
				} else {
					$("#zsy").html("00");
					$("#ytx").html("00");
					$("#ktx").html("00");
					localStorage.setItem('kxt',"0");
				}
			},
			error: function(data) {
				console.log(data)
			},
			complete: function() {
			}
		})
	}
	//点击提现
	$('.withdrawDeposit').click(function(){
		var ktx=localStorage.getItem('kxt')||'';
		console.log(ktx);
		if(ktx<=0){
			mui.toast('可提现金额必须大于0元');
			return false;
		}
		window.location = "add_bank_card.html?v="+ Math.random();
	});
	
})
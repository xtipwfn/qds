var app=angular.module('myapp',[]);
app.controller('controller',function($scope){
	var user_uuid=localStorage.getItem('user_uuid')?localStorage.getItem('user_uuid'):'';
	$scope.list = [];
	var qds =localStorage.getItem('qds');	
	//提交
	$('.saveBankCard').click(function(){
		var khmc=$("#khmc").val();		
		if(khmc=="" || khmc==null || khmc=="null"){
			mui.toast('请输入开户账户名');
			return false;
		}
		
		var yhzh=$("#yhzh").val();
		if(yhzh=="" || yhzh==null || yhzh=="null"){
			mui.toast('请输入银行账号');
			return false;
		}
		
		var khhmc=$("#khhmc").val();
		if(khhmc=="" || khhmc==null || khhmc=="null"){
			mui.toast('请输入开户行名称');
			return false;
		}
		var bz=$("#bz").val();
		
		$('#loading').show();
		$(".saveBankCard").attr("disabled", "true");
		$.ajax({
			type: 'POST',
			data: {
				action: 'saveTxsq',
				je:localStorage.getItem('kxt'),
				bz:encodeURI(bz),
				zhxm:encodeURI(khmc),
				yhzh:yhzh,
				khhmc:encodeURI(khhmc),
				qds:qds
			},
			url: baseUrl + 'UserServlet',
			dataType: 'json',
			success: function(data) {
				console.log(data);
				if (data.result == 'success') {
					mui.toast(data.msg);
					window.location.href = "destoon_finance_cash.html?v="+ Math.random();
				} else {
					mui.toast(data.msg);
				}
			},
			error: function(data) {
				console.log(data)
			},
			complete: function(){
				$('#loading').hide();
				$(".saveBankCard").removeAttr("disabled");
			}
		})
	});
	
});
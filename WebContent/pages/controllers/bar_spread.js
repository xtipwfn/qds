app.controller('spreadController', function($scope, $route, $routeParams) {
	$scope.$route = $route;
	$scope.thisTxet = "THIS IS spreadController!";
	
	$(function() {
		document.title = localStorage.getItem('qdsTitle');
		$(".qdsTitle").text(localStorage.getItem('qdsTitle'));
		
		var sj_user_uuid = GetArgs(window.location.search,'sj_user_uuid');
			
		$("#button").click(function() {
			var agreement = $("input[type='checkbox']").is(':checked');

			var openid = "";
			var xm = $("#xm").val();
			var tel = $("#tel").val();
			var smscode = $("#smscode").val();
			var job = $('#job').val();
			if (xm == '') {
				mui.toast('请输入您的姓名！');
				return false;
			}
			if (!(/^1[3456789]\d{9}$/.test(tel))) {
				mui.toast("手机号码有误！");
				return false;
			}
			if (job == '') {
				mui.toast('请选择您的职业！');
				return false;
			}
			if (agreement == false) {
				mui.toast('请查看并同意注册协议');
				return false;
			}
			$('#loading').show();
			$.ajax({
				type: 'POST',
				dataType: 'json',
				data: {
					'action': 'saveRegister',
					'xm': xm,
					'phone': tel,
					'zw': job,
					'code': smscode,
					'sj_user_uuid': sj_user_uuid
				},
				url: baseUrl + 'RegisterServlet',
				success: function(data) {
					console.log(data);
					if (data.result == 'success') {
						mui.toast(data.msg);
						localStorage.setItem('user_uuid', data.userUuid);
						localStorage.setItem('user_name', data.xm);
						window.location.href = "https://www.rongyixin.net/ryx/IndexServlet";
					} else {
						mui.toast(data.msg);
					}
				},
				error: function(data) {
					console.log(data)
				},
				complete: function(){
					$('#loading').hide()
				}
			})
		})
		
		var phoneReg = /(^1[3|4|5|6|7|8|9]\d{9}$)|(^09\d{8}$)/; //手机号正则
		var count = 180; //间隔函数，1秒执行
		var InterValObj; //timer变量，控制时间
		var curCount; //当前剩余秒数
		/*第一*/
		
		$("#sendcode").click(function() {
			console.log(22)
			curCount = count;
			var tel = $.trim($('#tel').val());
			if (!phoneReg.test(tel)) {
				mui.toast("请输入有效的手机号码");
				return false;
			}
			//向后台发送处理数据
			$.ajax({
				type: 'POST',
				data: {
					action: 'sendCode',
					phone: $.trim($('#tel').val())
				},
				url: baseUrl + 'RegisterServlet',
				dataType: 'json',
				success: function(data) {
					console.log(data);
					if (data.result == 'success') {
						mui.toast('已发送');
						//设置button效果，开始计时
						$("#sendcode").attr("disabled", "true");
						$("#sendcode").val(+curCount + "秒再获取");
						InterValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次
		
					} else {
						mui.toast('发送失败，请重试');
						return false;
					}
				},
				error: function(data) {
					console.log(data)
				}
			})
		
		});
	
	function SetRemainTime() {
		if (curCount == 0) {
			window.clearInterval(InterValObj); //停止计时器
			$("#sendcode").removeAttr("disabled"); //启用按钮
			$("#sendcode").val("重新发送");
		} else {
			curCount--;
			$("#sendcode").val(+curCount + "秒再获取");
		}
	}
	
	
	});
	
});
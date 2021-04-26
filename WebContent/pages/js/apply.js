var app=angular.module('myapp',[]);
app.controller('controller',function($scope){
	
	var prodUuid = GetArgs(window.location.search,'prodUuid');
	var qdsid = GetArgs(window.location.search,'qdsid');
	var qds = localStorage.getItem('qds');

	if(!qdsid){
		qdsid = localStorage.getItem('user_uuid') ? localStorage.getItem('user_uuid') : ''
	}

	var xzqhDm="440100";
	var xzqhMc="广州市";
	$scope.areaList=[];
	//获取存储的地址显示
	var locationLocal=localStorage.getItem('locationLocal');
	if(locationLocal!=null){
		$('.area span').text(localStorage.getItem('locationLocalMc'));
		xzqhDm=locationLocal;
	}else{
		localStorage.setItem("locationLocal",xzqhDm);
		localStorage.setItem("locationLocalMc",xzqhMc);
	}

	$('#loading').show();
	$.ajax({
		type: 'POST',
		data: {
			action: 'queryCsName',
			xzqhszDm: xzqhDm,
		},
		url: baseUrl + 'ProdServlet',
		dataType: 'json',
		success: function(data) {
			if (data.result == 'success') {
				$('.area span').text(data.XZQHMC);
				
			} else {

			}
		},
		error: function(data) {
			console.log(data)
		},
		complete: function() {
			$('#loading').hide();
		}
	});


	$("#button").click(function() {
		console.log(xzqhDm);
		var gsmc = $("#gsmc").val();
		var xm = $("#xm").val();
		var tel = $("#tel").val();
		var smscode = $("#smscode").val();
		var sfz = $('#sfz').val();
	//				if (gsmc == '') {
	//					mui.toast('请输入公司名称！');
	//					return false;
	//				}
		if (xm == '') {
			mui.toast('请输入法人姓名！');
			return false;
		}
		if (!(/^1[3456789]\d{9}$/.test(tel))) {
			mui.toast("手机号码有误！");
			return false;
		}
		if (smscode == '') {
			mui.toast('请输入短信验证码！');
			return false;
		}
		if (!(/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(sfz))) {
			mui.toast('身份证号码有误！');
			return false;
		}
		
		$('#loading').show();
		$(".submitButton").attr("disabled", "true");
		$.ajax({
			type: 'POST',
			data: {
				action: 'saveSq',
				'gsmc': encodeURI(gsmc),
				'frXm': encodeURI(xm),
				'frPhone': tel,
				'frSfzjhm': sfz,
				'code': smscode,
				'prodUuid': prodUuid,
				'qdsUuid': qdsid,
				'xzqhDm':xzqhDm,
				'qds':qds
			},
			dataType: 'json',
			url: baseUrl + 'ProdServlet',
			success: function(data) {
				console.log(data);
				if (data.result == 'success') {
					window.location.href = data.url;
				}else{
					alert(data.msg)
				}
			},
			error: function(data) {
				console.log(data)
			},
			complete: function(){
				$('#loading').hide();
				$(".submitButton").removeAttr("disabled");
			}
		})
	})
	
	
	
	
	$('.area_pick_mask').on('click', 'li', function(e) {
		e.stopPropagation();
		var dm = $(this).attr('dm');
		var dmmc = $(this).text().trim();
		$.ajax({
			type: 'POST',
			data: {
				action: 'queryCs',
				xzqhszDm: dm
			},
			url: baseUrl + 'ProdServlet',
			dataType: 'json',
			success: function(data) {
				console.log(data);
				if (data.result == 'success') {
					$scope.areaList = data.list;
					$scope.$apply();
				} else {
					xzqhDm = dm;
					localStorage.setItem("locationLocal",dm);
					localStorage.setItem("locationLocalMc",dmmc);
					$('.area span').text(dmmc.substring(0, 3))
					hideAreaMenu();
					
				}
			},
			error: function(data) {
				console.log(data)
			}
		})
	
	})
	
	$('.area').click(function() {
		$.ajax({
			type: 'POST',
			data: {
				action: 'querySf'
			},
			url: baseUrl + 'ProdServlet',
			dataType: 'json',
			success: function(data) {
				console.log(data);
				if (data.result == 'success') {
					$scope.areaList = data.list;
					$scope.$apply();
					showAreaMenu()
				}
			},
			error: function(data) {
				console.log(data)
			}
		})
	})
	
	function showAreaMenu() {
		$('.area_pick_mask').show();
		setTimeout(function() {
			$('.area_pick_mask .list').css({
				"right": "0px"
			})
		}, 10)
	}
	
	function hideAreaMenu() {
		$('.area_pick_mask .list').css({
			"right": "-200px"
		})
		setTimeout(function() {
			$('.area_pick_mask').hide();
		}, 400)
	}
	
	
});
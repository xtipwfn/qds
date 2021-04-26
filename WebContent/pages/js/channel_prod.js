var app=angular.module('myapp',[]);
app.controller('controller',function($scope){
	
	var prod_uuid = GetArgs(window.location.search,'prod_uuid');
	var prod_name = GetArgs(window.location.search,'prod_name');
	var sbm = GetArgs(window.location.search,'sbm');
	prod_name=decodeURI(prod_name);
	console.log(prod_name,sbm);
	document.title = prod_name;
	var qds =localStorage.getItem('qds');
	
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
			action: 'checkSbm',
			prodUuid: prod_uuid,
			sbm:sbm,
			qds:qds
		},
		url: baseUrl + 'ProdServlet',
		dataType: 'json',
		success: function(data) {
			console.log(data);
			if (data.result == 'fail') {
				window.location.href = "fail.html";
			}
		},
		error: function(data) {
			console.log(data)
			$('#loading').hide();
		},
		complete: function(){
			$('#loading').hide();
		}
	})
	
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
	
	
	$('.submitButton').click(function(){
		console.log(xzqhDm);
		var frXm=$("#frXm").val();
		var frPhone=$("#frPhone").val();
		var gsmc=$("#gsmc").val();
		if(frXm=="" || frXm==null || frXm=="null"){
			mui.toast('请输入申请人姓名！');
			return false;
		}
		if(frPhone=="" || frPhone==null || frPhone=="null"){
			mui.toast('请输入申请人手机号码！');
			return false;
		}
		if (!(/^1[3456789]\d{9}$/.test(frPhone))) {
			mui.toast("手机号码有误！");
			return false;
		}
		
		if(gsmc=="" || gsmc==null || gsmc=="null"){
			mui.toast('请输入企业名称！');
			return false;
		}
		$('#loading').show();
		$(".submitButton").attr("disabled", "true");
		$.ajax({
			type: 'POST',
			data: {
				action: 'prodSqSave',
				prodUuid: prod_uuid,
				frXm:encodeURI(frXm),
				frPhone:frPhone,
				gsmc: encodeURI(gsmc),
				sbm:sbm,
				xzqhDm:xzqhDm,
				qds:qds
			},
			url: baseUrl + 'ProdServlet',
			dataType: 'json',
			success: function(data) {
				console.log(data);
				if (data.result == 'success') {
					if(data.url){						
						window.location.href = data.url;
					}else{
						mui.toast(prod_name+"产品未配置银行链接地址");
					}
				} else {
					mui.toast(data.msg);
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
	});
	
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
app.controller('indexController', function($scope, $route, $routeParams) {
	$scope.$route = $route;
	$scope.areaList = [];
	$scope.prodList = [];	
	$scope.bannerList =[];
	$scope.prodFlList =[{'FL_MC':'税贷','FL_DM':'01'},{'FL_MC':'发票贷','FL_DM':'02'},
						{'FL_MC':'个人贷','FL_DM':'03'}];
	var xzqhszDm = '';
	$scope.prodFl = '01';	
	var user_uuid = localStorage.getItem('user_uuid');
	var user_name = localStorage.getItem('user_name');	
	var qds = GetArgs(window.location.search,'qds')|| localStorage.getItem('qds');;	
	localStorage.setItem('qds',qds);
	
	$(function() {
		$.ajax({
			type: 'POST',
			data: {
				action: 'queryInit',
				qds: qds,
			},
			url: baseUrl + 'index',
			dataType: 'json',
			success: function(data) {
				console.log(data);
				if (data.result == 'success') {					
					$(".logoImg").attr("src",data.LOGO);					
					$("#ljjj").html(data.LJJJ+"笔");
					$("#ljrz").html(data.LJRZ+"亿");
					$("#cpsl").html(data.CPSL+"个");
					document.title = data.TITLE;
					localStorage.setItem('qdsTitle',data.TITLE);
					if(data.bannerlist){						
						$scope.bannerList =data.bannerlist;
					}
					localStorage.setItem('kfImg',data.KF_EWM);
					localStorage.setItem('tel',data.KF_PHONE);
					
					console.log($scope.bannerList )
				} else {
					$("#ljjj").html("0笔");
					$("#ljrz").html("0亿");
					$("#cpsl").html("0个");
				}
				$scope.$apply();
			},
			error: function(data) {
				console.log(data)
			},
			complete: function() {
				$('#loading').hide();
			}
		});
	
		var positionCode = '';
		var map = new AMap.Map('container', {
			resizeEnable: true
		});
		positionCode = map.getAdcode();
		if (positionCode) {
			xzqhszDm=positionCode;
			$.ajax({
				type: 'POST',
				data: {
					action: 'queryCsName',
					xzqhszDm: positionCode,
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
			})
			queryProdList(positionCode, '01', '');
		} else {
			queryProdList('440100', '01', '');
		}
		
		$.ajax({
			type: 'POST',
			async: false,
			data: {
				action: 'checkLogin'
			},
			url: baseUrl + 'LoginServlet',
			dataType: 'json',
			success: function(data) {	
				if (data.result == 'success') {					
//					console.log("checkLogin:",data)
					if(data.XM){						
						$('.username').text(data.XM);
					}					
				} else {
					$('.username').text("您好");				
				}
			},
			error: function(data) {
				console.log(data)
			},
			complete: function() {
			}
		})
		
		$scope.toDetails = function(_bankLink, _prodUuid) {
			localStorage.setItem('bankLink', _bankLink)
			window.location.href = "prod_details.html?prodUuid=" + _prodUuid
		}


		// 搜索
		$('.button_box button').click(function() {
			var search_value = $('#search_value').val();
			if (!search_value) {
				search_value='';
			}
			queryProdList(xzqhszDm, $scope.prodFl, search_value);
		})

		// 点击分类
		$('.prod_classify').on('click', '.fl', function() {
			var index = $(this).index('.fl');	
			if (index == 0) {
				$scope.prodFl = '01';
			}else if (index == 1) {
				$scope.prodFl = '02';
			}else if (index == 2) {
				$scope.prodFl = '03';
			}
			$(this).addClass('active').siblings('.fl').removeClass('active');
			
			
			var search_value = $('#search_value').val();
			if (!search_value) {
				search_value='';
			}
			queryProdList(xzqhszDm, $scope.prodFl, search_value);
		})

		function queryProdList(_xzqhszDm, _prodFl, _prodName) {
			console.log(_xzqhszDm,_prodFl,_prodName);
			$('#loading').show();
			$.ajax({
				type: 'POST',
				data: {
					action: 'queryProd',
					xzqhszDm: _xzqhszDm,
					prodFl: _prodFl,
					prodName: encodeURI(_prodName),
					qds:qds
				},
				url: baseUrl + 'ProdServlet',
				dataType: 'json',
				success: function(data) {
//					console.log(data)
					if (data.result == 'success') {
						var list=[];
						$scope.prodList=[];
						for(var i=0;i<data.list.length;i++){
							var je=fmoney(data.list[i].MAX_JE* 10000);
							list={
								LOGO_URL:data.list[i].LOGO_URL,
								DKLL:data.list[i].DKLL,
								MAX_JE:je,
								PROD_NAME:data.list[i].PROD_NAME,
								HKFS:data.list[i].HKFS,
								DKQS:data.list[i].DKQS,
								ZSFY:data.list[i].ZSFY,
								PROD_UUID:data.list[i].PROD_UUID
							}
							$scope.prodList.push(list);
						}					
					} else {
						$scope.prodList = []
					}
					$scope.$apply();
				},
				error: function(data) {
					console.log(data)
				},
				complete: function() {
					$('#loading').hide();
				}
			})
		}
		
		mui(".mui-slider").slider({
			interval: 3000,
			infinite: true
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
						xzqhszDm = dm;
						$('.area span').text(dmmc.substring(0, 3))
						hideAreaMenu();
						var search_value = $('#search_value').val();
						if (!search_value) {
							search_value='';
						}
						queryProdList(dm, $scope.prodFl, search_value);
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

	}) //jq


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


}); //ng

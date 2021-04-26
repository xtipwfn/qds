var app = angular.module('merchantCenter', ['ngRoute']);
app.controller('mainController', function($scope, $route) {
	$scope.$route = $route;
	$scope.name = "SaaS";
	$(function() {
		//初始化当前所在菜单
		var currentItem = window.location.hash.split('?')[0];
		$('.item span').css({
			"color": "#909298"
		});
		console.log(currentItem)
		switch (currentItem) {
			case '#/loan':
				$('.item').eq(0).find('img')[0].src = "img/menu01_1.png";
				$('.item').eq(0).find('span').css({
					"color": "#C41B20"
				});
				break;
			case '#/spread':
				$('.item').eq(1).find('img')[0].src = "img/menu02_1.png";
				$('.item').eq(1).find('span').css({
					"color": "#C41B20"
				});
				break;
			case '#/mine':
				$('.item').eq(2).find('img')[0].src = "img/menu03_1.png";
				$('.item').eq(2).find('span').css({
					"color": "#C41B20"
				});
				break;
			default:
				break;
		}

		//控制路由跳转
		$('footer').on('click', '.item', function() {
			var index = $(this).index('.item');

			var routeLocation = $(this).attr('routeTo');
			// if(  routeLocation=="#/mine"  ){
			// 	window.location.href=baseUrl+'MyInfoServlet?action=myInfoInit'
			// }else if(  routeLocation=="#/news"  ){
			//  window.location.href=baseUrl+'NewsServlet?action=init'
			// }else if(  routeLocation=="#/qAnda"  ){
			//  window.location.href=baseUrl+'QaServlet?action=init'
			// }else{      	
			// 	window.location.href=routeLocation;
			// }
			
//			if( routeLocation=="#/spread" ){
//				mui.toast('维护中')
//				return;
//			}

			window.location.href = routeLocation+'?'+window.location.hash.split('?')[1];
			$('.item span').css({
				"color": "#909298"
			});
			switch (index) {
				case 0:
					$('.item').eq(0).find('img')[0].src = "img/menu01_1.png";
					$('.item').eq(1).find('img')[0].src = "img/menu02_0.png";
					$('.item').eq(2).find('img')[0].src = "img/menu03_0.png";
					$('.item').eq(0).find('span').css({
						"color": "#C41B20"
					});
					break;
				case 1:
					$('.item').eq(0).find('img')[0].src = "img/menu01_0.png";
					$('.item').eq(1).find('img')[0].src = "img/menu02_1.png";
					$('.item').eq(2).find('img')[0].src = "img/menu03_0.png";
					$('.item').eq(1).find('span').css({
						"color": "#C41B20"
					});
					break;
				case 2:
					$('.item').eq(0).find('img')[0].src = "img/menu01_0.png";
					$('.item').eq(1).find('img')[0].src = "img/menu02_0.png";
					$('.item').eq(2).find('img')[0].src = "img/menu03_1.png";
					$('.item').eq(2).find('span').css({
						"color": "#C41B20"
					});
					break;
				default:
					break;
			}
		})


	})
})

app.config(function($routeProvider) {

	$routeProvider.
	when('/loan', {
		templateUrl: 'components/bar_index.html',
		controller: 'indexController'
	}).
	when('/spread', {
//		templateUrl: 'components/bar_member.html',
		templateUrl:'components/bar_spread.html',
		controller: 'spreadController'
	}).
	when('/mine', {
		templateUrl: 'components/bar_mine.html',
		controller: 'mineController'
	}).
	otherwise({
		redirectTo: '/loan'
	});
});

function GetArgs(params, paramName) {
	var argsIndex = params.indexOf("?");
	var arg = params.substring(argsIndex + 1);
	args = arg.split("&");
	var valArg = "";
	for (var i = 0; i < args.length; i++) {
		str = args[i];
		var arg = str.split("=");

		if (arg.length <= 1) continue;
		if (arg[0] == paramName) {
			valArg = arg[1];
		}
	}
	return valArg;
}


// var ajaxBack = $.ajax;
// var ajaxCount = 0;
// var allAjaxDone = function(){
// 	$('#loading').hide();
// }
// $.ajax = function(setting){
// 	if( setting.data.list && setting.data.list>1 ){
// 		ajaxCount++;
// 		var cb = setting.complete;
// 	    ajaxBack(setting);
// 	}else{
// 	    ajaxCount++;
// 	    var loading=$('<div id="loading"><img src="imgs/loading.png"/></div>');
// 			$(loading).appendTo('body');
// 	    var cb = setting.complete;
// 	    setting.complete = function(){
// 				$('#loading').remove();
// 	    }
// 	    ajaxBack(setting);	
// 	}
// }

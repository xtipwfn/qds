var app=angular.module('myapp',[]);
app.controller('controller',function($scope){
	$scope.list=[];
	var qds =localStorage.getItem('qds');
	$('#loading').show();
	
	//初始化信息
	var clickSearch=false;
	var _yjbz='N';
	var _phone="";
	
	init(_phone,_yjbz);
	
	function init(){
		_phone=$('#search_phone').val();	
		console.log(_phone,_yjbz);
		$.ajax({
			type: 'POST',
			data: {
				action: 'querySymx',
				"phone":_phone,
				"yjbz":_yjbz,
				"qds":qds
			},
			url: baseUrl + 'UserServlet',
			dataType: 'json',
			success: function(data) {
				console.log(data);
				if (data.result == 'success') {		
					if(data.HEAD_URL){						
						$('.headUrl').attr("src", data.HEAD_URL);
					}else{
						$('.headUrl').attr("src", "img/default.png");
					}
					$('.username').html(data.XM);
					$('.phone').html(data.PHONE);
					$('.yjhhr').html(data.YJHHR);
					$('.hhrtd').html(data.HHRTD);
					var bytdzsy=gettoDecimal(data.BYTDZYJ);
					$('.bytdzsy').html(bytdzsy);
					var syzyj=gettoDecimal(data.SYTDZYJ);
					$('.syzyj').html(syzyj);
					
					var tdmx_list=[];	
					var gryj="";
					var tdyj="";
					$scope.list=[];
					if(data.tdmx.length>0){						
						for(var i=0;i<data.tdmx.length;i++){
							gryj=gettoDecimal(data.tdmx[i].GRYJ);
							tdyj=gettoDecimal(data.tdmx[i].TDYJ);
							tdmx_list={
									HEAD_URL:data.tdmx[i].HEAD_URL || 'img/default.png',
									XM:data.tdmx[i].XM,
									PHONE:data.tdmx[i].PHONE,
									GRYJ:gryj,
									TDYJ:tdyj,
									LRSJ:data.tdmx[i].LRSJ,
									TDRS:data.tdmx[i].TDRS
							}					
							$scope.list.push(tdmx_list);
						}
						$(".team_subor").css("padding","10px");
					}else{
						$(".team_subor").css("padding","inherit");
					}
				}else{
					if(clickSearch==true){
						$('.headUrl').attr("src", data.HEAD_URL);
						$('.username').html(data.XM);
						$('.phone').html(data.PHONE);
						$('.yjhhr').html(data.YJHHR);
						$('.hhrtd').html(data.HHRTD);								
						var bytdzsy=gettoDecimal(data.BYTDZYJ);
						$('.bytdzsy').html(bytdzsy);
						var syzyj=gettoDecimal(data.SYTDZYJ);
						$('.syzyj').html(syzyj);
						$scope.list=[];
					}else{						
						$('.headUrl').attr("src", "");
						$('.username').html("");
						$('.phone').html("");
						$('.yjhhr').html("");
						$('.hhrtd').html("");						
						$('.bytdzsy').html("");
						$('.syzyj').html("");
						$scope.list=[];
					}
					mui.toast(data.msg);
				}
				$scope.$apply();
			},
			error: function(data) {
				console.log(data);
			},
			complete: function(){
				$('#loading').hide();
			}
		});
		
	}
	
	//搜索
	$('#search').click(function(){		
		_yjbz='N';
		clickSearch=true;
		init();
	});

	//查看有业绩的下级
	$('.search_xjyj').click(function(){	
		_yjbz='Y';
		clickSearch=true;
		init();
	});
	
});
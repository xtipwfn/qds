	var app=angular.module('myapp',[]);
	app.controller('controller',function($scope){
		var qds =localStorage.getItem('qds');
		$scope.list = [];
		
		$('#loading').show();
		$.ajax({
			type: 'POST',
			data: {
				action: 'queryQuestion',
				qds:qds
			},
			url: baseUrl + 'UserServlet',
			dataType: 'json',
			success: function(data) {
				console.log(data);
				if (data.result == 'success') {
					$scope.list = data.list;
				} else {
					mui.toast(data.msg);
					$scope.list = [];
				}
				$scope.$apply();
			},
			error: function(data) {
				console.log(data)
			},
			complete: function(){
				$('#loading').hide();
			}
		})

});
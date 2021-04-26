var app=angular.module('myapp',[]);
app.controller('controller',function($scope){
	$scope.list = [];
	var qds =localStorage.getItem('qds');
	
	$.ajax({
		type: 'POST',
		data: {
			action: 'querySq',
			userUuid:localStorage.getItem('user_uuid')?localStorage.getItem('user_uuid'):'',
			qds:qds
		},
		url: baseUrl + 'ProdServlet',
		dataType: 'json',
		success: function(data) {
			console.log(data);
			if (data.result == 'success') {
				$scope.list = data.list;
			} else {
				$scope.list = [];
			}
			$scope.$apply();
		},
		error: function(data) {
			console.log(data)
		}
	})
})
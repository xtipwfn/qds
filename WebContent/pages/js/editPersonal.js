$(function(){
	var user_uuid = localStorage.getItem('user_uuid') || '';
	console.log(user_uuid);
	var picker_job = new mui.PopPicker();
	var qds =localStorage.getItem('qds');
	
	var username="";
	var phone="";
	var passWord="";
	var idCard="";
	var zfb="";	
	var zyDm="";
	var zyMc="";
	
	$('#submitbtn').click(function(){
		username=$('#username').val();
		if( username.length<=0 ){
			alert('请输入姓名');
			return false;
		}
		
		phone=$('#phone').val();
		passWord=$('#passWord').val();
		idCard=$('#idCard').val();
		zfb=$('#zfb').val();
		if(idCard){
			const regIdCard = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
			if (!regIdCard.test(idCard)) {
				alert('身份证号填写有误');
				return false;
			}			
		}
		$.ajax({
			url: baseUrl + 'LoginServlet',
			type: 'POST',
			async: true,
			data: {
				action: "saveUser",
				user_uuid:user_uuid,
				xm:encodeURI(username),
				phone:phone,
				pwd:passWord,
				sfzjhm:idCard,
				zfbzh:zfb,
				zw:encodeURI(zyMc),
				qds:qds
			},
			timeout: 60000,
			dataType: 'json',
			success: function(data, textStatus, jqXHR) {
				console.log(data);
				if(data.result=="success"){				
					alert(data.msg);
					window.location.href = "index.html#/mine";
				}else{
					alert(data.msg);
				}
			},
			error: function(xhr, textStatus) {
				console.log(xhr);
			}
		});
		
	})
	//初始化查询用户信息
	$.ajax({
		url: baseUrl + 'LoginServlet',
		type: 'POST',
		async: true,
		data: {
			action: "queryUser",
			user_uuid:user_uuid,
			qds:qds
		},
		timeout: 60000,
		dataType: 'json',
		success: function(data, textStatus, jqXHR) {
			console.log(data);
			if(data.result=="success"){				
				$('#username').val(data.XM);
				$('#passWord').val(data.PWD);
				$('#phone').val(data.PHONE);
				$('#idCard').val(data.SFZJHM);
				$('#zfb').val(data.ZFBZH);
				$('#job').val(data.ZW);
				zyMc=data.ZW;
				console.log(data.HEAD_URL);
				if(data.HEAD_URL){
					$('#headUrl').attr("src", data.HEAD_URL);
				}else{
					$('#headUrl').attr("src", "img/default.png");
				}
			}else{
				alert(data.msg)
			}
		},
		error: function(xhr, textStatus) {
			console.log(xhr);
		}
	});
	
	//初始化查询代码表用户类型
	$.ajax({
		url: baseUrl + 'CommonServlet',
		type: 'POST',
		async: true,
		data: {
			action: "queryDmb",
			dmType:"职位"
		},
		timeout: 60000,
		dataType: 'json',
		success: function(data, textStatus, jqXHR) {
			console.log(data);		
	    	var job_list=[];
			if(data.result=="success"){
				for(var i=0;i<data.dmList.length;i++){
					var obj={};
					obj.value=data.dmList[i].DM;
					obj.text=data.dmList[i].DMMC;
					job_list.push(obj);
				}			
				picker_job.setData( job_list );
			}
		},
		error: function(xhr, textStatus) {
			console.log(xhr);
		}
	});
	
	$('#job').click(function(){
		picker_job.show(function (selectItems) {
			zyDm=selectItems[0].value;
			zyMc=selectItems[0].text;
			
			$('#job').val(zyMc);
		})
	})
	
})

$(function(){	
	//初始化参数获取
	var prod_uuid=GetArgs(window.location.href,'prod_uuid');
	console.log(prod_uuid);
	var qds =localStorage.getItem('qds');
	
	//确认提交
	$('.submitButton').click(function(){
		var xm=$('#xm').val();		
		if (xm == null || xm == "") {
			mui.toast("姓名不能为空！");
			return false;
		}
		var sfzhm=$('#sfzhm').val();		
		if (sfzhm == null || sfzhm == "") {
			mui.toast("身份证件号码不能为空！");
			return false;
		}
		
		var phone=$('#phone').val();
		if (phone == null || phone == "") {
			mui.toast("手机号码不能为空！");
			return false;
		}
		if (!(/^1[3456789]\d{9}$/.test(phone))) {
			mui.toast("手机号码有误！");
			return false;
		}
		var ajfcdz=$('#ajfcdz').val();
		if (ajfcdz == null || ajfcdz == "") {
			mui.toast("按揭房产地址不能为空！");
			return false;
		}
		$('#loading').show();
		$.ajax({
			url : baseUrl+'ProdServlet',
			type : 'POST',
			async : true,
			data : {
				action: "saveBbxx",
				prodUuid:prod_uuid,
				xm: encodeURI(xm),
				sfzjhm:sfzhm,
				phone: phone,
				dz:encodeURI(ajfcdz),
				qds:qds
			},
			timeout : 60000,
			dataType : 'json',
			success : function(data, textStatus, jqXHR) {
				console.log(data);
				if(data.result=="success"){
					if(data.url){						
						window.location.href = data.url;						
					}
				}else{
					mui.toast(data.msg);
				}
				$('#loading').hide();
			},
			error : function(xhr, textStatus) {
					console.log(xhr);	
					$('#loading').hide()
			}
//			,complete: function(){
//				$('#loading').hide()
//			}
		});
			
	});
	
});
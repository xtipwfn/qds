$(function(){
	var prodUuid = GetArgs(window.location.search,'prodUuid');
	var qdsid = GetArgs(window.location.search,'qdsid');
	var prodName = '';
	var qds = GetArgs(window.location.search,'qds') || localStorage.getItem('qds');
	
	if(qdsid){
		$('.footer #yjtg').remove();
		$('.footer span').remove();
	}
	
	$.ajax({
		type: 'POST',
		data: {
			action: 'queryProdDetils',
			prodUuid: prodUuid,
			qds:qds
		},
		url: baseUrl + 'ProdServlet',
		dataType: 'json',
		success: function(data) {
			console.log(data);
			if (data.result == 'success') {
				prodName = data.list[0].PROD_NAME;
				document.title = data.list[0].PROD_NAME;				
				$('#PROD_NAME').text(data.list[0].PROD_NAME);
				var je = fmoney(data.list[0].MAX_JE* 10000);
				
				console.log(je);
				$('#MAX_JE').text(je);
				
				if(data.list[0].JSZQ){
					$('#JSZQ').text(data.list[0].JSZQ);
				}else{
					$('#JSZQ').text('0');
				}
				
				if(data.list[0].LOGO_URL){					
					document.getElementById("LOGO_URL").src = data.list[0].LOGO_URL;
				}
				$('#MAX_JE_2').text(data.list[0].MAX_JE);
				$('#DKQS').text(data.list[0].DKQS);
				$('#JJED').text(data.list[0].JJED);
				$('#DKLL').text(data.list[0].DKLL);
				$('#HKFS').text(data.list[0].HKFS);
				$('#DKQS_2').text(data.list[0].DKQS);
				if(data.list[0].YGMYHK){
					$('#YGMYHK').text(data.list[0].YGMYHK);
				}
				
				$('#BYFY').text(data.list[0].BYFY);
				$('#HJFY').text(data.list[0].HJFY);
				$('#ZSFY').text(data.list[0].ZSFY);				
				$('#zgjl').text(data.list[0].ZSFY);
				$('#ZKFKSJ').html(data.list[0].JSSM);
				$('#DQ').text(data.list[0].DQ);
				$('#PROD_TD').html(data.list[0].PROD_TD);
				$('#PROD_TJ').html(data.list[0].PROD_TJ);				
				
				if(data.list[0].PROD_SXZL){
					$('#PROD_SXZL').html(data.list[0].PROD_SXZL);
					$('.sxzl').show();
				}
				
				if(data.list[0].PROD_LCSM){
					$('#PROD_LCSM').html(data.list[0].PROD_LCSM);
					var lc=data.list[0].PROD_LCSM;
					lc=lc.slice(0,4);
					console.log(lc);
					$('.sqlcsm').show();
					if(lc!="http"){						
						$('.lcsm').show();
						$('#toGuide').hide();
					}else{
						$('.lcsm').hide();
						$('#toGuide').show();
						
					}
				}
				if(data.list[0].PROD_SM){
					$('#PROD_SM').html(data.list[0].PROD_SM);
					$('.cpsm').show();
				}
				
				
				
				
			} else {
				
			}
		},
		error: function(data) {
			console.log(data)
		}
	})
	
	$('#ljsq').click(function(){
		window.location.href = "apply.html?prodUuid="+prodUuid+'&qdsid='+qdsid
	})
	
	$('#yjtg').click(function(){
		
		var user_uuid = localStorage.getItem('user_uuid');
		var user_name = localStorage.getItem('user_name');
		
		window.location.href = baseUrl + 'ProdServlet?action=getTghbImg&userUuid=' +
		user_uuid + '&prodName=' +
		prodName + '&prodUuid=' +
		prodUuid+'&qds=' +
		qds
		
		// 192.168.1.236:8080/ryx/ProdServlet?action=getTghbImg&userUuid=12323&prodName=XX贷&prodUuid=sssie
		// userUuid：用户UUID（必录）
		// prodUuid：产品UUID(必录)
		// prodName：产品名称
	})
	
	$('#toGuide').click(function(){
		var lcsm_url=$('#PROD_LCSM').html();
		window.location.href = lcsm_url;
	})
})
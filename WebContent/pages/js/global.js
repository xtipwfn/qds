//var baseUrl="../";
var baseUrl="http://192.168.1.8:8080/qds/";

//公用取参函数
function GetArgs(params,paramName){
    var argsIndex = params.indexOf("?");
    var arg = params.substring(argsIndex+1);
    args = arg.split("&");
    var valArg = "";
    for(var i =0;i<args.length;i++){
	    str = args[i];
	    var arg = str.split("=");
	  
	    if(arg.length<=1) continue;
	    if(arg[0] == paramName){
	       valArg = arg[1];
	    }
    }
    return valArg;
}
function gettoDecimal(num) {
    var result = parseFloat(num);
    if (isNaN(result)) {
        return false;
    }
    result = Math.round(num * 100) / 100;
    var s_x = result.toString();
    var pos_decimal = s_x.indexOf('.');
    if (pos_decimal < 0) {
        pos_decimal = s_x.length;
        s_x += '.';
    }
    while (s_x.length <= pos_decimal + 2) {
        s_x += '0';
    }
    return s_x;
}
function fmoney(s){ 
   s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(0)+ ""; 
   var l = s.split(".")[0].split("").reverse(),  
   r = s.split(".")[1];  
   var t = "";  
   for(var i = 0; i < l.length; i ++ )  
   {  
      t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");  
   }  
   return t.split("").reverse().join("");  
}

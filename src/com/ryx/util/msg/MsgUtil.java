package com.ryx.util.msg;

import java.io.IOException;

import org.json.JSONException;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
/**
 * 发送消息公共类
 */
public class MsgUtil {
	/**
	 * 腾讯云发送消息
	 * @param phone
	 * @param msg 以【企数标普】开头的短信消息
	 * @throws HTTPException
	 * @throws JSONException
	 * @throws IOException
	 */
	public static SmsSingleSenderResult sendMsg(String phone,String msg) throws HTTPException, JSONException, IOException{
		int appid = 1400393720;//appid固定值
        String appkey = "d6af14f64ccff0e9d3b71c9fe063679f";//腾讯云账号建的项目appkey
		  //初始化单发
        SmsSingleSender singleSender = new SmsSingleSender(appid, appkey);
        SmsSingleSenderResult singleSenderResult;
      //普通单发
        singleSenderResult = singleSender.send(0, "86", phone, msg, "", "");
        System.out.println(new String(singleSenderResult.toString().getBytes(),"utf-8"));
		return singleSenderResult;
	}
	public static void main(String[] args) throws HTTPException, JSONException, IOException {
		sendMsg("13119548507", "【融亿鑫企服】您的验证码为：145287，请于5分钟内填写。如非本人操作，请忽略本短信。");
	}
}

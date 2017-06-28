package com.fengyang.tallynote.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetReceiver extends BroadcastReceiver {

	public static String NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	public static OnNetEventHandler handler;

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(NET_CHANGE_ACTION)){
			handler = getHandler();
			if(handler != null){
				handler.onNetChange();
			}

		}
	}

	public interface OnNetEventHandler {
		void onNetChange();
	}

	/**
	 * 注册回调
	 * @return
	 */
	public static void registerHandler(OnNetEventHandler netEventHandler){
		handler = netEventHandler;
	}

	/**
	 * 调用
	 * @return
	 */
	private static OnNetEventHandler getHandler(){
		return handler;
	}

}

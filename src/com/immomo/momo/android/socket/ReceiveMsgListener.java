package com.immomo.momo.android.socket;

import com.immomo.momo.android.entity.Message;

/**
 * 接收消息监听的listener接口
 * @author ccf
 *
 */
public interface ReceiveMsgListener {
	public boolean receive(Message msg);

}

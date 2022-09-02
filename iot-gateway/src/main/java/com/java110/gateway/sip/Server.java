package com.java110.gateway.sip;

import com.java110.gateway.sip.callback.OnProcessListener;
import com.java110.gateway.sip.codec.CommonParser;
import com.java110.gateway.sip.codec.Frame;
import com.java110.gateway.sip.remux.Observer;


import java.util.concurrent.ConcurrentLinkedDeque;

public  abstract class Server extends CommonParser implements Observable {

	protected Observer observer;
	
	public abstract  void startServer(ConcurrentLinkedDeque<Frame> frameDeque, int ssrc, int port, boolean checkSsrc);
	public abstract  void stopServer();
	
	public OnProcessListener onProcessListener = null;

	@Override
	public void subscribe(Observer observer) {
		this.observer = observer;
	}
	
	@Override
	protected void onMediaStreamCallBack(byte[] data,int offset,int length,boolean isAudio){
		if(observer != null){
			observer.onMediaStream(data, offset, length,isAudio);
		}
	}
	
	@Override
	protected void onPtsCallBack(long pts,boolean isAudio){
		if(observer != null){
			observer.onPts(pts,isAudio);
		}
	}
	public void setOnProcessListener(OnProcessListener onProcessListener){
		this.onProcessListener = onProcessListener;
	}
}

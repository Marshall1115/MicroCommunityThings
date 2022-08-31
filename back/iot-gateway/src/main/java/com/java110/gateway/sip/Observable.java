package com.java110.gateway.sip;


import com.java110.gateway.sip.remux.Observer;

public interface Observable {

    public void subscribe(Observer observer);
}

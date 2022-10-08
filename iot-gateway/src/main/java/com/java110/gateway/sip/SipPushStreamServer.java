package com.java110.gateway.sip;


import com.java110.gateway.sip.remux.Observer;

import java.io.Serializable;

public class SipPushStreamServer implements Serializable{

    public SipPushStreamServer() {
    }

    public SipPushStreamServer(Observer observer, Server server) {
        this.observer = observer;
        this.server = server;
    }

    private  Observer observer;

   private Server server;

    public Observer getObserver() {
        return observer;
    }

    public void setObserver(Observer observer) {
        this.observer = observer;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}

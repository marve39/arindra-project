/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.socket;

import com.nokia.gdc.Application;
import java.net.InetSocketAddress;
import java.util.Date;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 *
 * @author Arindra
 */
public class NetactAlarmForwardingHandler extends IoHandlerAdapter {

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) session.getServiceAddress();
        Application.addMessageCounter(inetSocketAddress.getPort(), 1, 0);

        String str = message.toString();
        if (str.trim().equalsIgnoreCase("quit")) {
            session.close();
            return;
        }

        Date date = new Date();
        session.write(date.toString());
        System.out.println("Message written...");

        Application.addMessageCounter(inetSocketAddress.getPort(), 0, 1);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        System.out.println("IDLE " + session.getIdleCount(status));
    }
}

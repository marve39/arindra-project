/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nokia.gdc.socket.NetactAlarmForwardingHandler;
import java.nio.charset.Charset;
import java.util.Calendar;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 *
 * @author Arindra
 */
@Data
@RequiredArgsConstructor
public class SocketInfo {

    private final String socketName;
    private final Integer port;
    private Boolean isActive = false;
    private Boolean isDisposing = false;
    private Boolean isDisposed = false;
    private Integer incomingCounter = 0;
    private Integer processingCounter = 0;
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm")
    private Calendar createdTime;
    private Long lifeTime;

    @JsonIgnore
    private IoAcceptor acceptor;

    protected SocketInfo() {
        this(null, null);
    }

    //   public void setNewAcceptor(IoAcceptor acceptor) {
    //       this.acceptor = acceptor;
    //   }
    public void prepareAcceptor() {
        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("logger", new LoggingFilter(socketName));
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

        acceptor.setHandler(new NetactAlarmForwardingHandler());
        acceptor.getSessionConfig().setReadBufferSize(2048);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        
        incomingCounter = 0;
        processingCounter = 0;
        lifeTime = new Long(0);
    }

    public void socketCreated() {
        createdTime = Calendar.getInstance();
    }

    public void refreshInfo() {
        if (createdTime != null && isActive) {
            lifeTime = (Calendar.getInstance().getTimeInMillis() - createdTime.getTimeInMillis()) / 1000;
        }
        if (acceptor != null) {
            isActive = acceptor.isActive();
            isDisposing = acceptor.isDisposing();
            isDisposed = acceptor.isDisposed();
        }
    }
    
    public void addIncomingCounter(Integer counter){
        incomingCounter = incomingCounter + counter;
    }
    
    public void addProcessingCounter(Integer counter){
        processingCounter = processingCounter + counter;
    }
}

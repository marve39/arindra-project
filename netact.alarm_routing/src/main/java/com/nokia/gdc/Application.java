package com.nokia.gdc;

import com.nokia.gdc.domain.SocketInfo;
import com.nokia.gdc.exception.SocketAlreadyRunningException;
import com.nokia.gdc.exception.SocketNotFoundException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.net.InetSocketAddress;
import java.util.HashMap;

import org.apache.mina.core.service.IoAcceptor;

@SpringBootApplication
@EnableDiscoveryClient //http://cloud.spring.io/spring-cloud-zookeeper/spring-cloud-zookeeper.html
public class Application {

    private static final HashMap<Integer, SocketInfo> socketListener = new HashMap<Integer, SocketInfo>();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public static SocketInfo stopNetactAlarmListener(SocketInfo socketInfo) throws SocketNotFoundException {

        if (socketListener.containsKey(socketInfo.getPort())) {
            socketInfo = socketListener.get(socketInfo.getPort());
            IoAcceptor acceptor = socketListener.get(socketInfo.getPort()).getAcceptor();
            acceptor.unbind();
            acceptor.dispose();
            return socketInfo;
        } else {
            throw new SocketNotFoundException("");
        }
    }

    public static SocketInfo refreshNetactAlarmListenerInfo(SocketInfo socketInfo) throws SocketNotFoundException {
        if (socketListener.containsKey(socketInfo.getPort())) {
            return socketListener.get(socketInfo.getPort());
        } else {
            throw new SocketNotFoundException("");
        }
    }
    
    public static void addMessageCounter(Integer port, Integer incomingCounter, Integer processingCounter){
        if (socketListener.containsKey(port)) {
           SocketInfo socketInfo = socketListener.get(port);
           socketInfo.addIncomingCounter(incomingCounter);
           socketInfo.addProcessingCounter(processingCounter); 
        }
    }

    public static SocketInfo startNetActAlarmListener(SocketInfo socketInfo) throws Exception {
        boolean isCreated = false;

        if (socketListener.containsKey(socketInfo.getPort())) {
            socketInfo = socketListener.get(socketInfo.getPort());
            isCreated = true;
            if (socketInfo.getAcceptor().isActive()) {
                throw new SocketAlreadyRunningException("");
            }
        }
        IoAcceptor acceptor;
        socketInfo.prepareAcceptor();
        acceptor = socketInfo.getAcceptor();

        acceptor.bind(new InetSocketAddress(socketInfo.getPort()));
        socketInfo.socketCreated();
        if (!isCreated) {
            socketListener.put(socketInfo.getPort(), socketInfo);
        }
        return socketInfo;

    }
}

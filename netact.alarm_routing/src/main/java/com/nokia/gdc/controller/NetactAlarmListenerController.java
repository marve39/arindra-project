/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.controller;

import com.nokia.gdc.Application;
import com.nokia.gdc.domain.SocketInfo;
import com.nokia.gdc.exception.SocketAlreadyRunningException;
import com.nokia.gdc.exception.SocketNotFoundException;
import java.io.IOException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author Arindra
 */
@RestController
public class NetactAlarmListenerController {
    
    //com.nokia.gdc.socket.NetactAlarmForwardingHandler
     @RequestMapping("/socket/start/{port}/{handlerClass}")
    public SocketInfo create(@PathVariable("port") Integer port, @PathVariable("handlerClass") String handlerClass){
        SocketInfo socketInfo = new SocketInfo("[" + port + "]",port,handlerClass);
        try{
            socketInfo = Application.startNetActAlarmListener(socketInfo);
            socketInfo.setMessage("[SUCCESS] - Socket Successfully Created");
        }catch(SocketAlreadyRunningException socRun){
            try{
                socketInfo = Application.refreshNetactAlarmListenerInfo(socketInfo);
                socketInfo.setMessage("[INFO] - Socket Already Running");
            }catch (SocketNotFoundException socNotFound){
                socketInfo.setMessage("[ERROR] - Inconsistance data on HashMap");
            }
        }catch(IOException ioEx){
            socketInfo.setMessage("[ERROR] - " + ioEx.getMessage());
        }catch(Exception ex){
            socketInfo.setMessage("[ERROR] - " + ex.getMessage());
            ex.printStackTrace();
        }
        socketInfo.refreshInfo();
        return socketInfo;
    }
    
    @RequestMapping("/socket/stop/{port}")
    public SocketInfo stop(@PathVariable("port") Integer port){
        SocketInfo socketInfo = new SocketInfo("[" + port + "]",port,"");
        try{
            socketInfo = Application.stopNetactAlarmListener(socketInfo);
            socketInfo.setMessage("[SUCCESS] - Socket Successfully Stopped");
        }catch (SocketNotFoundException socNotFound){
                socketInfo.setMessage("[ERROR] - Socket Not Found On System");
        }
        socketInfo.refreshInfo();    
        return socketInfo;
    }
    
     @RequestMapping("/socket/status/{port}")
    public SocketInfo status(@PathVariable("port") Integer port){
        SocketInfo socketInfo = new SocketInfo("[" + port + "]",port,"");
        try{
            socketInfo = Application.refreshNetactAlarmListenerInfo(socketInfo);
            socketInfo.setMessage("[SUCCESS] - Socket Return Status");
        }catch (SocketNotFoundException socNotFound){
                socketInfo.setMessage("[ERROR] - Socket Not Found On System");
        }
        socketInfo.refreshInfo();
        return socketInfo;
    }

}

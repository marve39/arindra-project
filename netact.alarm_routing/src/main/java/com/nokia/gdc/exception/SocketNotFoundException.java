/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.exception;

/**
 *
 * @author Arindra
 */
public class SocketNotFoundException extends Exception {
    public SocketNotFoundException(String message){
        super(message);
    }
}

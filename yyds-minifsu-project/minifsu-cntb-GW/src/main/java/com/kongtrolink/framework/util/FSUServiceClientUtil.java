/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kongtrolink.framework.util;


import com.kongtrolink.framework.scservice.Invoke;
import com.kongtrolink.framework.scservice.InvokeResponse;
import com.kongtrolink.framework.scservice.SCServiceServiceStub;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 *
 * @author 
 */
public class FSUServiceClientUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FSUServiceClientUtil.class);
    
    public static String sendReq(String message,String ip,int port) {
        try {
            org.apache.axis2.databinding.types.soapencoding.String param = 
                    new org.apache.axis2.databinding.types.soapencoding.String();
            param.setString(message);
            LOGGER.info("--------------------------****************---------------------------------");
            LOGGER.info("[LSCService Web Client]  request...\n"+param.getString());
            LOGGER.info("--------------------------****************---------------------------------");
			Invoke invokeRequest = new Invoke();
            invokeRequest.setXmlData(param);
            SCServiceServiceStub stub = new SCServiceServiceStub(ip,port);
            InvokeResponse invokeResponse = stub.invoke(invokeRequest);
            LOGGER.info("--------------------------****************---------------------------------");
            LOGGER.info("[LSCService Web Client]  response...\n"+invokeResponse.getInvokeReturn());
            LOGGER.info("--------------------------****************---------------------------------");
            return invokeResponse.getInvokeReturn().toString();
        } catch (AxisFault ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		return null;
    }
}

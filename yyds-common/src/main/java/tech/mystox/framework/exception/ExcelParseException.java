/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tech.mystox.framework.exception;

/**
 * Excel解析异常
 * @author Jasper
 */
public class ExcelParseException extends Exception {

    public ExcelParseException() {
    }

    public ExcelParseException(String message) {
        super(message);
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kongtrolink.framework.gateway.tower.core.entity.base;

import javax.xml.bind.annotation.*;

/**
 *
 * @author Mag
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name","code" })
@XmlRootElement(name = "PK_Type")
public class PKType {
    
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Code")
    protected int code;

    public PKType() {
    }

    public PKType(String name,int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

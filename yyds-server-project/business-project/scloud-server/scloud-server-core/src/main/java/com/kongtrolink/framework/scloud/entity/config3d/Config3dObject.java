/** *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ****************************************************** */
package com.kongtrolink.framework.scloud.entity.config3d;

import java.io.Serializable;
import java.util.List;

/**
 * copy form scloud
 * @author Mag
 */
public class Config3dObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4042405080133687230L;
	Config3dInfo name;
	List<Double> matrix;
	String type;
	List<Config3dObject> children;

	public Config3dInfo getName() {
		return name;
	}

	public void setName(Config3dInfo name) {
		this.name = name;
	}

	public List<Double> getMatrix() {
		return matrix;
	}

	public void setMatrix(List<Double> matrix) {
		this.matrix = matrix;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Config3dObject> getChildren() {
		return children;
	}

	public void setChildren(List<Config3dObject> children) {
		this.children = children;
	}

    @Override
    public String toString() {
        return "Config3dObject{" + "name=" + name + ", matrix=" + matrix + ", type=" + type + ", children=" + children + '}';
    }

}

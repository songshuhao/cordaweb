package com.greenbirdtech.blockchain.cordapp.webdiamond.cfg;

import java.io.Serializable;

public class NotaryStruct implements Serializable 
{
	private static final long serialVersionUID = -6801779784360140083L;

	private boolean validating;

	public boolean isValidating() {
		return validating;
	}

	public void setValidating(boolean validating) {
		this.validating = validating;
	}
}

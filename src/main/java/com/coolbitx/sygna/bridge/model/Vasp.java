package com.coolbitx.sygna.bridge.model;

import java.util.ArrayList;

public class Vasp {

	private String signature;
	private ArrayList<VaspDetail> vasp_data;
	public Vasp() {

	}
	public String getSignature() {
		return signature;
	}
	public ArrayList<VaspDetail> getVasp_data() {
		return vasp_data;
	}
	
}

package pkg.skapik.icp.func;

import java.util.ArrayList;

public class Key_Group {
	private String name;
	private ArrayList<Integer> codes;
	
	public Key_Group(String name){
		this.name = name;
		this.codes = new ArrayList<>();
	}
	
	public void Register_key(int key_code){
		this.codes.add(key_code);
	}
	
	public String GimmeYourName(){
		return this.name;
	}
	
}

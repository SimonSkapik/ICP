package pkg.skapik.icp.func;

import java.util.ArrayList;

public class Key_Manager {
	private ArrayList<Key_Group> key_groups;
	
	public Key_Manager() {
		key_groups = new ArrayList<>();
	}
	
	public void Register_key(String group_name, int key_code) {
		int group_index = group_exists(group_name);
		if(group_index < 0){
			this.Create_group(group_name);
			group_index = (this.key_groups.size()-1);
		}
		this.key_groups.get(group_index).Register_key(key_code);
	}

	private void Create_group(String group_name) {
		this.key_groups.add(new Key_Group(group_name));
	}

	private int group_exists(String group_name) {
		int i = -1;
		for(Key_Group group : this.key_groups){
			if(group.GimmeYourName().equals(group_name)){
				i = this.key_groups.indexOf(group);				 
				break; 
			}
		}
		return i;
	}
	
	public String get_group_name_of_key(int key_code){
		String group_name = "NONE";
		
		return group_name;
	}
	

	
}

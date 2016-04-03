package pkg.skapik.icp.func;

import pkg.skapik.icp.assets.Block;

public class Crafting_recipe {
	
	public static int[] check_recipe(int[] parts, int workstation){
		int[] result = new int[4];
		result[0] = -1;
		result[1] = 0;
		result[2] = -1;
		result[3] = 0;
		switch(workstation){
			case Block.WORKBENCH:{
				if(parts[0] == Block.OAK_LOG && parts[1] == -1 && parts[2] == -1 && parts[3] == -1 && parts[4] == -1 && parts[5] == -1 && parts[6] == -1 && parts[7] == -1 && parts[8] == -1){
					result[0] = Block.PLANKS;
					result[0] = 4;
					return result; 
				}
				if(parts[0] == Block.STONE && parts[1] == Block.ORE_DIAMOND && parts[2] == Block.STONE && 
				   parts[3] == Block.PLANKS && parts[4] == Block.WORKBENCH && parts[5] == Block.PLANKS && 
				   parts[6] == Block.PLANKS && parts[7] == Block.STONE && parts[8] == Block.PLANKS){
					result[0] = Block.BEDROCK;
					result[0] = 1;
					return result;
				}
			}
			default:{
				return result;
			}
		}
	}
	
}
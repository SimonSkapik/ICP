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
				if((parts[0] == Block.OAK_LOG || parts[0] == Block.SPRUCE_LOG ) && parts[1] == -1 && parts[2] == -1 && parts[3] == -1 && parts[4] == -1 && parts[5] == -1 && parts[6] == -1 && parts[7] == -1 && parts[8] == -1){
					result[0] = Block.PLANKS;
					result[1] = 4;
					return result; 
				}else if(parts[0] == Block.PALM_LOG && parts[1] == -1 && parts[2] == -1 && parts[3] == -1 && parts[4] == -1 && parts[5] == -1 && parts[6] == -1 && parts[7] == -1 && parts[8] == -1){
					result[0] = Block.PLANKS;
					result[1] = 1;
					return result; 
				}else if(parts[0] == Block.PLANKS && parts[1] == -1 && parts[2] == -1 && parts[3] == -1 && parts[4] == -1 && parts[5] == -1 && parts[6] == -1 && parts[7] == -1 && parts[8] == -1){
					result[0] = Block.WOODEN_ROD;
					result[1] = 2;
					return result;
				}else if(parts[0] == Block.PLANKS && parts[1] == Block.PLANKS && parts[2] == Block.PLANKS && 
						parts[3] == -1 && parts[4] == Block.WOODEN_ROD && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.WOODEN_PICKAXE;
					result[1] = 1;
					return result;
				}else if(parts[0] == Block.PLANKS && parts[1] == Block.PLANKS && parts[2] == -1 && 
						parts[3] == Block.PLANKS && parts[4] == Block.WOODEN_ROD && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.WOODEN_AXE;
					result[1] = 1;
					return result;
				}else if(parts[0] == -1 && parts[1] == Block.PLANKS && parts[2] == -1 && 
						parts[3] == Block.WOODEN_ROD && parts[4] == Block.PLANKS && parts[5] == Block.WOODEN_ROD && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.WOODEN_SWORD;
					result[1] = 1;
					return result;
				}else if(parts[0] == -1 && parts[1] == Block.PLANKS && parts[2] == -1 && 
						parts[3] == -1 && parts[4] == Block.WOODEN_ROD && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.WOODEN_SHOVEL;
					result[1] = 1;
					return result;
				}else if(parts[0] == Block.PLANKS && parts[1] == Block.WOODEN_ROD && parts[2] == Block.PLANKS  && 
						parts[3] == Block.PLANKS  && parts[4] == Block.WOODEN_ROD && parts[5] == Block.PLANKS  && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.WOODEN_HAMMER;
					result[1] = 1;
					return result;
				}else if(parts[0] == Block.ROCK && parts[1] == Block.ROCK && parts[2] == Block.ROCK && 
						parts[3] == -1 && parts[4] == Block.WOODEN_ROD && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.STONE_PICKAXE;
					result[1] = 1;
					return result;
				}else if(parts[0] == Block.ROCK && parts[1] == Block.ROCK && parts[2] == -1 && 
						parts[3] == Block.ROCK && parts[4] == Block.WOODEN_ROD && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.STONE_AXE;
					result[1] = 1;
					return result;
				}else if(parts[0] == -1 && parts[1] == Block.ROCK && parts[2] == -1 && 
						parts[3] == Block.WOODEN_ROD && parts[4] == Block.ROCK && parts[5] == Block.WOODEN_ROD && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.STONE_SWORD;
					result[1] = 1;
					return result;
				}else if(parts[0] == -1 && parts[1] == Block.ROCK && parts[2] == -1 && 
						parts[3] == -1 && parts[4] == Block.WOODEN_ROD && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.STONE_SHOVEL;
					result[1] = 1;
					return result;
				}else if(parts[0] == Block.ROCK && parts[1] == Block.WOODEN_ROD && parts[2] == Block.ROCK  && 
						parts[3] == Block.ROCK  && parts[4] == Block.WOODEN_ROD && parts[5] == Block.ROCK  && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.STONE_HAMMER;
					result[1] = 1;
					return result;
				}else if(parts[0] == Block.IRON_SHARD && parts[1] == Block.IRON_SHARD && parts[2] == Block.IRON_SHARD && 
						parts[3] == -1 && parts[4] == Block.WOODEN_ROD && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.IRON_PICKAXE;
					result[1] = 1;
					return result;
				}else if(parts[0] == Block.IRON_SHARD && parts[1] == Block.IRON_SHARD && parts[2] == -1 && 
						parts[3] == Block.IRON_SHARD && parts[4] == Block.WOODEN_ROD && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.IRON_AXE;
					result[1] = 1;
					return result;
				}else if(parts[0] == -1 && parts[1] == Block.IRON_SHARD && parts[2] == -1 && 
						parts[3] == Block.WOODEN_ROD && parts[4] == Block.IRON_SHARD && parts[5] == Block.WOODEN_ROD && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.IRON_SWORD;
					result[1] = 1;
					return result;
				}else if(parts[0] == -1 && parts[1] == Block.IRON_SHARD && parts[2] == -1 && 
						parts[3] == -1 && parts[4] == Block.WOODEN_ROD && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.IRON_SHOVEL;
					result[1] = 1;
					return result;
				}else if(parts[0] == Block.IRON_SHARD && parts[1] == Block.WOODEN_ROD && parts[2] == Block.IRON_SHARD  && 
						parts[3] == Block.IRON_SHARD  && parts[4] == Block.WOODEN_ROD && parts[5] == Block.IRON_SHARD  && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.IRON_HAMMER;
					result[1] = 1;
					return result;
				}else if(parts[0] == Block.GOLD_SHARD && parts[1] == Block.GOLD_SHARD && parts[2] == Block.GOLD_SHARD && 
						parts[3] == -1 && parts[4] == Block.WOODEN_ROD && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.GOLD_PICKAXE;
					result[1] = 1;
					return result;
				}else if(parts[0] == Block.GOLD_SHARD && parts[1] == Block.GOLD_SHARD && parts[2] == -1 && 
						parts[3] == Block.GOLD_SHARD && parts[4] == Block.WOODEN_ROD && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.GOLD_AXE;
					result[1] = 1;
					return result;
				}else if(parts[0] == -1 && parts[1] == Block.GOLD_SHARD && parts[2] == -1 && 
						parts[3] == Block.WOODEN_ROD && parts[4] == Block.GOLD_SHARD && parts[5] == Block.WOODEN_ROD && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.GOLD_SWORD;
					result[1] = 1;
					return result;
				}else if(parts[0] == -1 && parts[1] == Block.GOLD_SHARD && parts[2] == -1 && 
						parts[3] == -1 && parts[4] == Block.WOODEN_ROD && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.GOLD_SHOVEL;
					result[1] = 1;
					return result;
				}else if(parts[0] == Block.GOLD_SHARD && parts[1] == Block.WOODEN_ROD && parts[2] == Block.GOLD_SHARD  && 
						parts[3] == Block.GOLD_SHARD  && parts[4] == Block.WOODEN_ROD && parts[5] == Block.GOLD_SHARD  && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.GOLD_HAMMER;
					result[1] = 1;
					return result;
				}else if(parts[0] == Block.DIAMOND && parts[1] == Block.DIAMOND && parts[2] == Block.DIAMOND && 
						parts[3] == -1 && parts[4] == Block.WOODEN_ROD && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.DIAMOND_PICKAXE;
					result[1] = 1;
					return result;
				}else if(parts[0] == Block.DIAMOND && parts[1] == Block.DIAMOND && parts[2] == -1 && 
						parts[3] == Block.DIAMOND && parts[4] == Block.WOODEN_ROD && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.DIAMOND_AXE;
					result[1] = 1;
					return result;
				}else if(parts[0] == -1 && parts[1] == Block.DIAMOND && parts[2] == -1 && 
						parts[3] == Block.WOODEN_ROD && parts[4] == Block.DIAMOND && parts[5] == Block.WOODEN_ROD && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.DIAMOND_SWORD;
					result[1] = 1;
					return result;
				}else if(parts[0] == -1 && parts[1] == Block.DIAMOND && parts[2] == -1 && 
						parts[3] == -1 && parts[4] == Block.WOODEN_ROD && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.DIAMOND_SHOVEL;
					result[1] = 1;
					return result;
				}else if(parts[0] == Block.DIAMOND && parts[1] == Block.WOODEN_ROD && parts[2] == Block.DIAMOND  && 
						parts[3] == Block.DIAMOND  && parts[4] == Block.WOODEN_ROD && parts[5] == Block.DIAMOND  && 
						parts[6] == -1 && parts[7] == Block.WOODEN_ROD && parts[8] == -1){
					result[0] = Block.DIAMOND_HAMMER;
					result[1] = 1;
					return result;
				}else if(parts[0] == Block.DIRT && parts[1] == -1 && parts[2] == -1  && 
						parts[3] == -1  && parts[4] == -1 && parts[5] == -1 && 
						parts[6] == -1 && parts[7] == -1 && parts[8] == -1){
					result[0] = Block.BEDROCK;
					result[1] = 1;
					return result;
				}else{
					return result; 
				}
			}
			default:{
				return result;
			}
		}
	}
	
}
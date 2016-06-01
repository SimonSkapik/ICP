package pkg.skapik.icp.assets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import pkg.skapik.icp.func.Perlin_Noise;
import pkg.skapik.icp.func.Position;

public class World_generator {
	public static final int MAX_WORLD_HEIGHT = 100;
	private int seed;
	
	public World_generator(){
		Random rnd = new Random();
		this.seed = 100000;//rnd.nextInt();
	}
	
	public int generate_chunk(int origin_x, int origin_z, Block[][][] chunk_map, ArrayList<Foliage> foliage){
		int surface = 0,global_x,global_z;
		float surface_F,distance,continent_cutoff = 0.2f;
		double continent = 0;
		int highest = 5;
		int period = 200;
		
		ArrayList<Environmental_seed> seed = new ArrayList<>();
		boolean gen_trees = true;
		File f = new File("./World/Chunks/"+origin_x+"_"+origin_z+".txt");
		if(f.exists()) { 
			gen_trees = false;
		}
		
		
		for(int z = 0; z < 16; z++){
			for(int x = 0; x < 16; x++){
				Perlin_Noise PN = new Perlin_Noise();
				PN.offset(1, 1, 1);
				

				global_x = (origin_x*16)+x;
				global_z = (origin_z*16)+z;
				distance = (float)(Math.sqrt( global_x*global_x + global_z*global_z )*0.005);
				global_x = (int)(global_x+this.seed);
				global_z = (int)(global_z+this.seed);
				
				
				continent = PN.turbulentNoise( (float)((Math.sin((global_x)*0.0005)+1)), (float)((Math.sin((global_z)*0.0005)+1)), 1, 6 )*2;
				continent = Math.pow(continent, 1.2);
				if(continent <= continent_cutoff){
					continent = continent*0.1;
				}else if(continent >= (continent_cutoff+0.2f)){
					continent = 1;
				}else{
					continent = (Math.cos( Math.PI*(5*(continent-continent_cutoff*0.9) + (1-continent_cutoff*0.1)) )+1)/2.0;
				}
				
				surface_F = Math.min(MAX_WORLD_HEIGHT-1, (
								( PN.turbulentNoise( (float)((Math.sin((global_x)*0.007)+1)), (float)((Math.sin((global_z)*0.007)+1)), distance, 6 ) * 8)+
								( PN.turbulentNoise( (float)((Math.sin((global_x)*0.015)+1)), (float)((Math.sin((global_z)*0.015)+1)), distance, 3 ) * 2)
								)
							);

				surface_F /= 5;
				surface = (int)(((Math.pow(surface_F, 2)*35)+15)*continent+3);
				

				highest = Math.max(surface, highest);
				chunk_map[x][0][z] = new Block(Block.BEDROCK);
				chunk_map[x][1][z] = new Block(select_by_chance(Block.BEDROCK,Block.STONE,1,2));
				chunk_map[x][2][z] = new Block(Block.GRAVEL);
				chunk_map[x][3][z] = new Block(Block.GRAVEL);
				chunk_map[x][4][z] = new Block(Block.GRAVEL);
				for(int i = 2; i < (surface - 2); i++){
					if(select_by_chance(0,1,1,7) == 0){
						switch(ThreadLocalRandom.current().nextInt(1, 7)){
							case 1:{
								chunk_map[x][i][z] = new Block(Block.ORE_COAL);
							}break;
							case 2:{
								chunk_map[x][i][z] = new Block(Block.ORE_IRON);
							}break;
							case 3:{
								chunk_map[x][i][z] = new Block(Block.ORE_GOLD);
							}break;
							case 4:{
								chunk_map[x][i][z] = new Block(Block.ORE_LAPIS);
							}break;
							case 5:{
								chunk_map[x][i][z] = new Block(Block.ORE_REDSTONE);
							}break;
							case 6:{
								chunk_map[x][i][z] = new Block(Block.ORE_DIAMOND);
							}break;
							default:{
								chunk_map[x][i][z] = new Block(Block.ORE_COAL);
							}
						}
						
					}else{
						chunk_map[x][i][z] = new Block(Block.STONE);
					}
				}
				if(surface > 4){
					if(surface < 19){
						chunk_map[x][surface - 2][z] = new Block(Block.GRAVEL);
						chunk_map[x][surface - 1][z] = new Block(Block.GRAVEL);
						chunk_map[x][surface][z] = new Block(Block.GRAVEL);
					}else if(surface < 24){
						chunk_map[x][surface - 2][z] = new Block(Block.SAND);
						chunk_map[x][surface - 1][z] = new Block(Block.SAND);
						chunk_map[x][surface][z] = new Block(Block.SAND);
						if(surface < (World_generator.MAX_WORLD_HEIGHT - 8) && gen_trees){
							Environmental_seed S = new Environmental_seed(x, surface + 1, z, Block.SAND);
							if(S.is_entity()){
								seed.add(S);
							}
						}
					}else if(surface < 40){
						chunk_map[x][surface - 2][z] = new Block(Block.DIRT);
						chunk_map[x][surface - 1][z] = new Block(Block.DIRT);
						chunk_map[x][surface][z] = new Block(Block.GRASS);
						if(surface < (World_generator.MAX_WORLD_HEIGHT - 20) && gen_trees){
							Environmental_seed S = new Environmental_seed(x, surface + 1, z, Block.GRASS);
							if(S.is_entity()){
								seed.add(S);
							}
						}
						
					}else {
						chunk_map[x][surface - 2][z] = new Block(select_by_chance(select_by_chance(Block.ORE_IRON,Block.ORE_COAL,1,5),Block.STONE,1,10));
						chunk_map[x][surface - 1][z] = new Block(select_by_chance(select_by_chance(Block.ORE_IRON,Block.ORE_COAL,1,5),Block.STONE,1,10));
						chunk_map[x][surface][z] = new Block(select_by_chance(select_by_chance(Block.ORE_IRON,Block.ORE_COAL,1,5),Block.STONE,1,20));
					}
					
				}
				for(int i = (surface + 1); i < MAX_WORLD_HEIGHT; i++){
					if(i <= 20){
						chunk_map[x][i][z] = new Block(Block.WATER);
						highest = Math.max(i, highest);
					}else{
						chunk_map[x][i][z] = new Block(-1);
					}
				}
			}	
		}
		
		boolean b;
		Random rng = new Random();
		if(gen_trees){
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("./World/Chunks/"+origin_x+"_"+origin_z+".txt", true)))) {
				for(Environmental_seed s: seed){
					switch(s.get_entity()){
						case Environmental_seed.TREE:{
							if(s.getX() > 1 && s.getX() < 14 && s.getZ() > 1 && s.getZ() < 14){
								b = true;
								Out:
								for(int i = (s.getX() - 1); i <= (s.getX() + 1); i++){
									for(int j = (s.getZ() - 1); j <= (s.getZ() + 1); j++){
										if(chunk_map[i][s.getY()][j].block_id >= 0){
											b = false;
											break Out;
										}
									}
								}
								if(b){
									Tree t = new Tree(s.getX(), s.getY(), s.getZ());
									t.Grow(chunk_map,out);
									//highest = Math.max(t.getY(), highest);
								}
							}
						}break;
						case Environmental_seed.FOLIAGE:{
							Foliage fol = new Foliage(new Position(s.getX()+(rng.nextFloat()*0.2-0.1)+0.5, s.getY()-0.05, s.getZ()+(rng.nextFloat()*0.2-0.1)+0.5));
							foliage.add(fol);
							chunk_map[s.getX()][s.getY()-1][s.getZ()].attach_floiage(fol);
						}break;
						case Environmental_seed.ROCK:{
							chunk_map[s.getX()][s.getY()][s.getZ()] = new Block(Block.MOSSY_COBBLESTONE);
							out.println(s.getX()+";"+s.getY()+";"+s.getZ()+";"+Block.MOSSY_COBBLESTONE);
						}break;
					}
				}
			}catch (IOException e) {
				System.err.println("Error writing to file '" + "./World/Chunks/"+origin_x+"_"+origin_x+".txt" + "'");
			}
		}
		return highest;
	}

	private int select_by_chance(int option1, int option2, int chance, int from) {
		if(ThreadLocalRandom.current().nextInt(1, from + 1) <= chance){
			return option1;
		}
		return option2;
	}	
	
	
}

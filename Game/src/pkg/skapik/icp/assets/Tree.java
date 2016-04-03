package pkg.skapik.icp.assets;

import java.awt.HeadlessException;
import java.awt.RadialGradientPaint;
import java.io.PrintWriter;
import java.util.Random;

import com.jogamp.opengl.util.texture.spi.TGAImage.Header;

public class Tree {

	private static Block[][][] leafes_small = {{{new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF)}},{{new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF)}},{{new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF)}}};
	private static Block[][][] leafes_big = {{{new Block(-1),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(-1)}},{{new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF)}},{{new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF)}},{{new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF)}},{{new Block(-1),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(Block.OAK_LEAF),new Block(-1)}}};
	
	private int x, y, z, height, mid, bot;
	
	public Tree(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public void Grow(Block[][][] chunk_map, PrintWriter out) {
		Random rnd = new Random();
		this.height = (int)(rnd.nextFloat()*4.8+4);
		this.mid = (int)(rnd.nextFloat()*(this.height-4+0.8)+1);
		this.bot = (int)(rnd.nextFloat()*2.8);
		int y = this.y+height-1;
		for(int i = (x - 1); i <= (x + 1); i++){
			chunk_map[i][y][z-1] = new Block(Block.OAK_LEAF);
			out.println(i+";"+y+";"+(z-1)+";"+Block.OAK_LEAF);
			chunk_map[i][y][z] = new Block(Block.OAK_LEAF);
			out.println(i+";"+y+";"+(z)+";"+Block.OAK_LEAF);
			chunk_map[i][y][z+1] = new Block(Block.OAK_LEAF);
			out.println(i+";"+y+";"+(z+1)+";"+Block.OAK_LEAF);
		}
		for(int j = 0; j < mid; j++){
			y--;
			chunk_map[x-2][y][z-1] = new Block(Block.OAK_LEAF);
			out.println((x-2)+";"+y+";"+(z-1)+";"+Block.OAK_LEAF);
			chunk_map[x-2][y][z] = new Block(Block.OAK_LEAF);
			out.println((x-2)+";"+y+";"+(z)+";"+Block.OAK_LEAF);
			chunk_map[x-2][y][z+1] = new Block(Block.OAK_LEAF);
			out.println((x-2)+";"+y+";"+(z+1)+";"+Block.OAK_LEAF);
			for(int i = (x - 1); i <= (x + 1); i++){
				chunk_map[i][y][z-2] = new Block(Block.OAK_LEAF);
				out.println(i+";"+y+";"+(z-2)+";"+Block.OAK_LEAF);
				chunk_map[i][y][z-1] = new Block(Block.OAK_LEAF);
				out.println(i+";"+y+";"+(z-1)+";"+Block.OAK_LEAF);
				chunk_map[i][y][z] = new Block(Block.OAK_LEAF);
				out.println(i+";"+y+";"+(z)+";"+Block.OAK_LEAF);
				chunk_map[i][y][z+1] = new Block(Block.OAK_LEAF);
				out.println(i+";"+y+";"+(z+1)+";"+Block.OAK_LEAF);
				chunk_map[i][y][z+2] = new Block(Block.OAK_LEAF);
				out.println(i+";"+y+";"+(z+2)+";"+Block.OAK_LEAF);
			}
			chunk_map[x+2][y][z-1] = new Block(Block.OAK_LEAF);
			out.println((x+2)+";"+y+";"+(z-1)+";"+Block.OAK_LEAF);
			chunk_map[x+2][y][z] = new Block(Block.OAK_LEAF);
			out.println((x+2)+";"+y+";"+(z)+";"+Block.OAK_LEAF);
			chunk_map[x+2][y][z+1] = new Block(Block.OAK_LEAF);
			out.println((x+2)+";"+y+";"+(z+1)+";"+Block.OAK_LEAF);
		}
		y--;
		switch(this.bot){
			case 1:{
					chunk_map[x-1][y][z] = new Block(Block.OAK_LEAF);
					out.println((x-1)+";"+y+";"+(z)+";"+Block.OAK_LEAF);
					chunk_map[x+1][y][z] = new Block(Block.OAK_LEAF);
					out.println((x+1)+";"+y+";"+(z)+";"+Block.OAK_LEAF);
					chunk_map[x][y][z-1] = new Block(Block.OAK_LEAF);
					out.println(x+";"+y+";"+(z-1)+";"+Block.OAK_LEAF);
					chunk_map[x][y][z+1] = new Block(Block.OAK_LEAF);
					out.println(x+";"+y+";"+(z+1)+";"+Block.OAK_LEAF);
			}break;
			case 2:{
				for(int i = (x - 1); i <= (x + 1); i++){
					chunk_map[i][y][z-1] = new Block(Block.OAK_LEAF);
					out.println(i+";"+y+";"+(z-1)+";"+Block.OAK_LEAF);
					chunk_map[i][y][z] = new Block(Block.OAK_LEAF);
					out.println(i+";"+y+";"+(z)+";"+Block.OAK_LEAF);
					chunk_map[i][y][z+1] = new Block(Block.OAK_LEAF);
					out.println(i+";"+y+";"+(z+1)+";"+Block.OAK_LEAF);
				}
			}break;
			default:{
				
			}
		}
		for(int i = 0; i < (height - 1); i++){
			chunk_map[x][this.y+i][z] = new Block(Block.OAK_LOG);
			out.println(x+";"+(this.y+i)+";"+z+";"+Block.OAK_LOG);
		}
	}


	
	
	
}

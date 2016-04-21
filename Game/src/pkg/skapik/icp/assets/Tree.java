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
		if(this.y < 25){
			growPalm(chunk_map, out);
		}else if(this.y < 32){
			growOak(chunk_map, out);
		}else{
			growSpruce(chunk_map, out);	
		}
	}
	
	private void growOak(Block[][][] chunk_map, PrintWriter out) {
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
	
	private void growPalm(Block[][][] chunk_map, PrintWriter out) {
		Random rnd = new Random();
		// rnd.nextInt(max - min + 1) + min
		int trunk = rnd.nextInt(2) + 1;
		int direction = rnd.nextInt(4) + 1;
		int height = (rnd.nextInt(3) + 5);
		int y = this.y+height;
		
		// top leaves
		chunk_map[x][y][z] = new Block(Block.PALM_LEAF);
		out.println(x+";"+y+";"+(z)+";"+Block.PALM_LEAF);
		for(int i = x-1; i <= x+1; i+=2){
			chunk_map[i][y][z-1] = new Block(Block.PALM_LEAF );
			out.println(i+";"+y+";"+(z-1)+";"+Block.PALM_LEAF);
			chunk_map[i][y][z+1] = new Block(Block.PALM_LEAF);
			out.println(i+";"+y+";"+(z+1)+";"+Block.PALM_LEAF);
		}
		// bot leaves
		for(int i = x-2; i <= x+2; i+=4){
			chunk_map[i][y-1][z-2] = new Block(Block.PALM_LEAF );
			out.println(i+";"+(y-1)+";"+(z-2)+";"+Block.PALM_LEAF);
			chunk_map[i][y-1][z+2] = new Block(Block.PALM_LEAF);
			out.println(i+";"+(y-1)+";"+(z+2)+";"+Block.PALM_LEAF);
		}
		// direction of trunk
		int x1 = 0,x2 = 0, z1 = 0,z2 = 0;		
		switch(direction){
			case 1:{
				x1 = x-1;
				x2 = x-2;
				z1 = z;
				z2 = z;
			}break;
			case 2:{
				x1 = x+1;
				x2 = x+2;
				z1 = z;
				z2 = z;
			}break;
			case 3:{
				x1 = x;
				x2 = x;
				z1 = z-1;
				z2 = z-1;
			}break;
			case 4:{
				x1 = x;
				x2 = x;
				z1 = z+1;
				z2 = z+2;
			}break;
			default:{
				
			}
		}	
		// trunk
		chunk_map[x][y-1][z] = new Block(Block.PALM_LOG);
		out.println(x+";"+(y-1)+";"+z+";"+Block.PALM_LOG);
		if(trunk == 1){		
			chunk_map[x1][y-2][z1] = new Block(Block.PALM_LOG);
			out.println((x1)+";"+(y-2)+";"+(z1)+";"+Block.PALM_LOG);
			for(int i = y-3; i >= y-height; i--){
				chunk_map[x2][i][z2] = new Block(Block.PALM_LOG);
				out.println((x2)+";"+i+";"+(z2)+";"+Block.PALM_LOG);
			}
		}else{
			for(int i = y-2; i >= y-4; i--){
				chunk_map[x1][i][z1] = new Block(Block.PALM_LOG);
				out.println((x1)+";"+(i)+";"+(z1)+";"+Block.PALM_LOG);
			}
			for(int i = y-5; i >= y-height; i--){
				chunk_map[x][i][z] = new Block(Block.PALM_LOG);
				out.println((x)+";"+i+";"+(z)+";"+Block.PALM_LOG);
			}
		}
	}

	private void growSpruce(Block[][][] chunk_map, PrintWriter out) {
		//growOak(chunk_map, out);
		
		Random rnd = new Random();
		int trunk = rnd.nextInt(2) + 1;
		int direction = rnd.nextInt(4) + 1;
		int height = (rnd.nextInt(3) + 5);
		int y = this.y+height;
		growOak(chunk_map, out);
	}
}
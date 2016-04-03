package pkg.skapik.icp.func;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.xml.soap.Text;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import pkg.skapik.icp.assets.Block;
import pkg.skapik.icp.assets.Chunk;
import pkg.skapik.icp.assets.Cloud;
import pkg.skapik.icp.assets.Creature;
import pkg.skapik.icp.assets.Player;
import pkg.skapik.icp.assets.Skybox;
import pkg.skapik.icp.assets.World_generator;
import pkg.skapik.icp.assets.Zombie;
import pkg.skapik.icp.main.Game;

public class Renderer implements GLEventListener  {
	
	private GL2 gl;
	private GLU glu;
    private GLUT glut;
    
    private Game game;
    public int width;
    public int height;
    private BufferedImage image;
    private Texture main_texture;
    private Texture hud_texture;
    private Texture skybox_texture;
    private Texture zombie_texture;
    private Texture steve_texture;
    private int frame_count;
    private Coords_Manager main_coords_manager;
    private Skybox skybox;
    private ArrayList<Chunk> chunk_list;
    private World_generator world_gen;
    private Chunk_Loader chunk_loader;
    private int world_list;
    private int draw_human;
    public int hbar_bg;
    public int hbar_bg_sel;
    public int hbar_bg_hold;
    public int inventory_bg;
    public int crosshair;
    public int hp_2;
    public int hp_1;
    public int hp_0;
    public int foliage_0;
    public int foliage_1;
    public int foliage_2;
    private float[] fogColor;
    private Mat light_spectrum;
    private ArrayList<Creature> mobs;
    private ArrayList<Creature> dead_mobs;
    private ArrayList<Cloud> clouds;
    private Random random;
    private Vector wind;
    
	private Camera my_cam;
    private Thread cam_thread;
	private Texture cam_texture;
	
	public Renderer(Game game_instance){

		game = game_instance;
		game.init(this);
		glu = new GLU();
	    glut = new GLUT();
	    width = game.width;
	    height = game.height;
	    game.get_player().hud_init();
	    frame_count = 10000;
	    random = new Random();
	    chunk_list = new ArrayList<>();
	    mobs = new ArrayList<>();
	    dead_mobs = new ArrayList<>();
	    clouds = new ArrayList<>();
	    wind = new Vector(1,0,0.2);
	    main_coords_manager = new Coords_Manager("./Assets/Textures/Sphax_MC.png", 16);
	    light_spectrum = Imgcodecs.imread("./Assets/Textures/light.png");
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		update();
        render(drawable);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glEnable( GL2.GL_DEPTH_TEST ); 				//testovani hloubky
        gl.glEnable( GL2.GL_LINE_SMOOTH );    			//antialiasing car
        
        gl.glPolygonMode( GL2.GL_FRONT, GL2.GL_FILL );  // nastaveni rezimu vykresleni modelu
        gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);   // zpusob ulozeni bytu v texture
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);  // Pokus se to pls vykreslit hezky. Kdyz to teda pujde
        gl.glLineWidth( 0.0f ); //sirka cary
        gl.glBlendFunc( GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA );
        gl.glEnable( GL2.GL_CULL_FACE );               // zadne hrany ani steny se nebudou odstranovat - zpomaluje, ale vykresli vzdy a vsechno

        float[] lightPosition = new float[] {1,1,1,0};
        //gl.glLightModelfv( GL2.GL_LIGHT_MODEL_AMBIENT, Custom_Draw.float_color("light"), 0); // zakladni barva ambientniho a difuzniho osvetleni
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition,0);	//Position The Light
        //gl.glLighti(GL2.GL_LIGHT1, GL2.GL_LIGHT, arg2);
        gl.glShadeModel(GL2.GL_SMOOTH);     // smooooooth prechody mezi vertex barvama
        gl.glEnable( GL2.GL_LIGHT0 );         // zapni zdroj svetla
        gl.glEnable( GL2.GL_LIGHT1 );         // zapni zdroj svetla
        gl.glDisable( GL2.GL_LIGHT2 );         // zapni zdroj svetla
        gl.glDisable( GL2.GL_LIGHT3 );         // zapni zdroj svetla
        gl.glDisable( GL2.GL_LIGHT4 );         // zapni zdroj svetla
        gl.glDisable( GL2.GL_LIGHT5 );         // zapni zdroj svetla
        gl.glDisable( GL2.GL_LIGHT6 );         // zapni zdroj svetla
        gl.glDisable( GL2.GL_LIGHT7 );         // zapni zdroj svetla
        gl.glEnable( GL2.GL_LIGHTING );         // zapni zdroj svetla
        
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LEVEL, 7);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);     // texture application method - modulation
        
        gl.glDisable(GL2.GL_BLEND);

        
        // Nacteni textury
        //Mat mat_image = Imgcodecs.imread("./Assets/Textures/Sphax_MC.png", Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
        //Imgproc.cvtColor(mat_image, mat_image, Imgproc.COLOR_RGBA2BGRA);
        //image = createAwtImage(mat_image);
        
        File cam_tex = new File("./Assets/Textures/cam_texture_init.png");
		
        TextureData cam_textureData = null;
		try {
			cam_textureData = TextureIO.newTextureData(GLContext.getCurrentGL().getGLProfile(), cam_tex, GL2.GL_RGBA, GL2.GL_SRGB8_ALPHA8,    false, TextureIO.PNG);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        cam_texture = TextureIO.newTexture(cam_textureData);//new Texture(textureData);
        cam_texture.enable();
        
        File tex_png = new File("./Assets/Textures/Sphax_MC.png");
        TextureData textureData = null;
		try {
			textureData = TextureIO.newTextureData(GLContext.getCurrentGL().getGLProfile(), tex_png, GL2.GL_RGBA, GL2.GL_SRGB8_ALPHA8,    false, TextureIO.PNG);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        main_texture = TextureIO.newTexture(textureData);//new Texture(textureData);
        main_texture.enable();
        main_texture.bind();
        
        hbar_bg = gl.glGenLists(11);
        hbar_bg_sel = hbar_bg+1;
        inventory_bg = hbar_bg+2;
        crosshair = hbar_bg+3;
        hbar_bg_hold = hbar_bg+4;
        hp_0 = hbar_bg+5;
        hp_1 = hbar_bg+6;
        hp_2 = hbar_bg+7;
        foliage_0 = hbar_bg+8;
        foliage_1 = hbar_bg+9;
        foliage_2 = hbar_bg+10;
	    gl.glNewList(hbar_bg, GL2.GL_COMPILE );
	    
	    // activate and specify pointer to vertex array
 		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glEnable(GL2.GL_BLEND);
 		
 		FloatBuffer vertices;
		vertices = Buffers.newDirectFloatBuffer(8);
		vertices.put(-0.5f);
		vertices.put(-0.5f);
		vertices.put(-0.5f);
		vertices.put(0.5f);
		vertices.put(0.5f);
		vertices.put(0.5f);
		vertices.put(0.5f);
		vertices.put(-0.5f);
		vertices.rewind();
 		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, vertices);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, main_coords_manager.get_texture_coords(Texture_List.HBAR_BG));
 		// draw a cube
 		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		
 		// deactivate vertex arrays after drawing
 		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glDisable(GL2.GL_BLEND);
 		
	    gl.glEndList();
	    
        gl.glNewList(hbar_bg_sel, GL2.GL_COMPILE );
	    
	    // activate and specify pointer to vertex array
 		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glEnable(GL2.GL_BLEND);
 		
 		vertices = Buffers.newDirectFloatBuffer(8);
		vertices.put(-0.5f);
		vertices.put(-0.5f);
		vertices.put(-0.5f);
		vertices.put(0.5f);
		vertices.put(0.5f);
		vertices.put(0.5f);
		vertices.put(0.5f);
		vertices.put(-0.5f);
		vertices.rewind();
 		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, vertices);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, main_coords_manager.get_texture_coords(Texture_List.HBAR_BG_SEL));
 		// draw a cube
 		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		
 		// deactivate vertex arrays after drawing
 		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glDisable(GL2.GL_BLEND);
 		
	    gl.glEndList();
	    
	    gl.glNewList(hbar_bg_hold, GL2.GL_COMPILE );
	    
	    // activate and specify pointer to vertex array
 		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glEnable(GL2.GL_BLEND);
 		
 		vertices = Buffers.newDirectFloatBuffer(8);
		vertices.put(-0.5f);
		vertices.put(-0.5f);
		vertices.put(-0.5f);
		vertices.put(0.5f);
		vertices.put(0.5f);
		vertices.put(0.5f);
		vertices.put(0.5f);
		vertices.put(-0.5f);
		vertices.rewind();
 		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, vertices);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, main_coords_manager.get_texture_coords(Texture_List.HBAR_BG_HOLD));
 		// draw a cube
 		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		
 		// deactivate vertex arrays after drawing
 		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glDisable(GL2.GL_BLEND);
 		
	    gl.glEndList();
	    
	    gl.glNewList(crosshair, GL2.GL_COMPILE );
	    
	    // activate and specify pointer to vertex array
 		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glEnable(GL2.GL_BLEND);
 		
 		vertices = Buffers.newDirectFloatBuffer(8);
		vertices.put(-0.5f);
		vertices.put(-0.5f);
		vertices.put(-0.5f);
		vertices.put(0.5f);
		vertices.put(0.5f);
		vertices.put(0.5f);
		vertices.put(0.5f);
		vertices.put(-0.5f);
		vertices.rewind();
 		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, vertices);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, main_coords_manager.get_texture_coords(Texture_List.CROSS));
 		// draw a cube
 		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		
 		// deactivate vertex arrays after drawing
 		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glDisable(GL2.GL_BLEND);
 		
	    gl.glEndList();
	    
	    gl.glNewList(hp_0, GL2.GL_COMPILE );
	    
	    // activate and specify pointer to vertex array
 		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glEnable(GL2.GL_BLEND);
 		
 		vertices = Buffers.newDirectFloatBuffer(8);
		vertices.put(0);
		vertices.put(0);
		vertices.put(0);
		vertices.put(-1);
		vertices.put(-2);
		vertices.put(-2);
		vertices.put(-2);
		vertices.put(-1);
		vertices.rewind();
 		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, vertices);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, main_coords_manager.get_texture_coords(Texture_List.HP_BAR_0));
 		// draw a cube
 		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		
 		// deactivate vertex arrays after drawing
 		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glDisable(GL2.GL_BLEND);
 		
	    gl.glEndList();
	    
	    gl.glNewList(hp_1, GL2.GL_COMPILE );
	    
	    // activate and specify pointer to vertex array
 		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glEnable(GL2.GL_BLEND);
 		
 		vertices = Buffers.newDirectFloatBuffer(8);
		vertices.put(0);
		vertices.put(0);
		vertices.put(0);
		vertices.put(-1);
		vertices.put(-2);
		vertices.put(-2);
		vertices.put(-2);
		vertices.put(-1);
		vertices.rewind();
 		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, vertices);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, main_coords_manager.get_texture_coords(Texture_List.HP_BAR_1));
 		// draw a cube
 		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		
 		// deactivate vertex arrays after drawing
 		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glDisable(GL2.GL_BLEND);
 		
	    gl.glEndList();
	    
	    gl.glNewList(hp_2, GL2.GL_COMPILE );
	    
	    // activate and specify pointer to vertex array
 		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glEnable(GL2.GL_BLEND);
 		
 		vertices = Buffers.newDirectFloatBuffer(8);
		vertices.put(0);
		vertices.put(0);
		vertices.put(0);
		vertices.put(-1);
		vertices.put(-2);
		vertices.put(-2);
		vertices.put(-2);
		vertices.put(-1);
		vertices.rewind();
 		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, vertices);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, main_coords_manager.get_texture_coords(Texture_List.HP_BAR_2));
 		// draw a cube
 		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		
 		// deactivate vertex arrays after drawing
 		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glDisable(GL2.GL_BLEND);
 		
	    gl.glEndList();
	    
	    gl.glNewList(foliage_0, GL2.GL_COMPILE );
	    
	    // activate and specify pointer to vertex array
 		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glEnable(GL2.GL_BLEND);
 		gl.glDisable(GL2.GL_CULL_FACE);
 		
		vertices = Buffers.newDirectFloatBuffer(12);
		vertices.put(-0.375f);
		vertices.put(0.375f);
		vertices.put(0);
		vertices.put(-0.375f);
		vertices.put(0);
		vertices.put(0);
		vertices.put(0.375f);
		vertices.put(0);
		vertices.put(0);
		vertices.put(0.375f);
		vertices.put(0.375f);
		vertices.put(0);
		vertices.rewind();
 		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, main_coords_manager.get_texture_coords(Texture_List.GRASS_PATCH));
 		// draw a cube
 		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		gl.glRotated(60,0 ,1 ,0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		gl.glRotated(60,0 ,1 ,0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		
 		// deactivate vertex arrays after drawing
 		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glDisable(GL2.GL_BLEND);
 		gl.glEnable(GL2.GL_CULL_FACE);
 		
	    gl.glEndList();
	    
	    gl.glNewList(foliage_1, GL2.GL_COMPILE );
	    
	    // activate and specify pointer to vertex array
 		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glEnable(GL2.GL_BLEND);
 		gl.glDisable(GL2.GL_CULL_FACE);
 		
		vertices = Buffers.newDirectFloatBuffer(12);
		vertices.put(-0.375f);
		vertices.put(0.375f);
		vertices.put(0);
		vertices.put(-0.375f);
		vertices.put(0);
		vertices.put(0);
		vertices.put(0.375f);
		vertices.put(0);
		vertices.put(0);
		vertices.put(0.375f);
		vertices.put(0.375f);
		vertices.put(0);
		vertices.rewind();
 		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, main_coords_manager.get_texture_coords(Texture_List.ROSE));
 		// draw a cube
 		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		gl.glRotated(60,0 ,1 ,0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		gl.glRotated(60,0 ,1 ,0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		
 		// deactivate vertex arrays after drawing
 		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glDisable(GL2.GL_BLEND);
 		gl.glEnable(GL2.GL_CULL_FACE);
 		
	    gl.glEndList();
	    
	    gl.glNewList(foliage_2, GL2.GL_COMPILE );
	    
	    // activate and specify pointer to vertex array
 		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glEnable(GL2.GL_BLEND);
 		gl.glDisable(GL2.GL_CULL_FACE);
 		
		vertices = Buffers.newDirectFloatBuffer(12);
		vertices.put(-0.375f);
		vertices.put(0.375f);
		vertices.put(0);
		vertices.put(-0.375f);
		vertices.put(0);
		vertices.put(0);
		vertices.put(0.375f);
		vertices.put(0);
		vertices.put(0);
		vertices.put(0.375f);
		vertices.put(0.375f);
		vertices.put(0);
		vertices.rewind();
 		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, main_coords_manager.get_texture_coords(Texture_List.DANDELION));
 		// draw a cube
 		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		gl.glRotated(60,0 ,1 ,0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		gl.glRotated(60,0 ,1 ,0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		
 		// deactivate vertex arrays after drawing
 		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glDisable(GL2.GL_BLEND);
 		gl.glEnable(GL2.GL_CULL_FACE);
 		
	    gl.glEndList();
        tex_png = new File("./Assets/Textures/HUD_texture.png");
        TextureData textureData3 = null;
		try {
			textureData3 = TextureIO.newTextureData(GLContext.getCurrentGL().getGLProfile(), tex_png, GL2.GL_RGBA, GL2.GL_SRGB8_ALPHA8,    false, TextureIO.PNG);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        hud_texture = TextureIO.newTexture(textureData3);//new Texture(textureData);
        hud_texture.enable();
        hud_texture.bind();
        
        gl.glNewList(inventory_bg, GL2.GL_COMPILE );
	    
	    // activate and specify pointer to vertex array
 		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glEnable(GL2.GL_BLEND);
 		
 		vertices = Buffers.newDirectFloatBuffer(8);
		vertices.put(-0.5f);
		vertices.put(-0.245741f);
		vertices.put(-0.5f);
		vertices.put(0.245741f);
		vertices.put(0.5f);
		vertices.put(0.245741f);
		vertices.put(0.5f);
		vertices.put(-0.245741f);
		vertices.rewind();
		
		FloatBuffer coords;
		coords = Buffers.newDirectFloatBuffer(8);
		coords.put(0);
		coords.put(0);
		coords.put(0);
		coords.put(1);
		coords.put(1);
		coords.put(1);
		coords.put(1);
		coords.put(0);
		coords.rewind();
 		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, vertices);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, coords);
 		// draw a cube
 		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		
 		// deactivate vertex arrays after drawing
 		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glDisable(GL2.GL_BLEND);
 		
	    gl.glEndList();
        
	    tex_png = new File("./Assets/Textures/steve.png");
        TextureData textureData5 = null;
		try {
			textureData5 = TextureIO.newTextureData(GLContext.getCurrentGL().getGLProfile(), tex_png, GL2.GL_RGBA, GL2.GL_SRGB8_ALPHA8,    false, TextureIO.PNG);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		steve_texture = TextureIO.newTexture(textureData5);//new Texture(textureData);
		steve_texture.enable();
        steve_texture.bind();
	    
	    tex_png = new File("./Assets/Textures/zombie.png");
        TextureData textureData4 = null;
		try {
			textureData4 = TextureIO.newTextureData(GLContext.getCurrentGL().getGLProfile(), tex_png, GL2.GL_RGBA, GL2.GL_SRGB8_ALPHA8,    false, TextureIO.PNG);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        zombie_texture = TextureIO.newTexture(textureData4);//new Texture(textureData);
        zombie_texture.enable();
        zombie_texture.bind();
        
        draw_human = gl.glGenLists(4);
        Zombie.pre_draw(gl, draw_human);
        game.get_player().set_draw_list(draw_human);
	    
	    
        Mat mat_image = Imgcodecs.imread("./Assets/Textures/skybox_overlap.jpg");
        image = createAwtImage(mat_image);
        TextureData textureData2=AWTTextureIO.newTextureData(GLContext.getCurrentGL().getGLProfile(),image,false);
        skybox_texture = new Texture(textureData2);
        skybox_texture.enable();
        
        this.skybox = new Skybox(800,gl);
        world_gen = new World_generator();
        //this.world_map = world_gen.get_map();
        world_list = gl.glGenLists(1);
        this.chunk_loader = new Chunk_Loader(this);
        chunk_loader.load_chunks(chunk_list, game.get_player().get_view_distance());
        
        //this.pre_render_world();

        
        // Pripojeni kamery - to bych asi pak prehodil do Game.
		my_cam = new Camera();
		cam_thread = my_cam;
		my_cam.start_cam();
		cam_thread.start();
	}
	
	public void bind_texture(int tex){
		if(tex == 1){
			main_texture.bind();
		}else{
			hud_texture.bind();
		}
	}

	public BufferedImage createAwtImage(Mat mat) { // prevod formatu textury OpenCV Mat -> Java BufferedImage 

	    int type = 0;
	    if (mat.channels() == 1) {
	        type = BufferedImage.TYPE_BYTE_GRAY;
	    } else if (mat.channels() == 3) {
	        type = BufferedImage.TYPE_3BYTE_BGR;
	    } else {
	    	type = BufferedImage.TYPE_4BYTE_ABGR;
	    }

	    BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
	    WritableRaster raster = image.getRaster();
	    DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
	    byte[] data = dataBuffer.getData();
	    mat.get(0, 0, data);

	    return image;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		System.out.println("reshape() called: x = "+x+", y = "+y+", width = "+width+", height = "+height);
 
        if (height <= 0) // avoid a divide by zero error!
        {
            height = 1;
        }
 
        final float h = (float) width / (float) height; // aspect ratio
 
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(80.0f, h, 0.01, 2000.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
	}
	
	private void update() {
        this.pre_render_world();
    }
    
	private void pre_render_world(){
	    gl.glNewList( world_list, GL2.GL_COMPILE );
	    for(Chunk chunk : chunk_list){
			gl.glPushMatrix();
			chunk.draw(gl,1);
			gl.glPopMatrix();
		}
		gl.glEndList();
	}
	
    private void render(GLAutoDrawable drawable) {
     	frame_count++;
     	Random rnd = new Random();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT); 	// vymazani bitovych rovin barvoveho bufferu
        gl.glLoadIdentity(); 											// vynulovani vsech predchozich transformaci
        gl.glMatrixMode(GL2.GL_MODELVIEW);         						// bude se menit matice modelview
        gl.glLoadIdentity();											// vynulovani vsech predchozich transformaci
        
        Position player_pos = game.get_player().get_position();			// kde je ten progamer a kam kouka?
        Vector player_dir = new Vector(game.get_player().get_view_direction());
        player_dir.add(player_pos);
        glu.gluLookAt(player_pos.getX_D(), player_pos.getY_D(), player_pos.getZ_D(),
        		player_dir.getX_D(), player_dir.getY_D(), player_dir.getZ_D(), 0, 1, 0); // postaveni kamery at cumi tam co player
        
        Vector sun_rot = new Vector(game.get_player().get_view_direction());
        sun_rot.setY(0);
        Vector y_axis = new Vector(0,1,0);
        Vector path_rot = new Vector(0,0,1);
        sun_rot.rotate(y_axis, -sun_rot.get_angle_to(new Vector(path_rot)));
        float sun_angle = (float)((frame_count/750.0f)%(2*Math.PI));
    	path_rot.rotate(new Vector((path_rot).getZ_D(),0,-path_rot.getX_D()), sun_angle);
       	sun_rot.rotate(new Vector((sun_rot).getZ_D(),0,-sun_rot.getX_D()), (float)((sun_angle%Math.PI) + Math.PI));
        double[] light_color_2 = light_spectrum.get(1, (int)((sun_angle*(1800/Math.PI))+1600)%3600);
        float[] light_color = new float[]{(float)(light_color_2[2]/255.0),(float)(light_color_2[1]/255.0),(float)(light_color_2[0]/255.0)};
        if(sun_angle < Math.PI){
        	if(this.skybox.is_day()){
        		this.skybox.pre_draw(gl, Custom_Draw.float_color(0.07f*1,0.153f*1,0.239f*1,1.0f));
        		this.skybox.set_day(false);
        	}
        }else{
        	if(!this.skybox.is_day()){
        		this.skybox.pre_draw(gl, Custom_Draw.float_color(0.07f*3.5f,0.153f*3.5f,0.239f*3.5f,1.0f));
        		this.skybox.set_day(true);
        	}
        }
        
        sun_rot.setX(sun_rot.getX_F()+0.25);
        sun_rot.normalize();
        float[] lightPosition = new float[] {sun_rot.getX_F(),sun_rot.getY_F(),sun_rot.getZ_F(),0};
        gl.glLightModelfv( GL2.GL_LIGHT_MODEL_AMBIENT, light_color, 0); // zakladni barva ambientniho a difuzniho osvetleni
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition,0);
        
        gl.glPushMatrix();
        path_rot.setX(path_rot.getX_F()+0.25);
        path_rot.normalize();
        gl.glTranslatef(path_rot.getX_F()*350,path_rot.getY_F()*350,path_rot.getZ_F()*350);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, light_color, 0);
        glut.glutSolidSphere(10, 10, 5);
        gl.glRotatef(rnd.nextFloat()*220-110, 1, 0, 0);
		gl.glRotatef(rnd.nextFloat()*220-110, 0, 1, 0);
		gl.glRotatef(rnd.nextFloat()*220-110, 0, 0, 1);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("orange"), 0);
        glut.glutSolidSphere(10, 8, 4);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslatef(path_rot.getX_F()*350,-path_rot.getY_F()*350,-path_rot.getZ_F()*350);
        //gl.glDisable(GL2.GL_LIGHTING);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, Custom_Draw.float_color("white"), 0);
        glut.glutSolidSphere(8, 10, 5);
        gl.glRotatef(rnd.nextFloat()*220-110, 1, 0, 0);
		gl.glRotatef(rnd.nextFloat()*220-110, 0, 1, 0);
		gl.glRotatef(rnd.nextFloat()*220-110, 0, 0, 1);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("grey"), 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, Custom_Draw.float_color("grey"), 0);
        glut.glutWireSphere(8, 8, 4);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        //gl.glEnable(GL2.GL_LIGHTING);
        gl.glPopMatrix();
        
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("grey"), 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, Custom_Draw.float_color("black"), 0);
        if(this.game.get_player().is_in_water()){
        	fogColor = new float[]{0.07f,0.153f,0.239f,1.0f};
	        gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_LINEAR);
	        gl.glFogfv(GL2.GL_FOG_COLOR, fogColor, 0); 
	        gl.glHint(GL2.GL_FOG_HINT, GL2.GL_DONT_CARE);
	        gl.glFogf(GL2.GL_FOG_START, 1.0f); // Fog Start Depth 
	        gl.glFogf(GL2.GL_FOG_END, 10.0f); // Fog End Depth
	        gl.glEnable(GL2.GL_FOG);
        }else{
        	/*fogColor = new float[]{0.07f*4,0.153f*4,0.239f*4,1.0f};
	        gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_LINEAR);
	        gl.glFogfv(GL2.GL_FOG_COLOR, fogColor, 0); 
	        gl.glHint(GL2.GL_FOG_HINT, GL2.GL_DONT_CARE);
	        gl.glFogf(GL2.GL_FOG_START, 200.0f); // Fog Start Depth 
	        gl.glFogf(GL2.GL_FOG_END, 220.0f); // Fog End Depth
	        gl.glEnable(GL2.GL_FOG);*/
        	gl.glDisable(GL2.GL_FOG);
        }
   
      	//TrackImage(&PICTURE_X, &PICTURE_Y); //vrati souradnice vyhledaneho objektu

        /*gl.glPushMatrix();
        gl.glTranslated(player_pos.getX_D()-(0.375), player_pos.getY_D()-(1.8-0.18), player_pos.getZ_D()-(0.375));
        //game.get_player().draw_boundery(gl);
        gl.glPopMatrix();*/
        
        /////// !!!!!!! A KRESLIIIIMEEEE !!!!!!!! \\\\\\\\
        skybox_texture.bind();
        gl.glPushMatrix();
        gl.glTranslatef(-400.0f, -400.0f, -400.0f);
    	skybox.draw(gl,1);
        gl.glPopMatrix();
        
        
        ArrayList<Cloud> to_remove = new ArrayList<>();
        for(Cloud C : clouds){
        	if(C.get_position().distance_to_2d(game.get_player().get_position()) > 500){
        		to_remove.add(C);
        	}else{
	    		gl.glPushMatrix();
	            gl.glTranslatef(C.get_position().getX_F(), C.get_position().getY_F(), C.get_position().getZ_F());
	            C.Draw(gl,glut);
	            gl.glPopMatrix();
	            C.Update(wind);
            }
    	}
        for(Cloud C : to_remove){
        	clouds.remove(C);
        }
        
        main_texture.bind();
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);

 		draw_world();

    	gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
    	                       // provedeni a vykresleni zmen


    	
    	zombie_texture.bind();
    	for(Creature C : mobs){
    		C.Update();
    		gl.glPushMatrix();
            gl.glTranslatef(C.get_position().getX_F(), C.get_position().getY_F(), C.get_position().getZ_F());
            C.draw(gl);
            gl.glPopMatrix();
    		
    	}
    	
    	for(Creature C : dead_mobs){
    		gl.glPushMatrix();
            gl.glTranslatef(C.get_position().getX_F(), C.get_position().getY_F(), C.get_position().getZ_F());
            C.draw(gl);
            gl.glPopMatrix();
    	}
    	
    	
    	main_texture.bind();
    	for(Chunk C : chunk_list){
    		gl.glPushMatrix();
    		gl.glTranslatef(C.getX()*16, 0, C.getZ()*16);
    		C.draw_foliage(gl);
    		gl.glPopMatrix();
    	}
    	

    	steve_texture.bind();
    	gl.glPushMatrix();
    	gl.glTranslatef(game.get_player().get_position().getX_F(), game.get_player().get_position().getY_F(), game.get_player().get_position().getZ_F());
    	game.get_player().draw(gl);
    	gl.glPopMatrix();
    	
    	if(this.game.get_player().is_in_water()){
    		fogColor = new float[]{0.07f,0.153f,0.239f,1.0f};
            gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_LINEAR);
            gl.glFogfv(GL2.GL_FOG_COLOR, fogColor, 0); 
            gl.glHint(GL2.GL_FOG_HINT, GL2.GL_DONT_CARE);
            gl.glFogf(GL2.GL_FOG_START, 10.0f); // Fog Start Depth 
            gl.glFogf(GL2.GL_FOG_END, 60.0f); // Fog End Depth
        }
    	
    	main_texture.bind();
    	game.get_player().draw_HUD(gl, frame_count);

    	
    	//boolean new_frame = false;
    	if(game.get_player().cam_enabled()){
	    	try{
	    		File cam_tex = new File("./Assets/Textures/cam_texture_done.png");
	    		
		        TextureData cam_textureData = null;
				try {
					cam_textureData = TextureIO.newTextureData(GLContext.getCurrentGL().getGLProfile(), cam_tex, GL2.GL_RGBA, GL2.GL_SRGB8_ALPHA8,    false, TextureIO.PNG);
			        cam_texture = TextureIO.newTexture(cam_textureData);//new Texture(textureData);
			        cam_texture.enable();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (GLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					
				}
				
			}catch(Exception e){
				//new_frame = false;
			}
	    	
	    	cam_texture.bind();
	    	game.get_player().draw_CAM(gl);
	    	
    	}	
	    	
	    gl.glFlush();   
    	
    	game.get_player().Update();
 		if(frame_count >= 360000000){
 			frame_count = 0;
 		}
 		int chance = Math.abs(random.nextInt())%1000;
 		if(sun_angle < Math.PI){
	 		if(mobs.size() < 20 && chance < 10){
	 			int spawn_y = -1;
	 			float spawn_x = 0;
	 			float spawn_z = 0;
	 			do{
		 			spawn_x = random.nextFloat()*2-1;
		 			spawn_z = random.nextFloat()*2-1;
		 			float dist = random.nextFloat()*30+20;
		 			Vector dir = new Vector(spawn_x,0,spawn_z);
		 			dir.normalize();
		 			dir.setX(Math.floor(dir.getX_D()*dist));
		 			dir.setZ(Math.floor(dir.getZ_D()*dist));
		 			spawn_x = game.get_player().get_position().getX_I()+dir.getX_I();
		 			spawn_z = game.get_player().get_position().getX_I()+dir.getZ_I();
		 			spawn_y = this.get_col_height((int)spawn_x,(int)spawn_z);
	 			}while(spawn_y < 0);
	 			//Zombie Z = new Zombie(new Position(spawn_x,spawn_y+3,spawn_z), this, draw_human);
	 			mobs.add(new Zombie(new Position(spawn_x,spawn_y+3,spawn_z), this, draw_human));
	 		}
 		}
 		
 		chance = Math.abs(random.nextInt())%1000;
 		if(clouds.size() < 30 && chance < 20){
 			float c_x = 0, c_z = 0;
 			if(wind.getX_D() > wind.getZ_D()){
	 			int sgn = (int) Math.signum(wind.getX_D());
	 			c_x = (game.get_player().get_position().getX_F()-(sgn*428));
	 			c_z = game.get_player().get_position().getZ_F()+(rnd.nextFloat()-0.5f)*450;
 			}else{
 				int sgn = (int) Math.signum(wind.getZ_D());
 				c_z = (game.get_player().get_position().getZ_F()-(sgn*428));
 				c_x = game.get_player().get_position().getX_F()+(rnd.nextFloat()-0.5f)*450;
 			}
 			clouds.add(new Cloud(new Position(c_x, (rnd.nextFloat()*60+100), c_z)));
 		}
    }

	private int get_col_height(int x2, int z2) {
		int ch_x = (int)Math.floor(x2/16.0);
		int ch_z = (int)Math.floor(z2/16.0);
		int b_x = Math.floorMod(x2,16);
		int b_z = Math.floorMod(z2,16);
		//ArrayList<Chunk> chunks = (ArrayList<Chunk>) chunk_list.clone();
		for(Chunk chunk : chunk_list){
			try{
				if(chunk.is_position(ch_x,ch_z)){
					for(int i = 0; i < World_generator.MAX_WORLD_HEIGHT-3; i++){
						if(chunk.get_block_id(b_x,i,b_z) < 0){
							if(chunk.get_block_id(b_x,i-1,b_z) != Block.WATER){
								return i-1;
							}
							return -1; 
						}
					}
				}
			}
			catch (ConcurrentModificationException e){
				return -1;
			}
			catch (NullPointerException e){
				return -1;
			}
		}
		return -1;
	}

	private void draw_world() {
		
		for(Chunk chunk : chunk_list){
			gl.glPushMatrix();
			chunk.draw(gl,1);
			gl.glPopMatrix();
		}
		
		//gl.glCallList( world_list );
		
		/*gl.glPushMatrix();
		int N = 30; 
		gl.glTranslatef(-N/2.0f, -N/2.0f, -N/2.0f);
		for(int x = 0; x < N; x++){
			for(int y = 0; y < N; y++){
				for(int z = 0; z < N; z++){
					if(world_map[x][y][z] >= 0){
						Block b = new Block(main_coords_manager, world_map[x][y][z]);
						b.draw(gl, 1);
						//System.out.println("Draw: " +  world_map[x][y][z]);
					}
					gl.glTranslatef(0, 0, 1);
				}
				gl.glTranslatef(0, 1, -N);
			}
			gl.glTranslatef(1, -N, 0);
		}
		gl.glPopMatrix();*/
	}
	
	public int get_block_id(Position pos) {
		return this.get_block_id(pos.getX_I(),pos.getY_I(),pos.getZ_I());
	}

	public int get_block_id(int x2, int y2, int z2) {
		if(y2 >= 0 && y2 < World_generator.MAX_WORLD_HEIGHT){
			int ch_x = (int)Math.floor(x2/16.0);
			int ch_z = (int)Math.floor(z2/16.0);
			int b_x = Math.floorMod(x2,16);
			int b_z = Math.floorMod(z2,16);
			//ArrayList<Chunk> chunks = (ArrayList<Chunk>) chunk_list.clone();
			for(Chunk chunk : chunk_list){
				try{
					if(chunk.is_position(ch_x,ch_z)){ 
						return chunk.get_block_id(b_x,y2,b_z);
					}
				}
				catch (ConcurrentModificationException e){
					return -1;
				}
				catch (NullPointerException e){
					return -1;
				}
			}
		}
		return -1;
	}
	

	public Block get_block(Position pos) {
		return this.get_block(pos.getX_I(),pos.getY_I(),pos.getZ_I());
	}


	public Block get_block(int x2, int y2, int z2) {
		if(y2 >= 0 && y2 < World_generator.MAX_WORLD_HEIGHT){
			int ch_x = (int)Math.floor(x2/16.0);
			int ch_z = (int)Math.floor(z2/16.0);
			int b_x = Math.floorMod(x2,16);
			int b_z = Math.floorMod(z2,16);
			//ArrayList<Chunk> chunks = (ArrayList<Chunk>) chunk_list.clone();
			for(Chunk chunk : chunk_list){
				try{
					if(chunk.is_position(ch_x,ch_z)){ 
						return chunk.get_block(b_x,y2,b_z);
					}
				}
				catch (ConcurrentModificationException e){
					return new Block(-1);
				}
				catch (NullPointerException e){
					return new Block(-1);
				}
			}
		}
		return new Block(-1);
	}
	
	public int get_block_solidity(int x2, int y2, int z2) {
		if(y2 >= 0 && y2 < World_generator.MAX_WORLD_HEIGHT){
			int ch_x = (int)Math.floor(x2/16.0);
			int ch_z = (int)Math.floor(z2/16.0);
			int b_x = Math.floorMod(x2,16);
			int b_z = Math.floorMod(z2,16);
			//ArrayList<Chunk> chunks = (ArrayList<Chunk>) chunk_list.clone();
			for(Chunk chunk : chunk_list){
				try{
					if(chunk.is_position(ch_x,ch_z)){ 
						return chunk.get_block_solidity(b_x,y2,b_z);
					}
				}
				catch (ConcurrentModificationException e){
					return 0;
				}
				catch (NullPointerException e){
					return 0;
				}
			}
		}
		return 0;
	}
	
	

	public void change_defaut_chunk(int new_ch_x, int new_ch_z) {
		chunk_loader.change_default_chunk(new_ch_x,new_ch_z);
		chunk_loader.load_chunks(chunk_list, game.get_player().get_view_distance());
		//chunk_loader.start();
        ///chunk_loader.load_chunks(chunk_loader.get_chunk_list(), game.get_player().get_view_distance());
	}

	public World_generator get_world_generator() {
		return this.world_gen;
	}

	public Coords_Manager get_coords_manager() {
		return this.main_coords_manager; 
	}
	
	public Chunk get_chunk(Position find_chunk) {
		if(find_chunk != null){
			return get_chunk(new Point((int)Math.floor(find_chunk.getX_D()/16.0),(int)Math.floor(find_chunk.getZ_D()/16.0)));
		}
		return null;
	}

	public Chunk get_chunk(Point find_chunk) {
		for(Chunk ch : chunk_list){
			if(ch.is_position(find_chunk.x, find_chunk.y))
				return ch;
		}
		return null;
	}

	public Position[] get_bloc_in_direction(Position position, Vector direction, float distance) {
		Position[] blocks = new Position[2];
		blocks[0] = null;
		blocks[1] = null;
		float dist = 0;
		boolean found = false;
		double x = 0, y = 0, z = 0,t = 0;
		byte d_x = 0, d_y = 0, d_z = 0, found_intersection = 0;
		Position track_point = new Position(position);
		Position current_block = new Position( Math.floor(position.getX_D()), Math.floor(position.getY_D()), Math.floor(position.getZ_D()) );
		if(direction.getX_D() > 0)
			d_x = 1;
		if(direction.getY_D() > 0)
			d_y = 1;
		if(direction.getZ_D() > 0)
			d_z = 1;
		
		while((dist < distance) && !found){
			found_intersection = 0;

			// kontrola XY steny:
			if(direction.getZ_D() != 0){ // kdyz je 0 je vektor rovnobezny s plochou -> nehledam prunik
				found_intersection = 1;
				z = current_block.getZ_I() + d_z;
				t = ((z - track_point.getZ_D()) / direction.getZ_D());
				x = direction.getX_D()*t + track_point.getX_D();
				y = direction.getY_D()*t + track_point.getY_D();
				if((x < current_block.getX_I() || x > (current_block.getX_I()+1))){
					found_intersection = 0;
				}
				if((y < current_block.getY_I() || y > (current_block.getY_I()+1))){
					found_intersection = 0;
				}
			}
			
			// kontrola YZ steny:
			if(direction.getX_D() != 0 && found_intersection == 0){
				found_intersection = 2;
				x = current_block.getX_I() + d_x;
				t = ((x - track_point.getX_D()) / direction.getX_D());
				y = direction.getY_D()*t + track_point.getY_D();
				z = direction.getZ_D()*t + track_point.getZ_D();
				if((y < current_block.getY_I() || y > (current_block.getY_I()+1))){
					found_intersection = 0;
				}
				if((z < current_block.getZ_I() || z > (current_block.getZ_I()+1))){
					found_intersection = 0;
				}
			}

			// kontrola XZ steny:
			if(direction.getY_D() != 0 && found_intersection == 0){
				found_intersection = 3;
				y = current_block.getY_I() + d_y;
				t = ((y - track_point.getY_D()) / direction.getY_D());
				x = direction.getX_D()*t + track_point.getX_D();
				z = direction.getZ_D()*t + track_point.getZ_D();
				if((x < current_block.getX_I() || x > (current_block.getX_I()+1))){
					found_intersection = 0;
				}
				if((z < current_block.getZ_I() || z > (current_block.getZ_I()+1))){
					found_intersection = 0;
				}
			}
			
			track_point.set(x,y,z);
			switch(found_intersection){
				case 1:{
					if(d_z > 0){
						current_block.addZ(1);
					}else{
						current_block.addZ(-1);
					}
				}break;
				case 2:{
					if(d_x > 0){
						current_block.addX(1);
					}else{
						current_block.addX(-1);
					}
				}break;
				case 3:{
					if(d_y > 0){
						current_block.addY(1);
					}else{
						current_block.addY(-1);
					}
				}break;
				default:{
					
				}
			}
			
			dist += 1;
			blocks[1] = blocks[0];
			blocks[0] = new Position(current_block);
			if(this.get_block_id(current_block) >= 0){
				found = true;
			}
		}
		return blocks;
	}

	public Player get_player() {
		return game.get_player();
	}

	public int get_frame_count() {
		return frame_count;
	}

	public ArrayList<Creature> get_creatures() {
		return mobs;
	}

	public void Kill_mob(Creature mob) {
		mobs.remove(mob);
		if(dead_mobs.size()< 10){
			dead_mobs.add(mob);
		}else{
			dead_mobs.remove(0);
			dead_mobs.add(mob);
		}
	}

	public void revive_player() {
		this.game.revive(this);
	}

	public void add_time(int i) {
		frame_count +=  i;
	}
}


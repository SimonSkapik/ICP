package pkg.skapik.icp.func;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.*;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class Camera extends Thread{
	
	private Mat cam_image;
	private Point obj_at;
	private VideoCapture cam;
	private boolean run_cam;
	private Random rnd;
	private int roi;
	private Mat[] cam_alpha;
	
	public Camera() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		rnd = new Random();
		roi = 0;
		run_cam = false;
		cam_image = new Mat(new Size(320,240), 0);
		obj_at = new Point(0,0);
		cam_alpha = new Mat[4];
		Mat alpha = Imgcodecs.imread("./Assets/Textures/cam_alpha_0.png",Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
		List<Mat> RGBA = new ArrayList<Mat>(4);
		Core.split(alpha,RGBA);
		cam_alpha[0] = RGBA.get(3);
		alpha = Imgcodecs.imread("./Assets/Textures/cam_alpha.png_1",Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
		RGBA = new ArrayList<Mat>(4);
		Core.split(alpha,RGBA);
		cam_alpha[1] = RGBA.get(3);
		alpha = Imgcodecs.imread("./Assets/Textures/cam_alpha.png_2",Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
		RGBA = new ArrayList<Mat>(4);
		Core.split(alpha,RGBA);
		cam_alpha[2] = RGBA.get(3);
		alpha = Imgcodecs.imread("./Assets/Textures/cam_alpha.png_3",Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
		RGBA = new ArrayList<Mat>(4);
		Core.split(alpha,RGBA);
		cam_alpha[3] = RGBA.get(3);
		try{
			cam = new VideoCapture();
			cam.open(0);
			Thread.sleep(500); 
			cam.set(Videoio.CAP_PROP_FRAME_WIDTH , 320); 
			cam.set(Videoio.CAP_PROP_FRAME_HEIGHT , 240);
			cam.set (Videoio.CAP_PROP_FOURCC, Videoio.CAP_MODE_BGR);
			Thread.sleep(500);
		}catch(Exception e){
			System.err.println(e.toString());
		}
	}
	
	public void start_cam(){
		run_cam = true;
	}
	
	public void stop_cam(){
		run_cam = false;
	}
	
	public Mat get_picture(){
		return cam_image;
	}
	
	public BufferedImage createAwtImage(Mat mat) {

	    int type = 0;
	    if (mat.channels() == 1) {
	        type = BufferedImage.TYPE_BYTE_GRAY;
	    } else if (mat.channels() == 3) {
	        type = BufferedImage.TYPE_3BYTE_BGR;
	    } else {
	        return null;
	    }

	    BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
	    WritableRaster raster = image.getRaster();
	    DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
	    byte[] data = dataBuffer.getData();
	    mat.get(0, 0, data);

	    return image;
	}
	
	private void find_object(Mat pic,int from, int to){
		
		double start = 0;
		double stop = 0;
		double times[] = new double[7];
		
		//start  = System.currentTimeMillis();
		Mat image = pic;
		Mat proc_image = new Mat(new Size(image.width(),image.height()),image.type());
		Mat proc_image_th1 = new Mat(new Size(image.width(),image.height()),CvType.CV_8U);
		Mat proc_image_th2 = new Mat(new Size(image.width(),image.height()),CvType.CV_8U);
		//Mat hierarchy = new Mat(new Size(image.width(),image.height()),CvType.CV_8U);
    	if (image.empty())
    	{
    		 System.out.println("not found\n");	
    		 System.exit(1);
    	}
    	//stop  = System.currentTimeMillis();
    	//times[0] = stop - start;
    	
		//start  = System.currentTimeMillis();
    	Imgproc.cvtColor(image, proc_image, Imgproc.COLOR_RGB2HSV);
    	//stop  = System.currentTimeMillis();
    	//times[1] = stop - start;
    	
		//start  = System.currentTimeMillis();
    	List<Mat> hsv_planes = new ArrayList<Mat>();// = new List<Mat>;
    	Core.split( proc_image, hsv_planes );
    	proc_image = hsv_planes.get(0).clone();
    	//stop  = System.currentTimeMillis();
    	//times[2] = stop - start;
    	
    	
    	//start  = System.currentTimeMillis();
    	Imgproc.threshold(proc_image, proc_image_th1, to, 255, Imgproc.THRESH_BINARY);
    	Imgproc.threshold(proc_image, proc_image_th2, from, 255, Imgproc.THRESH_BINARY);
    	Core.subtract(new Mat(new Size(proc_image_th1.width(),proc_image_th1.height()),CvType.CV_8U,new Scalar(255,255,255)), proc_image_th1, proc_image_th1);// (proc_image_th2, proc_image_th2);
    	Core.bitwise_and(proc_image_th1, proc_image_th2, proc_image);
    	//stop  = System.currentTimeMillis();
    	//times[3] = stop - start;
		
    	
    	//start  = System.currentTimeMillis();
    	int erosion_size = 3, dilate_size = 2;
    	Mat erode_mat = Imgproc.getStructuringElement( Imgproc.MORPH_ERODE, new Size( 2*erosion_size + 1, 2*erosion_size+1 ), new Point( erosion_size, erosion_size ) );
    	Mat dilate_mat = Imgproc.getStructuringElement( Imgproc.MORPH_DILATE, new Size( 2*dilate_size + 1, 2*dilate_size+1 ), new Point( dilate_size, dilate_size ) );
    	//stop  = System.currentTimeMillis();
    	//times[4] = stop - start;
		
    	//start  = System.currentTimeMillis();
    	Imgproc.dilate (proc_image, proc_image, dilate_mat);
    	Imgproc.erode (proc_image, proc_image, erode_mat);
    	//List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    	//Imgproc.findContours(proc_image, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
    	//Imgproc.fillPoly(proc_image, contours, new Scalar(255));
    	Imgproc.erode (proc_image, proc_image, erode_mat);
    	Imgproc.erode (proc_image, proc_image, erode_mat);
    	//stop  = System.currentTimeMillis();
    	//times[5] = stop - start;
    	
    	//start  = System.currentTimeMillis();
    	
    	Point mass_center = new Point(-1,-1);
    	// FIND MASS CENTER
    	/*
    	System.out.println("-----------");
    	do{
    		System.out.println("start roi " + roi);
	    	switch(roi){
	    		case 0:{
	    			mass_center = find_mass_center(proc_image, new Point(0,0),new Point(640,480));
	    		}break;
	    		case 1:{
	    			mass_center = find_mass_center(proc_image, new Point(Math.max(0,(int)(obj_at.x-160)),Math.max(0,(int)(obj_at.y-120))),new Point(Math.min(640,(int)(obj_at.x+160)),Math.min(480,(int)(obj_at.y+120))));
	    		}break;
	    		case 2:{
	    			mass_center = find_mass_center(proc_image, new Point(Math.max(0,(int)(obj_at.x-80)),Math.max(0,(int)(obj_at.y-60))),new Point(Math.min(640,(int)(obj_at.x+80)),Math.min(480,(int)(obj_at.y+60))));
	    		}break;
	    	}
	    	System.out.println("end roi " + roi);
    	}while(mass_center.x < 0);
    	System.out.println("-----------");
    	*/
    	//stop  = System.currentTimeMillis();
    	//times[6] = stop - start;
    	//Imgproc.rectangle(image, new Point(min_x, min_y), new Point(max_x, max_y), new Scalar(250,130,0)); 
    	//Imgproc.line(image, new Point(mass_x, mass_y-30), new Point(mass_x, mass_y+30), new Scalar(130,250,0));
    	//Imgproc.line(image, new Point(mass_x-30, mass_y), new Point(mass_x+30, mass_y), new Scalar(130,250,0));
    	
    	/*Imgcodecs.imwrite("proc_"+from+"-"+to+ "_" + name,proc_image);
    	Imgcodecs.imwrite("proc_h_" + name, hsv_planes.get(0) );
    	Imgcodecs.imwrite("proc_s_" + name, hsv_planes.get(1) );
    	Imgcodecs.imwrite("proc_v_" + name, hsv_planes.get(2) );
    	Imgcodecs.imwrite("th1_" + name,proc_image_th1);
    	Imgcodecs.imwrite("th2_" + name,proc_image_th2);*/
    	
    	//Imgcodecs.imwrite("proc_"+from+"-"+to+ "_" + name,image);
    	
    	//Imgcodecs.imwrite("proc_" + name,image);
		//System.out.println(times[0] + " " + times[1] + " " + times[2] + " " + times[3] + " " + times[4] + " " + times[5] + " " + times[6]);
    	mass_center = find_mass_center(proc_image, new Point(0,0),new Point(640,480));
		obj_at = mass_center;
		
	}
	
	private Point find_mass_center(Mat img, Point from, Point to){
		Point center = new Point(-1,-1);
		
		//System.out.println("from x: " + from.x + "from y: " + from.y + "to x: " + to.x + "to y: " + to.y);
		
		int white_pxls = 0, count_x = 0, count_y = 0;
		for( int i = (int)from.y; i < to.y; i+=2 ){
   	  		for( int j = (int)from.x; j < to.x; j+=2 ){
   				if(!Custom_Draw.is_black(img.get(i, j))){
   					white_pxls += 1;
   					count_x += j;
   					count_y += i;
   				}
   			}
   	  	}
		//System.out.println("wp: " + white_pxls + "cx: " + count_x + "cy" + count_y);
    	
    	if(white_pxls > 10){
    		center = new Point(count_x/white_pxls,count_y/white_pxls);
    		if(roi < 2){
    			roi += 1;
    		}
    	}else{
    		//center = obj_at;
    		if(roi > 0){
    			roi -= 1;
    		}else{
    			center.x = 0;
    			center.y = 0;
    		}
    	}
		
		return center;
	}
	
	public Point Get_obj_position(Size frame_size){
		Point result = new Point(obj_at.x,obj_at.y);
		double k_x = frame_size.width / 640;
		double k_y = frame_size.height / 480;
		result.x = (int)(result.x * k_x);
		result.y = (int)(result.y * k_y);
		return result;
	}
	
	public void release_cam(){
		cam.release();
	}
	
	private Mat applyAlpha(Mat src) {
		float noise = rnd.nextFloat()*12.99f;
		if(noise >= 10){
			noise = 0;
		}else{
			noise = (noise-9);
		}
		List<Mat> RGB = new ArrayList<Mat>(3);
		Core.split(src,RGB);
		
		List<Mat> RGBA = new ArrayList<Mat>(4);
		RGBA.add(RGB.get(0));
		RGBA.add(RGB.get(1));
		RGBA.add(RGB.get(2));
		
		RGBA.add(cam_alpha[(int)noise]);
		Mat out = new Mat();
		Core.merge(RGBA, out);
		return out;
		
	}

	@Override
	public void run() {
		//System.out.println("start");
		for(;;){
			//System.out.println("in_for");
			if(run_cam){
				//System.out.println("run_true");
				//System.out.println("in_try");
				cam.read(cam_image);
				//System.out.println("done_readin");
				//find_object(cam_image,115,125);
				Imgcodecs.imwrite("./Assets/Textures/cam_texture.png",applyAlpha(cam_image));
				try {
					Files.copy(Paths.get("./Assets/Textures/cam_texture.png"), Paths.get("./Assets/Textures/cam_texture_done.png"), REPLACE_EXISTING);
				} catch (IOException e) {
					
				}
				Thread.yield();
				//System.out.println("ryze and shrine");
			}
			//System.out.println("repete");
		}
		
	}
}

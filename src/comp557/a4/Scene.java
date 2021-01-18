package comp557.a4;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import javax.vecmath.Color4f;
import javax.vecmath.Point2d;

/**
 * Simple scene loader based on XML file format.
 */
public class Scene {
    
    /** List of surfaces in the scene */
    public List<Intersectable> surfaceList = new ArrayList<Intersectable>();
	
	/** All scene lights */
	public Map<String,Light> lights = new HashMap<String,Light>();

    /** Contains information about how to render the scene */
    public Render render;
    
    /** The ambient light colour */
    public Color3f ambient = new Color3f();
 
    public boolean depthBlur = false;
    public int blurSamples;
    public double focallength;

    public boolean motion = false;
    public double speed;
    
    public FastPoissonDisk fpd = new FastPoissonDisk();
    /** 
     * Default constructor.
     */
    public Scene() {
    	this.render = new Render();
    }
    
    /**
     * renders the scene
     */
    public void render(boolean showPanel) {
        Camera cam = render.camera; 
        int w = cam.imageSize.width;
        int h = cam.imageSize.height;
        render.init(w, h, showPanel);
        
        MiraclecWoker mw0 = new MiraclecWoker(w, h, 0, this, cam);
        MiraclecWoker mw1 = new MiraclecWoker(w, h, 1, this, cam);
        MiraclecWoker mw2 = new MiraclecWoker(w, h, 2, this, cam);
        MiraclecWoker mw3 = new MiraclecWoker(w, h, 3, this, cam);
        
        mw0.start();
        mw1.start();
        mw2.start();
        mw3.start();
        
        render.save();
        render.waitDone();
        
    }

    /**
     * Generate a ray through pixel (i,j).
     * 
     * @param i The pixel row.
     * @param j The pixel column.
     * @param offset The offset from the center of the pixel, in the range [-0.5,+0.5] for each coordinate. 
     * @param cam The camera.
     * @param ray Contains the generated ray.
     */

    

    public static void generateRay(final int i, final int j, final double[] offset, final Camera cam, Ray ray) {
    	double width = cam.imageSize.width;
		double height = cam.imageSize.height;
		
		double d  = cam.from.distance(cam.to);
		double ar = (width / height); // aspect ratio	
		double top = Math.tan(Math.toRadians(cam.fovy)/2.0) * d;
		double bottom = -top;
		double left = -ar * top;
		double right = ar * top;
		
		Vector3d W = new Vector3d();
        W.sub(cam.from, cam.to);
        W.normalize();	

		Vector3d U = new Vector3d();
		U.cross(cam.up, W);
		U.normalize();
		
		Vector3d V = new Vector3d();
		V.cross(U, W);
		V.normalize();	
		double u = left + (right - left) * (j+offset[0]) / cam.imageSize.width; 
		double v = bottom + (top - bottom) * (i+offset[1]) / cam.imageSize.height; 			
		
		U.scale(u);
		V.scale(v);
		W.scale(d);

		Vector3d s = new Vector3d();
		s.add(U);
		s.add(V);
		s.sub(W);

		s.normalize();
		ray.set(cam.from, s);
	}
	
	
	
	public Color4f getLight(Ray ray, IntersectResult result) {
    	Color4f c = new Color4f();
    	if(result.t == Double.POSITIVE_INFINITY) {
    		c.x = render.bgcolor.x;
    		c.y = render.bgcolor.y;
    		c.z = render.bgcolor.z;
    		
    		return c;
    	}
    	
    	// ambient
    	c.x = ambient.x * result.material.diffuse.x;
    	c.y = ambient.y * result.material.diffuse.y;
    	c.z = ambient.z * result.material.diffuse.z;
    	
    
    	for(Light light: lights.values()) {
    		Ray shadowRay = new Ray();
    		IntersectResult shadowResult = new IntersectResult();
    		
    		int minGrid = 2;
    		int maxGrid = 8; 
    
    		int randomNum = minGrid + (int)(Math.random() * ((maxGrid - minGrid) + 1));
    		
    		Color4f tmpC = new Color4f();
    		
    		for(int i = 0; i < randomNum; i++) {
        		for(int j = 0; j < randomNum; j++) {
        			
        			Light jittered = new Light();
        			jittered.color.set(light.color);
        			jittered.from.set(light.from);
        			jittered.power = light.power;
        			
            		Vector3d a = new Vector3d(2, 2, 0);
            		Vector3d b = new Vector3d(0, 2, 2);
            		a.scale((double)i/(double)randomNum); 
            		b.scale((double)j/(double)randomNum);
            		
            		jittered.from.set(jittered.from.x+a.x+b.x, jittered.from.y+a.y+b.y, jittered.from.z+a.z+b.z);
            		
            		
            		if(!inShadow(result, surfaceList, jittered, shadowResult, shadowRay)) {
        	    		// Lambertian light
        	    		Color3f lam = new Color3f();
        	    		Vector3d l = new Vector3d();
        	    		l.sub(jittered.from, result.p);
        	    		l.normalize();
        	    	
        	    		double max = Math.max(0.0, l.dot(result.n));
        	    		lam.x = (float)(light.color.x * light.power * result.material.diffuse.x * max);
        	    		lam.y = (float)(light.color.y * light.power * result.material.diffuse.y * max);
        	    		lam.z = (float)(light.color.z * light.power * result.material.diffuse.z * max);
        	    		
        	    		// Blinn-Phone light
        	    		Color3f bling = new Color3f();
        	    		Vector3d bi = new Vector3d(ray.viewDirection); // bisector
						bi.negate();
						bi.add(l);
						bi.normalize();
        	    		
        	    		max = Math.max(0.0, bi.dot(result.n));
        	    		bling.x = (float)( light.color.x * light.power * result.material.specular.x * Math.pow(max, result.material.shinyness));
        	    		bling.y = (float)( light.color.y * light.power * result.material.specular.y * Math.pow(max, result.material.shinyness));
        	    		bling.z = (float)( light.color.z * light.power * result.material.specular.z * Math.pow(max, result.material.shinyness));
        	    		
        	    		tmpC.x += lam.x+bling.x;
        	    		tmpC.y += lam.y+bling.y;
        	    		tmpC.z += lam.z+bling.z;
            		}
            		
            		// reflection
            		if(result.material.reflect != null) {
            			Vector3d d = new Vector3d();
            			Vector3d n = new Vector3d();
            			
            			Ray reflectRay = new Ray();
            			IntersectResult reflectResult = new IntersectResult();
            			reflectRay.eyePoint.set(result.p); 
            			reflectRay.viewDirection.set(ray.viewDirection);
            			
            			d.set(ray.viewDirection);
            			n.set(result.n);
            			n.scale(-2.0 * d.dot(n)); 
            			reflectRay.viewDirection.add(n);
            			
            			Vector3d EPS = new Vector3d();
            			EPS.set(reflectRay.viewDirection);
            			EPS.scale(1e-8);
            			reflectRay.eyePoint.add(EPS); 
            			
            			for(Intersectable surface: surfaceList) {
            				surface.intersect(reflectRay, reflectResult); 
            			}
            			
            			Color4f refelectC = getLight(reflectRay, reflectResult);
            			tmpC.x += result.material.reflect.x * refelectC.x;
            			tmpC.y += result.material.reflect.y * refelectC.y;
            			tmpC.z += result.material.reflect.z * refelectC.z;
            		}
        		}
    		}
    		tmpC.scale(1/(float)(randomNum * randomNum));
    		c.add(tmpC);	
    	}
    	
    	return c;
    }

	/**
	 * Shoot a shadow ray in the scene and get the result.
	 * 
	 * @param result Intersection result from raytracing. 
	 * @param light The light to check for visibility.
	 * @param root The scene node.
	 * @param shadowResult Contains the result of a shadow ray test.
	 * @param shadowRay Contains the shadow ray used to test for visibility.
	 * 
	 * @return True if a point is in shadow, false otherwise. 
	 */
	public static boolean inShadow(final IntersectResult result, final List<Intersectable> surfaces, final Light light, IntersectResult shadowResult, Ray shadowRay) {

		shadowRay.viewDirection.x = light.from.x - result.p.x;
		shadowRay.viewDirection.y = light.from.y - result.p.y;
		shadowRay.viewDirection.z = light.from.z - result.p.z;
		
		Point3d eye = new Point3d();
		Vector3d d = new Vector3d();
		
		d.set(shadowRay.viewDirection);
		d.scale(0.000001d);
		eye.set(result.p);
		eye.add(d);
		shadowRay.eyePoint.set(eye);
		
		for(Intersectable surface: surfaces) {
			surface.intersect(shadowRay, shadowResult);
		}
		
		if(shadowResult.t>0 && shadowResult.t < 1) {
			return true;
		}
		return false;

	}    


	class MiraclecWoker extends Thread{
		Scene scene;
		Camera cam;
		int top; 
		int bot;
		int left; 
		int right;  
		int label;
		
		MiraclecWoker(int w, int h, int label, Scene scene, Camera cam){
			this.scene = scene;
			this.cam = cam;
			this.label = label;
			if(this.label == 0) { // top left part
				this.left = 0;
				this.right = w/2;
				this.top = 0;
				this.bot = h/2;
			}
			if(this.label == 1) { // bottom left part
				this.left = 0;
				this.right = w/2;
				this.top = h/2;
				this.bot = h;
	
			}
			if(this.label == 2) { // top right part
				this.left = w/2;
				this.right = w;
				this.top = 0;
				this.bot = h/2;
			}
			if(this.label == 3) { // bottom right part
				this.left = w/2;
				this.right = w;
				this.top = h/2;
				this.bot = h;
			}
			
		}

		public void run() {
			double[] offset = new double[2];
			Random rand = new Random();
			for ( int i = top; i < bot && !render.isDone(); i++ ) {
	            for ( int j = left; j < right && !render.isDone(); j++ ) {
	            	
	            	Color4f c = new Color4f();
	            	
	            	ArrayList<Ray> rays = new ArrayList<Ray>(); //store the sample rays 
	            	
	            	offset[0] = (rand.nextDouble() - 0.5) * 0.1;
                    offset[1] = (rand.nextDouble() - 0.5) * 0.1;
	            	
                    // depth of field blur
	            	if( depthBlur ){
	                    Ray blur_ray = new Ray();
	                    generateRay(i, j, offset, cam, blur_ray);
	                    Point3d fp = new Point3d(blur_ray.eyePoint);
	                    Vector3d temp = new Vector3d(blur_ray.viewDirection);
	                    temp.scale(focallength);
	                    fp.add(temp);

	                    for(int k = 0; k < blurSamples; k++){
	                        Point2d p = new Point2d();
	                        Ray focal = new Ray();

	                        fpd.get(p, k, blurSamples);

	                        Camera tempCam = new Camera();
	                        tempCam.to.set(cam.to);
	                        tempCam.from = new Point3d(cam.from.x + p.x/10f, cam.from.y + p.y/10f,
	                                cam.from.z);
	                        tempCam.up.set(cam.up);
	                        tempCam.fovy = cam.fovy;
	                        tempCam.imageSize.setSize(cam.imageSize);


	                        focal.eyePoint.set(tempCam.from);
	                        focal.viewDirection.set(fp);
	                        focal.viewDirection.sub(tempCam.from);
	                        focal.viewDirection.normalize();

	                        rays.add(focal);
	                    }

	                }
	            	
	            	
	            	int grid = (int)Math.sqrt(render.samples);
	                double jitter = 0.5;
	            	
	            	for(int p = 0; p < grid; p++) {
	            		jitter = render.jitter? Math.random() * 0.5 : 0.5;
	            		offset[0] = (double)(p + jitter)/grid; 
	            		for(int q = 0; q < grid; q++) {
	            			jitter = render.jitter? Math.random() * 0.5 : 0.5;
	                		offset[1] = (double)(q + jitter)/grid; 
	            	
	            			Ray tmpRay = new Ray();
	            			generateRay(i, j, offset, cam, tmpRay);
	            			rays.add(tmpRay);
	            		}
	            	}
	            	
	            	
	            	// calculate light for each ray
	            	for(Ray ray: rays) {
	            		IntersectResult result = new IntersectResult();
	            		for(Intersectable surface: surfaceList) {
	            			surface.intersect(ray, result);
	            			result.n.normalize();
	            		}
	            		
	                	Color4f tempColor = getLight(ray, result);
	            		
	            		if(depthBlur) {
	            			tempColor.scale(1f/blurSamples);
                            c.x += tempColor.x;
                            c.y += tempColor.y;
                            c.z += tempColor.z;
                        }
	            		c.add(tempColor);
	            	}
	            
	            	c.scale(1/(float)render.samples);
	            	c.clamp(0,1);
	            	
	            	int r = (int)(255 * c.x);
	                int g = (int)(255 * c.y);
	                int b = (int)(255 * c.z);
	                
	                if(r > 255){
						r = 255;
					}
					if(g > 255){
						g = 255;
					}
					if(b > 255){
						b = 255;
					}     
	                int a = 255;
	                int argb = (a<<24 | r<<16 | g<<8 | b);    
	            
	                render.setPixel(j, i, argb);
	            }
	        }
	        
	        // save the final render image
	        render.save();
	        
	        // wait for render viewer to close
	        render.waitDone();
		}
	}
	
}
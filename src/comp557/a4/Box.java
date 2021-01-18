package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple box class. A box is defined by it's lower (@see min) and upper (@see max) corner. 
 */
public class Box extends Intersectable {

	public Point3d max;
	public Point3d min;
	
    /**
     * Default constructor. Creates a 2x2x2 box centered at (0,0,0)
     */
    public Box() {
    	super();
    	this.max = new Point3d( 1, 1, 1 );
    	this.min = new Point3d( -1, -1, -1 );
    }	
    private void swap(double x, double y) {
    	double temp = x;
    	x = y;
    	y = temp;
    	return;
    }
	@Override
	public void intersect(Ray ray, IntersectResult result) {
		// TODO: Objective 6: intersection of Ray with axis aligned box
	
		double tmin, tmax, tymin, tymax, tzmin, tzmax;
		if(ray.viewDirection.x < 0){
			tmin = (max.x - ray.eyePoint.x) / ray.viewDirection.x;
			tmax = (min.x - ray.eyePoint.x) / ray.viewDirection.x;
		}else{
			tmin = (min.x - ray.eyePoint.x) / ray.viewDirection.x;
			tmax = (max.x - ray.eyePoint.x) / ray.viewDirection.x;
		}
		if (tmin > tmax) {
			swap(tmin, tmax);
		}
		
		if(ray.viewDirection.y < 0){
			tymin = (max.y - ray.eyePoint.y) / ray.viewDirection.y;
			tymax = (min.y - ray.eyePoint.y) / ray.viewDirection.y;
		}else{
			tymin = (min.y - ray.eyePoint.y) / ray.viewDirection.y;
			tymax = (max.y - ray.eyePoint.y) / ray.viewDirection.y;
		}
		if (tymin > tymax) {
			swap(tymin, tymax);
		}
		
		if ((tmin > tymax) || (tymin > tmax)) return;
		if (tymax < tmax) tmax = tymax;
		if (tymin > tmin) tmin = tymin;
		
		if(ray.viewDirection.z < 0){
			tzmin = (max.z - ray.eyePoint.z) / ray.viewDirection.z;
			tzmax = (min.z - ray.eyePoint.z) / ray.viewDirection.z;
		}else{
			tzmin = (min.z - ray.eyePoint.z) / ray.viewDirection.z;
			tzmax = (max.z - ray.eyePoint.z) / ray.viewDirection.z;
		}
		if (tzmin > tzmax) {
			swap(tzmin, tzmax);
		}
		if ((tmin > tzmax) || (tzmin > tmax)) return;
		if (tzmin > tmin) tmin = tzmin;
		if (tzmax < tmax) tmax = tzmax;
		
		
		Point3d intersect = new Point3d();
		ray.getPoint(tmin, intersect); 		
		result.p = intersect;
		result.material = this.material;
		result.t = tmin;
		
		Vector3d n = new Vector3d(intersect);

		if(Math.abs(n.x - min.x) < (float)0.00001) {
			n.set(-1,0,0);
		}else if(Math.abs(n.x - max.x) < (float)0.00001) {
			n.set(1,0,0);
		}else if(Math.abs(n.y - min.y) < (float)0.00001) {
			n.set(0,-1,0);
		}else if(Math.abs(n.y - max.y) < (float)0.00001) {
			n.set(0,1,0);
		}else if(Math.abs(n.z - min.z) < (float)0.00001) {
			n.set(0,0,-1);
		}else if(Math.abs(n.z - max.z) < (float)0.00001) {
			n.set(0,0,1);
		}
		result.n = n;
		
	}	

}











package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple sphere class.
 */
public class Sphere extends Intersectable {
    
	/** Radius of the sphere. */
	public double radius = 1;
    
	/** Location of the sphere center. */
	public Point3d center = new Point3d( 0, 0, 0 );
    
    /**
     * Default constructor
     */
    public Sphere() {
    	super();
    }
    
    /**
     * Creates a sphere with the request radius and center. 
     * 
     * @param radius
     * @param center
     * @param material
     */
    public Sphere( double radius, Point3d center, Material material ) {
    	super();
    	this.radius = radius;
    	this.center = center;
    	this.material = material;
    }
    
    @Override
    public void intersect( Ray ray, IntersectResult result ) {
        // TODO: Objective 2: intersection of ray with sphere
    	
    	Vector3d p = new Vector3d();
    	p.sub(ray.eyePoint, center);
    	Vector3d d = new Vector3d(ray.viewDirection);
    	
    	double discriminant = Math.pow(d.dot(p),2) - d.dot(d) * (p.dot(p) - radius * radius);
    	
    	if (discriminant < 0) return;
    	double t1 = (-d.dot(p) + Math.sqrt(discriminant)) / d.dot(d);   		
		double t2 = (-d.dot(p) - Math.sqrt(discriminant)) / d.dot(d);

		double t = Math.min(t1, t2);
    	
    	Point3d intersect = new Point3d();
    	ray.getPoint(t, intersect);
    	result.p = intersect;
    	result.material = this.material;
    	if (t < result.t && t >= 0.0) {
    		result.t = t;
    	}
    	
    	
    	Vector3d n = new Vector3d(intersect);
    	n.sub(center);
    	n.scale(1/this.radius);
    	result.n = n;
    	
    }
    
}



















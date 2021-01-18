package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Class for a plane at y=0.
 * 
 * This surface can have two materials.  If both are defined, a 1x1 tile checker 
 * board pattern should be generated on the plane using the two materials.
 */
public class Plane extends Intersectable {
    
	/** The second material, if non-null is used to produce a checker board pattern. */
	Material material2;
	
	/** The plane normal is the y direction */
	public static final Vector3d n = new Vector3d( 0, 1, 0 );
    
    /**
     * Default constructor
     */
    public Plane() {
    	super();
    }

        
    @Override
    public void intersect( Ray ray, IntersectResult result ) {
        // TODO: Objective 4: intersection of ray with plane
    	Vector3d eyePos = new Vector3d(ray.eyePoint);
    	Vector3d dir = new Vector3d(ray.viewDirection);
    	double t = - eyePos.y / dir.y;
    	
    	if (dir.y >= 0 || t >= result.t || (t < 1e-9)) return;	

		// intersect result
		Point3d intersect = new Point3d();
		ray.getPoint(t, intersect); 
		result.n.set(n);
		result.p.set(intersect);	
		if (t < result.t && t >= 0.0) {
    		result.t = t;
    	}
			
		// set material
		boolean gridx, gridz;
		
		if (((int) Math.floor(intersect.x) % 2) != 0) gridx = true;
		else gridx = false;
		if (((int) Math.floor(intersect.z) % 2) != 0) gridz = true;
		else gridz = false;
		
		if (gridx == gridz) 
			result.material = this.material;	// only one material
		else {									// checker board
			if (this.material2 == null) result.material = this.material;
			else result.material = this.material2;
		}
		
    }
    
}

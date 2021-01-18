package comp557.a4;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Mesh extends Intersectable {
	
	/** Static map storing all meshes by name */
	public static Map<String,Mesh> meshMap = new HashMap<String,Mesh>();
	
	/**  Name for this mesh, to allow re-use of a polygon soup across Mesh objects */
	public String name = "";
	
	/**
	 * The polygon soup.
	 */
	public PolygonSoup soup;

	public Mesh() {
		super();
		this.soup = null;
	}			
		
	@Override
	public void intersect(Ray ray, IntersectResult result) {
		
		// TODO: Objective 9: ray triangle intersection for meshes
		double a,b,c,d,e,f;
		
		for(int[] vertex: soup.faceList){	
			// triangle node
			Point3d v1 = soup.vertexList.get(vertex[0]).p;
			Point3d v2 = soup.vertexList.get(vertex[1]).p;
			Point3d v3 = soup.vertexList.get(vertex[2]).p;		

			a = v1.x - v2.x;
			b = v1.y - v2.y;
			c = v1.z - v2.z;
			d = v1.x - v3.x;
			e = v1.y - v3.y;
			f = v1.z - v3.z;
			
			double dirx = ray.viewDirection.x;
			double diry = ray.viewDirection.y;
			double dirz = ray.viewDirection.z;
			
			double dx = v1.x - ray.eyePoint.x;
			double dy = v1.y - ray.eyePoint.y;
			double dz = v1.z - ray.eyePoint.z;

			double gr = a * ((e * dirz)-(diry * f)) + b * ((dirx * f)-(d * dirz)) 
					+ c * ((d * diry)-(e * dirx));
			
			double t = - (f * (a * dy - dx * b) + e * (dx * c-a * dz) 
					+ d * (b * dz - dy * c)) / gr;

			if(t > -0.0001 && t < result.t){
				double div1 = (dirz * (a * dy - dx * b) + 
						diry * (dx * c - a * dz) + dirx * (b * dz - dy * c)) / gr;
				
				if(div1 > 0 & div1 < 1){
					double div2 = (dx*(e*dirz-diry*f) + dy*(dirx*f-d*dirz) + dz*(d*diry-e*dirx)) / gr;
					
					if(div2 > 0 && div2 < 1 - div1){
						// p = e + td
						Point3d intersect = new Point3d(ray.viewDirection);
						intersect.scale(t);
						intersect.add(ray.eyePoint); 		

						// normal
						Vector3d ba = new Vector3d();
						Vector3d ca = new Vector3d();
						Vector3d n = new Vector3d();
						ba.sub(v2,v1);
						ca.sub(v3,v1);
						n.cross(ba, ca);
						n.normalize();
						
						result.n.set(n);
						result.p.set(intersect);
						result.material = this.material;
						if (t < result.t && t >= 0.0) {
				    		result.t = t;
				    	}
					}
				}
			}
		}
		
	}

}

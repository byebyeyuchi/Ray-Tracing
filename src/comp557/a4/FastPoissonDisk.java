package comp557.a4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class FastPoissonDisk {

	private double r = 0.05;
	
	private ArrayList<Point2d> samples = new ArrayList<Point2d>();

	public FastPoissonDisk() {
		createSamples();
	}
	
	public FastPoissonDisk( double r ) {
		this.r = r;
		createSamples();
	}
	
	private void createSamples() {		
		ArrayList<Point2d> sampleList = new ArrayList<Point2d>();

		Point2d p = new Point2d(0, 0); 
		sampleList.add( p );
		samples.add( p );

		Random rand = new Random();

		while ( !sampleList.isEmpty() ) {
			int index = rand.nextInt(sampleList.size());
			p = sampleList.get(index);
			int added = 0;
			
			for ( int k = 0; k < 30; k++ ) {
				final Vector2d v = new Vector2d();
				do {
					v.set( rand.nextDouble() * 4 * r - 2 * r, rand.nextDouble() * 4 * r - 2 * r);
				} while ( (v.length() > 2 * r) || (v.length() < r) );
				
				
				Point2d pnew = new Point2d();
				pnew.add( p, v );
				if ( pnew.x >  1 ) pnew.x -= 2;
				if ( pnew.x < -1 ) pnew.x += 2;
				if ( pnew.y >  1 ) pnew.y -= 2;
				if ( pnew.y < -1 ) pnew.y += 2;
				boolean far = true;
				for ( int i = 0; i < samples.size(); i++ ) {
					Point2d ptmp = samples.get( i );
					if ( ptmp == p ) continue;
					if ( ptmp.distance(pnew) < r ) {
						far = false;
						break;
					}
				}
				if ( far ) {
					sampleList.add( pnew );
					samples.add( pnew );					
					added++;
				}
			}
			if ( added == 0 ) {
				sampleList.remove(index);
			}
		}
	
		Collections.sort( samples, new Comparator<Point2d>() {
			public int compare( Point2d p1, Point2d p2 ) {
				final Point2d midPoint = new Point2d(0,0);
				double v = p1.distance(midPoint) - p2.distance(midPoint);
				if ( v < 0 ) return -1;
				if ( v > 0 ) return 1;
				return 0;
			}
		});
	}

	public void get( Point2d p, int i, int N ) {
		final Point2d mid = new Point2d(0,0);
		Point2d farthest = samples.get(N-1);
		double radius = farthest.distance(mid);
		if ( radius == 0 ) radius = 1;
		
		p.set( samples.get(i) );
		p.scale( 1/radius );
	}
	
}
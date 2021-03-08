package me.elgamer.publicbuilds.utils;

public class Line {
	
	Point p1;
	Point p2;
	double a;
	double b;
	
	double dis;

	
	public Line(Point p1, Point p2) {
		
		this.p1 = p1;
		this.p2 = p2;
		
		a = (p2.z-p1.z)/(p2.x-p2.x);
		b = p1.z-(a*p1.x);
		
		dis = Math.sqrt(Math.pow((p2.x-p1.x),2)+Math.pow((p2.z-p1.z),2));
		
	}
}

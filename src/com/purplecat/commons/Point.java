package com.purplecat.commons;

public final class Point {
	public int x, y;
	
	public Point() {
		x = 0;
		y = 0;
	}
	
	public Point(int x1, int y2) {
		x = x1;
		y = y2;
	}
	
	public Point(Point p) {
		x = p.x;
		y = p.y;
	}
}

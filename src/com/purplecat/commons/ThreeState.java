package com.purplecat.commons;

public enum ThreeState {
	UNKNOWN,
	CHECKED,
	UNCHECKED;
//	public ImageIcon mIcon = null;
	public ThreeState	next()			{
		int i = ThreeState.getValue(this);
		i = (i+1) % ThreeState.values().length;
		return(ThreeState.values()[i]);
	}
	
	public static int getValue(ThreeState state) {
		for ( int i = 0; i < ThreeState.values().length; i++ ) {
			if ( ThreeState.values()[i] == state )
				return(i);
		}
		return(-1);
	}
}
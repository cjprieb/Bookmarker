package com.purplecat.commons;

public final class TTableColumn {	
	final String 	mField;
	final String 	mName;
	final Class<?> 	mClass;
	final Object 	mSample;	

	public TTableColumn(String name) {
		mField = name;
		mName = name;
		mClass = String.class;
		mSample = null;
	}

	public TTableColumn(String name, Class<?> cls, Object sample) {
		mField = name;
		mName = name;
		mClass = cls;
		mSample = sample;
	}
	
	public String getField()		{ return(mField); 	}
	public String getName() 		{ return(mName); 	}
	public Class<?> getClassType() 	{ return(mClass); 	}
	public Object getSampleValue() 	{ return(mSample); 	}
	@Override public String toString()		{ return(mName + "-" + mClass); }
}

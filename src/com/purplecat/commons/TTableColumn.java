package com.purplecat.commons;

public final class TTableColumn {	
	final String 	mField;
	final String 	mName;
	final Class<?> 	mClass;
	final Object 	mSample;
	final String	mImageId;

	public TTableColumn(String name) {
		mField = name;
		mName = name;
		mClass = String.class;
		mSample = null;
		mImageId = null;
	}

	public TTableColumn(String name, Class<?> cls, Object sample) {
		mField = name;
		mName = name;
		mClass = cls;
		mSample = sample;
		mImageId = null;
	}

	public TTableColumn(String name, Class<?> cls, Object sample, boolean isImage) {
		mField = name;
		mName = name;
		mClass = cls;
		mSample = isImage ? null : sample;
		mImageId = isImage ? sample.toString() : null;
	}
	
	public String getField()		{ return(mField); 	}
	public String getName() 		{ return(mName); 	}
	public Class<?> getClassType() 	{ return(mClass); 	}
	public Object getSampleValue() 	{ return(mSample); 	}
	public String getImageId() 		{ return(mImageId); 	}
	@Override public String toString()		{ return(mName + "-" + mClass); }
}

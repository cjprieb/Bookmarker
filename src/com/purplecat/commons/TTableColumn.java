package com.purplecat.commons;

public final class TTableColumn {	
	final int 	mNameId;
	final Class<?> 	mClass;
	final Object 	mSample;
	final String	mImageId;

	public TTableColumn(int name) {
		mNameId = name;
		mClass = String.class;
		mSample = null;
		mImageId = null;
	}

	public TTableColumn(int name, Class<?> cls, Object sample) {
		mNameId = name;
		mClass = cls;
		mSample = sample;
		mImageId = null;
	}

	public TTableColumn(int name, Class<?> cls, Object sample, boolean isImage) {
		mNameId = name;
		mClass = cls;
		mSample = isImage ? null : sample;
		mImageId = isImage ? sample.toString() : null;
	}
	
	public int getNameId() 		{ return(mNameId); 	}
	public Class<?> getClassType() 	{ return(mClass); 	}
	public Object getSampleValue() 	{ return(mSample); 	}
	public String getImageId() 		{ return(mImageId); 	}
	@Override public String toString()		{ return(mNameId + "-" + mClass); }
}

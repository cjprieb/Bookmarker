package com.purplecat.commons.swing;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.logs.ILoggingService;

public class SwingResourceService implements IResourceService {
	static final String TAG = "SwingResourceService";
	
	String _resourcePath;	
	Map<Integer, String> _stringCache;
	Map<Integer, String> _imageCache;
	ILoggingService _logger;
	
	@Inject
	public SwingResourceService(ILoggingService logger, @Named("Resource File") String resourcePath) {
		_resourcePath = resourcePath;
		_logger = logger;
		
		_stringCache = new HashMap<Integer, String>();
		_imageCache = new HashMap<Integer, String>();
		
		//String sResourceClass = String.format("com.purplecat.%s.Resources", appName);
		String sXmlLocation = String.format("/%s/data/strings_labels.xml", resourcePath.replaceAll("\\.", "/").toLowerCase());
		loadItemMapFromXml(resourcePath, sXmlLocation, "string", _stringCache);
		
		sXmlLocation = String.format("/%s/data/images.xml", resourcePath.replaceAll("\\.", "/").toLowerCase());
		loadItemMapFromXml(resourcePath, sXmlLocation, "image", _imageCache);
		//SwingFileManager.loadItemMapFromXml("com.purplecat.commons.Resources", "/com/purplecat/commons/resources/strings_labels.xml", "string", mCache);
	}

	@Override
	public String getString(int id) {
		if ( _stringCache.containsKey(id) ) {
			return _stringCache.get(id);
		}
		else {
			_logger.error(TAG, "Could not find matching string for id " + Integer.toHexString(id));
			return "";
		}
	}

	@Override
	public String getImageFile(int id) {
		if ( _imageCache.containsKey(id) ) {
			return _imageCache.get(id);
		}
		else {
			_logger.error(TAG, "Could not find matching image string for id " + Integer.toHexString(id));
			return "";
		}
	}
	
	private void loadItemMapFromXml(String classPath, String resourcePath, String tagName, Map<Integer, String> cache){
//		Log.logMessage(0, "loading xml in " + resourcePath);
		try {
			InputStream stream = getClass().getResourceAsStream(resourcePath);
			if ( stream == null ) {
				_logger.error(TAG, "stream is null for resource " + resourcePath);
			}
			else {
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
	            NodeList list = doc.getElementsByTagName(tagName);
	            for ( int i = 0; i < list.getLength(); i++ ) {
	            	Node node = list.item(i);
	            	String name = node.getAttributes().getNamedItem("name").getNodeValue();
	            	try {	            		
	            		Class<?> rClass = Class.forName(classPath);
	            		Class<?> foundClass = null;
	            		for ( Class<?> declaredClass : rClass.getDeclaredClasses() ) {
	            			if ( declaredClass.getSimpleName().equals(tagName) ) {
	            				foundClass = declaredClass;
	            				break;
	            			}
	            		}
	            		if ( foundClass != null ) {
		            		Field field = foundClass.getField(name);
		            		if ( field != null ) {
		            			int key = (Integer)field.get(foundClass);
		            			cache.put(key, node.getTextContent());
		            		}
	            		}
	            		else {
		            		_logger.error(TAG, "No Such Class in: " + classPath + " for tag " + tagName);  
	            		}
	            	} catch (ClassNotFoundException e) {
	            		_logger.error(TAG, "Class doesn't exist: \"" + classPath + "\"", e);
	            	} catch (NoSuchFieldException e) {
	            		_logger.error(TAG, "No Such Field in: " + classPath + " for tag " + tagName, e);            		
	            	} catch (SecurityException e) {
	            		_logger.error(TAG, "Cannot access " + classPath + " for tag " + tagName, e);            		
	            	} catch (IllegalArgumentException e) {
	            		_logger.error(TAG, "Illegal Argument for class " + classPath + " and tag " + tagName, e);
					} catch (IllegalAccessException e) {
						_logger.error(TAG, "Illegal Access for class " + classPath + " and tag " + tagName, e);
					}
	            }
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			_logger.error(TAG, "XML Resource doesn't exist: \"" + resourcePath + "\"", e);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}

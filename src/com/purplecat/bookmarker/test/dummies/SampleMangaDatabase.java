package com.purplecat.bookmarker.test.dummies;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.joda.time.DateTime;

import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.Place;
import com.purplecat.bookmarker.services.databases.IMediaRepository;

public class SampleMangaDatabase extends SampleDatabaseService<Media> implements IMediaRepository {
	
	public Map<Integer, Long> _preferredOrder = new HashMap<Integer, Long>();
	
	public SampleMangaDatabase() {
		setup();
	}
	
	private void setup() {
		System.out.println("setting up");
		String fileName = "/com/purplecat/bookmarker/test/dummies/resources/sample_manga.txt";
		try {
			InputStream stream = getClass().getResourceAsStream(fileName);
			if ( stream == null ) { throw new NullPointerException("Resource stream is null for "  + fileName); }
			JsonReader reader = Json.createReader(stream);
			JsonArray array = reader.readArray();
			reader.close();
			
			for ( int i = 0; i < array.size(); i++ ) {
				JsonObject obj = array.getJsonObject(i);
				Media media = new Media();
				int order = obj.getInt("order");
				media._id = obj.getInt("_id");
				media._displayTitle = obj.getString("_displayTitle");
				media._lastReadPlace = parsePlace(obj.getJsonObject("_lastReadPlace"));
				media._lastReadDate = parseDate(obj.getString("_lastReadDate"));
				media._updatedPlace = parsePlace(obj.getJsonObject("_updatedPlace"));
				media._updatedDate = parseDate(obj.containsKey("_updatedDate") ? obj.getString("_updatedDate") : null);
				System.out.println("Adding to list: " + media);
				insert(media);
				if ( media._id > this._maxIndex ) {
					this._maxIndex = (int)media._id;
				}
				_preferredOrder.put(order, media._id);
			}
		} catch (Exception e) {
			System.out.println("Unable to load file: "  + fileName);
			e.printStackTrace();
		}
	}
	
	private Place parsePlace(JsonObject obj) {
		if ( obj != null ) {
			Place place = new Place();
			place._volume = obj.getInt("_volume");
			place._chapter = obj.getInt("_chapter");
			return place;
		}
		else {
			return null;
		}
	}
	
	private DateTime parseDate(String str) {
		if ( str != null ) {
			return new DateTime(str);
		}
		else {
			return null;
		}
	}
	
	@Override
	public Media copy(Media item) {
		return item.copy();
	}
	
	@Override
	public List<Media> querySavedMedia(IListLoadedObserver<Media> observer) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Media> queryByTitle(String title) {
		// TODO Auto-generated method stub
		Media m = new Media();
		m._displayTitle = title;
		m._id = 10;
		insert(m);
		
		List<Media> list = new LinkedList<Media>();
		list.add(m);
		return list;
	}
}
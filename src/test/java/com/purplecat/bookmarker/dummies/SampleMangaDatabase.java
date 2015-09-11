package com.purplecat.bookmarker.dummies;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.joda.time.DateTime;

import com.google.inject.Singleton;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.Place;
import com.purplecat.bookmarker.services.databases.IMediaRepository;

@Singleton
public class SampleMangaDatabase extends SampleDatabaseService<Media> implements IMediaRepository {
	
	public Map<Integer, Long> _preferredOrder = new HashMap<Integer, Long>();
	
	public SampleMangaDatabase() {
		setup();
	}
	
	private void setup() {
//		System.out.println("setting up sample manga database");
		String fileName = "/com/purplecat/bookmarker/dummies/resources/sample_manga.txt";
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
				media.setDisplayTitle(obj.getString("_displayTitle"));
				media._lastReadPlace = parsePlace(obj.getJsonObject("_lastReadPlace"));
				media._lastReadDate = parseDate(obj.getString("_lastReadDate"));
				media._updatedPlace = parsePlace(obj.getJsonObject("_updatedPlace"));
				media._updatedDate = parseDate(obj.containsKey("_updatedDate") ? obj.getString("_updatedDate") : null);
				media._titleUrl = obj.containsKey("_titleUrl") ? obj.getString("_titleUrl") : null;
				media._isSaved = true;
//				System.out.println("Adding to list: " + media);
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
		return _map.values().stream()
				.collect(Collectors.toList());
	}
	@Override
	public List<Media> queryByTitle(String title) {	
		return _map.values().stream()
				.filter(media -> media.getDisplayTitle().equalsIgnoreCase(title))
				.collect(Collectors.toList());
	}
}
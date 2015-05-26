package com.purplecat.bookmarker.test.dummies;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.joda.time.DateTime;

import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.Place;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;

public class SampleOnlineMangaDatabase extends SampleDatabaseService<OnlineMediaItem> implements IOnlineMediaRepository {
	
	public Map<Integer, Long> _preferredOrder = new HashMap<Integer, Long>();
	
	public SampleOnlineMangaDatabase() {
		setup();
	}
	
	private void setup() {
		System.out.println("setting up");
		String fileName = "/com/purplecat/bookmarker/test/dummies/resources/updated_sample_manga.txt";
		try {
			InputStream stream = getClass().getResourceAsStream(fileName);
			if ( stream == null ) { throw new NullPointerException("Resource stream is null for "  + fileName); }
			JsonReader reader = Json.createReader(stream);
			JsonArray array = reader.readArray();
			reader.close();
			
			for ( int i = 0; i < array.size(); i++ ) {
				JsonObject obj = array.getJsonObject(i);
				OnlineMediaItem media = new OnlineMediaItem();
				int order = obj.getInt("order");
				media._id = obj.getInt("_id");
				media._mediaId = obj.getInt("_mediaId");
				media._displayTitle = obj.getString("_displayTitle");
				media._lastReadPlace = parsePlace(obj.getJsonObject("_lastReadPlace"));
				media._lastReadDate = parseDate(obj.containsKey("_lastReadDate") ? obj.getString("_lastReadDate") : null);
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
	public OnlineMediaItem copy(OnlineMediaItem item) {
		return item.copy();
	}

	@Override
	public OnlineMediaItem findOrCreate(OnlineMediaItem item) {
		if ( item._mediaId <= 0 ) {
			_maxIndex++;
			item._mediaId = _maxIndex;
		}
		if ( item._id <= 0 ) {
			insert(item);
		}
		return item;
	}
}
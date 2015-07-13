package com.purplecat.bookmarker.test.dummies;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.purplecat.bookmarker.extensions.OnlineMediaItemExt;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.Place;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.databases.DatabaseException;
import com.purplecat.bookmarker.services.databases.IMediaRepository;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;

@Singleton
public class SampleOnlineMangaDatabase extends SampleDatabaseService<OnlineMediaItem> implements IOnlineMediaRepository {
	
	public Map<Integer, Long> _preferredOrder = new HashMap<Integer, Long>();
	IMediaRepository _mediaRepository;
	
	@Inject
	public SampleOnlineMangaDatabase(IMediaRepository mediaRepository) {
		setup();
		_mediaRepository = mediaRepository;
	}
	
	private void setup() {
//		System.out.println("setting up sample online manga database");
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
	public OnlineMediaItem copy(OnlineMediaItem item) {
		return item.copy();
	}

	@Override
	public OnlineMediaItem findOrCreate(OnlineMediaItem item) {
		try {
			List<Media> media = _mediaRepository.queryByTitle(item._displayTitle);
			if ( media.size() > 0 ) {
				item._mediaId = media.get(0)._id;
				item._lastReadDate = media.get(0)._lastReadDate;
				item._lastReadPlace = media.get(0)._lastReadPlace;
				item._isSaved = media.get(0)._isSaved;
				
				long mediaId = item._mediaId;
				Optional<OnlineMediaItem> result = this._map.values().stream().filter(m -> m._mediaId == mediaId).findFirst();
				if ( result.isPresent() ) {
					OnlineMediaItemExt.copyNewToExisting(item, result.get());
					item = result.get();
				}
			}
			if ( item._mediaId <= 0 ) {
				_maxIndex++;
				item._mediaId = _maxIndex;
				
				Media media1 = new Media();
				media1._id = _maxIndex;
				media1.setDisplayTitle(item._displayTitle);
				_mediaRepository.insert(media1);
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ( item._id <= 0 ) {
			insert(item);
		}
		return item;
	}

	@Override
	public List<OnlineMediaItem> queryByMediaId(long mediaId) {
		return _map.values().stream()
				.filter(item -> item._mediaId == mediaId)
				.collect(Collectors.toList());
	}
}
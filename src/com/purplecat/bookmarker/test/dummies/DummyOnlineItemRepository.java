package com.purplecat.bookmarker.test.dummies;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;

public class DummyOnlineItemRepository implements IOnlineMediaRepository {

	Map<Long, OnlineMediaItem> _map = new HashMap<Long, OnlineMediaItem>();
	int _maxIndex = 0;

	@Override
	public OnlineMediaItem findOrCreate(OnlineMediaItem item) {
		
		if ( item._displayTitle.equals("Shana oh Yoshitsune") ) {
			insert(item);
			item._mediaId = 50;
			item._isSaved = true;
			return item;
		}		
		else if ( item._displayTitle.equals("Haikyuu") ) {
			insert(item);
			item._mediaId = 60;
			item._isSaved = true;
			return item;
		}	
		else {
			insert(item);
			return item;
		}
	}

	@Override
	public void insert(OnlineMediaItem item) {		
		item._id = _maxIndex++;
		_map.put(item._id, item);
	}

	@Override
	public List<OnlineMediaItem> query() {
		List<OnlineMediaItem> list = new LinkedList<OnlineMediaItem>();
		list.addAll(_map.values());
		return list;
	}

	@Override
	public OnlineMediaItem queryById(long id) {
		return _map.get(id);
	}

	@Override
	public void update(OnlineMediaItem item) {
		_map.put(item._id, item);
	}

	@Override
	public void delete(long id) {
		_map.remove(id);
	}

	@Override
	public List<OnlineMediaItem> queryByMediaId(long mediaId) {
		return _map.values().stream()
				.filter(item -> item._mediaId == mediaId)
				.collect(Collectors.toList());
	}

}

package com.purplecat.bookmarker.controller.observers;

import com.purplecat.bookmarker.models.Folder;

public interface IFoldersObserver extends IItemChangedObserver<Folder>, IListLoadedObserver<Folder> {

}

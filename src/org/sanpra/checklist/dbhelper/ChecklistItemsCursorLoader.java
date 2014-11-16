package org.sanpra.checklist.dbhelper;

import android.content.Context;
import android.database.Cursor;

import com.commonsware.cwac.loaderex.acl.AbstractCursorLoader;

public final class ChecklistItemsCursorLoader extends AbstractCursorLoader {

	private final Context mContext;

	public ChecklistItemsCursorLoader(final Context context) {
		super(context);
		mContext = context;
	}

	@Override
	protected Cursor buildCursor() {
		return 	ItemsDbHelper.getInstance(mContext).fetchAllItems();
	}
}

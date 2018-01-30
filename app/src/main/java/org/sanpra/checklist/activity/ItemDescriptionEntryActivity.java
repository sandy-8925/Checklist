/*
 * Copyright (C) 2011-2015 Sandeep Raghuraman (sandy.8925@gmail.com)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sanpra.checklist.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.sanpra.checklist.R;
import org.sanpra.checklist.dbhelper.ItemsDatabase;

public final class ItemDescriptionEntryActivity extends Activity {

    static final String EXTRA_KEY_ITEM_ID = "item_id";
    /**
     * Holds a reference to the EditText view of the activity
     */
    private EditText itemDescText;
    private ItemsDao itemsDao;
    private ChecklistItem item;

    //TODO: Try to break code into smaller independent methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.itemdescentry);

        itemDescText = (EditText) findViewById(R.id.itemdesctext);
        itemsDao = ItemsDatabase.getInstance(this).itemsDao();

        final int actionType = getIntent().getIntExtra(ChecklistActivity.actionTag, -1);
        final View okButton = findViewById(R.id.itemdescconfbutton);

        switch (actionType)
        {
        case ChecklistActivity.EDIT_ITEM_ACTION:
            fetchChecklistItemDescriptionFromDatabase();
            okButton.setOnClickListener(new EditItemOnClickListener());
            break;

        default:
            Toast.makeText(getApplicationContext(),
                    "Error. Unable to get action type.", Toast.LENGTH_SHORT)
                    .show();
            setResult(RESULT_OK);
            finish();
        }
    }

    private final class EditItemOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View clickedView) {
            final String itemText = itemDescText.getText().toString();
            if (StringUtils.isNotEmpty(itemText)) {
                item.description = itemText;
                itemsDao.updateItem(item);
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private void fetchChecklistItemDescriptionFromDatabase() {
        /*
      USed only when checklist item is being edited. Holds the ID of the item being edited.
     */
        final long itemId = getIntent().getLongExtra(EXTRA_KEY_ITEM_ID, -1);
        if (itemId == -1) {
            //TODO: Error - show dialog and finish activity
            return;
        }
        new FetchItemFromDbTask().execute(itemId);
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchItemFromDbTask extends AsyncTask<Long, Void, ChecklistItem> {
        @Override
        protected ChecklistItem doInBackground(Long... integers) {
            final long itemId = integers[0];
            return itemsDao.fetchItem(itemId);
        }

        @Override
        protected void onPostExecute(ChecklistItem item) {
            super.onPostExecute(item);
            ItemDescriptionEntryActivity.this.item = item;
            itemDescText.setText(item.description);
        }
    }
}

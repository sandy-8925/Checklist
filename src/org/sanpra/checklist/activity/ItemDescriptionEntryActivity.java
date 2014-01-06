/*
 * Copyright (C) 2011-2012 Sandeep Raghuraman (sandy.8925@gmail.com)

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

import org.sanpra.checklist.R;
import org.sanpra.checklist.dbhelper.ItemsDbHelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ItemDescriptionEntryActivity extends Activity {

    private ItemsDbHelper mDbHelper;
    private EditText itemDescText;
    private long itemId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.itemdescentry);

        itemDescText = (EditText) findViewById(R.id.itemdesctext);
        itemId = -1;

        initalizeDatabaseHelper();

        final int actionType = getIntent().getIntExtra("action", -1);

        switch (actionType)
        {
        case ChecklistActivity.NEW_ITEM_ACTION:
            break;

        case ChecklistActivity.EDIT_ITEM_ACTION:
            fetchChecklistItemDescriptionFromDatabase();
            break;

        default:
            Toast.makeText(getApplicationContext(),
                    "Error. Unable to get action type.", Toast.LENGTH_SHORT)
                    .show();
            setResult(RESULT_OK);
            finish();
        }

        Button okButton = (Button) findViewById(R.id.itemdescconfbutton);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String itemText = itemDescText.getText().toString();
                int resultCode = RESULT_OK;
                switch (actionType)
                {
                case ChecklistActivity.NEW_ITEM_ACTION:
                    if (itemText.length() != 0) {
                        mDbHelper.addItem(itemText);
                    }
                    resultCode = RESULT_OK;
                    break;

                case ChecklistActivity.EDIT_ITEM_ACTION:
                    if (itemText.length() != 0) {
                        mDbHelper.editItemDesc(itemId, itemText);
                    }
                    resultCode = RESULT_OK;
                    break;
                }
                setResult(resultCode);
                finish();
            }
        });
    }

    private void fetchChecklistItemDescriptionFromDatabase() {
        itemId = getIntent().getLongExtra("item_id", -1);
        itemDescText.setText(mDbHelper.getItemDesc(itemId));
        if (itemId == -1) { /* error: show toast and finish activity */
        }
    }

    private void initalizeDatabaseHelper() {
        mDbHelper = new ItemsDbHelper(this);
        mDbHelper.open();
    }
}

/*
 * Copyright (C) 2011 Sandeep Raghuraman (sandy.8925@gmail.com)

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

package com.android.checklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ItemDescriptionEntryActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itemdescentry);
		
		Button okButton = (Button) findViewById(R.id.itemdescconfbutton);
		okButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Bundle resultBundle = new Bundle();
						EditText itemDescText = (EditText) findViewById(R.id.itemdesctext);
						resultBundle.putString(ItemsDbHelper.COL_DESC, itemDescText.getText().toString());
						Intent resultIntent = new Intent();
						resultIntent.putExtras(resultBundle);
						setResult(RESULT_OK, resultIntent);
						finish();
					}
				}
				);
	}
		
	@Override
	protected void onStop()
	{
		super.onStop();
	}
	
	@Override
	protected void onDestroy()
	{	
		super.onDestroy();
	}
		
}

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
	
	
}

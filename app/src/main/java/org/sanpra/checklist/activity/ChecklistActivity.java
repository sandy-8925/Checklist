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

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.sanpra.checklist.R;
import org.sanpra.checklist.databinding.ChecklistBinding;

/**
 * Main activity, that is displayed when the app is launched. Displays the list of ToDo items.
 */
public final class ChecklistActivity extends AppCompatActivity {

    /**
     * These constants are used to specify whether the ItemDescriptionEntryActivity is being opened
     * to edit an existing list item
     */
    static final int EDIT_ITEM_ACTION = 6;

    static final String actionTag = "actionTag";

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ChecklistBinding binding = DataBindingUtil.setContentView(this, R.layout.checklist);
        setSupportActionBar(binding.toolbar);
    }
}
package org.sanpra.checklist.activity;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sanpra.checklist.R;

/**
 * Fragment that just displays the checklist. Meant to be a reusable component.
 */
public final class ChecklistFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.checklist_fragment, container, false);
    }
}
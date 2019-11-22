/*
 * Copyright (C) 2011-2018 Sandeep Raghuraman (sandy.8925@gmail.com)

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

package org.sanpra.checklist.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.util.Linkify
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.util.LinkifyCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

import org.sanpra.checklist.R
import org.sanpra.checklist.databinding.ChecklistBinding

/**
 * Main activity, that is displayed when the app is launched. Displays the list of ToDo items.
 */
class ChecklistActivity : AppCompatActivity() {

    /** Called when the activity is first created.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ChecklistBinding>(this, R.layout.checklist)
        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_license_info -> {
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
                return true
            }
            R.id.menu_about -> {
                val aboutDialog = AboutDialog()
                aboutDialog.show(supportFragmentManager, aboutDialog.TAG)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

class AboutDialog : DialogFragment() {
    internal val TAG : String = "AboutDialog"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message : Spannable = SpannableString(getText(R.string.about_dlg_msg))
        LinkifyCompat.addLinks(message, Linkify.WEB_URLS)
        val builder = AlertDialog.Builder(requireContext())
                        .setTitle(R.string.about)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, null)
        return builder.create()
    }
}
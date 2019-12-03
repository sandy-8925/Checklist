package org.sanpra.checklist.activity


import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ChecklistActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ChecklistActivity::class.java)


}

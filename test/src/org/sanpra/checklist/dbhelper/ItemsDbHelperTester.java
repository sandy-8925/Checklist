package org.sanpra.checklist.dbhelper;

import junit.framework.TestCase;

public class ItemsDbHelperTester extends TestCase {

    public void testIsIdStringBuiltCorrectly() {
        final long ID = 4;
        final String expectedIDString = "_id=" + ID;
        final String actualIDString = ItemsDbHelper.buildIdSelectString(ID);
        assertNotNull(actualIDString);
        assertEquals(expectedIDString, actualIDString);
    }

}

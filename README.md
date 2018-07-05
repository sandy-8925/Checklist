Checklist for Android
---------------------

<h1 align=center>
<img src="Logo/horizontal.png" width=60%>
</h1>

DESCRIPTION
-----------
Basic checklist app for Android

USAGE
-----
- Unchecked items are white, checked items are grey.

- Tap an item to toggle between checked and unchecked state. Press menu button to get a menu for options like adding new items,deleting all checked items etc.

DEVELOPMENT
-----------
Requires Android Support Library v4, Appcompat v7 support library and cwac-loaderex peroject (https://github.com/commonsguy/cwac-loaderex)

Database description
--------------------

Table name - items
Columns:

item id  -  item description(string)     -   status(integer: 0-unchecked 1-checked)
-------    -----------------------      --------------------------------------

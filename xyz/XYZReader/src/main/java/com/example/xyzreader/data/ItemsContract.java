package com.example.xyzreader.data;

import android.net.Uri;

public class ItemsContract {
// ------------------------------ FIELDS ------------------------------

    public static final String CONTENT_AUTHORITY = "com.example.xyzreader";
    public static final Uri BASE_URI = Uri.parse("content://com.example.xyzreader");

// --------------------------- CONSTRUCTORS ---------------------------

    private ItemsContract() {
    }

// -------------------------- INNER CLASSES --------------------------

    public static class Items implements ItemsColumns {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.example.xyzreader.items";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.example.xyzreader.items";
        public static final String DEFAULT_SORT = PUBLISHED_DATE + " DESC";

        /**
         * Matches: /items/
         */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath("items").build();
        }

        /**
         * Matches: /items/[_id]/
         */
        public static Uri buildItemUri(long _id) {
            return BASE_URI.buildUpon().appendPath("items").appendPath(Long.toString(_id)).build();
        }

        /**
         * Read item ID item detail URI.
         */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
    }

    interface ItemsColumns {
        /**
         * Type: INTEGER PRIMARY KEY AUTOINCREMENT
         */
        String _ID = "_id";
        /**
         * Type: TEXT
         */
        String SERVER_ID = "server_id";
        /**
         * Type: TEXT NOT NULL
         */
        String TITLE = "title";
        /**
         * Type: TEXT NOT NULL
         */
        String AUTHOR = "author";
        /**
         * Type: TEXT NOT NULL
         */
        String BODY = "body";
        /**
         * Type: TEXT NOT NULL
         */
        String PHOTO_URL = "photo_url";
        /**
         * Type: INTEGER NOT NULL DEFAULT 0
         */
        String PUBLISHED_DATE = "published_date";
    }
}
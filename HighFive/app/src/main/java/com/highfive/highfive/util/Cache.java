package com.highfive.highfive.util;

import android.content.Context;

import com.iainconnor.objectcache.BuildConfig;
import com.iainconnor.objectcache.CacheManager;
import com.iainconnor.objectcache.DiskCache;

import java.io.File;
import java.io.IOException;

/**
 * Created by heat_wave on 20.01.17.
 */

public class Cache {
    private static Cache instance;
    private static DiskCache diskCache;

    private Cache() {
    }

    public Cache getInstance(Context context) {
        if (instance == null) {
            try {
                instance = new Cache();
                String cachePath = context.getCacheDir().getPath();
                File cacheFile = new File(cachePath + File.separator + BuildConfig.PACKAGE_NAME);
                diskCache = new DiskCache(cacheFile, BuildConfig.VERSION_CODE, 1024 * 1024 * 10);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public static CacheManager getCacheManager() {
        return CacheManager.getInstance(diskCache);
    }

}

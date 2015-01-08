package com.lyc.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class ApkUtil {

	/**
	 * 应该放在子线程中运行，以免ANR
	 * @param context
	 * @param apkPath
	 * @return
	 */
	public static Drawable loadApkIcon(Context context,String apkPath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo apkInfo = info.applicationInfo;
			apkInfo.publicSourceDir = apkPath;
			Drawable icon = apkInfo.loadIcon(pm);
			return icon;
		}
		return null;
	}
	
}

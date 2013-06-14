package com.blueodin.taskman;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
	private ActivityManager mActivityManager;
	private MemoryInfo mMemoryInfo = new MemoryInfo();
	private List<RunningAppProcessInfo> mRunningProcesses;
	private List<RunningTaskInfo> mRunningTasks;
	private List<RunningServiceInfo> mRunningServices;
	
	public TaskManager(Context context) {
		mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		mActivityManager.getMemoryInfo(mMemoryInfo);
		refresh();
	}
	
	public long getAvailableMemory() {
		mActivityManager.getMemoryInfo(mMemoryInfo);
		return mMemoryInfo.availMem;
	}
	
	public long getTotalMemory() {
		return mMemoryInfo.totalMem;
	}
	
	public void refresh() {
		mRunningProcesses = mActivityManager.getRunningAppProcesses();

		if(mRunningProcesses == null)
			mRunningProcesses = new ArrayList<RunningAppProcessInfo>();
		
		mRunningTasks = mActivityManager.getRunningTasks(Integer.MAX_VALUE);
		mRunningServices = mActivityManager.getRunningServices(Integer.MAX_VALUE);
	}
	
	public List<RunningAppProcessInfo> getRunningProcesses() {
		return mRunningProcesses;
	}
	
	public List<RunningTaskInfo> getRunningTasks() {
		return mRunningTasks;
	}
	
	public List<RunningServiceInfo> getRunningServices() {
		return mRunningServices;
	}
}

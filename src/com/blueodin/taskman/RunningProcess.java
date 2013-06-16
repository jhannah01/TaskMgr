package com.blueodin.taskman;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;

import java.util.ArrayList;
import java.util.List;

public class RunningProcess {
	private String mName;
	private String mClassName;
	private ProcessType mType;
	
	private int mPid = 0;
	private int mUid = 0;
	
	public RunningProcess(String name, ProcessType processType) {
		this(name, "", processType);
		
		int idx = name.lastIndexOf('.');
		
		if(idx > -1) {
			mClassName = name.substring(0, idx);
			mName = name.substring(idx+1);
		}
	}
	
	public RunningProcess(String name, String className, ProcessType processType) {
		mName = name;
		mClassName = className;
		mType = processType;
	}
	
	public RunningProcess(RunningAppProcessInfo processInfo) {
		this(processInfo.processName.toString(), ProcessType.Process);
		mPid = processInfo.pid;
		mUid = processInfo.uid;
	}
	
	public RunningProcess(RunningTaskInfo taskInfo) {
		this(taskInfo.baseActivity.getClassName().toString(), ProcessType.Task);
	}
	
	public RunningProcess(RunningServiceInfo serviceInfo) {
		this(serviceInfo.service.getClassName(), ProcessType.Service);
		mPid = serviceInfo.pid;
		mUid = serviceInfo.uid;
	}
	
	public static List<RunningProcess> getRunningProcesses(ActivityManager activityManager) {
		return getRunningProcesses(activityManager, ProcessType.All);
	}
	
	public static List<RunningProcess> getRunningProcesses(ActivityManager activityManager, ProcessType processType) {
		List<RunningProcess> results = new ArrayList<RunningProcess>();
		
		if((processType == ProcessType.All) || (processType == ProcessType.Process)) {
			for(RunningAppProcessInfo processInfo : activityManager.getRunningAppProcesses())
				results.add(new RunningProcess(processInfo));
		}
		
		if((processType == ProcessType.All) || (processType == ProcessType.Task)) {
			for(RunningTaskInfo taskInfo : activityManager.getRunningTasks(Integer.MAX_VALUE))
				results.add(new RunningProcess(taskInfo));
		}
		
		if((processType == ProcessType.All) || (processType == ProcessType.Service)) {
			for(RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE))
				results.add(new RunningProcess(serviceInfo));
		}
		
		return results;
	}
	
	public String getName() {
		return mName;
	}
	
	public String getClassName() {
		return mClassName;
	}
	
	public boolean hasClassName() {
		return !(mClassName.isEmpty());
	}
	
	public ProcessType getType() {
		return mType;
	}
	
	public boolean hasPid() {
		return (mPid > 0);
	}
	
	public boolean hasUid() {
		return (mUid > 0);
	}
	
	public int getPid() {
		return mPid;
	}
	
	public int getUid() {
		return mUid;
	}
	
	@Override
	public String toString() {
		String result = String.format("%s: %s", mType.toString(), mName);
		
		if(hasClassName())
			result = String.format("%s (%s)", result, mClassName);
		
		if(hasUid() && hasPid())
			result = String.format("%s [uid: %d pid: %d]", result, mUid, mPid);
		
		return result;
	}
	
	public enum ProcessType {
		Task,
		Process,
		Service,
		All;
	}
}

package com.blueodin.taskman;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug.MemoryInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProcessListAdapter extends BaseExpandableListAdapter {
	private ActivityManager mActivityManager;
	private LayoutInflater mLayoutInflater;
	private List<ProcessEntry> mProcessList = new ArrayList<ProcessEntry>();
	private long mTotalMemory = 0;
	private long mAvailableMemory = 0;

	public ProcessListAdapter(Context context) {
		super();
		mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		updateMemoryInfo();
	}
	
	public void updateMemoryInfo() {
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		mActivityManager.getMemoryInfo(memoryInfo);
		mTotalMemory = memoryInfo.totalMem;
		mAvailableMemory = memoryInfo.availMem;
	}
	
	public long getTotalMemory() {
		return mTotalMemory;
	}
	
	public long getAvailableMemory() {
		return mAvailableMemory;
	}
	
	@Override
	public int getGroupCount() {
		return mProcessList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if(getGroup(groupPosition) == null)
			return 0;
		
		return 1;
	}

	@Override
	public ProcessEntry getGroup(int groupPosition) {
		return mProcessList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ProcessEntry entry = getGroup(groupPosition);
		RunningProcess process = entry.getProcess();
		
		View view = mLayoutInflater.inflate(R.layout.process_row, parent, false);
		
		((TextView)view.findViewById(R.id.text_process_name)).setText(process.getName());
		((TextView)view.findViewById(R.id.text_process_class)).setText(process.hasClassName() ? process.getClassName() : "");
		((TextView)view.findViewById(R.id.text_process_type)).setText(process.getType().toString());

		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ProcessEntry entry = getGroup(groupPosition);
		MemoryInfo memoryInfo = entry.getMemoryInfo();
		
		View view = mLayoutInflater.inflate(R.layout.process_detail_row, parent, false);
		
		((TextView)view.findViewById(R.id.text_process_detail_memory)).setText(formatMemoryInfo(memoryInfo));
		
		return view;
	}
	
	private String formatMemoryInfo(MemoryInfo memoryInfo) {
		int c = memoryInfo.getTotalPss();
		
		if(c < 1024)
			return String.format("%d bytes");
		
		return String.format("%.02f MB", (c / 1024));
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public class ProcessEntry {
		private RunningProcess mProcess;
		
		public ProcessEntry(RunningProcess process) {
			mProcess = process;
		}

		public boolean hasMemoryInfo() {
			return mProcess.hasPid();
		}
		
		public MemoryInfo getMemoryInfo() {
			if(!hasMemoryInfo())
				return null;
			
			return mActivityManager.getProcessMemoryInfo(new int[] { mProcess.getPid() })[0];
		}
		
		public RunningProcess getProcess() {
			return mProcess;
		}
	}
	
}

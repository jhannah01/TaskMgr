package com.blueodin.taskman;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.blueodin.taskman.RunningProcess.ProcessType;

import java.util.ArrayList;
import java.util.List;


public class TasksListFragment extends SherlockListFragment implements OnPreferenceChangeListener {
	private OnProcessListInteractionListener mListener;
	private TaskListAdapter mListAdapter;
	private LayoutInflater mLayoutInflater;
	private boolean mIncludeSystemTasks = true;
	
	public TasksListFragment() { }
	
	public static TasksListFragment newInstance(boolean includeSystemTasks) {
		TasksListFragment f = new TasksListFragment();
		Bundle args = new Bundle();
		args.putBoolean(SettingsActivity.KEY_INCLUDE_SYSTEM_TASKS, includeSystemTasks);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
		mListAdapter = new TaskListAdapter();
		
		if((savedInstanceState != null) && (savedInstanceState.containsKey(SettingsActivity.KEY_INCLUDE_SYSTEM_TASKS)))
			mIncludeSystemTasks = savedInstanceState.getBoolean(SettingsActivity.KEY_INCLUDE_SYSTEM_TASKS);
		
		setListAdapter(mListAdapter);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putBoolean(SettingsActivity.KEY_INCLUDE_SYSTEM_TASKS, mIncludeSystemTasks);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnProcessListInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnProcessListInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (null != mListener)
			mListener.onProcessSelected(mListAdapter.getItem(position));
	}

	public interface OnProcessListInteractionListener {
		public void onProcessSelected(RunningProcess process);
	}
	
	public void setProcessType() {
		if(mListAdapter != null)
			mListAdapter.setProcessType();
	}
	
	public void setProcessType(ProcessType processType) {
		if(mListAdapter != null)
			mListAdapter.setProcessType(processType);
	}

	private class TaskListAdapter extends ArrayAdapter<RunningProcess> {
		private ActivityManager mActivityManager;
		private ProcessType mProcessType = ProcessType.All;
		
		public TaskListAdapter() {
			super(getActivity(), android.R.id.text1);
			mActivityManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
			update();
		}
		
		public TaskListAdapter(ProcessType processType) {
			this();
			mProcessType = processType;
			update();
		}
		
		public void update() {
			clear();
			addAll(RunningProcess.getRunningProcesses(mActivityManager));
			notifyDataSetChanged();
		}
		
		public void setProcessType() {
			setProcessType(RunningProcess.ProcessType.All);
		}
		
		public void setProcessType(RunningProcess.ProcessType processType) {
			mProcessType = processType;
			update();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RunningProcess process = getItem(position);
			
			View rowView = mLayoutInflater.inflate(R.layout.process_row, parent, false);
			
			((TextView)rowView.findViewById(R.id.text_process_name)).setText(process.getName());
			((TextView)rowView.findViewById(R.id.text_process_class)).setText(process.hasClassName() ? process.getClassName() : "");
			((TextView)rowView.findViewById(R.id.text_process_type)).setText(process.getType().toString());
			((TextView)rowView.findViewById(R.id.text_process_pid)).setText(process.hasPid() ? String.format("%d", process.getPid()) : "N/A");
			((TextView)rowView.findViewById(R.id.text_process_uid)).setText(process.hasUid() ? String.format("%d", process.getUid()) : "N/A");
			
			return rowView;
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference.getKey().equals(SettingsActivity.KEY_INCLUDE_SYSTEM_TASKS)) {
			boolean value = (Boolean)newValue;
			if(value != mIncludeSystemTasks) {
				mIncludeSystemTasks = value;
				mListAdapter.update();
			}
		}

		return false;
	}
}

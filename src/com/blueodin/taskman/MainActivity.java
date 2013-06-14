package com.blueodin.taskman;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.blueodin.taskman.RunningProcess.ProcessType;
import com.blueodin.taskman.TasksListFragment.OnProcessListInteractionListener;

public class MainActivity extends SherlockFragmentActivity implements OnProcessListInteractionListener, ActionBar.TabListener {
	private static final int TAB_OVERVIEW_TAG = 0;
	private static final int TAB_TASKS_TAG = 1;
	private static final int TAB_PROCESSES_TAG = 2;
	private static final int TAB_SERVICES_TAG = 3;
	
	private SharedPreferences mSharedPreferences;
	private TasksListFragment mTasksListFragment;
	private SparseArray<TabFragment> mTabFragments = new SparseArray<TabFragment>();
	private ActionBar mActionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		setContentView(R.layout.activity_main);
		
		mTasksListFragment = TasksListFragment.newInstance(SettingsActivity.shouldIncludeSystemTasks(mSharedPreferences));
        
        getSupportFragmentManager().beginTransaction()
			.replace(R.id.frame_main_list, mTasksListFragment)
			.commit();
		
        mActionBar = getSupportActionBar();
    
        mTabFragments.put(TAB_OVERVIEW_TAG, new OverviewTabFragment());
        mTabFragments.put(TAB_TASKS_TAG, new TasksTabFragment());
        mTabFragments.put(TAB_PROCESSES_TAG, new ProcessesTabFragment());
        mTabFragments.put(TAB_SERVICES_TAG, new ServicesTabFragment());
        
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        mActionBar.addTab(mActionBar.newTab()
        		.setText("Overview")
        		.setTag(TAB_OVERVIEW_TAG)
        		.setTabListener(this));
        
        mActionBar.addTab(mActionBar.newTab()
        		.setText("Tasks")
        		.setTag(TAB_TASKS_TAG)
        		.setTabListener(this));
        
        mActionBar.addTab(mActionBar.newTab()
        		.setText("Processes")
        		.setTag(TAB_PROCESSES_TAG)
        		.setTabListener(this));
        
        mActionBar.addTab(mActionBar.newTab()
        		.setText("Services")
        		.setTag(TAB_SERVICES_TAG)
        		.setTabListener(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSherlock().getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_exit:
			finish();
			return true;
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}
		
		return false;
	}

	@Override
	public void onProcessSelected(RunningProcess process) {
		Toast.makeText(this, String.format("Selected task: %s", process.toString()), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		TabFragment f = getTabFragment((Integer)tab.getTag());
		ft.replace(R.id.frame_main_content, f);
		f.onTabSelected();
		mTasksListFragment.setProcessType(f.getProcessType());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		getTabFragment((Integer)tab.getTag()).onTabSelected();
	}
	
	private TabFragment getTabFragment(int id) {
		return mTabFragments.get(id);
	}
	
	private abstract class TabFragment extends SherlockFragment {
		public TabFragment() { }
		
		public abstract void onTabSelected();
		public abstract RunningProcess.ProcessType getProcessType();
	}
	
	private class OverviewTabFragment extends TabFragment {
		public OverviewTabFragment() { }
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_overview, container, false);
			
			return view;			
		}
		
		@Override
		public void onTabSelected() {
			
		}

		@Override
		public ProcessType getProcessType() {
			return ProcessType.All;
		}
	}
	
	private class TasksTabFragment extends TabFragment {
		public TasksTabFragment() { }
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_tasks, container, false);
			
			return view;			
		}

		@Override
		public void onTabSelected() {
			
		}

		@Override
		public ProcessType getProcessType() {
			return ProcessType.Task;
		}
	}
	
	private class ProcessesTabFragment extends TabFragment {
		public ProcessesTabFragment() { }
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_processes, container, false);
			
			return view;
					
		}

		@Override
		public void onTabSelected() {
			
		}

		@Override
		public ProcessType getProcessType() {
			return ProcessType.Process;
		}
	}
	
	private class ServicesTabFragment extends TabFragment {
		public ServicesTabFragment() { }
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_services, container, false);
			
			return view;
					
		}
		
		@Override
		public void onTabSelected() {
			
		}

		@Override
		public ProcessType getProcessType() {
			return ProcessType.Service;
		}
	}
}
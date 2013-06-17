/*
 * Created by David Gerber <dg@zapek.com>
 * 
 * Copyright (c) 2013 by David Gerber
 * All Rights Reserved
 * 
 * This file is WTFPL (see LICENSE file). Just copy it into your
 * project and you're done.
 */
package com.zapek.android.workerfragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Invisible {@link Fragment} used to persist some work and not miss the result
 * even when the {@link Activity} is recreated (for example during a
 * Configuration Change).
 * 
 * @author David Gerber
 */
public class WorkerFragment extends Fragment {

	private static final String TAG = "worker";

	public static final int RESULTCODE_HELLOWORLD = 1; /* this is used by the example */

	/* add additional result codes here */

	public interface WorkerFragmentReceiver {
		public void onReceiveResult(int resultCode, Bundle resultData);
	}

	private class Result {
		private final int resultCode;
		private final Bundle resultData;

		private Result(int resultCode, Bundle resultData) {
			this.resultCode = resultCode;
			this.resultData = resultData;
		}
	}

	private static final String KEY_RECEIVER = "receiver";

	private final ArrayList<Result> results = new ArrayList<Result>();
	private final FragmentResultReceiver receiver;

	private class FragmentResultReceiver extends ResultReceiver {

		private boolean enabled;

		private FragmentResultReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			if (this.enabled)
			{
				WorkerFragment.this.onReceiveResult(resultCode, resultData);
			}
		}

		private void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	/**
	 * Get the WorkerFragment. This is to be called on your
	 * {@link Activity#onCreate} method at all times.
	 * 
	 * @param fragmentManager
	 *            {@link FragmentManager} of the {@link Activity}
	 * @return the WorkerFragment
	 */
	public static WorkerFragment getWorkerFragment(FragmentManager fragmentManager) {
		WorkerFragment workerFragment = (WorkerFragment) fragmentManager.findFragmentByTag(TAG);
		if (workerFragment == null)
		{
			workerFragment = new WorkerFragment();
			fragmentManager.beginTransaction().add(workerFragment, TAG).commit();
		}
		return (workerFragment);
	}

	public WorkerFragment() {
		super();
		this.receiver = new FragmentResultReceiver(new Handler());
		this.receiver.setEnabled(true);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Activity activity = this.getActivity();

		for (Result result : this.results)
		{
			this.handleResult(activity, result.resultCode, result.resultData);
		}
		this.results.clear();
	}

	@Override
	public void onDestroy() {
		this.receiver.setEnabled(false);
		super.onDestroy();
	}

	private void onReceiveResult(int resultCode, Bundle resultData) {
		Activity activity = this.getActivity();
		if (activity != null)
		{
			this.handleResult(activity, resultCode, resultData);
		}
		else
		{
			this.results.add(new Result(resultCode, resultData));
		}
	}

	/**
	 * To be called on the {@link Intent} to pass to
	 * {@link Context#startService}.
	 * 
	 * @param intent
	 *            {@link Intent} to send to the service
	 */
	public void putReceiver(Intent intent) {
		intent.putExtra(KEY_RECEIVER, this.receiver);
	}

	/**
	 * Get the {@link ResultReceiver}.
	 * 
	 * @return the {@link ResultReceiver}
	 */
	public ResultReceiver getReceiver() {
		return (this.receiver);
	}

	/**
	 * To be called by the {@link Service} to send the result back to the
	 * {@link Activity}.
	 * 
	 * @param intent
	 *            {@link Intent} that the {@link Service} received.
	 * @param resultCode
	 *            the result code, used to differentiate what command this
	 *            result belongs to
	 * @param result
	 *            the {@link Bundle} which contains the data you're interested
	 *            in
	 */
	public static void sendResult(Intent intent, int resultCode, Bundle result) {
		ResultReceiver receiver = intent.getParcelableExtra(KEY_RECEIVER);
		receiver.send(resultCode, result);
	}

	private void handleResult(Activity activity, int resultCode, Bundle resultData) {
		WorkerFragmentReceiver receiver = null;
		try
		{
			receiver = (WorkerFragmentReceiver) activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() + " must implement WorkerFragmentReceiver");
		}
		receiver.onReceiveResult(resultCode, resultData);
	}
}

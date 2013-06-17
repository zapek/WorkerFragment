/*
 * Created by David Gerber <dg@zapek.com>
 * 
 * Copyright (c) 2013 by David Gerber
 * All Rights Reserved
 * 
 * This file is WTFPL (see LICENSE file). It is just an example
 * file so that you can see how to use WorkerFragment.
 */
package com.zapek.android.workerfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements WorkerFragment.WorkerFragmentReceiver {

	private WorkerFragment workerFragment;
	private TextView resultView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		this.resultView = (TextView) this.findViewById(R.id.result);
		this.workerFragment = WorkerFragment.getWorkerFragment(this.getSupportFragmentManager());

		if (savedInstanceState == null)
		{
			Intent exampleIntent = new Intent(this, ExampleService.class);
			exampleIntent.setAction(ExampleService.ACTION_GET_HELLO);
			this.workerFragment.putReceiver(exampleIntent);
			this.startService(exampleIntent);
			this.resultView.setText("Service started, awaiting result...");
		}
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		switch (resultCode)
		{
			case WorkerFragment.RESULTCODE_HELLOWORLD:
			{
				String result = resultData.getString(ExampleService.KEY_RESULT);
				Log.d("MainActivity", "Got resulting string: " + result);
				this.resultView.setText("Resulting string: " + result);
			}
			break;
		}
	}
}

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

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ExampleService extends IntentService {

	private static final String ACTION_BASE = "com.zapek.android.workerfragment.";
	public static final String ACTION_GET_HELLO = ACTION_BASE + "GET_HELLO";

	public static final String KEY_RESULT = "result";

	public ExampleService() {
		super("ExampleService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null)
		{
			String action = intent.getAction();

			if (action.equals(ACTION_GET_HELLO))
			{
				Log.d("ExampleService", "got HELLO action, working...");

				/*
				 * This simulated a 5 seconds busy work.
				 */
				try
				{
					Thread.sleep(1000 * 5);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				Log.d("ExampleService", "done, sending reply...");

				Bundle result = new Bundle();
				result.putString(KEY_RESULT, "hello world!");
				WorkerFragment.sendResult(intent, WorkerFragment.RESULTCODE_HELLOWORLD, result);
			}
		}
	}
}

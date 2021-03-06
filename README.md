WorkerFragment
==============

Purpose
-------
WorkerFragment is a mechanism that allows an Activity to start an IntentService and not lose its reply, even
if the Activity is recreated (rotation, etc...).

How to use
----------
Simply copy WorkerFragment.java into your project, then on your Activity's onCreate() do this:

    this.workerFragment = WorkerFragment.getWorkerFragment(this);

If savedInstanceState == null, you start your IntentService to do some background work:

    Intent exampleIntent = new Intent(this, ExampleService.class);
    exampleIntent.setAction(ExampleService.ACTION_GET_HELLO);
    this.workerFragment.putReceiver(exampleIntent); /* this sets the WorkerFragment as receiver */
    this.startService(exampleIntent);

Then you implement the WorkerFragment.WorkerFragmentReceiver interface in your Activity to handle the result:

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
    switch (resultCode)
    {
        case WorkerFragment.RESULTCODE_HELLOWORLD:
        {
            /* do your stuff here */
        }
        break;
    }

Now in your IntentService, sending your result is as simple as:

    Bundle result = new Bundle();
    result.putString(KEY_RESULT, "hello world!");
    WorkerFragment.sendResult(intent, WorkerFragment.RESULTCODE_HELLOWORLD, result);


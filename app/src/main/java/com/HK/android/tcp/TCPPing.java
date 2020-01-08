package com.HK.android.tcp;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TCPPing {
	private final static String TAG = TCPPing.class.getSimpleName();

	private String mCommand = "";
	private int mStatus = -1;
	private boolean mPingEnd = false;

	private ThreadUtil mStdoutUtil = null, mErroroutUtil = null;
	// 保存进程的输入流信息
	private List<String> mStdoutList = new ArrayList<String>();
	// 保存进程的错误流信息
	private List<String> mErroroutList = new ArrayList<String>();

	private static final long OVER_TIME = 5 * 1000;
	private long mPingStartTime = 0;
	private Handler mHandler = new Handler();
	private Runnable mOvertimeRunnable = new Runnable() {

		@Override
		public void run() {
			if (System.currentTimeMillis() - mPingStartTime >= OVER_TIME) {
				Log.i(TAG, "Ping Over Time...");
				mStatus = -1;
				mPingEnd = true;
				mStdoutUtil.stop();
				mErroroutUtil.stop();
				mHandler.removeCallbacks(mOvertimeRunnable);
			} else
				mHandler.postDelayed(this, 1000);
		}
	};

	public void threadExecuteCommand(String command) {
		mCommand = command;
		mPingEnd = false;
		new Thread() {
			public void run() {
				mStatus = executeCommand(mCommand);
				mPingEnd = true;
			}
		}.start();
	}

	public int getStatus() {
		return mStatus;
	}

	public boolean isPingEnd() {
		return mPingEnd;
	}

	private int executeCommand(String command) {

		mStdoutList.clear();
		mErroroutList.clear();

		Process p = null;
		int status = -1;
		try {
			p = Runtime.getRuntime().exec(command);

			mPingStartTime = System.currentTimeMillis();
			mHandler.postDelayed(mOvertimeRunnable, 10);

			mStdoutUtil = new ThreadUtil(p.getInputStream(), mStdoutList);
			mErroroutUtil = new ThreadUtil(p.getErrorStream(), mErroroutList);

			mStdoutUtil.start();
			mErroroutUtil.start();

			status = p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return status;
	}

	public List<String> getStdoutList() {
		return mStdoutList;
	}

	public List<String> getErroroutList() {
		return mErroroutList;
	}

}

class ThreadUtil implements Runnable {

	private String mCharacter = "GB2312";
	private List<String> mListString;
	private InputStream mInputStream;

	Thread mThread = null;

	public ThreadUtil(InputStream inputStream, List<String> list) {
		this.mInputStream = inputStream;
		this.mListString = list;
	}

	public void start() {
		mThread = new Thread(this);
		mThread.setDaemon(true);
		mThread.start();
	}

	public void stop() {
		mThread.interrupt();
	}

	public void run() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(mInputStream,
					mCharacter));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line != null) {
					mListString.add(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				mInputStream.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

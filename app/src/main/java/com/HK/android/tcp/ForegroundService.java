package com.HK.android.tcp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ForegroundService {
	private boolean mReflectFlg = false;

	public static final int NOTIFICATION_ID = 1;

	private static final Class<?>[] mSetForegroundSignature = new Class[] { boolean.class };
	private static final Class<?>[] mStartForegroundSignature = new Class[] {
			int.class, Notification.class };
	private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };

	private NotificationManager mNM = null;
	private Method mSetForeground = null;
	private Method mStartForeground = null;
	private Method mStopForeground = null;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];

	private Service mService = null;

	public ForegroundService(Service service) {
		mService = service;

		try {
			mNM = (NotificationManager) service
					.getSystemService(Context.NOTIFICATION_SERVICE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			mStartForeground = mService.getClass().getMethod("startForeground",
					mStartForegroundSignature);
			mStopForeground = mService.getClass().getMethod("stopForeground",
					mStopForegroundSignature);
		} catch (NoSuchMethodException e) {
			mStartForeground = mStopForeground = null;
		}

		try {
			mSetForeground = mService.getClass().getMethod("setForeground",
					mSetForegroundSignature);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(
					"OS doesn't have Service.startForeground OR Service.setForeground!");
		}
	}

	public ForegroundService(Context context, Service service) {
		mService = service;

		mNM = (NotificationManager) context.getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		try {
			mStartForeground = mService.getClass().getMethod("startForeground",
					mStartForegroundSignature);
			mStopForeground = mService.getClass().getMethod("stopForeground",
					mStopForegroundSignature);
		} catch (NoSuchMethodException e) {
			mStartForeground = mStopForeground = null;
		}

		try {
			mSetForeground = getClass().getMethod("setForeground",
					mSetForegroundSignature);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(
					"OS doesn't have Service.startForeground OR Service.setForeground!");
		}
	}

	void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(this, args);
		} catch (InvocationTargetException e) {
			// Should not happen.
			Log.w("ForegroundService", "Unable to invoke method", e);
		} catch (IllegalAccessException e) {
			// Should not happen.
			Log.w("ForegroundService", "Unable to invoke method", e);
		}
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	void startForegroundCompat(int id, Notification notification) {
		if (mReflectFlg) {
			// If we have the new startForeground API, then use it.
			if (mStartForeground != null) {
				mStartForegroundArgs[0] = Integer.valueOf(id);
				mStartForegroundArgs[1] = notification;
				invokeMethod(mStartForeground, mStartForegroundArgs);
				return;
			}

			// Fall back on the old API.
			mSetForegroundArgs[0] = Boolean.TRUE;
			invokeMethod(mSetForeground, mSetForegroundArgs);
			mNM.notify(id, notification);
		} else {

			if (VERSION.SDK_INT >= 5) {
				mService.startForeground(id, notification);
			} else {
				// Fall back on the old API.
				mSetForegroundArgs[0] = Boolean.TRUE;
				invokeMethod(mSetForeground, mSetForegroundArgs);
				mNM.notify(id, notification);
			}
		}
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	void stopForegroundCompat(int id) {
		if (mReflectFlg) {
			// If we have the new stopForeground API, then use it.
			if (mStopForeground != null) {
				mStopForegroundArgs[0] = Boolean.TRUE;
				invokeMethod(mStopForeground, mStopForegroundArgs);
				return;
			}

			// Fall back on the old API. Note to cancel BEFORE changing the
			// foreground state, since we could be killed at that point.
			mNM.cancel(id);
			mSetForegroundArgs[0] = Boolean.FALSE;
			invokeMethod(mSetForeground, mSetForegroundArgs);
		} else {
			if (VERSION.SDK_INT >= 5) {
				mService.stopForeground(true);
			} else {
				// Fall back on the old API. Note to cancel BEFORE changing the
				// foreground state, since we could be killed at that point.
				mNM.cancel(id);
				mSetForegroundArgs[0] = Boolean.FALSE;
				invokeMethod(mSetForeground, mSetForegroundArgs);
			}
		}
	}

}

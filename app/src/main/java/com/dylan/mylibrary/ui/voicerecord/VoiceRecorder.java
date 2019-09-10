package com.dylan.mylibrary.ui.voicerecord;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.dylan.mylibrary.R;

import java.io.File;

public class VoiceRecorder {
	public static final int REQUEST_AUDIO_RECORD_PERMISSION = 253;
	public static final int REQUEST_STORAGE_PERMISSION = 264;
	private static final int MSG_VOLUME_CHAMGED = 0x111;
	private Dialog dialog;
	private LinearLayout llTextTip;
	private ImageView imageRecord, imageVolume;
	private TextView textHint;
	private MediaRecordHelper recordHelper;
	private RecordFinishListener recordFinishListener;
	private long recordTime;
	private boolean isRecording = false;
	private boolean clearHistory;
	private boolean showTextHint;


	public VoiceRecorder(boolean clearCachebeforStart) {
		clearHistory = clearCachebeforStart;
	}

	private void showDialog(Context context) {
		dialog = new Dialog(context, R.style.DLDialogStyle);
		dialog.setCanceledOnTouchOutside(false);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dl_dialog_voicerecord, null);
		dialog.setContentView(view);
		llTextTip = (LinearLayout) dialog.findViewById(R.id.ll_text_tip);
		if (!showTextHint) llTextTip.setVisibility(View.GONE);
		imageRecord = (ImageView) dialog.findViewById(R.id.imageRecord);
		imageVolume = (ImageView) dialog.findViewById(R.id.imageVolume);
		textHint = (TextView) dialog.findViewById(R.id.textHint);
		dialog.show();
	}


	public void showTextHint(boolean bl) {
		showTextHint = bl;
	}


	//开始录音
	public void startRecord(final Context context) {
		if (context == null) return;
		//6.0 以上获取权限， audio,
		Activity activity = (Activity) context;
		boolean hasAudioPermission=PermissionUtils.hasAudioPermission(activity);
		if (!hasAudioPermission) {
			ActivityCompat.requestPermissions(activity, PermissionUtils.audioRecordPermission, REQUEST_AUDIO_RECORD_PERMISSION);
			return;
		}
		boolean hasStoragePermission=PermissionUtils.hasStoragePermission(activity);
		if (!hasStoragePermission){
			ActivityCompat.requestPermissions(activity, PermissionUtils.storagePermission, REQUEST_STORAGE_PERMISSION);
			return;
		}
		if (recordHelper == null) {
			String dir = Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/audioFile/";
			recordHelper = MediaRecordHelper.getInstance(dir);
		}
		recordHelper.clearHistoryBeforeStart(clearHistory);
		recordHelper.start(new MediaRecordHelper.AudioRecordStartListener() {
			@Override
			public void hasStarted() {
				showDialog(context);
				isRecording = true;
				// 音量变化
				new Thread(getVolumeRunnable).start();
			}

			@Override
			public void startFailure(Exception e) {
				Toast.makeText(context, "开启录音失败!!!", Toast.LENGTH_SHORT).show();
			}
		});
	}


	public void cancelRecord() {
		isRecording = false;
		dismissDialog();
		recordHelper.cancel();
		recordTime = 0;
	}

	public VoiceResult finishRecordWithResult() {
		isRecording = false;
		dismissDialog();
		recordHelper.finish();
		VoiceResult result = new VoiceResult();
		result.setDuration(recordTime);
		result.setFilePath(recordHelper.getCurrentPath());
		String uri = Uri.fromFile(new File(recordHelper.getCurrentPath())).toString();
		result.setUri(uri);
		recordTime = 0;
		return result;
	}


	public void finishRecord() {
		isRecording = false;
		dismissDialog();
		recordHelper.finish();
		if (recordFinishListener != null) recordFinishListener.onFinish(recordTime, recordHelper.getCurrentPath());
		recordTime = 0;
	}


	/**
	 * --------录音过程中的状态变化----------
	 **/
	public void stateRecording() {
		if (dialog != null && dialog.isShowing()) {
			imageRecord.setVisibility(View.VISIBLE);
			imageVolume.setVisibility(View.VISIBLE);
			textHint.setVisibility(View.VISIBLE);
			imageRecord.setImageResource(R.drawable.ic_recording);
			textHint.setText("手指上滑，取消发送");
		}
	}

	public void stateWantCancel() {
		if (dialog != null && dialog.isShowing()) {
			imageRecord.setVisibility(View.VISIBLE);
			imageRecord.setImageResource(R.drawable.icon_record_cancel);
			imageVolume.setVisibility(View.GONE);
			textHint.setVisibility(View.VISIBLE);
			textHint.setText("松开手指，取消发送");
		}
	}

	public void stateLengthShort() {
		if (dialog != null && dialog.isShowing()) {
			imageRecord.setVisibility(View.VISIBLE);
			imageRecord.setImageResource(R.drawable.ic_record_short);
			imageVolume.setVisibility(View.GONE);
			textHint.setVisibility(View.VISIBLE);
			textHint.setText("录音时间过短");
		}
		if (recordHelper!=null)recordHelper.cancel();
		mHanlder.postDelayed(new Runnable() {
			@Override
			public void run() {
				dismissDialog();
			}
		}, 1300);
	}

	/**
	 * --------录音过程中的状态变化----------
	 **/


	public void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
	}


	public boolean isRecording() {
		return isRecording;
	}

	public void setNoRecording() {
		isRecording = false;
	}

	public long getRecordTime() {
		return recordTime;
	}

	public void clearRecordTime() {
		recordTime = 0;
	}

	/**
	 * 更新音量
	 *
	 * @param level
	 */
	public void updateVolumeLevel(int level) {
		if (dialog != null && dialog.isShowing()) {
			Resources resources = dialog.getContext().getResources();
			int volumeResId = resources.getIdentifier("icon_volume_" + level, "drawable", dialog.getContext().getPackageName());
			imageVolume.setImageResource(volumeResId);
		}
	}


	private Runnable getVolumeRunnable = new Runnable() {
		@Override
		public void run() {
			while (isRecording) {
				try {
					Thread.sleep(100);
					recordTime += 100;
					Log.e("getVolumeRunnable", "recordTime " + recordTime);
					mHanlder.sendEmptyMessage(MSG_VOLUME_CHAMGED);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};


	private Handler mHanlder = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case MSG_VOLUME_CHAMGED:
					updateVolumeLevel(recordHelper.getVoiceLevel(7));
					break;
				default:
					break;
			}
		}

		;
	};


	/**
	 * 录音完成后的回调
	 */
	public interface RecordFinishListener {
		void onFinish(long millisecond, String filePath);
	}

	public void setRecordFinishListener(RecordFinishListener listener) {
		recordFinishListener = listener;
	}


	public static class VoiceResult {
		private long duration;
		private String uri;
		private String filePath;

		public long getDuration() {
			return duration;
		}

		public void setDuration(long duration) {
			this.duration = duration;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		@Override
		public String toString() {
			return "VoiceResult{" +
					"duration=" + duration +
					", uri='" + uri + '\'' +
					", filePath='" + filePath + '\'' +
					'}';
		}
	}

	public void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull int[] grantResults) {
		if (requestCode == REQUEST_AUDIO_RECORD_PERMISSION) {
			if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(activity, "无录音权限，无法打开麦克风", Toast.LENGTH_SHORT).show();
			}
		} else if (requestCode == REQUEST_STORAGE_PERMISSION) {
			if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(activity, "无存储权限，无法进行录音", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private static class PermissionUtils {
		public static String[] camerStoragePermission = new String[]{Manifest.permission.CAMERA,
				Manifest.permission.WRITE_EXTERNAL_STORAGE};

		public static String[] storagePermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
		public static String[] audioRecordPermission = new String[]{Manifest.permission.RECORD_AUDIO};

		public static boolean hasCameraStoragePermission(Activity activity) {
			int hasCameraPermison = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
			int hasWritePermison = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
			//权限还没有授予，需要在这里写申请权限的代码
			if (hasCameraPermison != PackageManager.PERMISSION_GRANTED || hasWritePermison != PackageManager.PERMISSION_GRANTED) {
				return false;
			} else {
				return true;
			}
		}

		public static boolean hasStoragePermission(Activity activity) {
			int hasReadPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
			int hasWritePermison = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
			//权限还没有授予，需要在这里写申请权限的代码
			if (hasReadPermission != PackageManager.PERMISSION_GRANTED || hasWritePermison != PackageManager.PERMISSION_GRANTED) {
				return false;
			} else {
				return true;
			}
		}


		public static boolean hasAudioPermission(Activity activity) {
			int hasAudioRecordPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
			if (hasAudioRecordPermission != PackageManager.PERMISSION_GRANTED) {
				return false;
			} else {
				return true;
			}
		}
	}

}

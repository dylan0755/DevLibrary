package com.dankal.mylibrary.ui.voicerecord;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dankal.mylibrary.R;
import com.dankal.mylibrary.ui.VoiceRecordActivity;

import java.text.SimpleDateFormat;
import java.util.List;

public class VoiceListAdapter extends ArrayAdapter<VoiceRecordActivity.Recorder> {

	private List<VoiceRecordActivity.Recorder> mDatas;
	private Context context;
	private int minItemWidth;
	private int maxItemWidth;
    private SimpleDateFormat format;

	private LayoutInflater inflater;

	public VoiceListAdapter(Context context, List<VoiceRecordActivity.Recorder> datas) {
		super(context, -1, datas);
		this.context = context;
		mDatas = datas;
        format=new SimpleDateFormat("mm:ss");
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		maxItemWidth = (int) (outMetrics.widthPixels * 0.8);
		maxItemWidth = (int) (outMetrics.widthPixels * 0.2);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.list_item_voice, parent,
					false);
			holder = new ViewHolder();
			holder.seconds = (TextView) convertView
					.findViewById(R.id.textLength);
			holder.length = convertView.findViewById(R.id.voiceAnim);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
        long duration=mDatas.get(position).getAudioLength();
		holder.seconds.setText( MediaTime.getMediaDurtionTime(duration)+ "\"");


		return convertView;
	}

	private class ViewHolder {
		TextView seconds;
		View length;
	}

}

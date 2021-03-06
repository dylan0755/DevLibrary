package com.dylan.library.widget.irecycler.footer;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dylan.library.R;
import com.dylan.library.utils.Logger;


public class LoadMoreFooterView extends FrameLayout {
    private static CharSequence globalNoMoreText;
    private Status mStatus;

    private View mLoadingView;

    private View mErrorView;

    private View mTheEndView;
    private TextView tvEndView;

    public static String defaultErrorTip="加载失败";


    private OnRetryListener mOnRetryListener;

    public LoadMoreFooterView(Context context) {
        this(context, null);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.dl_load_more_footer_view, this, true);

        mLoadingView = findViewById(R.id.loadingView);
        mErrorView = findViewById(R.id.errorView);
        mTheEndView = findViewById(R.id.theEndView);
        mErrorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRetryListener != null) {
                    mOnRetryListener.onRetry(LoadMoreFooterView.this);
                }
            }
        });

        setStatus(Status.GONE);
    }



    public static void setGlobalNoMoreText(CharSequence charSequence){
        globalNoMoreText =charSequence;
    }

    public void setOnRetryListener(OnRetryListener listener) {
        this.mOnRetryListener = listener;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        this.mStatus = status;
        change();
    }

    public void setErrorText(String text){
        TextView textView= (TextView) mErrorView;
        textView.setText(text);
    }
    public boolean canLoadMore() {
        return mStatus == Status.GONE || mStatus == Status.ERROR;
    }

    private void change() {
        switch (mStatus) {
            case GONE:
                mLoadingView.setVisibility(GONE);
                mErrorView.setVisibility(GONE);
                mTheEndView.setVisibility(GONE);
                break;
            case LOADING:
                mLoadingView.setVisibility(VISIBLE);
                mErrorView.setVisibility(GONE);
                mTheEndView.setVisibility(GONE);
                break;
            case ERROR:
                mLoadingView.setVisibility(GONE);
                mErrorView.setVisibility(VISIBLE);
                mTheEndView.setVisibility(GONE);
                break;
            case THE_END:
                mLoadingView.setVisibility(GONE);
                mErrorView.setVisibility(GONE);
                mTheEndView.setVisibility(VISIBLE);

                if (tvEndView==null)tvEndView= (TextView) mTheEndView;
                if (tvEndView!=null){
                    if (globalNoMoreText !=null&& globalNoMoreText.length()>0){
                        String currentText=getNoMoreTextView().getText().toString();
                        if ("已加载全部".equals(currentText)){//没有设置过text,又有全局的，则设置全局的
                            tvEndView.setText(globalNoMoreText);
                        }

                    }
                }
                break;
        }
    }



    public TextView getNoMoreTextView(){
        return (TextView) mTheEndView;
    }

    public TextView getLoadingTextView(){
        return (TextView) mLoadingView.findViewById(R.id.tvState);
    }

    public TextView getErrorTextView(){
        return (TextView) mErrorView;
    }


    public ProgressBar getLoadingProgressBar(){
        return mLoadingView.findViewById(R.id.progressBar);
    }





    public enum Status {
        GONE, LOADING, ERROR, THE_END
    }

    public void setTheEndViewBackgoundColor(String colorString){
        mTheEndView.setBackgroundColor(Color.parseColor(colorString));
    }

    public interface OnRetryListener {
        void onRetry(LoadMoreFooterView view);
    }

}

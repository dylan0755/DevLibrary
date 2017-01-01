package com.dankal.mylibrary.ui.tab;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.dankal.mylibrary.R;
import com.dankal.mylibrary.adapter.VideoDataAdapter;
import com.dankal.mylibrary.bean.VideoData;
import com.dankal.mylibrary.ui.base.BaseFragment;
import com.dankal.mylibrary.util.IResponBodyImpl;
import com.dankal.mylibrary.util.ResponseBodyParser;
import com.dylan.library.widget.GridHorizontalSpace;
import com.dylan.library.widget.RefreshRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;

/**
 * Created by Dylan on 2016/9/7.
 */
public class OriginalSubFragment extends BaseFragment {
    public static String TAG_ID = "tagid";
    public static String RECORMMEND = "recordmment";
    private RefreshRecyclerView mRecyclerView;
    private VideoDataAdapter mAdapter;
    private int tagid;
    private boolean isSuggest;
    private int initPageIndex=1;
    private int pageIndex = initPageIndex;


    @Override
    public View onCreateView(Bundle savedInstanceState) {
        tagid = getArguments().getInt(TAG_ID);
        isSuggest = getArguments().getBoolean(RECORMMEND);
        return setContentView(R.layout.fragment_original_sub);
    }

    @Override
    public void init() {
        initView();
        loadVideo();
    }


    private void initView() {
        mRecyclerView = (RefreshRecyclerView) findViewById(R.id.rv_original_sub);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.addItemDecoration(new GridHorizontalSpace(20));
        mRecyclerView.setLoadMoreListener(new RefreshRecyclerView.LoadMoreListener() {

            @Override
            public void pullToLoadMore() {
                if (mAdapter != null){
                    mAdapter.showFooter();
                    pageIndex++;
                    loadVideo();
                }
            }
        });
    }


    public void reLoad(int tagId){
        tagid=tagId;
        loadVideo();
    }


    private void loadVideo() {
        Call<String> call = null;
        if (isSuggest) call = mRestApi.loadVRrecommend(pageIndex);
        else call = mRestApi.loadVRVideo(tagid, pageIndex);
        ResponseBodyParser.parse(call, new IResponBodyImpl() {
            @Override
            public void onSucess(String jsonData, Gson gson) {
                try {
                    if (isSuggest) jsonData = new JSONObject(jsonData).getString("recommend");
                    else jsonData = new JSONObject(jsonData).getString("classify");
                    List<VideoData> videoDataList = gson.fromJson(jsonData, new TypeToken<List<VideoData>>() { }.getType());
                    setAdapter(videoDataList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onErro(String erromessage) {
                super.onErro(erromessage);

            }
        });



    }

    /**
     * 设置，更新适配器
     */
    private void setAdapter(List<VideoData> videoDataList) {
        if (videoDataList != null && videoDataList.size() > 0) {
            if (mAdapter == null) {
                mAdapter = new VideoDataAdapter(getContext(), mRecyclerView, videoDataList,null);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.loadmore(videoDataList);
            }
        } else {
            if (pageIndex !=initPageIndex) {
                if (mAdapter != null) mAdapter.showNoMore();
            }
        }
    }


}

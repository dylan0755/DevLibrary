package com.dylan.mylibrary.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.dylan.library.graphics.BitmapHelper;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.Logger;
import com.dylan.library.utils.MathUtils;
import com.dylan.library.widget.DragMapView;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.bean.Chicken;
import com.dylan.mylibrary.bean.Fish;
import com.dylan.mylibrary.bean.Plaint;
import com.dylan.mylibrary.util.OnSceneClickListener;
import com.dylan.mylibrary.util.ScenePointHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Author: Administrator
 * Date: 2020/10/9
 * Desc:
 */
public class FarmMapView extends DragMapView {
    private ScenePointHelper pointHelper;
    private OnSceneClickListener mSceneClickListener;
    private SparseArray<Plaint> plaintArrays = new SparseArray<>();
    private List<Chicken> chickenList = new ArrayList<>();
    private List<Fish> fishList = new ArrayList<>();


    int[] image = {R.drawable.ic_loading_white_01, R.drawable.ic_loading_white_02, R.drawable.ic_loading_white_02,
            R.drawable.ic_loading_white_03, R.drawable.ic_loading_white_04, R.drawable.ic_loading_white_05,
            R.drawable.ic_loading_white_06, R.drawable.ic_loading_white_07, R.drawable.ic_loading_white_08,
            R.drawable.ic_loading_white_09};//需要循环的图片

    private Bitmap snail;
    private int index;
    private Handler mHandler;
    private Timer mTimer;


    public FarmMapView(Context context) {
        this(context, null);
    }

    public FarmMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.MATRIX);
        pointHelper = new ScenePointHelper();

        //帧动画计时器
        mHandler= new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (!isDragging() && !isScrolling()) {
                    if (msg.what == 0x11) {
                        index++;
                        if (index >= 9) {
                            index = 0;
                        }
                        snail = BitmapFactory.decodeResource(getContext().getResources(), image[index]);
                        invalidate();
                    }
                }
            }
        };
        mTimer=new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0x11);
            }
        }, 0, 400);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制帧动画
        if (snail != null) {
            canvas.drawBitmap(snail, pointHelper.getChickensPoints().get(0).x + getOffsetX(), pointHelper.getChickensPoints().get(0).y + getOffsetY(), null);
        }


    }


    @Override
    protected void initMap() {
        setMatrixTranslate(-2090, -1042);
        //画鸡舍范围
        drawChickZone();
        //画种植区域
        drawPlaintBitmap();
    }


    //更新小鸡数量
    public void updateChickenList(List<Chicken> list) {
        if (list == null) return;
        chickenList = list;
        drawScence();
    }

    public void updateFishList(List<Fish> list) {
        if (list == null) return;
        fishList = list;
        drawScence();
    }


    public void updateChickenAndFishes(List<Chicken> chickens, List<Fish> fishes) {
        if (chickens != null) chickenList = chickens;
        if (fishes != null) fishList = fishes;
        drawScence();
    }

    //更新种植
    public void updatePlaint() {

    }


    /**
     * 绘制场景
     */
    private void drawScence() {
        mBitmap = sourceBitmap;//重置底图
        drawChickZone();
        drawPlaintBitmap();
        drawFishPond();
    }


    /**
     * 绘制种植区域
     */

    private void drawPlaintBitmap() {
        try {
            for (int index = 0; index < pointHelper.getPlaintHolderLeftTopPoints().size(); index++) {
                Point point = null;
                Plaint plaint = plaintArrays.get(index);
                if (plaint != null) {
                    point = plaint.getPoint();
                } else {
                    point = pointHelper.getPlaintHolderLeftTopPoints().get(index);
                    plaint = new Plaint();
                    plaint.setBitmapType(Plaint.TYPE_PLACE_HOLDER);
                    plaint.setNo(index);
                    plaint.setPoint(point);
                    plaintArrays.put(index, plaint);
                }


                if (point != null) {
                    //绘制底图
                    Bitmap tempBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    tempBitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
                    Canvas canvas = new Canvas(tempBitmap);
                    Rect bottomRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
                    canvas.drawBitmap(mBitmap, bottomRect, bottomRect, null);
                    //绘制果树或占位图
                    Paint paint = new Paint();
                    Bitmap plaintBitmap;
                    if (plaint.getBitmapType() == Plaint.TYPE_PLACE_TREE) {//绘制果树
                        plaintBitmap = BitmapFactory.decodeStream(getContext().getAssets().open("appleTree.png"));
                    } else {//绘制占位图
                        plaintBitmap = BitmapFactory.decodeStream(getContext().getAssets().open("plant_field.png"));
                    }
                    plaintBitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
                    canvas.drawBitmap(plaintBitmap, point.x, point.y, paint);
                    //绘制果树  果树还要判断状态   开花  结果（有几个果实）
                    if (plaint.getBitmapType() == Plaint.TYPE_PLACE_TREE) {
                        if (plaint.getStatus() == Plaint.STATUS_FLOWER) {//开花
                            Bitmap flowerBitmap = BitmapFactory.decodeStream(getContext().getAssets().open("apple-flower.png"));
                            flowerBitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
                            int offsetX = 25;
                            int offsetY = 30;
                            canvas.drawBitmap(flowerBitmap, point.x + offsetX, point.y + offsetY, paint);
                        } else if (plaint.getStatus() == Plaint.STATUS_FRUIT) {//结果
                            int fruitNumber = plaint.getFruitNumber();
                            if (fruitNumber >= 3) {
                                Bitmap applesBitmap = BitmapFactory.decodeStream(getContext().getAssets().open("apples.png"));
                                applesBitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
                                int offsetX = 18;
                                int offsetY = 35;
                                canvas.drawBitmap(applesBitmap, point.x + offsetX, point.y + offsetY, paint);
                            } else {
                                for (int i = 0; i < fruitNumber; i++) {
                                    if (i == 0) {//画第一个苹果
                                        Bitmap singleApple = BitmapFactory.decodeStream(getContext().getAssets().open("appleOne.png"));
                                        singleApple.setDensity(getResources().getDisplayMetrics().densityDpi);
                                        int offsetX = 60;
                                        int offsetY = 35;
                                        canvas.drawBitmap(singleApple, point.x + offsetX, point.y + offsetY, paint);
                                    } else {//画第二个苹果
                                        Bitmap singleApple = BitmapFactory.decodeStream(getContext().getAssets().open("appleOne.png"));
                                        singleApple.setDensity(getResources().getDisplayMetrics().densityDpi);
                                        int offsetX = 20;
                                        int offsetY = 105;
                                        canvas.drawBitmap(singleApple, point.x + offsetX, point.y + offsetY, paint);
                                    }
                                }
                            }
                        }
                    }
                    mBitmap = tempBitmap;
                    setImageBitmap(tempBitmap);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 画鸡圈和小鸡
     */
    private void drawChickZone() {
        Bitmap tempBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        tempBitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
        Canvas canvas = new Canvas(tempBitmap);
        Rect bottomRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        canvas.drawBitmap(mBitmap, bottomRect, bottomRect, null);


        if (EmptyUtils.isEmpty(chickenList)) {
            try { //鸡圈
                Bitmap feedWood = BitmapFactory.decodeStream(getContext().getAssets().open("chickZone.png"));
                feedWood.setDensity(getResources().getDisplayMetrics().densityDpi);
                canvas.drawBitmap(feedWood, 2360, 1380, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                //食物槽
                try {
                    Bitmap feedWood = BitmapFactory.decodeStream(getContext().getAssets().open("feedWood.png"));
                    feedWood.setDensity(getResources().getDisplayMetrics().densityDpi);
                    canvas.drawBitmap(feedWood, 2558, 1741, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //小鸡
                int size = chickenList.size() < 5 ? chickenList.size() : pointHelper.getChickensPoints().size();
                for (int i = 0; i < size; i++) {
                    String fileName = "chick" + (i + 1) + ".png";
                    Bitmap chick = BitmapFactory.decodeStream(getContext().getAssets().open(fileName));
                    chick.setDensity(getResources().getDisplayMetrics().densityDpi);
                    Point point = pointHelper.getChickensPoints().get(i);
                    canvas.drawBitmap(chick, point.x, point.y, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mBitmap = tempBitmap;
        setImageBitmap(tempBitmap);
    }

    //绘制鱼塘小鱼
    private void drawFishPond() {
        Bitmap tempBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        tempBitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
        Canvas canvas = new Canvas(tempBitmap);
        Rect bottomRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        canvas.drawBitmap(mBitmap, bottomRect, bottomRect, null);

        try {
            int size = fishList.size() < 3 ? fishList.size() : pointHelper.getFishPoints().size();
            for (int i = 0; i < size; i++) {
                String fileName = "fish" + (i + 1) + ".png";
                Bitmap fish = BitmapFactory.decodeStream(getContext().getAssets().open(fileName));
                fish.setDensity(getResources().getDisplayMetrics().densityDpi);
                Point point = pointHelper.getFishPoints().get(i);
                canvas.drawBitmap(fish, point.x, point.y, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mBitmap = tempBitmap;
        setImageBitmap(tempBitmap);
    }


    /**
     * 点击场景监听
     */

    @Override
    public boolean onClickScene(MotionEvent e) {
        Point point = BitmapHelper.getClickPoint(FarmMapView.this,e);
        Logger.e("dstX=" + point.x + "  dstY=" + point.y);


        boolean isClickInFishPond = MathUtils.isInPolygon(point, pointHelper.getFishpondRectBound());
        if (isClickInFishPond) {
            if (mSceneClickListener != null) mSceneClickListener.onClickFishPond();
        }

        //点击第N棵树
        for (int index = 0; index < pointHelper.getPlaintLeftTopPoints().size(); index++) {
            boolean isClickPlaint = MathUtils.isInPolygon(point, pointHelper.getPlaintHolderRectBound(index));
            if (isClickPlaint) {
                if (plaintArrays.get(index).getBitmapType() == Plaint.TYPE_PLACE_HOLDER) {
                    plaintArrays.get(index).setBitmapType(Plaint.TYPE_PLACE_TREE);
                    plaintArrays.get(index).setPoint(pointHelper.getPlaintLeftTopPoints().get(index));
                } else {
                    plaintArrays.get(index).setStatus(Plaint.STATUS_FLOWER);
                }
                drawScence();
                setImageBitmap(mBitmap);
                if (mSceneClickListener != null)
                    mSceneClickListener.onClickPlaint(plaintArrays.get(index));
                return true;
            }
        }

        //点击第N只鸡
        if (EmptyUtils.isNotEmpty(chickenList)) {
            for (int index = 0; index < pointHelper.getChickensPoints().size(); index++) {
                boolean isClickChicken = MathUtils.isInPolygon(point, pointHelper.getChickenRectBound(index));
                if (isClickChicken) {
                    if (index <= chickenList.size() - 1) {
                        if (mSceneClickListener != null)
                            mSceneClickListener.onClickChicken(chickenList.get(index));
                        return true;
                    }
                }
            }
        }

        //点击鸡圈
        boolean isInChickenRect = MathUtils.isInPolygon(point, pointHelper.getChickenZoneRectBound());
        if (isInChickenRect) {
            if (mSceneClickListener != null) mSceneClickListener.onClickChickenZone();
        }

        //点击第N条鱼
        if (EmptyUtils.isNotEmpty(fishList)) {
            for (int index = 0; index < pointHelper.getFishPoints().size(); index++) {
                boolean isClickFish = MathUtils.isInPolygon(point, pointHelper.getFishRectBound(index));
                if (isClickFish) {
                    if (index <= fishList.size() - 1) {
                        if (mSceneClickListener != null)
                            mSceneClickListener.onClickFish(fishList.get(index));
                        return true;
                    }
                }
            }
        }


        //点击批发市场
        boolean isClickMarket = MathUtils.isInPolygon(point, pointHelper.getMarketRectBound());
        if (isClickMarket) {
            if (mSceneClickListener != null) mSceneClickListener.onClickMarket();
            return true;
        }
        boolean isClickShop = MathUtils.isInPolygon(point, pointHelper.getShopRectBound());
        //点击商店
        if (isClickShop) {
            if (mSceneClickListener != null) mSceneClickListener.onClickShop();
            return true;
        }
        //点击仓库
        boolean isClickWareHouse = MathUtils.isInPolygon(point, pointHelper.getWareHouseRectBound());
        if (isClickWareHouse) {
            if (mSceneClickListener != null) mSceneClickListener.onClickWareHouse();
            return true;
        }
        return true;
    }


    @Override
    public void onFiling(int currentX, int currentY, float deltaX, float deltaY) {

    }


    public void setOnSceneClickListener(OnSceneClickListener clickListener) {
        mSceneClickListener = clickListener;
    }


    public void destroy() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap=null;
        }
        if (sourceBitmap!=null){
            sourceBitmap.recycle();
            sourceBitmap=null;
        }
        pointHelper.clear();
        pointHelper=null;
        plaintArrays.clear();
        plaintArrays=null;
        mSceneClickListener=null;
        chickenList.clear();
        fishList.clear();
        mTimer.cancel();
        mHandler.removeCallbacksAndMessages(null);
        mHandler=null;
    }


}

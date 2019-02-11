package com.sj.custom_view.simulation.jk.refresh;

import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import com.sj.custom_view.Util;

/**
 * Created by SJ on 2019/1/18.
 */
public class TouchHelper2 {

    private static final String TAG = "===TouchHelper";

    private VelocityTracker mVelocityTracker;

    private int mTouchSloup;
    private int mMinVelocity;

    private float mDownY;
    private float mLastY;
    private int mTrackingPointId;
    private boolean isScrollingScene;

    /**
     * 用于解决“连贯滑动”情况下(往上推RefreshLayout至其到顶部,如果子View可滑动的话,需要让子View接着滑动),需要把MotionEvent的Action置为DOWN
     * 否则,ScrollView会一下往上滑很大一段距离
     */
    private boolean isNeedResetMotionActionFlag = true;

    private View mProxyView;

    private IMove mMoveListsner;
    private IProxyView mProxyListener;

    public TouchHelper2(View proxy, IMove moveListener, IProxyView proxyListener) {
        Util.checkNull(proxy, "The Proxy View cannot be Null.");
        Util.checkNull(moveListener, "The IMove Callback cannot be Null.");
        Util.checkNull(proxyListener, "The IProxyView Callback cannot be Null.");

        this.mProxyView = proxy;
        this.mMoveListsner = moveListener;
        this.mProxyListener = proxyListener;

        this.mVelocityTracker = VelocityTracker.obtain();

        ViewConfiguration vc = ViewConfiguration.get(proxy.getContext());
        this.mTouchSloup = vc.getScaledTouchSlop();
        this.mMinVelocity = vc.getScaledMinimumFlingVelocity();
    }

    /**
     * 具体的Touch事件处理方法
     * @param ev MotionEvent
     * @return true:RefreshLayout处理Touch事件。
     *         false:需要交给子View去处理
     */
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Util.checkNull(this.mProxyView, "The 'mProxyView' attribute cannot be Null.");
        Util.checkNull(this.mMoveListsner, "The IMove Callback Instance Is Null.");

        //检查ProxyView是否处理
        //1:Head正处于Refreshing状态，向上移动需拦截，等滑动到最大位置后，交由子 View 处理

        //2:Header处于非Refreshing状态
        //2.1:向下滑动，需拦截
        //2.2:向上滑动，需拦截，等滑动到最大位置后，交由子 View 处理

        //如果手指按在了RefreshLauout范围内,不管RefreshLayout还是其子类消不消费此次Touch事件序列
        //Touch事件序列序列否会交给RefreshLayout或者其子类去处理
        boolean result = true;

        switch (ev.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                this.mTrackingPointId = ev.getPointerId(0);
                this.mDownY = ev.getY();

                //down是整个序列的开始事件,这个事件也需要往下传
                this.mProxyListener.dispatchTouchEvent(ev);

                break;

            case MotionEvent.ACTION_MOVE:
                int tempIndex = ev.findPointerIndex(this.mTrackingPointId);
                float curY = ev.getY(tempIndex);
                if(this.mLastY == 0){
                    this.mLastY = curY;
                    break;
                }
                //移动差值
                float offsetY = curY - this.mLastY;
                //保存上次的滑动坐标
                this.mLastY = curY;

                if(!this.isScrollingScene
                        && Math.abs(curY - this.mDownY) >= this.mTouchSloup){
                    this.isScrollingScene = true;
                }

                //是否可滑动
                if(this.isScrollingScene){
                    HeaderRefreshState state = this.mProxyListener.state();
                    if(state != null){
                        //case1:Head正处于Refreshing状态
                        if(state == HeaderRefreshState.STATE_REFREING){
                            break;
                        }

                        //case2:Header处于非Refreshing状态
                        if(offsetY > 0){ //向下
                            if(!this.mProxyListener.childNeedTouchToMove()){
                                this.mMoveListsner.move(offsetY);
                            } else if(!this.mProxyListener.dispatchTouchEvent(ev)){
                                this.mMoveListsner.move(offsetY);
                            }

                        } else if(offsetY < 0){ //向上
                            if(!this.mProxyListener.isTop()){
                                Log.e(TAG, "向上---未到顶部");

                                this.mMoveListsner.move(offsetY);

                                this.isNeedResetMotionActionFlag = true;
                            } else {
                                boolean flag;

                                if(this.isNeedResetMotionActionFlag){
                                    this.isNeedResetMotionActionFlag = false;

                                    ev.setAction(MotionEvent.ACTION_DOWN);
                                    flag = this.mProxyListener.dispatchTouchEvent(ev);

                                    Log.e(TAG, "向上---到顶部了---重置Down:" + flag + "---Action:" + ev.getActionMasked());
                                }

                                flag = this.mProxyListener.dispatchTouchEvent(ev);

                                Log.e(TAG, "向上---到顶部了---flag:" + flag + "---Action:" + ev.getActionMasked());

                                if(!flag){
                                    this.mMoveListsner.move(offsetY);
                                }
                            }
                        }
                    }
                } else {
                    //RefreshLayout不处理的话,需要把事件往下传
                    //这样可以解决,子View如果具备滑动功能的话,可以保证其正常处理
                    this.mProxyListener.dispatchTouchEvent(ev);
                }

                break;

            case MotionEvent.ACTION_UP:
                //子View如果具备滑动功能的话,需要把事件往下传,这样可以保证其正常处理
                if(this.mProxyListener.childNeedTouchToMove()){
                    this.mProxyListener.dispatchTouchEvent(ev);

                } else if(this.mProxyListener.state() != HeaderRefreshState.STATE_DONE){
                    this.mMoveListsner.up();

                } else {
                    this.mProxyListener.dispatchTouchEvent(ev);
                }

                this.reset();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                int index = ev.getActionIndex();

                this.mTrackingPointId = ev.getPointerId(index);
                this.mLastY = ev.getY(index);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                int upPointIndex = ev.getActionIndex();
                int upPointId = ev.getPointerId(upPointIndex);

                if (upPointId == this.mTrackingPointId) {
                    int newPointIndex = ev.getPointerCount() - 1;
                    newPointIndex += upPointIndex == newPointIndex ? -1 : 0;

                    this.mTrackingPointId = ev.getPointerId(newPointIndex);
                    this.mLastY = ev.getY(newPointIndex);
                }
                break;
        }


        return result;
    }

    private void reset(){
        this.mDownY = 0;
        this.mLastY = 0;
        this.mTrackingPointId = 0;
        this.isNeedResetMotionActionFlag = true;
        this.isScrollingScene = false;
    }

    public interface IMove{
        void move(float offset);
        void up();
    }

    public interface IProxyView{
        /**
         * 判断是否滑动到了顶部
         * @return true：到顶了
         *         false: 没有
         */
        boolean isTop();

        /**
         * 判断是否子View需要获得Touch事件做move操作
         * @return true：子View需要move
         *         false: 子View不需要
         */
        boolean childNeedTouchToMove();

        /**
         * 调用RefreshLayout的dispatchTouchEvent()做事件传递
         * @return true：消费Touch事件
         *         false: 不消费
         */
        boolean dispatchTouchEvent(MotionEvent event);

        /**
         * 获取当前的HeaderRefreshState
         * @return HeaderRefreshState
         */
        HeaderRefreshState state();
    }
}

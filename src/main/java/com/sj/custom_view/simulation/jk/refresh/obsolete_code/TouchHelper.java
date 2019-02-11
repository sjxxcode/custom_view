package com.sj.custom_view.simulation.jk.refresh.obsolete_code;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import com.sj.custom_view.Util;

/**
 * Created by SJ on 2019/1/18.
 */
public class TouchHelper {

    private static final String TAG = "===TouchHelper";

    private VelocityTracker mVelocityTracker;

    private int mTouchSloup;
    private int mMinVelocity;

    private float mLastY;
    private int mTrackingPointId;
    private boolean isInterceptTouch;

    private View mProxyView;

    private IMove mMoveListsner;
    private IProxyView mProxyListener;

    public TouchHelper(View proxy, IMove moveListener, IProxyView proxyListener) {
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

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(!this.isInterceptTouch){
            Util.checkNull(this.mProxyView, "The 'mProxyView' attribute cannot be Null.");

            return this.mProxyListener.dispatchTouchEvent(ev);
        }

        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = false;
        float curY = ev.getY();

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Util.checkNull(this.mMoveListsner, "The IMove Callback Instance Is Null.");
                this.mLastY = curY;
                break;

            case MotionEvent.ACTION_MOVE:
                if (Math.abs(curY - this.mLastY) > this.mTouchSloup) { //拦截
                    //1:在顶部且向下拉
                    if(curY > this.mLastY && this.mProxyListener.isTop()){
                        this.isInterceptTouch = true;
                        this.mVelocityTracker.clear();

                        result = true;
                    }
                }
                break;
        }

        return result;
    }

    protected boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                this.mTrackingPointId = event.getPointerId(0);
                this.mLastY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                int tempIndex = event.findPointerIndex(this.mTrackingPointId);
                if(this.isInterceptTouch){
                    this.isInterceptTouch = false;
                    this.mLastY = event.getY(tempIndex);
                    break;
                }

                float offsetY = event.getY(tempIndex) - this.mLastY;

                this.mLastY = event.getY(tempIndex);

                this.mMoveListsner.move(offsetY);
                break;

            case MotionEvent.ACTION_UP:
                this.mMoveListsner.up();
                this.reset();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                int index = event.getActionIndex();
                this.mTrackingPointId = event.getPointerId(index);

                this.mLastY = event.getY(index);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                int upPointIndex = event.getActionIndex();
                int upPointId = event.getPointerId(upPointIndex);

                if (upPointId == this.mTrackingPointId) {
                    int newPointIndex = event.getPointerCount() - 1;
                    newPointIndex += upPointIndex == newPointIndex ? -1 : 0;

                    this.mTrackingPointId = event.getPointerId(newPointIndex);

                    this.mLastY = event.getY(newPointIndex);
                }
                break;
        }

        return true;
    }

    private void reset(){
        this.mLastY = 0;
        this.mTrackingPointId = 0;
        this.isInterceptTouch = false;
    }

    public interface IMove{
        void move(float offset);
        void up();
    }

    public interface IProxyView{
        boolean dispatchTouchEvent(MotionEvent event);
        boolean isTop();
    }
}

package com.sj.custom_view.simulation.jk.refresh;

/**
 * 说明:自定义Header需要实现的根接口
 * 作用:用于连接RefreshLayout与Header,方便他们之间通信
 *
 * Created by SJ on 2019/1/18.
 */
public interface IHeaderView {
    //最大滑动距离
    int maxMoveOffsetY();

    //刷新中的最大滑动距离
    int refreshOffsetY();

    //滑动
    void move(float currentOffsetY, float offsetY);

    //-------------------------------------------------------//

    //刷新
    void refresh();

    //刷新完毕
    void complate();

    //Header的刷新状态完全交于Header去管理
    HeaderRefreshState refreshState();
}

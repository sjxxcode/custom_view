package com.sj.custom_view.simulation.jk.refresh;

/**
 * Created by SJ on 2019/1/23.
 */
public enum HeaderRefreshState {
    STATE_REFRESH_BEFOR, //正在执行下拉操作,还未到达可刷新状态
    STATE_REFRESH_CAN,   //可以执行刷新动作了(此时还在执行Move操作)

    STATE_REFREING,      //正在执行刷新动作

    STATE_DONE          //刷新动作执行完毕
}

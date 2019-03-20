package com.seiyaya.work;

/**
 * 监听任务完成
 * @author 王佳
 * @created 2018年3月30日 上午10:34:37
 */
public interface CompleteWorkListener
{
    void onCompleteWork(Worker worker, Work work,WorkResult workResult);
}

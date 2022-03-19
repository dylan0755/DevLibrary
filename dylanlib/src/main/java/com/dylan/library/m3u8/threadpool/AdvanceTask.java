package com.dylan.library.m3u8.threadpool;

import java.util.List;

public interface AdvanceTask<T extends SubTask> {

  public void completeOne(T subTask);

  public void completeAll();

  public boolean failedRetry(T subTask);

  public List<T> getSubTaskList();
}

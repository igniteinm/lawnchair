/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.quickstep;

import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.android.launcher3.R;
import com.android.quickstep.views.TaskItemView;
import com.android.systemui.shared.recents.model.Task;

import java.util.List;

/**
 * Recycler view adapter that dynamically inflates and binds {@link TaskHolder} instances with the
 * appropriate {@link Task} from the recents task list.
 */
public final class TaskAdapter extends Adapter<TaskHolder> {

    private static final int MAX_TASKS_TO_DISPLAY = 6;
    private static final String TAG = "TaskAdapter";
    private final TaskListLoader mLoader;
    private final ArrayMap<Integer, TaskItemView> mTaskIdToViewMap = new ArrayMap<>();
    private TaskInputController mInputController;

    public TaskAdapter(@NonNull TaskListLoader loader) {
        mLoader = loader;
    }

    public void setInputController(TaskInputController inputController) {
        mInputController = inputController;
    }

    /**
     * Get task item view for a given task id if it's attached to the view.
     *
     * @param taskId task id to search for
     * @return corresponding task item view if it's attached, null otherwise
     */
    public @Nullable TaskItemView getTaskItemView(int taskId) {
        return mTaskIdToViewMap.get(taskId);
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskItemView itemView = (TaskItemView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item_view, parent, false);
        TaskHolder holder = new TaskHolder(itemView);
        itemView.setOnClickListener(view -> mInputController.onTaskClicked(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(TaskHolder holder, int position) {
        List<Task> tasks = mLoader.getCurrentTaskList();
        if (position >= tasks.size()) {
            // Task list has updated.
            return;
        }
        holder.bindTask(tasks.get(position));

    }

    @Override
    public void onViewAttachedToWindow(@NonNull TaskHolder holder) {
        mTaskIdToViewMap.put(holder.getTask().key.id, (TaskItemView) holder.itemView);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull TaskHolder holder) {
        mTaskIdToViewMap.remove(holder.getTask().key.id);
    }

    @Override
    public int getItemCount() {
        return Math.min(mLoader.getCurrentTaskList().size(), MAX_TASKS_TO_DISPLAY);
    }
}

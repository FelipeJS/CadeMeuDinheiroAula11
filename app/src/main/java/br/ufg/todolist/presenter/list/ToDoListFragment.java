package br.ufg.todolist.presenter.list;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import br.ufg.todolist.R;
import br.ufg.todolist.data.EasySharedPreferences;
import br.ufg.todolist.model.Task;
import br.ufg.todolist.presenter.BaseFragment;
import br.ufg.todolist.web.WebLogin;
import br.ufg.todolist.web.WebTodo;


/**
 * A simple {@link Fragment} subclass.
 */
public class ToDoListFragment extends BaseFragment {

    private List<Task> taskList;
    private AdapterTodo adapter;


    public ToDoListFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        taskList = new LinkedList<>();

        getTasks();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        initRecycler();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void initRecycler(){
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AdapterTodo(taskList,getActivity());
        recyclerView.setAdapter(adapter);
    }


    public void getTasks(){
        showDialogWithMessage(getString(R.string.load_tasks));
        String token = EasySharedPreferences.getStringFromKey(getActivity(),
                EasySharedPreferences.KEY_TOKEN);
        WebTodo webTodo = new WebTodo(token);
        webTodo.call();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Exception exception) {
        dismissDialog();
        showAlert(exception.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(List<Task> tasks) {
        dismissDialog();
        adapter.setTasks(tasks);
        adapter.notifyDataSetChanged();
    }

}

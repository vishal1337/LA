package com.v15h4l.la;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.v15h4l.la.Database.AlarmDBHelper;
import com.v15h4l.la.adapter.AlarmListAdapter;
import com.v15h4l.la.model.Alarm;

public class HomeFragment extends ListFragment {

    AlarmDBHelper dbHelper;
    AlarmListAdapter mAdapter;
    Context mContext;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;

        dbHelper = new AlarmDBHelper(mContext);

        mAdapter = new AlarmListAdapter(mContext,dbHelper.getAlarms(),HomeFragment.this);
        setListAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.add_alarm:
                startAlarmDetailsActivity(-1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setAlarmEnabled(Long id, Boolean isChecked){
        Alarm alarm = dbHelper.getAlarm(id);
        alarm.isEnabled = isChecked;
        dbHelper.updateAlarm(alarm);
        mAdapter.setAlarms(dbHelper.getAlarms());
        mAdapter.notifyDataSetChanged();
    }

    public void deleteAlarm(Long id){
        final Long alarmId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Please Confirm!!!")
                .setTitle("Delete Alarm?")
                .setCancelable(true)
                .setNegativeButton("Abort",null)
                .setPositiveButton("Proceed",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteAlarm(alarmId);
                        mAdapter.setAlarms(dbHelper.getAlarms());
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .show();
    }

    public void startAlarmDetailsActivity(long id){
        Intent intent = new Intent(mContext,AlarmDetailsActivity.class);
        intent.putExtra("id",id);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case 1:
                    mAdapter.setAlarms(dbHelper.getAlarms());
                    mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }


}
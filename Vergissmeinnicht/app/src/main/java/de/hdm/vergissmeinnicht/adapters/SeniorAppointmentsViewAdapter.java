package de.hdm.vergissmeinnicht.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.helpers.StaticValues;
import de.hdm.vergissmeinnicht.interfaces.DataHolderClickInterface;
import de.hdm.vergissmeinnicht.model.AppointmentData;
import de.hdm.vergissmeinnicht.view.DataViewHolder;

/**
 * Created by Dennis Jonietz on 5/16/15.
 */
public class SeniorAppointmentsViewAdapter extends RecyclerView.Adapter implements View.OnTouchListener {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;

    private Context mContext;
    private ArrayList<AppointmentData> mEventList;
    private RecyclerView mRecyclerView;
    private float mCurrentTextsize;

    public SeniorAppointmentsViewAdapter(Activity activity, ArrayList<AppointmentData> eventList,
                                         RecyclerView recyclerView, float currentTextsize) {
        mContext = activity;
        mEventList = eventList;
        mRecyclerView = recyclerView;
        mCurrentTextsize = currentTextsize;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = inflater.inflate(R.layout.layout_event_listview_heading, parent, false);
        } else {
            view = inflater.inflate(R.layout.layout_event_listview_body, parent, false);
            view.setBackgroundColor(parent.getContext().getResources().getColor(R.color.default_red_alpha));
        }
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        final AppointmentData appointmentData = mEventList.get(pos);
        final View itemView = holder.itemView;
        final GridSLM.LayoutParams params = GridSLM.LayoutParams.from(itemView.getLayoutParams());

        // set content to ViewHolder
        if (appointmentData.isHeader()) {
            // format date, e.g. 'Monday, 26.09.1990'
            DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, dd.MM.yyyy");
            ((DataViewHolder) holder).eventNameTxt.setText(appointmentData.getDateTime().toString(fmt));
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;

        } else {
            ((DataViewHolder) holder).eventNameTxt.setText(appointmentData.getInfo());
            ((DataViewHolder) holder).eventInfoTxt.setText(String.valueOf(
                    StaticMethods.makeTwoDigits(appointmentData.getDateTime().getHourOfDay()) + ":" +
                            StaticMethods.makeTwoDigits(appointmentData.getDateTime().getMinuteOfHour())));

            // set ClickListener on the deleteBtn
            ((DataViewHolder) holder).editDataBtn.setOnTouchListener(this);
            ((DataViewHolder) holder).deleteDataBtn.setOnTouchListener(this);

            // set current textsize
            ((DataViewHolder) holder).eventNameTxt.setTextSize(mCurrentTextsize);
            ((DataViewHolder) holder).eventInfoTxt.setTextSize(mCurrentTextsize);
            ((DataViewHolder) holder).editDataBtn.setTextSize(mCurrentTextsize);
            ((DataViewHolder) holder).deleteDataBtn.setTextSize(mCurrentTextsize);
        }
        ((DataViewHolder) holder).eventNameTxt.setTextSize(mCurrentTextsize+1);

        itemView.setOnTouchListener(this);

        params.setSlm(LinearSLM.ID );
        //params.setColumnWidth(8);
        //params.setNumColumns(3);
        params.setFirstPosition(appointmentData.getSectionFirstPosition());
        itemView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return (null != mEventList ? mEventList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return mEventList.get(position).isHeader() ?
                VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public boolean onTouch(View v, MotionEvent evt) {
        int action = evt.getActionMasked();

        if (v.getId() == R.id.event_data_container) {

        } else {
            AppointmentData clickedEvent = mEventList.get(
                    mRecyclerView.getChildAdapterPosition((View) v.getParent().getParent()));

            if (action == MotionEvent.ACTION_DOWN) {
                v.setScaleX(StaticValues.SCALE_PRESSED);
                v.setScaleY(StaticValues.SCALE_PRESSED);

            } else if (action == MotionEvent.ACTION_UP) {
                v.setScaleX(StaticValues.SCALE_RELEASED);
                v.setScaleY(StaticValues.SCALE_RELEASED);

                switch (v.getId()) {
                    case R.id.btn_edit_data:
                        StaticValues.updateFlag = true;
                        ((DataHolderClickInterface) mContext).updateBtnClicked(clickedEvent);
                        break;

                    case R.id.btn_delete_data:
                        ((DataHolderClickInterface) mContext).deleteBtnClicked(clickedEvent);
                        break;
                }
            }
        }
        return true;
    }

}
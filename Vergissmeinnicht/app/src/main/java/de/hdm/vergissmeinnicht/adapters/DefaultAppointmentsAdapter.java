package de.hdm.vergissmeinnicht.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.fragments.DefaultAppointmentsFragment;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.model.AppointmentData;
import de.hdm.vergissmeinnicht.view.DataViewHolder;

/**
 * Created by root on 27.06.15.
 */
public class DefaultAppointmentsAdapter extends RecyclerView.Adapter implements View.OnLongClickListener{

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;

    private DefaultAppointmentsFragment mFragment;
    private ArrayList<AppointmentData> mEventList;
    private RecyclerView mRecyclerView;
    private float mCurrentTextsize;

    public DefaultAppointmentsAdapter(DefaultAppointmentsFragment fragment, ArrayList<AppointmentData> eventList, RecyclerView recyclerView) {
        mFragment = fragment;
        mEventList = eventList;
        mRecyclerView = recyclerView;
        mCurrentTextsize = ((CustomApplication) fragment.getActivity().getApplication()).getCurrentTextsize() - 10;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
   public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
        final AppointmentData appointmentData = mEventList.get(pos);
        final View itemView = holder.itemView;
        final GridSLM.LayoutParams params = GridSLM.LayoutParams.from(itemView.getLayoutParams());

        // set content to ViewHolder
        if (appointmentData.isHeader()) {
            // format date, e.g. 'Monday, 26.09.1990'
            DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, dd.MM.yyyy");
            ((DataViewHolder) holder).eventNameTxt.setText(
                    appointmentData.getDateTime().toString(fmt));
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;

        } else {
            ((DataViewHolder) holder).eventNameTxt.setText(appointmentData.getInfo());
            ((DataViewHolder) holder).eventInfoTxt.setText(String.valueOf(
                    StaticMethods.makeTwoDigits(appointmentData.getDateTime().getHourOfDay()) + ":" +
                            StaticMethods.makeTwoDigits(appointmentData.getDateTime().getMinuteOfHour())));

            // set current textsize
            ((DataViewHolder) holder).eventInfoTxt.setTextSize(mCurrentTextsize);

            // listener not needed on headings
            itemView.setOnLongClickListener(this);
        }
        ((DataViewHolder) holder).eventNameTxt.setTextSize(mCurrentTextsize+5);

        params.setSlm(appointmentData.isHeader() ? LinearSLM.ID : GridSLM.ID );
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
    public boolean onLongClick(View v) {
        mFragment.showUpdateDeleteDialog(
                mRecyclerView.getChildAdapterPosition(v));
        return true;
    }

}

package de.hdm.vergissmeinnicht.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;

import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.android.CustomApplication;
import de.hdm.vergissmeinnicht.fragments.DefaultMedicineFragment;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.model.MedicineData;
import de.hdm.vergissmeinnicht.view.DataViewHolder;

/**
 * Created by Dennis Jonietz on 5/16/15.
 */
public class DefaultMedicineViewAdapter extends RecyclerView.Adapter implements View.OnLongClickListener {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;

    private DefaultMedicineFragment mFragment;
    private ArrayList<MedicineData> mMedicineList;
    private RecyclerView mRecyclerView;
    private float mCurrentTextsize;

    public DefaultMedicineViewAdapter(DefaultMedicineFragment fragment, ArrayList<MedicineData> medicineList, RecyclerView recyclerView) {
        mFragment = fragment;
        mMedicineList = medicineList;
        mRecyclerView = recyclerView;
        mCurrentTextsize = ((CustomApplication) fragment.getActivity().getApplication()).getCurrentTextsize() - 10;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == VIEW_TYPE_HEADER) {
            view = inflater.inflate(R.layout.layout_event_listview_heading, parent, false);
        } else {
            view = inflater.inflate(R.layout.layout_event_listview_body, parent, false);
            view.setBackgroundColor(parent.getContext().getResources().getColor(R.color.default_yellow_alpha));
        }
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        final MedicineData medicineData = mMedicineList.get(pos);
        final View itemView = holder.itemView;
        final GridSLM.LayoutParams params = GridSLM.LayoutParams.from(itemView.getLayoutParams());

        // set content to ViewHolder
        if (medicineData.isHeader()) {
            ((DataViewHolder) holder).eventNameTxt.setText(translateInterval(medicineData.getInterval()));

        } else {
            ((DataViewHolder) holder).eventNameTxt.setText(medicineData.getName());
            ((DataViewHolder) holder).eventInfoTxt.setText("Information: " + medicineData.getInfo());

            // set current textsize
            ((DataViewHolder) holder).eventNameTxt.setText(medicineData.getName());
            ((DataViewHolder) holder).eventRepeatTxt.setTextSize(mCurrentTextsize + 1);
            ((DataViewHolder) holder).eventInfoTxt.setTextSize(mCurrentTextsize);

            // listener not needed on headings
            itemView.setOnLongClickListener(this);
        }
        ((DataViewHolder) holder).eventNameTxt.setTextSize(mCurrentTextsize+5);

        params.setSlm(LinearSLM.ID);
        params.setFirstPosition(medicineData.getSectionFirstPosition());
        itemView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return (null != mMedicineList ? mMedicineList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return mMedicineList.get(position).isHeader() ?
                VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public boolean onLongClick(View v) {
        mFragment.showUpdateDeleteDialog(
                mRecyclerView.getChildAdapterPosition(v));
        return true;
    }

    private String translateInterval(String interval) {
        String result = "";

        if (interval.equalsIgnoreCase("daily")) {
            result = mFragment.getActivity().getResources().getString(R.string.medicine_add_data_interval_daily);

        } else if (interval.equalsIgnoreCase("weekly")) {
            result = mFragment.getActivity().getResources().getString(R.string.medicine_add_data_interval_weekly);

        } else if (interval.equalsIgnoreCase("monthly")) {
            result = mFragment.getActivity().getResources().getString(R.string.medicine_add_data_interval_monthly);
        }
        return result;
    }
}
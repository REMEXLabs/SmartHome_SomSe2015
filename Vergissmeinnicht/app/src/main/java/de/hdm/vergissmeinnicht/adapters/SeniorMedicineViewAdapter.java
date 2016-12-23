package de.hdm.vergissmeinnicht.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;

import de.hdm.vergissmeinnicht.R;
import de.hdm.vergissmeinnicht.helpers.StaticMethods;
import de.hdm.vergissmeinnicht.helpers.StaticValues;
import de.hdm.vergissmeinnicht.interfaces.DataHolderClickInterface;
import de.hdm.vergissmeinnicht.model.MedicineData;
import de.hdm.vergissmeinnicht.view.DataViewHolder;

/**
 * Created by Dennis Jonietz on 5/16/15.
 */
public class SeniorMedicineViewAdapter extends RecyclerView.Adapter implements View.OnTouchListener {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;

    private Context mContext;
    private ArrayList<MedicineData> mMedicineList;
    private RecyclerView mRecyclerView;
    private float mCurrentTextsize;

    public SeniorMedicineViewAdapter(Context context, ArrayList<MedicineData> medicineList,
                                     RecyclerView recyclerView, float currentTextsize) {
        mContext = context;
        mMedicineList = medicineList;
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
            ((DataViewHolder) holder).editDataBtn.setOnTouchListener(this);
            ((DataViewHolder) holder).deleteDataBtn.setOnTouchListener(this);

            // set current textsize
            ((DataViewHolder) holder).eventRepeatTxt.setTextSize(mCurrentTextsize);
            ((DataViewHolder) holder).eventInfoTxt.setTextSize(mCurrentTextsize);
            ((DataViewHolder) holder).editDataBtn.setTextSize(mCurrentTextsize);
            ((DataViewHolder) holder).deleteDataBtn.setTextSize(mCurrentTextsize);

            // TouchListener is not needed on headings
            itemView.setOnTouchListener(this);
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
    public boolean onTouch(View v, MotionEvent evt) {
        int action = evt.getActionMasked();

        if (v.getId() == R.id.event_data_container) {

        } else {
            MedicineData clickedMedicine = mMedicineList.get(
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
                        ((DataHolderClickInterface) mContext).updateBtnClicked(clickedMedicine);
                        break;

                    case R.id.btn_delete_data:
                        ((DataHolderClickInterface) mContext).deleteBtnClicked(clickedMedicine);
                        break;
                }
            }
        }
        return true;
    }

    private String translateInterval(String interval) {
        String result = "";

        if (interval.equalsIgnoreCase("daily")) {
            result = mContext.getResources().getString(R.string.medicine_add_data_interval_daily);

        } else if (interval.equalsIgnoreCase("weekly")) {
            result = mContext.getResources().getString(R.string.medicine_add_data_interval_weekly);

        } else if (interval.equalsIgnoreCase("monthly")) {
            result = mContext.getResources().getString(R.string.medicine_add_data_interval_monthly);
        }
        return result;
    }

}
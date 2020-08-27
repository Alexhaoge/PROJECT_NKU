package com.kongx.nkuassistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExamFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Connector.Callback{
    private View myView = null;
    private ListView mExamList;
    private SwipeRefreshLayout mRefresh;
    private TextView mNoText;
    private Pattern pattern;
    private Matcher matcher;
    private AppCompatActivity m_activity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_exam, container, false);
        mExamList = (ListView) myView.findViewById(R.id.exam_list);
        mRefresh = (SwipeRefreshLayout) myView.findViewById(R.id.exam_refresh);
        mRefresh.setOnRefreshListener(this);
        mNoText = (TextView) myView.findViewById(R.id.textView_noExam);
        return myView;
    }


    @Override
    public void onResume() {
        super.onResume();
        m_activity = (AppCompatActivity) getActivity();
        if(Information.examCount == -1){
            onRefresh();
        }else {
            mNoText.setVisibility(View.GONE);
            mExamList.setAdapter(new MyAdapter(m_activity));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        m_activity = null;
    }

    @Override
    public void onRefresh() {
        mRefresh.setRefreshing(true);
        Connector.getInformation(Connector.RequestType.EXAM,this,null);
    }

    private void update(){
        Information.examCount = Information.exams.size();
        storeExams();
        mRefresh.setRefreshing(false);
        mExamList.setAdapter(new MyAdapter(m_activity));
    }

    @Override
    public void onConnectorComplete(Connector.RequestType requestType, Object result) {
        if(requestType == Connector.RequestType.EXAM){
            if(Information.exams.size() == 0){
                mExamList.setVisibility(View.INVISIBLE);
                mNoText.setText(getString(R.string.no_exam_info));
            }else{
                update();
            }
        }
    }


    boolean storeExams() {
        SharedPreferences settings = m_activity.getSharedPreferences(Information.EXAM_PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("examCount", Information.examCount);
        for (int i = 0; i < Information.examCount; i++) {
            editor.putString("name" + i, Information.exams.get(i).getCourseName());
            editor.putString("time" + i, Information.exams.get(i).getTimePeriod());
            editor.putString("classRoom" + i, Information.exams.get(i).getClassRoom());
            editor.putString("seat" + i, Information.exams.get(i).getSeatNum());
            editor.putString("date" + i, Information.exams.get(i).getDateString());
        }
        return editor.commit();
    }

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return Information.examCount;
        }

        @Override
        public Object getItem(int position) {
            return Information.exams.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.item_exam_list, null);
                holder = new ViewHolder();
                holder.date = convertView.findViewById(R.id.examDateView);
                holder.time = convertView.findViewById(R.id.examPeriodView);
                holder.name = convertView.findViewById(R.id.examNameView);
                holder.classroom = convertView.findViewById(R.id.examLocationView);
                holder.seat = convertView.findViewById(R.id.examSeatView);
                convertView.setTag(holder);//绑定ViewHolder对象
            }
            else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            holder.date.setText(Information.exams.get(position).getDateString());
            holder.time.setText(Information.exams.get(position).getTimePeriod());
            holder.name.setText(Information.exams.get(position).getCourseName());
            holder.classroom.setText(Information.exams.get(position).getClassRoom());
            holder.seat.setText("座号" + Information.exams.get(position).getSeatNum() + "号");

            return convertView;
        }

    }
    class ViewHolder{
        TextView date;
        TextView time;
        TextView name;
        TextView classroom;
        TextView seat;
    }
}

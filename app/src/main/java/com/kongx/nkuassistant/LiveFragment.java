package com.kongx.nkuassistant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;


public class  LiveFragment extends Fragment implements Connector.Callback{
    private static LivePage1.CCTVAdapter c_adapter;
    private static LivePage2.LocalAdapter l_adapter;
    private TabLayout tabLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager_tv);
        viewPager.setAdapter(new ScreenSlidePagerAdapter(((AppCompatActivity)getActivity()).getSupportFragmentManager()));

        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager, true);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ViewGroup vg = ((ViewGroup) tabLayout.getChildAt(0));
                vg.animate()
                        .setStartDelay(600)
                        .setDuration(400)
                        .setInterpolator(new LinearOutSlowInInterpolator())
                        .start();

                ViewGroup vgTab = (ViewGroup) vg.getChildAt(tab.getPosition());
                vgTab.setScaleX(0.8f);
                vgTab.setScaleY(0.8f);
                vgTab.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setInterpolator(new FastOutSlowInInterpolator())
                        .setDuration(450)
                        .start();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });

        Connector.getInformation(Connector.RequestType.TV_CHANNEL,this,null);
        return view;
    }

    @Override
    public void onConnectorComplete(Connector.RequestType requestType, Object result) {
        if(result.getClass() == Boolean.class && (Boolean)result){
            l_adapter.notifyDataSetChanged();
            c_adapter.notifyDataSetChanged();
//            if(tabLayout.getSelectedTabPosition() == 1){
//                l_adapter.notifyDataSetChanged();
//            }else {
//                c_adapter.notifyDataSetChanged();
//            }
        }
    }

    public static class TVChannel{
        String name;
        boolean isHDAvailable;
        boolean isSDAvailable;
        String HDUrl;
        String SDUrl;
        TVChannel(){isHDAvailable = isSDAvailable = false;}
    }

    public static class LivePage1 extends androidx.fragment.app.Fragment {
        public LivePage1(){}
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.list_tabpage_fragement, container, false);
            ListView list = (ListView)view.findViewById(R.id.simple_list);
            c_adapter = new CCTVAdapter(getActivity());
            list.setAdapter(c_adapter);
            return view;
        }

        public class CCTVAdapter extends BaseAdapter {
            private LayoutInflater mInflater;
            public CCTVAdapter(Context context) {
                this.mInflater = LayoutInflater.from(context);
            }
            @Override
            public int getCount() {
                return Information.CCTVChannels.size();
            }
            @Override
            public Object getItem(int position) {
                return Information.CCTVChannels.get(position);
            }
            @Override
            public long getItemId(int position) {
                return position;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                convertView = mInflater.inflate(R.layout.item_livetv_list,null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.textView_livetv_channelname);
                holder.button_sd = (Button) convertView.findViewById(R.id.button_livetv_sd);
                holder.button_hd = (Button) convertView.findViewById(R.id.button_livetv_hd);
                TVChannel tmpChannel = Information.CCTVChannels.get(position);
                holder.name.setText(tmpChannel.name);
                if(tmpChannel.isSDAvailable){
                    holder.button_sd.setBackground(getActivity().getResources().getDrawable(R.drawable.themecolorrect));
                    holder.button_sd.setContentDescription(tmpChannel.SDUrl);
                    holder.button_sd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(),VideoActivity.class);
                            intent.putExtra("url","http://tv6.byr.cn/hls/"+view.getContentDescription());
                            startActivity(intent);
                        }
                    });
                }
                if(tmpChannel.isHDAvailable){
                    holder.button_hd.setBackground(getActivity().getResources().getDrawable(R.drawable.themecolorrect));
                    holder.button_hd.setContentDescription(tmpChannel.HDUrl);
                    holder.button_hd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(),VideoActivity.class);
                            intent.putExtra("url","http://tv6.byr.cn/hls/"+view.getContentDescription());
                            startActivity(intent);
                        }
                    });
                }
                return convertView;
            }
        }

        class ViewHolder {
            TextView name;
            Button button_sd;
            Button button_hd;
        }
    }

    public static class LivePage2 extends androidx.fragment.app.Fragment {
        public LivePage2(){}
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.list_tabpage_fragement, container, false);
            ListView list = (ListView)view.findViewById(R.id.simple_list);
            l_adapter = new LocalAdapter(getActivity());
            list.setAdapter(l_adapter);
            return view;
        }

        public class LocalAdapter extends BaseAdapter {
            private LayoutInflater mInflater;
            public LocalAdapter(Context context) {
                this.mInflater = LayoutInflater.from(context);
            }
            @Override
            public int getCount() {
                return Information.LocalChannels.size();
            }
            @Override
            public Object getItem(int position) {
                return Information.LocalChannels.get(position);
            }
            @Override
            public long getItemId(int position) {
                return position;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                convertView = mInflater.inflate(R.layout.item_livetv_list,null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.textView_livetv_channelname);
                holder.button_sd = (Button) convertView.findViewById(R.id.button_livetv_sd);
                holder.button_hd = (Button) convertView.findViewById(R.id.button_livetv_hd);
                TVChannel tmpChannel = Information.LocalChannels.get(position);
                holder.name.setText(tmpChannel.name);
                if(tmpChannel.isSDAvailable){
                    holder.button_sd.setBackground(getActivity().getResources().getDrawable(R.drawable.themecolorrect));
                    holder.button_sd.setContentDescription(tmpChannel.SDUrl);
                    holder.button_sd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(),VideoActivity.class);
                            intent.putExtra("url","http://tv6.byr.cn/hls/"+view.getContentDescription());
                            startActivity(intent);
                        }
                    });
                }
                if(tmpChannel.isHDAvailable){
                    holder.button_hd.setBackground(getActivity().getResources().getDrawable(R.drawable.themecolorrect));
                    holder.button_hd.setContentDescription(tmpChannel.HDUrl);
                    holder.button_hd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(),VideoActivity.class);
                            intent.putExtra("url","http://tv6.byr.cn/hls/"+view.getContentDescription());
                            startActivity(intent);
                        }
                    });
                }
                return convertView;
            }
        }

        class ViewHolder {
            TextView name;
            Button button_sd;
            Button button_hd;
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }        @Override
        public androidx.fragment.app.Fragment getItem(int position) {
            if(position==1) return new LivePage2();
            return new LivePage1();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==1) return getString(R.string.tab_locals);
            return getString(R.string.tab_CCTV);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}

package com.qsc.linechartfragment;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qsc.barchartfragment.HeartrateDB;
import com.qsc.main.R;
import com.qsc.swipelist.SwipeMenu;
import com.qsc.swipelist.SwipeMenuCreator;
import com.qsc.swipelist.SwipeMenuItem;
import com.qsc.swipelist.SwipeMenuListView;
import com.qsc.swipelist.SwipeMenuListView.OnMenuItemClickListener;
import com.qsc.swipelist.SwipeMenuListView.OnSwipeListener;

public class LinechartFragment extends Fragment {
	
	private Context context;
	private View view;
	private GraphicalView chart;
	private SwipeMenuListView mlist;
	private ListData[] listdata;
	private int [] rate;
	private String[] time;
	
	private BaseAdapter madapter = new BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = View.inflate(getActivity(),
						R.layout.item_list_app, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			ListData item = getItem(position);
			holder.iv_icon.setImageResource(item.icon);
			holder.tv_name.setText(String.valueOf(item.heartrate));
			holder.tvtime.setText(item.time);
			return convertView;
		}

		class ViewHolder {
			ImageView iv_icon;
			TextView tv_name;
			TextView tvtime;

			public ViewHolder(View view) {
				iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				tv_name = (TextView) view.findViewById(R.id.tv_name);
				tvtime = (TextView) view.findViewById(R.id.tvtime);
				view.setTag(this);
			}
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public ListData getItem(int position) {
			// TODO Auto-generated method stub
			return listdata[position];
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listdata.length;
		}
	};
	private int dp2px(int dp) {//dpת��Ϊpx
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	private LinearLayout layout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_mydata, container, false);
		context = view.getContext();
		layout = (LinearLayout) view.findViewById(R.id.chart);

		readdb();           //��ѯ���ݿ�,��ȡ����
		givedata();         //��ListData��������ݣ�����ӵ�Adapter
		SwipeMenuCreator creator = new SwipeMenuCreator() {//��������������

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(//��������һ���˵���ť
						getActivity());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		mlist.setMenuCreator(creator);//ΪlistView��Ӳ˵���ť������
		
		
		// set SwipeListener�������¼�����
		mlist.setOnSwipeListener(new OnSwipeListener() {
			
			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}
			
			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});
		
		
		PointStyle[] styles = new PointStyle[]{PointStyle.DIAMOND};
		int[] colors = new int[] { R.color.anqing};
		renderer = buildRenderer(colors,styles,true);
		setChartSettings(renderer, "My HeartRate", "", "��/��", 0.5,12.5, 0, 100, Color.GRAY, Color.LTGRAY);
		
		setchartdata();           //���ͼ������
		chart = ChartFactory.getLineChartView(context, dataset, renderer);
		layout.addView(chart);
		
		mlist.setOnMenuItemClickListener(new OnMenuItemClickListener() {//Ϊ�˵�ѡ������¼�����
			@Override//�����˵���ť
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				ListData item = listdata[position];
				switch (index) {
				case 0:
					// delete
					delete(item);
					System.out.println("position-->"+position);
					readdb();
					givedata();
					handler.sendEmptyMessage(EMS);
					break;
				case 1:
					break;
				}
				return false;
			}
		});
		return view;
	}
	private static final int EMS = 1;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg){
			switch (msg.what) {
			case EMS:
//				setchartdata();
//				System.out.println("series-length-->"+series.getItemCount());
//				System.out.println("dataset-length-->"+dataset.getSeriesCount());
//				for (int i = 0; i < series.getItemCount(); i++) {
//					System.out.println(series.getX(i)+","+series.getY(i));
//				}
//				dataset.clear();
//				System.out.println("dataset-length-->"+dataset.getSeriesCount());
//				dataset.addSeries(series);
//				System.out.println("dataset-length-->"+dataset.getSeriesCount());
//				chart.invalidate();
				
				madapter.notifyDataSetChanged();//֪ͨ����list
				break;

			default:
				break;
			}
		}
	};
	
//	private void updatechart(){
//		dataset.removeSeries(series);
//		series.clear();
//	}
	private HeartrateDB hd;
	private void readdb(){
		
		int i = 0,k= 0;
		hd = new HeartrateDB(context);
		SQLiteDatabase dbread = hd.getReadableDatabase();
        Cursor c = dbread.query("myheartrate", null, null, null, null, null, null);
        while (c.moveToNext()) {
        	k++;			
		}
        rate = new int[k];
        time = new String[k];
        SQLiteDatabase dbread2 = hd.getReadableDatabase();
        Cursor c2 = dbread2.query("myheartrate", null, null, null, null, null, null);
        while (c2.moveToNext()) {
        	int id = c2.getInt(c2.getColumnIndex("id"));
			int heartrate = c2.getInt(c.getColumnIndex("heartrate"));
			String mtime = c2.getString(c.getColumnIndex("time"));
			System.out.println(mtime);
			rate[i] = heartrate;
			time[i] = mtime;
			i++;
			System.out.println(String.format("id=%d,height=%d",id,heartrate));
			
		}
	}
	
	private void givedata() {
		int vl = rate.length;
		listdata = new ListData[vl];
		for (int i = 0; i < vl; i++) {
			listdata[i] = new ListData(R.drawable.aa1_n, rate[vl-i-1],time[vl-i-1]);
		}
		mlist = (SwipeMenuListView) view.findViewById(R.id.list);
		mlist.setAdapter(madapter);
	}

	private void delete(ListData item){//ɾ������
		hd = new HeartrateDB(context);
		SQLiteDatabase dbdelete = hd.getWritableDatabase();
		dbdelete.delete("myheartrate", "time=?", new String[]{item.time});
	}
	
	private String[] titles = new String[] { "" };              //titles
	private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer renderer;
	private XYSeries series = new XYSeries(titles[0]); // ����ÿ���ߵ����ƴ���(�˴�ֻ��һ����)
	private void setchartdata() {
		
		List<double[]> x = new ArrayList<double[]>();       //x
		double[] ex = new double[rate.length];
		for (int i = 0; i < ex.length; i++){
			ex[i]= i+1; 
		}
		x.add(ex);
		List<double[]> values = new ArrayList<double[]>();  //values
		double[] data = new double[rate.length]; 
		for (int i = 0; i < data.length; i++) {
			data[i] = (double)rate[i];
		}
		values.add(data);
		dataset.removeSeries(series);
		series.clear();
		System.out.println("series-length-->"+series.getItemCount());
		System.out.println("dataset-length-->"+dataset.getSeriesCount());
		dataset = buildDatset(titles,x,values);
		
		
	}
	private XYMultipleSeriesDataset buildDatset(String[] titles, List<double[]> xValues,
			List<double[]> yValues) {
		// �������ݵĴ��
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		int length = titles.length; // �м�����
		for (int i = 0; i < length; i++) {
			//XYseries����,�����ṩ���Ƶĵ㼯�ϵ�����
			double[] xV = (double[]) xValues.get(i); // ��ȡ��i���ߵ�����
			double[] yV = (double[]) yValues.get(i);
			int seriesLength = xV.length; // �м�����

			for (int k = 0; k < seriesLength; k++) // ÿ�������м�����
			{
				series.add(xV[k], yV[k]);
			}

			dataset.addSeries(series);
		}

		return dataset;
	}
	
	private XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles, boolean fill) {
		//����ͼ�ĸ�ʽ��������ɫ��ֵ�ķ�Χ������ߵ���״�ȵ� 
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(); 
        int length = colors.length; 
        for (int i = 0; i < length; i++) 
        { 
            XYSeriesRenderer r = new XYSeriesRenderer(); 
            r.setColor(colors[i]); 
            r.setPointStyle(styles[i]); 
            r.setFillPoints(fill); 
            renderer.addSeriesRenderer(r); 
        } 
        return renderer; 
	}
	
	protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
		      String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
		      int labelsColor) {
		    renderer.setChartTitle(title);
		    renderer.setXTitle(xTitle);
		    renderer.setYTitle(yTitle);
		    renderer.setXAxisMin(xMin);
		    renderer.setXAxisMax(xMax);
		    renderer.setYAxisMin(yMin);
		    renderer.setYAxisMax(yMax);
		    renderer.setAxesColor(axesColor);
		    renderer.setLabelsColor(labelsColor);
		    renderer.setXLabels(12);//X��Ľ���������  
			renderer.setYLabels(10);//Y��Ľ��������� 
			renderer.setShowGrid(true);// �Ƿ���ʾ����
			renderer.setXLabelsAlign(Align.LEFT);//�̶�����X���������������� 
			renderer.setYLabelsAlign(Align.LEFT);//Y����Y��������������� 
			renderer.setPanEnabled(true, false); //���������϶�,�������������϶�.
			renderer.setZoomEnabled(false);
			renderer.setZoomButtonsVisible(false);// �������Ű�ť����ʾ״̬
			// ����ͼ����������
	        // -->start
	        renderer.setAxisTitleTextSize(16);// ������������ֵĴ�С
	        renderer.setApplyBackgroundColor(true);//Ӧ�ñ�����ɫ
	        renderer.setBackgroundColor(getResources().getColor(R.color.lightcoral));//���ñ�����ɫ
	        renderer.setChartTitleTextSize(25);// ��������ͼ��������ֵĴ�С
	        renderer.setLabelsTextSize(15);// ������̶����ֵĴ�С
	        renderer.setLegendTextSize(15);// ����ͼ�����ִ�С
	        renderer.setPointSize(5f);// ���õ�Ĵ�С(ͼ����ʾ�ĵ�Ĵ�С��ͼ���е�Ĵ�С���ᱻ����)
	        renderer.setMargins(new int[] { 30, 35, 0, 10 });// ����ͼ�����߿�(��/��/��/��)
	        // -->end
		    renderer.setMarginsColor(getResources().getColor(R.color.lightcoral));//����ͼ���ı���ɫ
	}
	
	
}

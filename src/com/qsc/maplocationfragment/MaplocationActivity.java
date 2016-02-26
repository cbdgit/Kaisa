package com.qsc.maplocationfragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.qsc.main.R;

/**
 * ��demo����չʾ��ν�϶�λSDKʵ�ֶ�λ,��ʹ��MyLocationOverlay���ƶ�λλ��,չʾ���ʹ���Զ���ͼ��,��¼�켣,��¼���
 * 
 */
@SuppressLint("SdCardPath")
public class MaplocationActivity extends Activity {

	// ��λ���
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	// UI���
	MapView mMapView;
	BaiduMap mBaiduMap;

	boolean isFirstLoc = true;// �Ƿ��״ζ�λ
	boolean isRecording = false;//�Ƿ��ڼ�¼ 
	boolean isFirstRecord = true;//�Ƿ��״μ�¼
	//��λģʽ�����õ�ǰλ��
	private LatLng local;
	//��¼ģʽ�����õ�ǰ��λλ�ú���һ�ζ�λλ��
	private LatLng nowlocal=null; //��ǰ
	private LatLng lastlocal=null; //��һ��
	private double distance = 0.0; //�ܾ���
	//��ͼ·��
	private File file = new File("/mnt/sdcard/recordShot.png");
	
	//������ʾ·��View
	private TextView distanceView;
	private ImageView map_topbar;
	//���ü�¼��ť
	private ImageView btnStartRecord,btnStopRecord,btnClear,btnScreenShot,btnScreenShotShare;
	//���ö�λ���ʱ��(Ms)
	private static final int UPDATE_TIME = 3000;
    //����Handler��Ϣ
	private static final int DISTANCE_UNEQUAL_ZERO = 1;
	private static final int DISTANCE_EQUAL_ZERO = 0;
	@SuppressLint("HandlerLeak")
	private Handler Handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DISTANCE_UNEQUAL_ZERO:
				distanceView.setText("������"+(int)distance+"��");
				break;
			case DISTANCE_EQUAL_ZERO:
				distanceView.setText("������0��");
			default:
				break;
			}
		};
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maplocation);
		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// ������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		// ��λ��ʼ��
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��GPS
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(UPDATE_TIME); //��λʱ����
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		mMapView.refreshDrawableState();  //ˢ��
		
		distanceView = (TextView) findViewById(R.id.distanceView);
		distanceView.setVisibility(View.GONE);
		distanceView.setTextColor(Color.GREEN);
		distanceView.setTextSize(20);
		
		map_topbar = (ImageView) findViewById(R.id.map_topbar);
		btnStartRecord  = (ImageView) findViewById(R.id.btnStartRecord);
		btnStopRecord   = (ImageView) findViewById(R.id.btnStopRecord);
		btnClear        = (ImageView) findViewById(R.id.btnClear);               
		btnScreenShot   = (ImageView) findViewById(R.id.btnScreenShot);
		btnScreenShotShare = (ImageView) findViewById(R.id.btnScreenShotShare);
		btnStopRecord.setVisibility(View.GONE);
		btnClear.setVisibility(View.GONE);
		btnScreenShot.setVisibility(View.GONE);
		btnScreenShotShare.setVisibility(View.GONE);
		OnClickListener onClickListener =new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (v.equals(btnStartRecord)) {
					isRecording = true;
					distanceView.setVisibility(View.VISIBLE);
					btnStartRecord.setVisibility(View.GONE);
					btnStopRecord.setVisibility(View.VISIBLE);
					btnClear.setVisibility(View.GONE);
					btnScreenShot.setVisibility(View.GONE);
					btnScreenShotShare.setVisibility(View.GONE);
				}else if (v.equals(btnStopRecord)) {
					isRecording = false;
					isFirstRecord = true;
					btnStopRecord.setVisibility(View.GONE);
					btnStartRecord.setVisibility(View.VISIBLE);
					btnClear.setVisibility(View.VISIBLE);
					btnScreenShot.setVisibility(View.VISIBLE);
					btnScreenShotShare.setVisibility(View.GONE);
				}else if (v.equals(btnClear)) {
					clearRecord();
					isRecording = false;
					isFirstRecord = true;
					distanceView.setVisibility(View.GONE);
					btnClear.setVisibility(View.GONE);
					btnScreenShot.setVisibility(View.GONE);
					btnScreenShotShare.setVisibility(View.GONE);
					distance = 0.0;
					Handler.sendEmptyMessage(DISTANCE_EQUAL_ZERO);
				}else if (v.equals(btnScreenShot)) {
					btnScreenShot.setVisibility(View.GONE);
					btnScreenShotShare.setVisibility(View.VISIBLE);
					//��ȡ�켣ͼ�����浽SD��
					mBaiduMap.snapshot(new SnapshotReadyCallback() {
						@SuppressLint("SdCardPath")
						@Override
						public void onSnapshotReady(Bitmap snapshot) {
							
							FileOutputStream out;
							try {
								out = new FileOutputStream(file);
								if (snapshot.compress(Bitmap.CompressFormat.PNG, 100, out)) {
									out.flush();
									out.close();
								}
								Toast.makeText(MaplocationActivity.this, 
										"��ͼ�ɹ���ͼƬ�����ڣ�"+file.toString(),
										Toast.LENGTH_SHORT).show();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}catch (IOException e) {
								e.printStackTrace();
							}
							
						}
					});	
					Toast.makeText(MaplocationActivity.this,
							"���ڽ�ȡͼƬ...", 
							Toast.LENGTH_SHORT).show();
				}else if (v.equals(btnScreenShotShare)) {
					btnScreenShotShare.setVisibility(View.GONE);
					btnScreenShot.setVisibility(View.VISIBLE);
					//�����ͼ
					shareMsg(file.toString() );
				}else if (v.equals(map_topbar)) {
					Intent intent=new Intent();
					intent.setClass(MaplocationActivity.this, com.qsc.main.MainActivity.class);
					startActivity(intent);
					finish();
				}				
			}
		};
		btnStartRecord.setOnClickListener(onClickListener);
		btnStopRecord.setOnClickListener(onClickListener);
		btnClear.setOnClickListener(onClickListener);
		btnScreenShot.setOnClickListener(onClickListener);
		btnScreenShotShare.setOnClickListener(onClickListener);
		map_topbar.setOnClickListener(onClickListener);
	  
	}
	
	/**  
	  * ������  
	  * @param imgPath ͼƬ·����������ͼƬ��null  
	  */  
	 public void shareMsg(String imgPath) {  
		 Intent intent = new Intent(Intent.ACTION_SEND);  
		 if (imgPath == null || imgPath.equals("")) {  
			 intent.setType("text/plain"); // ���ı�  
		 } else {  
			 File f = new File(imgPath);  
			 if (f != null && f.exists() && f.isFile()) {  
				 intent.setType("image/*");  
				 Uri u = Uri.fromFile(f);  
				 intent.putExtra(Intent.EXTRA_STREAM, u); 
			 }  
		 }  
		 intent.putExtra(Intent.EXTRA_TEXT, "���Ѿ�����"+distance+"��");
		 startActivity(Intent.createChooser(intent, "����"));
	 } 
	
	 /**
	  * ����켣��¼
	  */
	public void clearRecord() {
		// �������ͼ��
		mBaiduMap.clear();
	}
	
	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {
		
		//private LocationMode mCurrentMode = LocationMode.FOLLOWING;
		//private BitmapDescriptor mCurrentMarker=BitmapDescriptorFactory.fromResource(R.drawable.loc1_m);;

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ٴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;
			//��һ�ζ�λʱ
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng firstlocal = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(firstlocal);
				mBaiduMap.animateMapStatus(u); //����ǰλ����ʾ����ͼ����
				
			}
			//�ǵ�һ�ζ�λ
			
			MyLocationData locData = new MyLocationData.Builder()//�������ʾ��λ����Ȧ����accuracy��ֵΪ0���� 
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData); //���¶�λ����  
			mMapView.refreshDrawableState();
			//�Զ��嶨λͼ��
			//mBaiduMap
			//.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
			
			//��ȡ��ǰλ�õľ�γ��
			local = new LatLng(location.getLatitude(),
					location.getLongitude());
			
			//���¼�¼��ť���¼�켣���������
			if (isRecording) {
				if (isFirstRecord) {
					lastlocal = local;
					isFirstRecord = false;
				}
				//��ǰλ�ø���nowlocal
				nowlocal = local;
				//����벢����
				getRecord();
				lastlocal = nowlocal;
			}
			
		}
		
		//��¼�켣��������뺯��
		private void getRecord(){
			//���
			double dis = DistanceUtil.getDistance(nowlocal, lastlocal);
			//�����ܾ���
			distance +=dis;
			Handler.sendEmptyMessage(DISTANCE_UNEQUAL_ZERO);
		/*	new AlertDialog.Builder(MainActivity.this)
			      .setTitle("���Ѿ����ˣ�")
			      .setMessage(distance+"��")
			      .setPositiveButton("OK", null)
			      .show();
		*/	
			//����
			List<LatLng> points = new ArrayList<LatLng>();
			points.add(nowlocal);
			points.add(lastlocal);
			OverlayOptions polyline = new PolylineOptions().width(4).color(0xAAFF0000).points(points);
			mBaiduMap.addOverlay(polyline);
		}
		
		
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// �˳�ʱ���ٶ�λ
		mLocClient.stop();
		// �رն�λͼ��
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

}

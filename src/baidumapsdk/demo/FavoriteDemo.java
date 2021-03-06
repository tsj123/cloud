package baidumapsdk.demo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mapapi.favorite.FavoriteManager;
import com.baidu.mapapi.favorite.FavoritePoiInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.cloud.R;

/**
 * 婕旂ず濡備綍浣跨敤鏈湴鐐规敹钘忓姛鑳�
 *
 */
public class FavoriteDemo extends Activity
		implements OnMapLongClickListener, OnMarkerClickListener, OnMapClickListener {

	// 鍦板浘鐩稿叧
	private MapView mMapView;
	private BaiduMap mBaiduMap;

	// 鐣岄潰鎺т欢鐩稿叧
	private EditText locationText;
	private EditText nameText;
	private View mPop;
	private View mModify;
	EditText mdifyName;
	// 淇濆瓨鐐逛腑鐨勭偣id
	private String currentID;
	// 鐜板疄marker鐨勫浘鏍�
	BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
	List<Marker> markers = new ArrayList<Marker>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);
		// 鍒濆鍖栧湴鍥�
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMapLongClickListener(this);
		mBaiduMap.setOnMarkerClickListener(this);
		mBaiduMap.setOnMapClickListener(this);
		// 鍒濆鍖栨敹钘忓す
		FavoriteManager.getInstance().init();
		// 鍒濆鍖朥I
		initUI();
	}

	public void initUI() {
		locationText = (EditText) findViewById(R.id.pt);
		nameText = (EditText) findViewById(R.id.name);
		LayoutInflater mInflater = getLayoutInflater();
		mPop = (View) mInflater.inflate(R.layout.activity_favorite_infowindow, null, false);
	}

	/**
	 * 娣诲姞鏀惰棌鐐�
	 * 
	 * @param v
	 */
	public void saveClick(View v) {
		if (nameText.getText().toString() == null || nameText.getText().toString().equals("")) {
			Toast.makeText(FavoriteDemo.this, "鍚嶇О蹇呭～", Toast.LENGTH_LONG).show();
			return;
		}
		if (locationText.getText().toString() == null || locationText.getText().toString().equals("")) {
			Toast.makeText(FavoriteDemo.this, "鍧愭爣鐐瑰繀濉�", Toast.LENGTH_LONG).show();
			return;
		}
		FavoritePoiInfo info = new FavoritePoiInfo();
		info.poiName(nameText.getText().toString());

		LatLng pt;
		try {
			String strPt = locationText.getText().toString();
			String lat = strPt.substring(0, strPt.indexOf(","));
			String lng = strPt.substring(strPt.indexOf(",") + 1);
			pt = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
			info.pt(pt);
			if (FavoriteManager.getInstance().add(info) == 1) {
				Toast.makeText(FavoriteDemo.this, "娣诲姞鎴愬姛", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(FavoriteDemo.this, "娣诲姞澶辫触", Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(FavoriteDemo.this, "鍧愭爣瑙ｆ瀽閿欒", Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * 淇敼鏀惰棌鐐�
	 * 
	 * @param v
	 */
	public void modifyClick(View v) {
		mBaiduMap.hideInfoWindow();
		// 寮规淇敼
		LayoutInflater mInflater = getLayoutInflater();
		mModify = (LinearLayout) mInflater.inflate(R.layout.activity_favorite_alert, null);
		mdifyName = (EditText) mModify.findViewById(R.id.modifyedittext);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(mModify);
		String oldName = FavoriteManager.getInstance().getFavPoi(currentID).getPoiName();
		mdifyName.setText(oldName);
		builder.setPositiveButton("纭", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String newName = mdifyName.getText().toString();
				if (newName != null && !newName.equals("")) {
					// modify
					FavoritePoiInfo info = FavoriteManager.getInstance().getFavPoi(currentID);
					info.poiName(newName);
					if (FavoriteManager.getInstance().updateFavPoi(currentID, info)) {
						Toast.makeText(FavoriteDemo.this, "淇敼鎴愬姛", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(FavoriteDemo.this, "鍚嶇О涓嶈兘涓虹┖锛屼慨鏀瑰け璐�", Toast.LENGTH_LONG).show();
				}
				dialog.dismiss();
			}

		});

		builder.setNegativeButton("鍙栨秷", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/**
	 * 鍒犻櫎涓�涓寚瀹氱偣
	 * 
	 * @param v
	 */
	public void deleteOneClick(View v) {
		if (FavoriteManager.getInstance().deleteFavPois(currentID)) {
			Toast.makeText(FavoriteDemo.this, "鍒犻櫎鐐规垚鍔�", Toast.LENGTH_LONG).show();
			if (markers != null) {
				for (int i = 0; i < markers.size(); i++) {
					if (markers.get(i).getExtraInfo().getString("id").equals(currentID)) {
						markers.get(i).remove();
						markers.remove(i);
						mBaiduMap.hideInfoWindow();
						break;
					}
				}
			}
		} else {
			Toast.makeText(FavoriteDemo.this, "鍒犻櫎鐐瑰け璐�", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 鑾峰彇鍏ㄩ儴鏀惰棌鐐�
	 * 
	 * @param v
	 */
	public void getAllClick(View v) {
		mBaiduMap.clear();
		List<FavoritePoiInfo> list = FavoriteManager.getInstance().getAllFavPois();
		if (list == null || list.size() == 0) {
			Toast.makeText(FavoriteDemo.this, "娌℃湁鏀惰棌鐐�", Toast.LENGTH_LONG).show();
			return;
		}
		// 缁樺埗鍦ㄥ湴鍥�
		markers.clear();
		for (int i = 0; i < list.size(); i++) {
			MarkerOptions option = new MarkerOptions().icon(bdA).position(list.get(i).getPt());
			Bundle b = new Bundle();
			b.putString("id", list.get(i).getID());
			option.extraInfo(b);
			markers.add((Marker) mBaiduMap.addOverlay(option));
		}

	}

	/**
	 * 鍒犻櫎鍏ㄩ儴鐐�
	 * 
	 * @param v
	 */
	public void deleteAllClick(View v) {
		if (FavoriteManager.getInstance().clearAllFavPoi()) {
			Toast.makeText(FavoriteDemo.this, "鍏ㄩ儴鍒犻櫎鎴愬姛", Toast.LENGTH_LONG).show();
			mBaiduMap.clear();
			mBaiduMap.hideInfoWindow();
		} else {
			Toast.makeText(FavoriteDemo.this, "鍏ㄩ儴鍒犻櫎澶辫触", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPause() {
		// MapView鐨勭敓鍛藉懆鏈熶笌Activity鍚屾锛屽綋activity鎸傝捣鏃堕渶璋冪敤MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// MapView鐨勭敓鍛藉懆鏈熶笌Activity鍚屾锛屽綋activity鎭㈠鏃堕渶璋冪敤MapView.onResume()
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 閲婃斁鏀惰棌澶瑰姛鑳借祫婧�
		FavoriteManager.getInstance().destroy();
		bdA.recycle();
		// MapView鐨勭敓鍛藉懆鏈熶笌Activity鍚屾锛屽綋activity閿�姣佹椂闇�璋冪敤MapView.destroy()
		mMapView.onDestroy();
		mBaiduMap = null;
		super.onDestroy();
	}

	@Override
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub
		locationText.setText(String.valueOf(point.latitude) + "," + String.valueOf(point.longitude));
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		mBaiduMap.hideInfoWindow();
		// TODO Auto-generated method stub
		if (marker == null) {
			return false;
		}
		InfoWindow mInfoWindow = new InfoWindow(mPop, marker.getPosition(), -47);
		mBaiduMap.showInfoWindow(mInfoWindow);
		MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(marker.getPosition());
		mBaiduMap.setMapStatus(update);
		currentID = marker.getExtraInfo().getString("id");
		return true;
	}

	@Override
	public void onMapClick(LatLng point) {
		// TODO Auto-generated method stub
		mBaiduMap.hideInfoWindow();
	}

	@Override
	public boolean onMapPoiClick(MapPoi poi) {
		// TODO Auto-generated method stub
		return false;
	}

}

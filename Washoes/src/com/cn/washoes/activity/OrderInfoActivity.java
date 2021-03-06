package com.cn.washoes.activity;

import java.io.File;
import java.net.URISyntaxException;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.hongwei.BaseActivity;
import com.cn.hongwei.CarImageView;
import com.cn.hongwei.GridLayout;
import com.cn.hongwei.MyApplication;
import com.cn.hongwei.RequestWrapper;
import com.cn.hongwei.ResponseWrapper;
import com.cn.hongwei.TopTitleView;
import com.cn.washoes.R;
import com.cn.washoes.model.ComInfo;
import com.cn.washoes.model.ImgInfo;
import com.cn.washoes.model.LocInfo;
import com.cn.washoes.model.OrderAddress;
import com.cn.washoes.model.OrderInfo;
import com.cn.washoes.model.SS_Info;
import com.cn.washoes.util.Cst;
import com.cn.washoes.util.NetworkAction;
import com.ta.utdid2.android.utils.StringUtils;

/**
 * 订单详情界面
 * 
 * @author Administrator
 * 
 */
public class OrderInfoActivity extends BaseActivity {

	private TopTitleView topTitleView;
	private String oid;
	private OrderInfo orderInfo;

	private TextView textId;

	private TextView textDate;
	private TextView textPrice;
	private TextView textUserName;
	private TextView textUserType;
	private TextView textPhone;
	private TextView textAddress;

	private LinearLayout layoutColorService;

	// private TextView textHeel;

	private LinearLayout layoutPic;

	private TextView textEvaZy;
	private TextView textEvaTd;
	private TextView textEvaSs;
	private TextView textEvaContent;

	private TextView textConfirm;
	private LinearLayout layoutPicH;
	private LinearLayout layoutEva;
	private LinearLayout layoutCall;
	private LinearLayout layoutDetail;

	/**
	 * 界面初始化
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_info_layout);
		topTitleView = new TopTitleView(this);
		topTitleView.setTitle("订单详情");
		oid = getIntent().getStringExtra("oid");
		textId = (TextView) findViewById(R.id.order_info_id);
		textDate = (TextView) findViewById(R.id.order_item_text_data);
		textPrice = (TextView) findViewById(R.id.order_item_text_price);
		textUserName = (TextView) findViewById(R.id.order_item_text_username);
		textUserType = (TextView) findViewById(R.id.order_item_text_usertype);
		textPhone = (TextView) findViewById(R.id.order_item_text_phone);
		textAddress = (TextView) findViewById(R.id.order_item_text_address);

		layoutColorService = (LinearLayout) findViewById(R.id.order_info_color_layout);
		// textHeel = (TextView) findViewById(R.id.order_info_service_heel);
		layoutPic = (LinearLayout) findViewById(R.id.order_info_pic_layout);

		textEvaZy = (TextView) findViewById(R.id.order_info_eva_zy);
		textEvaTd = (TextView) findViewById(R.id.order_info_eva_td);
		textEvaSs = (TextView) findViewById(R.id.order_info_eva_ss);
		textEvaContent = (TextView) findViewById(R.id.order_info_eva_content);

		textConfirm = (TextView) findViewById(R.id.order_info_confirm_btn);
		layoutCall = (LinearLayout) findViewById(R.id.order_info_call_layout);
		layoutDetail = (LinearLayout) findViewById(R.id.order_detail_layout);
		layoutPicH = (LinearLayout) findViewById(R.id.order_info_picH_layout);
		layoutEva = (LinearLayout) findViewById(R.id.order_info_eva_layout);
		textId.setText("ID  " + oid);

		getOrderInfo();
	}

	/**
	 * 发送订单详情请求
	 */
	private void getOrderInfo() {

		RequestWrapper requestWrapper = new RequestWrapper();

		requestWrapper.setAid(MyApplication.getInfo().getAid());
		requestWrapper.setSeskey(MyApplication.getInfo().getSeskey());
		requestWrapper.setRank_id(MyApplication.getInfo().getRank_id());
		requestWrapper.setOp("order");
		requestWrapper.setVer("2");
		requestWrapper.setOrder_id(oid);
		requestWrapper.setShowDialog(true);
		sendData(requestWrapper, NetworkAction.detail);
	}

	/**
	 * 数据解析
	 */
	@Override
	public void showResualt(ResponseWrapper responseWrapper,
			NetworkAction requestType) {
		super.showResualt(responseWrapper, requestType);
		if (requestType == NetworkAction.detail) {
			orderInfo = responseWrapper.getOrder_info();
			if (orderInfo != null) {

				// 判断是否是订单查询模块进来的
				if (getIntent().getStringExtra("search") == null) {
					textDate.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(
									R.drawable.order_item_date), null, null,
							null);
					textDate.setText(orderInfo.getServicetime());
					textPrice.setText("￥ " + orderInfo.getPay_price());
				} else {
					textPrice.setText("￥ " + orderInfo.getReal_price());
					textDate.setText("下单时间：" + orderInfo.getCreatetime());

				}

				OrderAddress address = orderInfo.getInfo();
				if (address != null && address.getMobile() != null) {
					textUserName.setText(address.getRealname());
					// if(MyApplication.getInfo().getRank_id().equals("2"))
					// textUserType.setText(orderInfo.getArt_nickname());
					// else
					textUserType
							.setText("0".equals(orderInfo.getUtag()) ? "新用户"
									: "老用户");
					textPhone.setText(address.getMobile());
					textAddress.setText(address.getPosition()
							+ address.getAddress());
				}

				if (orderInfo.getList() != null
						&& orderInfo.getList().size() > 0) {
					for (com.cn.washoes.model.ServiceInfo sInfo : orderInfo
							.getList()) {

						// 1：洗护， 3：修理， 4：取送， 5：其他
						if ("1".equals(sInfo.getCategory_id())) {// 洗护
							setDataColorService(sInfo);
						} else {
							setDataRepairService(sInfo);
						}
					}

				}

				if (OrderListActivity.ORDER_STATUS_WAITING.equals(orderInfo
						.getFlag())) {
					if (getIntent().getBooleanExtra("isLeader", false))
						textConfirm.setVisibility(View.VISIBLE);
					layoutDetail.setVisibility(View.VISIBLE);
				} else if (OrderListActivity.ORDER_STATUS_WORKING
						.equals(orderInfo.getFlag())) {
					initPic(orderInfo);
					textConfirm.setVisibility(View.VISIBLE);

				} else if (OrderListActivity.ORDER_STATUS_FINISH
						.equals(orderInfo.getFlag())) {
					initPic(orderInfo);
					if ("1".equals(orderInfo.getIs_comment())) {
						initEva(orderInfo);
					}
				} else if (OrderListActivity.ORDER_STATUS_CANCEL
						.equals(orderInfo.getFlag())) {
					layoutCall.setVisibility(View.VISIBLE);
				}
				try {
					Intent intent = new Intent();
					// 检查订单菜单提示状态
					if (responseWrapper.getUnread_num().equals("0")) {
						intent.setAction(Cst.CLOSE_ORDER);
					} else {
						intent.setAction(Cst.OPEN_ORDER);
					}
					sendBroadcast(intent);
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

		} else if (requestType == NetworkAction.confirm_e) {
			Toast.makeText(this, "订单确认成功", Toast.LENGTH_SHORT).show();
			// OrderListActivity.REQ_REFRESH = true;
			Intent intent = new Intent(Cst.GET_ORDER);
			sendBroadcast(intent);
			finish();
		}
	}

	/**
	 * 初始化图片
	 * 
	 * @param orderInfo
	 */
	private void initPic(OrderInfo orderInfo) {
		layoutPicH.setVisibility(View.VISIBLE);
		if (orderInfo.getUltu_def() != null
				&& orderInfo.getUltu_def().size() > 0) {

			for (ImgInfo item : orderInfo.getUltu_def()) {
				if (item.getFile_path() != null) {
					CarImageView img = (CarImageView) getLayoutInflater()
							.inflate(R.layout.order_camare_item_img, layoutPic,
									false);
					img.loadImage(item.getFile_path());
					layoutPic.addView(img);
					img.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.putExtra("order_id", oid);
							intent.setClass(OrderInfoActivity.this,
									ImgDetailActivity.class);
							startActivity(intent);
						}
					});
					if (layoutPic.getChildCount() >= 3) {
						break;
					}
				}
			}
		}
	}

	/**
	 * 设置评论数据
	 * 
	 * @param orderInfo
	 */
	private void initEva(OrderInfo orderInfo) {
		layoutEva.setVisibility(View.VISIBLE);
		if (orderInfo.getCom_info() != null
				&& orderInfo.getCom_info().getComment_id() != null
				&& !"".equals(orderInfo.getCom_info().getComment_id())) {
			ComInfo cInfo = orderInfo.getCom_info();
			textEvaZy.setText(getString(R.string.order_info_zy,
					cInfo.getPro_stars()));
			textEvaTd.setText(getString(R.string.order_info_td,
					cInfo.getAtt_stars()));
			textEvaSs.setText(getString(R.string.order_info_ss,
					cInfo.getPun_stars()));
			textEvaContent.setText(cInfo.getComments());

		}
	}

	/**
	 * 设置洗护数据
	 * 
	 * @param info
	 */
	private void setDataColorService(com.cn.washoes.model.ServiceInfo info) {

		findViewById(R.id.xh).setVisibility(View.VISIBLE);
		LinearLayout cServiceLayout = (LinearLayout) getLayoutInflater()
				.inflate(R.layout.order_service_item, layoutColorService, false);
		TextView textType = (TextView) cServiceLayout
				.findViewById(R.id.service_item_type);
		textType.setText(info.getProject_name());

		TextView textServiceItem = (TextView) cServiceLayout
				.findViewById(R.id.service_item_0);
		textServiceItem.setText(getServiceHtml(info.getServer_name(),
				info.getBuy_num()));

		if (info.getSs_info() != null && info.getSs_info().size() > 0) {

			SS_Info ss_info = info.getSs_info().get(0);

			textServiceItem = (TextView) cServiceLayout
					.findViewById(R.id.service_item_1);
			textServiceItem.setText(getServiceHtml(ss_info.getSs_name(),
					ss_info.getSs_buy_num()));

			if (layoutColorService.getChildCount() != 0) {
				layoutColorService.addView(getLayoutInflater().inflate(
						R.layout.border_view, layoutColorService, false));
			}
			layoutColorService.addView(cServiceLayout);

		}

	}

	/**
	 * 设置修理数据
	 * 
	 * @param typeName
	 * @param ss_Infos
	 */
	private void setDataRepairService(com.cn.washoes.model.ServiceInfo info) {
		LinearLayout layout = null;
		if ("3".equals(info.getCategory_id())) {
			layout = (LinearLayout) findViewById(R.id.xL);
		} else if ("4".equals(info.getCategory_id())) {
			layout = (LinearLayout) findViewById(R.id.qs);
		} else if ("100".equals(info.getCategory_id())) {
			layout = (LinearLayout) findViewById(R.id.other);
		}
		if (layout != null) {
			layout.setVisibility(View.VISIBLE);
			GridLayout gridlLayout = (GridLayout) layout.getChildAt(1);
			TextView text = (TextView) getLayoutInflater().inflate(
					R.layout.service_gridlayout_item, gridlLayout, false);
			text.setText(getServiceHtml(info.getProject_name(),
					info.getBuy_num()));
			gridlLayout.addView(text);

		}

	}

	/**
	 * 获取服务字体
	 * 
	 * @param sName
	 * @param num
	 * @return
	 */
	private Spanned getServiceHtml(String sName, String num) {

		String html = "";
		if (sName != null) {
			html = "<font color=\"#969696\">" + sName + "</font>";
			if (num != null) {
				html = html + "<font color=\"#e64626\">  X" + num + "</font>";
			}
		}
		return Html.fromHtml(html);

	}

	/**
	 * 按钮点击事件
	 * 
	 * @param v
	 */
	public void onViewClick(View v) {
		if (v.getId() == R.id.order_info_text_call_u
				|| v.getId() == R.id.order_info_text_call_kf) {
			String phone = textPhone.getText().toString();
			if (phone != null && !"".equals(phone)) {
				Intent phoneIntent = new Intent("android.intent.action.CALL",
						Uri.parse("tel:" + phone));
				startActivity(phoneIntent);
			} else {
				Toast.makeText(this, "用户未填写电话", Toast.LENGTH_SHORT).show();
			}

		} else if (v.getId() == R.id.order_info_text_navig) {

			// RequestWrapper requestWrapper = new RequestWrapper();
			// requestWrapper.setOp("artificer");
			// requestWrapper.setPage("1");
			// requestWrapper.setPer("1");
			// requestWrapper.setPos("1");
			// requestWrapper.setSeskey(MyApplication.getInfo().getSeskey());
			// requestWrapper.setAid(MyApplication.getInfo().getAid());
			// sendData(requestWrapper, NetworkAction.pos_list);
			navigate(MyApplication.locInfo);
		} else if (v.getId() == R.id.order_info_confirm_btn) {

			ConfirmDialog dlg = new ConfirmDialog(this);
			dlg.setTitle("提示");
			dlg.setMessage("请确认您已完成了服务，确定确认吗？");
			dlg.setOkButton("确认", new ConfirmDialog.OnClickListener() {

				@Override
				public void onClick(Dialog dialog, View view) {
					RequestWrapper requestWrapper = new RequestWrapper();
					requestWrapper.setOp("order");
					requestWrapper.setOrder_id(oid);
					requestWrapper.setSeskey(MyApplication.getInfo()
							.getSeskey());
					requestWrapper.setAid(MyApplication.getInfo().getAid());
					sendData(requestWrapper, NetworkAction.confirm_e);
				}
			});

			dlg.setCancelButton("暂不确认", new ConfirmDialog.OnClickListener() {

				@Override
				public void onClick(Dialog dialog, View view) {

				}
			});
			dlg.show();

		}
	}

	/**
	 * 路径导航
	 * 
	 * @param info
	 *            技师位置
	 */
	private void navigate(LocInfo info) {
		if (info != null
				&& !StringUtils.isEmpty(info.getCity_name())
				&& !StringUtils.isEmpty(info.getAddress())
				&& !StringUtils
						.isEmpty(textAddress.getText().toString().trim())) {
			try {
				if (new File("/data/data/com.baidu.BaiduMap").exists()) {
					String fromAdd = info.getAddress();
					String toAdd = textAddress.getText().toString();
					String city = info.getCity_name();
					Log.i("test", "fromAdd->" + fromAdd);
					Log.i("test", "toAdd->" + toAdd);
					Log.i("test", "city->" + city);

					Intent intent = Intent
							.parseUri(
									"intent://map/direction?origin=name:"
											+ fromAdd
											+ "&destination="
											+ toAdd
											+ "&mode=walking&region="
											+ city
											+ "&src=洗豆豆#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end",
									android.content.Intent.URI_INTENT_SCHEME);
					startActivity(intent);
				} else {
					Toast.makeText(this, "你还未安装百度地图", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

		} else {
			Toast.makeText(this, "地址错误", Toast.LENGTH_SHORT).show();
		}
	}

}

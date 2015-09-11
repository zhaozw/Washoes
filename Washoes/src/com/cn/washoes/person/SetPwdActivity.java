package com.cn.washoes.person;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.hongwei.BaseActivity;
import com.cn.hongwei.RequestWrapper;
import com.cn.hongwei.ResponseWrapper;
import com.cn.hongwei.TopTitleView;
import com.cn.washoes.R;
import com.cn.washoes.activity.ConfirmDialog;
import com.cn.washoes.util.NetworkAction;


/**
 * 设置密码页面
 * @author Wu Jiang
 *
 */
public class SetPwdActivity extends BaseActivity {

	private TopTitleView topTitleView;//标题栏
	private EditText pwd1Txt;//第一次输入的密码
	private EditText pwd2Txt;//第二次输入的密码
	private TextView btn;
	private String type;
	private String aid;//技师ID
	private String mobile;//手机号
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_setpwd);
		initView();
		getData();
	}
	private void getData() {
		Intent intent=getIntent();
		type=intent.getStringExtra("type");
		mobile=intent.getStringExtra("mobile");
		//注册页面设置密码
		if(type.equals("0"))
			aid=intent.getStringExtra("aid");
		
	}
	private void initView() {
		topTitleView = new TopTitleView(this);
		topTitleView.setTitle("设置密码");
		pwd1Txt=(EditText) findViewById(R.id.setpwd_pwd1);
		pwd2Txt=(EditText) findViewById(R.id.setpwd_pwd2);
		pwd2Txt.addTextChangedListener(watcher);
		pwd1Txt.addTextChangedListener(watcher);
		btn=(TextView) findViewById(R.id.setpwd_btn);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(pwd1Txt.length()>=6 && pwd2Txt.length()>=6 )
				{
					String pwd1=pwd1Txt.getText().toString();
					String pwd2=pwd2Txt.getText().toString();
					if(!pwd1.equals(pwd2))
					{
						Toast.makeText(SetPwdActivity.this, "两次输入的密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
						return;
					}
					//设置密码
					setpwd();
				}
				else
				{
					Toast.makeText(SetPwdActivity.this, "密码不允许为空并且要大于6位", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}
	
	/**
	 * 监听是否输入验证码以开启下一步按钮
	 */
	private TextWatcher watcher = new TextWatcher() {
		   
	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count) {
	        // TODO Auto-generated method stub
	       
	    }
	   
	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count,
	            int after) {
	        // TODO Auto-generated method stub
	       
	    }
	   
	    @Override
	    public void afterTextChanged(Editable s) {
	        if(pwd1Txt.length()>1 && pwd2Txt.length()>1)
	        {
	        	btn.setBackgroundResource(R.drawable.login_bg);
	        	btn.setEnabled(true);
	        }
	        	
	        else
	        {
	        	btn.setBackgroundResource(R.drawable.enable_btn_off);
	        	btn.setEnabled(false);
	        }
	        	
	       
	    }
	};
	
	/**
	 * 注册设置密码
	 */
	private void setpwd()
	{
		RequestWrapper requestWrapper=new RequestWrapper();
		
		requestWrapper.setMobile(mobile);
		requestWrapper.setPassword(pwd1Txt.getText().toString());
		requestWrapper.setRepassword(pwd2Txt.getText().toString());
		//注册页面设置密码
		if(type.equals("0"))
		{
			requestWrapper.setOp(NetworkAction.setpwd.toString());
			requestWrapper.setAid(aid);
			sendData(requestWrapper, NetworkAction.setpwd);
		}
		//找回密码
		else if(type.equals("1"))
		{
			requestWrapper.setOp(NetworkAction.getpwd_reset.toString());
			sendData(requestWrapper, NetworkAction.getpwd_reset);
		}
	}
	
	@Override
	public void showResualt(ResponseWrapper responseWrapper,
			NetworkAction requestType) {
		// TODO Auto-generated method stub
		super.showResualt(responseWrapper, requestType);
//		if(requestType==NetworkAction.setpwd)
//		{
			ConfirmDialog dialog=new ConfirmDialog(this,R.layout.msg_dialog);
			dialog.setTitle("提示");
			dialog.setMessage(responseWrapper.getMsg());
			
			dialog.setOkButton("我知道了", new ConfirmDialog.OnClickListener() {
				
				@Override
				public void onClick(Dialog dialog, View view) {
					SetPwdActivity.this.finish();
					
				}
			});
			dialog.show();
//		}
	}
}

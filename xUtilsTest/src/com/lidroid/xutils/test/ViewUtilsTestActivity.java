package com.lidroid.xutils.test;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.Res;
import com.lidroid.xutils.view.annotation.StringInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class ViewUtilsTestActivity extends Activity {
	
	@ViewInject(R.id.imageView1)
	private ImageView imageView;

	@ViewInject(R.id.btnShow)
	private Button btnStart;
	@StringInject(R.string.test1)
	public String testString;
	
	@Res(R.array.test_string_array)
	public String[] testStringArray;
	
	@Res(R.array.test_integer_array)
	public int[] testIntArray;
	
	@Res(R.drawable.ic_launcher)
	public Drawable testDrwable;
	
	@Res(R.xml.perference)
	public XmlResourceParser testXml;
	@Res(R.drawable.test_gif)
	public Movie testMovie;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.view_utils_test_activity);
		ViewUtils.inject(this); //注入view和事件
		this.btnStart.setText(this.testString);
	}

	

	@OnClick(R.id.btnShow)
	protected void onClickShow(View v) {
		
	}
	

}

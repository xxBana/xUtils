package com.lidroid.xutils.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.lidroid.xutils.view.ResLoader;


public class TestAcivity extends ActivityInstrumentationTestCase2<ViewUtilsTestActivity>  {

	// MainActivity是需要单元测试的android的activity
    private ViewUtilsTestActivity mActivity;
    // TextView是Activity中的一个组件
    private Button mView;
    private String resourceString;

    // 一个无参构造函数（必须）
    public TestAcivity() {
         super(ViewUtilsTestActivity.class);//"com.lidroid.xutils.ViewUtilsTestActivity",
    }

    // 复写supper类中的一个方法(初始化Activity使用)
    @Override
    protected void setUp() throws Exception {
         super.setUp();
         mActivity = this.getActivity();
         mView = (Button) mActivity.findViewById(R.id.btnShow);
         resourceString = mActivity.getString(R.string.test1);
    }

    // 单元测试的内容
    public void testPreconditions() {
   //断言
         assertNotNull(mView);
    }

    public void testText() {
         assertEquals(resourceString, mView.getText().toString());
         
         
    }
    public void testStringArray() {
    	assertEquals(mActivity.getResources().getStringArray(R.array.test_string_array)[0], mActivity.testStringArray[0]);
    	assertEquals(mActivity.getResources().getStringArray(R.array.test_string_array)[1], mActivity.testStringArray[1]);
    }
    public void testIntArray() {
    	assertEquals(mActivity.getResources().getIntArray(R.array.test_integer_array)[0], mActivity.testIntArray[0]);
    	assertEquals(mActivity.getResources().getIntArray(R.array.test_integer_array)[1], mActivity.testIntArray[1]);
    }
    public void testDrwable() {
    	assertNotNull(this.mActivity.testDrwable);
    	assertNotNull(mActivity.getResources().getDrawable(R.drawable.ic_launcher));
    }
    public void testXml() {
    	assertEquals(this.mActivity.getResources().getXml(R.xml.perference).getText(),mActivity.testXml.getText());
    }
    public void testMovie() {
    	assertNotNull(this.mActivity.getResources().getMovie(R.drawable.test_gif));
    	assertNotNull(mActivity.testMovie);
    }
    
    public String testNotFound;
    public void testNotFoundId() {
    	try{
    		ResLoader.loadRes(this.getClass().getField("testNotFound"),this.mActivity, Integer.MAX_VALUE);
    	}catch(IllegalArgumentException e){
    		return ;//OK!预期抛出这个异常
    	} catch (NoSuchFieldException e) {
			
		}
    	fail("异常");
    }
}

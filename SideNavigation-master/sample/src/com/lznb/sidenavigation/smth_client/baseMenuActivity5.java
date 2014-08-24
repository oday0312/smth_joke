package com.lznb.sidenavigation.smth_client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.lznb.sidenavigation.smth_client.LoadMoreListView.LoadMoreListView;
import com.lznb.sidenavigation.smth_client.imageCache.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.james.mime4j.util.CharsetUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created with IntelliJ IDEA.
 * User: apple
 * Date: 13-5-12
 * Time: 下午6:24
 * To change this template use File | Settings | File Templates.
 */
public class baseMenuActivity5  extends SherlockActivity implements ISideNavigationCallback {

    public static final String EXTRA_TITLE = "com.devspark.sidenavigation.yeeyanAndroid.extra.MTGOBJECT";
    public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.yeeyanAndroid.extra.RESOURCE_ID";
    public static final String EXTRA_MODE = "com.devspark.sidenavigation.yeeyanAndroid.extra.MODE";
    public static final String EXTRA_WEBURL = "com.devspark.sidenavigation.yeeyanAndroid.extra.weburl";


    public String BaseUrl = "";

    public ImageView icon;
    public SideNavigationView sideNavigationView;

    public LoadMoreListView listView;
    public ArrayList<PaperItem> rawData = new ArrayList<PaperItem>();
    public ArrayList<PaperItem> LoadingMoreArray = new ArrayList<PaperItem>();

    public String htmlContentString = "";

    public boolean displayImages = true;
    public int imageCacheSize = 200;
    public int imagesInParallel = 2;
    public String imageCacheDir = null;

    public  cuzyAdapter adapter = null;
    public ImageLoader imageLoader=  null;

    public ProgressBar progressBar = null;
    public String getHtmlStringInLocal()
    {
        //获取SharedPreferences对象
        Context ctx = baseMenuActivity5.this;
        SharedPreferences sp = ctx.getSharedPreferences("yeeyan_localhtmlStrign", MODE_PRIVATE);
        //存入数据
        //SharedPreferences.Editor editor = sp.edit();
        //editor.putString("STRING_KEY", "string");
        //editor.commit();

        String tempString = sp.getString(BaseUrl,"");
        return tempString;

    }

    public void setHtmlStringToLocal(String htmlContentString)
    {
        //获取SharedPreferences对象
        Context ctx = baseMenuActivity5.this;
        SharedPreferences sp = ctx.getSharedPreferences("yeeyan_localhtmlStrign", MODE_PRIVATE);
        //存入数据
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(BaseUrl, htmlContentString);
        editor.commit();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        BaseUrl = "http://select.yeeyan.org/lists/culture/horizontal/";
        LoadingMoreFlag = 0;
        setContentView(R.layout.menuactivity1);
        listView = (LoadMoreListView)findViewById(R.id.listView);
        listView.setDividerHeight(0);
        listView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            public void onLoadMore() {
                // Do the work to load more items at the end of list
                // here
                new LoadDataTask().execute();
            }
        });



        testSimpleListView();

        icon = (ImageView) findViewById(android.R.id.icon);
        sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);


        progressBar =  (ProgressBar)findViewById(R.id.myprogressBar);
        progressBar.setVisibility(View.INVISIBLE);

        if (getIntent().hasExtra(EXTRA_TITLE)) {
            String title = getIntent().getStringExtra(EXTRA_TITLE);
            int resId = getIntent().getIntExtra(EXTRA_RESOURCE_ID, 0);
            setTitle(title);
            //icon.setImageResource(resId);
            sideNavigationView.setMode(getIntent().getIntExtra(EXTRA_MODE, 0) == 0 ? SideNavigationView.Mode.LEFT : SideNavigationView.Mode.RIGHT);
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String localHtmlString = getHtmlStringInLocal();
        if (localHtmlString.length()>0)
        {
            //getRawDataFromHttpString(localHtmlString);
            //adapter.notifyDataSetChanged();
        }
        else
        {

        }
        testCuzySDKfunction();

        // Click on ListView Row:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Object o = listView.getItemAtPosition(position);
                Log.i("alex huang", "alex huang"+o.toString());
                PaperItem tempItem = rawData.get(position);
                startWebViewActivity(tempItem.urlString);
            }
        });
    }



    public void testSimpleListView()
    {



        imageLoader=new ImageLoader(this);
        adapter = new cuzyAdapter(rawData, this,this, imageLoader,1);
        listView.setAdapter(adapter);



    }
    public void testCuzySDKfunction()
    {

        new LongOperation().execute("");

    }

    public int currentPageIndex = 1;
    public int LoadingMoreFlag = 0;
    public class LongOperation extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String...params){

            if (LoadingMoreFlag ==0)
            {

                currentPageIndex = 1;
                try{
                    rawData.clear();
                    String urlstring = BaseUrl + currentPageIndex;
                    htmlContentString = Utils.getResultForHttpGet2(urlstring);

                    Utils.getRawDataFromHttpString(htmlContentString, rawData);


                }
                catch (Exception e)
                {

                    Log.d("alex huang", "the regex goes wrong"+e.toString());
                }


                Log.d("cuzy.com: ", "return of raw data: first loading string:  " + htmlContentString);
            }
            else
            {

                currentPageIndex++;
                try{
                    String urlstring = BaseUrl + currentPageIndex;
                    htmlContentString = Utils.getResultForHttpGet2(urlstring);
                    Utils.getRawDataFromHttpString(htmlContentString, rawData);
                }
                catch (Exception e)
                {
                    Log.d("alex huang", "the regex goes wrong loading more return "+e.toString());
                }
                Log.d("cuzy.com: ", "return of load More data: htmlcontent string:  " + htmlContentString);

            }

            return"Executed";
        }

        @Override
        protected void onPostExecute(String result){
            //might want to change "executed" for the returned string passed into onPostExecute() but that is upto you
            progressBar.setVisibility(View.INVISIBLE);

            if (LoadingMoreFlag==0)
            {
                reloadListView();

            }
            else
            {
                appendListView();
            }
        }

        @Override
        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values){
        }
    }

    public void appendListView()
    {

        adapter.notifyDataSetChanged();

    }
    public void reloadListView(){

        adapter = new cuzyAdapter(rawData, this,this,imageLoader,1);

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setAdapter(adapter);

    }



    public void startWebViewActivity(String urlString)
    {
        Intent intent = new Intent(this, webViewActivity.class);
        intent.putExtra(EXTRA_WEBURL, urlString);

        // all of the other activities on top of it will be closed and this
        // Intent will be delivered to the (now on top) old activity as a
        // new Intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_menu, menu);
        if (sideNavigationView.getMode() == SideNavigationView.Mode.RIGHT) {
            menu.findItem(R.id.mode_right).setChecked(true);
        } else {
            menu.findItem(R.id.mode_left).setChecked(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                sideNavigationView.toggleMenu();
                break;
            case R.id.mode_left:
                item.setChecked(true);
                sideNavigationView.setMode(SideNavigationView.Mode.LEFT);
                break;
            case R.id.mode_right:
                item.setChecked(true);
                sideNavigationView.setMode(SideNavigationView.Mode.RIGHT);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onSideNavigationItemClick(int itemId) {
        switch (itemId) {
            case R.id.side_navigation_menu_item1:
                invokeActivity1(getString(R.string.title1), R.drawable.ic_android1);
                break;

            case R.id.side_navigation_menu_item2:
                invokeActivity2(getString(R.string.title2), R.drawable.ic_android2);
                break;

            case R.id.side_navigation_menu_item3:
                invokeActivity3(getString(R.string.title3), R.drawable.ic_android3);
                break;

            case R.id.side_navigation_menu_item4:
                invokeActivity4(getString(R.string.title4), R.drawable.ic_android4);
                break;

            case R.id.side_navigation_menu_item5:
                invokeActivity5(getString(R.string.title5), R.drawable.ic_android5);
                break;

            default:
                return;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        // hide menu if it shown
        if (sideNavigationView.isShown()) {
            sideNavigationView.hideMenu();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Start activity from SideNavigation.
     *
     * @param title title of Activity
     * @param resId resource if of background image
     */
    protected void invokeActivity(String title, int resId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_RESOURCE_ID, resId);
        intent.putExtra(EXTRA_MODE, sideNavigationView.getMode() == SideNavigationView.Mode.LEFT ? 0 : 1);

        // all of the other activities on top of it will be closed and this
        // Intent will be delivered to the (now on top) old activity as a
        // new Intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);
    }


    protected void invokeActivity1(String title, int resId ) {

        Intent intent = new Intent(this, BaseMenuActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_RESOURCE_ID, resId);
        intent.putExtra(EXTRA_MODE, sideNavigationView.getMode() == SideNavigationView.Mode.LEFT ? 0 : 1);

        // all of the other activities on top of it will be closed and this
        // Intent will be delivered to the (now on top) old activity as a
        // new Intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);
    }

    protected void invokeActivity2(String title, int resId ) {

        Intent intent = new Intent(this, baseMenuActivity2.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_RESOURCE_ID, resId);
        intent.putExtra(EXTRA_MODE, sideNavigationView.getMode() == SideNavigationView.Mode.LEFT ? 0 : 1);

        // all of the other activities on top of it will be closed and this
        // Intent will be delivered to the (now on top) old activity as a
        // new Intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);
    }
    protected void invokeActivity3(String title, int resId ) {

        Intent intent = new Intent(this, baseMenuActivity3.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_RESOURCE_ID, resId);
        intent.putExtra(EXTRA_MODE, sideNavigationView.getMode() == SideNavigationView.Mode.LEFT ? 0 : 1);

        // all of the other activities on top of it will be closed and this
        // Intent will be delivered to the (now on top) old activity as a
        // new Intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);
    }
    public void invokeActivity4(String title, int resId ) {

        Intent intent = new Intent(this, baseMenuActivity4.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_RESOURCE_ID, resId);
        intent.putExtra(EXTRA_MODE, sideNavigationView.getMode() == SideNavigationView.Mode.LEFT ? 0 : 1);

        // all of the other activities on top of it will be closed and this
        // Intent will be delivered to the (now on top) old activity as a
        // new Intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);
    }

    public void invokeActivity5(String title, int resId ) {


    }


    public class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            if (isCancelled()) {
                return null;
            }

            //this is loading more call back
            // Simulates a background task
            try
            {
                LoadingMoreFlag = 1;
                new LongOperation().execute("");
            }
            catch (Exception e)
            {
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            //adapter.notifyDataSetChanged();
            listView.onLoadMoreComplete();

            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            // Notify the loading more operation has finished
            //((LoadMoreListView) getListView()).onLoadMoreComplete();
            listView.onLoadMoreComplete();;
        }
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    public String getResultForHttpGet(String inputUrlstring) throws ClientProtocolException, IOException {
        //服务器  ：服务器项目  ：servlet名称
        String path="";
        String uri=path+inputUrlstring;
        //name:服务器端的用户名，pwd:服务器端的密码
        //注意字符串连接时不能带空格

        String result="";

        HttpGet httpGet=new HttpGet(uri);//编者按：与HttpPost区别所在，这里是将参数在地址中传递
        HttpResponse response=new DefaultHttpClient().execute(httpGet);
        if(response.getStatusLine().getStatusCode()==200){
            HttpEntity entity=response.getEntity();
            result= EntityUtils.toString(entity, HTTP.UTF_8);
            setHtmlStringToLocal(result);
        }
        return result;
    }






}

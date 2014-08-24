package com.lznb.sidenavigation.smth_client;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import org.apache.http.client.ClientProtocolException;
import org.apache.james.mime4j.util.CharsetUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: apple
 * Date: 13-6-15
 * Time: 下午11:21
 * To change this template use File | Settings | File Templates.
 */
public class Utils  {
    private Utils(){

    }




    public static File appExternalDirPath(){
        File result = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            result = Environment.getExternalStorageDirectory();
        }
//        Log.d("CuzyAdSDK","appExternalDirPath:" + result.getAbsolutePath());
        return result;
    }
    public static String uniqueIdentifier(Context context){
//        return "android";


        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String tmDevice, tmSerial, androidId;
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = ""
                    + android.provider.Settings.Secure.getString(
                    context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(),
                    ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            return deviceUuid.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 图片资源缓存
     */
    private static Map<String, Bitmap> bitmapCache = new HashMap<String, Bitmap>();

    /**
     * 获取网落图片资源
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url) {
        //先从缓存里找
        Bitmap bitmap = bitmapCache.get(url);
        if (bitmap != null) {
            return bitmap;
        }

        //从网络上下载
        URL myFileURL;
        try {
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            bitmapCache.put(url, bitmap);
        }

        return bitmap;

    }




    public static int getWindowWith(Activity acitivty)
    {

        int screenWidth ;
        int screenHeight ;
        DisplayMetrics dm = new DisplayMetrics();
        dm = acitivty.getResources().getDisplayMetrics();
        // 获取屏幕密度（方法3）
        dm = new DisplayMetrics();
        acitivty.getWindowManager().getDefaultDisplay().getMetrics(dm);

        float density  = dm.density;        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        int densityDPI = dm.densityDpi;     // 屏幕密度（每寸像素：120/160/240/320）
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;


        density  = dm.density;      // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        densityDPI = dm.densityDpi;     // 屏幕密度（每寸像素：120/160/240/320）
        xdpi = dm.xdpi;
        ydpi = dm.ydpi;


        int screenWidthDip = dm.widthPixels;        // 屏幕宽（dip，如：320dip）
        int screenHeightDip = dm.heightPixels;      // 屏幕宽（dip，如：533dip）


        screenWidth  = (int)(dm.widthPixels * density + 0.5f);      // 屏幕宽（px，如：480px）
        screenHeight = (int)(dm.heightPixels * density + 0.5f);     // 屏幕高（px，如：800px）
        return screenWidth;
    }
    public static float getDensity(Activity acitivity)
    {
        int screenWidth ;
        int screenHeight ;
        DisplayMetrics dm = new DisplayMetrics();
        dm = acitivity.getResources().getDisplayMetrics();
        // 获取屏幕密度（方法3）
        dm = new DisplayMetrics();
        acitivity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        float density  = dm.density;
        return density;
    }


    public static void getRawDataFromHttpString(String htmlContentString, ArrayList<PaperItem>rawData)
    {
        htmlContentString = htmlContentString.replaceAll("[\\t\\n\\r]", "");
        htmlContentString = htmlContentString.replaceAll(" ", "");

        htmlContentString = htmlContentString.replaceAll("s_i_articleclearfix", "\n");

        String[] allstrings = htmlContentString.split("\\n");
        for (int i = 0;i<allstrings.length;i++)
        {
            String tempstring = allstrings[i];

            if (tempstring.contains("imageclearfix"))
            {
                //Log.d("alex huang", "the index is "+i);
                tempstring = tempstring.replaceAll("</a>", "</a>\n");
                Pattern pImage = Pattern.compile("<imgsrc=\\\"(.*)\\\"/></",Pattern.MULTILINE);
                Matcher mImage = pImage.matcher(tempstring);

                mImage.find();
                //Log.d("alex huang", "the  image string is "+mImage.group(1));


                Pattern titlePatter = Pattern.compile("title\\\">.*\\\">(.*)</a>", Pattern.MULTILINE);
                Matcher titleMatcher = titlePatter.matcher(tempstring);
                titleMatcher.find();
                // Log.d("alex huang", "the title string is " + titleMatcher.group(1));
                //

                Pattern contentPattern = Pattern.compile("<divclass=\\\"text\\\">(.*)</div></div>",Pattern.MULTILINE);
                Matcher contentMatcher = contentPattern.matcher(tempstring);
                contentMatcher.find();
                // Log.d("alex huang", "the content string is " + contentMatcher.group(1));


                Pattern urlStringPattern = Pattern.compile("atarget=\\\"_blank\\\"href=\\\"(.*)\\\">",Pattern.MULTILINE);
                Matcher urlStringMatch = urlStringPattern.matcher(tempstring);
                urlStringMatch.find();
                // Log.d("alex huang", "the url string is " + urlStringMatch.group(1));
                PaperItem item = new PaperItem();
                item.imageString = mImage.group(1);
                item.titleString = titleMatcher.group(1);
                item.contentString = contentMatcher.group(1);
                item.urlString  = urlStringMatch.group(1);

                rawData.add(item);

            }
            else
            {
                continue;
            }
        }
    }


    public static String getResultForHttpGet2(String inputUrlstring) throws ClientProtocolException, IOException
    {
        String result="";
        URL url;
        try {
            url = new URL(inputUrlstring);

            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");



            //urlConnection.setUseCaches(false);//不要用cache，用了也没有什么用，因为我们不会经常对一个链接频繁访问。（针对程序）
            urlConnection.setConnectTimeout(6 * 1000);
            urlConnection.setReadTimeout(6*1000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in, CharsetUtil.UTF_8);
            isw.toString();
            int b ;
            StringBuilder sb=new StringBuilder();
            while ((b = isw.read()) != -1)// 顺序读取文件text里的内容并赋值给整型变量b,直到文件结束为止。
            {
                sb.append((char) b);
            }
            result = sb.toString();

            in.close();
            isw.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return  result;

    }

    public static String getResultForHttpGet3(String inputUrlstring) throws ClientProtocolException, IOException
    {
        String result="";
        URL url;
        try {
            url = new URL(inputUrlstring);

            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");
            urlConnection.setRequestProperty("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            //urlConnection.setRequestProperty("Accept-Language", "zh-cn");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");//为什么没有deflate呢
            urlConnection.setRequestProperty("Cache-Control","max-age=0");
            urlConnection.setRequestProperty("Host","select.yeeyan.org");
            urlConnection.setRequestProperty("Cookie","PHPSESSID=9jm21ov1955fupu7mqagoq9bs1; __utma=68569166.406582513.1406344442.1406344442.1406344442.1; __utmb=68569166.8.10.1406344442; __utmc=68569166; __utmz=68569166.1406344442.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");

            urlConnection.setRequestProperty("Connection", "keep-Alive"); //keep-Alive，有什么用呢，你不是在访问网站，你是在采集。嘿嘿。减轻别人的压力，也是减轻自己。

            //urlConnection.setUseCaches(false);//不要用cache，用了也没有什么用，因为我们不会经常对一个链接频繁访问。（针对程序）
            urlConnection.setConnectTimeout(6 * 1000);
            urlConnection.setReadTimeout(6*1000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in, CharsetUtil.UTF_8);
            isw.toString();
            int b ;
            StringBuilder sb=new StringBuilder();
            while ((b = isw.read()) != -1)// 顺序读取文件text里的内容并赋值给整型变量b,直到文件结束为止。
            {
                sb.append((char) b);
            }
            result = sb.toString();

            in.close();
            isw.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return  result;

    }


    public static String[] UserAgent = {
            "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.2",
            "Mozilla/5.0 (iPad; U; CPU OS 3_2_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B500 Safari/531.21.11",
            "Mozilla/5.0 (SymbianOS/9.4; Series60/5.0 NokiaN97-1/20.0.019; Profile/MIDP-2.1 Configuration/CLDC-1.1) AppleWebKit/525 (KHTML, like Gecko) BrowserNG/7.1.18121"
            //http://blog.csdn.net/yjflinchong
            // "Nokia5700AP23.01/SymbianOS/9.1 Series60/3.0",         "UCWEB7.0.2.37/28/998",          "NOKIA5700/UCWEB7.0.2.37/28/977",         "Openwave/UCWEB7.0.2.37/28/978",          "Mozilla/4.0 (compatible; MSIE 6.0; ) Opera/UCWEB7.0.2.37/28/989"
             };
}

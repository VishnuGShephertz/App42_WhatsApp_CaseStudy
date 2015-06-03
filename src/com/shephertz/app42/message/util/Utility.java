/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.json.JSONArray;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.example.app42messanger.R;

/**
 * @author Vishnu
 * 
 */
public class Utility {
	private final static String PhonePattern = "^\\+?[0-9. ()-]{10,25}$";
	private final static String DateTimeFormat = "yyyy-mm-dd hh-mm-ss";
	private final static String TimeFormat = "HH:mm a";
//    public static ArrayList<String> customMessages;
//    static{
//    	customMessages=new ArrayList<String>();
//    	customMessages.add("Welcome Bro");
//    	customMessages.add("Whts Up");
//    	customMessages.add("How Spring works");
//    	customMessages.add("I Love U");
//    	customMessages.add("Missing U");
//    	customMessages.add("Tell Me");
//    	customMessages.add("Hum Bhi h Josh me");
//    	customMessages.add("Idhar a kamine");
//    	customMessages.add("Jara jara machlne lga ye dil mera");
//    	customMessages.add("Duniya jaye tail lene");
//    	customMessages.add("What about future planning");
//    	customMessages.add("Chal yar kahi ghumne chalte h Chill marenge");
//    }
//    
//    public static ArrayList<String> customImages;
//    static{
//    	customImages=new ArrayList<String>();
//    	customImages.add("http://cdn.shephertz.com/repository/files/a97bf47de509c32f702e562cab2e7508389fb7d67d3322c4714c09aef4305f7c/f406942e6192a0da165cfd09f7503014077114d7/Cut.png");
//    	customImages.add("http://cdn.shephertz.com/repository/files/a97bf47de509c32f702e562cab2e7508389fb7d67d3322c4714c09aef4305f7c/d8db99fafd70851d6cd65dc5edd737b0f1599f65/Contra.png");
//    	customImages.add("http://cdn.shephertz.com/repository/files/a97bf47de509c32f702e562cab2e7508389fb7d67d3322c4714c09aef4305f7c/c95069527cb0244e86668de871704814c2c834ab/Book.png");
//    	customImages.add("http://cdn.shephertz.com/repository/files/a97bf47de509c32f702e562cab2e7508389fb7d67d3322c4714c09aef4305f7c/3e50dc4e9e7acda38c6a18021b6796df23b27ccd/Bob.png");
//    	customImages.add("http://cdn.shephertz.com/repository/files/a97bf47de509c32f702e562cab2e7508389fb7d67d3322c4714c09aef4305f7c/f1480e7623acbba3e4e35ab4e44bdb0bf612cc34/angry.jpg");
//    	customImages.add("http://cdn.shephertz.com/repository/files/a97bf47de509c32f702e562cab2e7508389fb7d67d3322c4714c09aef4305f7c/e07985e3ddb8a537c457313aaa4402b80c5d66b3/port1push.jpg");
//    	customImages.add("http://cdn.shephertz.com/repository/files/a97bf47de509c32f702e562cab2e7508389fb7d67d3322c4714c09aef4305f7c/b6a815f64dea241873db785f3da67c67b631ba9f/Jet.png");
//    	customImages.add("http://cdn.shephertz.com/repository/files/a97bf47de509c32f702e562cab2e7508389fb7d67d3322c4714c09aef4305f7c/99e11947ebdd331aae79b6eba8ed403f916f167d/Subway.png");
//    	customImages.add("http://cdn.shephertz.com/repository/files/a97bf47de509c32f702e562cab2e7508389fb7d67d3322c4714c09aef4305f7c/b26e1d04818c5c54afc417320d25ddfcf816cb76/saga.png");
//    	customImages.add("http://cdn.shephertz.com/repository/files/a97bf47de509c32f702e562cab2e7508389fb7d67d3322c4714c09aef4305f7c/35d3ece46302c1e3a161649ab7c9276c27719005/Card.png");
//    	customImages.add("http://cdn.shephertz.com/repository/files/a97bf47de509c32f702e562cab2e7508389fb7d67d3322c4714c09aef4305f7c/524145ad7cab62d1dd4f602b73cc3bf2d30c2c3c/Heli.png");
//    	customImages.add("http://cdn.shephertz.com/repository/files/a97bf47de509c32f702e562cab2e7508389fb7d67d3322c4714c09aef4305f7c/6c6cac8ae2b5986dc4ec9a02cbafe362db1fb1a0/Despi.png");
//    }
//    
//    public static String getDefaultMessage(){
//    	int index=new Random().nextInt(customMessages.size());
//    	return customMessages.get(index);
//    }
	/**
	 * @param phoneNo
	 * @return
	 */
	public static String getFormattedNo(String phoneNo) {
		phoneNo = phoneNo.replaceAll("[^\\d]", "");
		if (phoneNo.startsWith("0"))
			phoneNo = "91" + phoneNo.substring(1);
		return phoneNo;
	}

	/**
	 * @param hashMap
	 * @return
	 */
	public static JSONArray getKeyList(HashMap<String, String> hashMap) {
		JSONArray json=new JSONArray();
		for (String str : hashMap.keySet()) {
			json.put(str.trim());		
		}
		return json;
	}

	/**
	 * @return
	 */
	public static String getDateTime() {
		SimpleDateFormat dd = new SimpleDateFormat(DateTimeFormat);
		return dd.format(new Date());
	}

	/**
	 * @return
	 */
	public static Integer getTime() {
		long time = new Date().getTime();
		return new Integer((int) time);
	}

	/**
	 * @param phoneNo
	 * @return
	 */
	public static boolean isPhoneNoValid(String phoneNo) {
		return isValid(phoneNo, PhonePattern);
	}

	/**
	 * @param text
	 * @param pattern
	 * @return
	 */
	private static boolean isValid(String text, String pattern) {
		return Pattern.matches(pattern, text);
	}

	/**
	 * @param timeInMilis
	 * @return
	 */
	public static String getTime(long timeInMilis) {
		SimpleDateFormat sdf = new SimpleDateFormat(TimeFormat);
		return sdf.format(new Date(timeInMilis));
	}

    /**
     * @param is
     * @param os
     */
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    
    /**
     * @param context
     * @return
     */
    public static String getCountryCode(Context context) {
		String countryCode = "";
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE);
		String CountryID = manager.getSimCountryIso().toUpperCase();
		if (CountryID != null || !CountryID.equals("")) {
			String[] rl = context.getResources().getStringArray(
					R.array.country_codes);
			for (int i = 0; i < rl.length; i++) {
				String[] g = rl[i].split(",");
				if (g[1].trim().equals(CountryID.trim())) {
					countryCode = g[0];
					break;
				}
			}
		}
		return countryCode;
	}
}

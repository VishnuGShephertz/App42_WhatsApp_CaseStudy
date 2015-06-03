/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.ui.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.example.app42messanger.R;

/**
 * @author Vishnu
 * 
 */
public class UiUtil {
	private static float density = -1.0F;
	private static Bitmap defaultPicBitMap;
	private static int DefaulIcon = R.drawable.default_pic;
	static final int NO_OF_EMOTICONS = 40;
	static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();
	private static final Factory spannableFactory = Spannable.Factory
			.getInstance();
	public static String[] emotionsPatterns = { ":)", ":-)", "-:)", ":(", ":-(", "-:(",
			"(:)", "(::)", "):)", "):(", "(:(", ")::)", "::)", ":-:)", "(::(",
			":>", "<:", ":3>", ":4>", ":5>", ":<>", ":<->", ":?", "?:?", "-:-",
			"-:-)", "(-:-)", ")-:)", ":)(", ":--)", "--:)", ":>)", "<:)",
			":3)", ":@)", "@:)", "#:#)", ":#))", "(#:)", "*:)" };
	static int[] emotionResId = { R.drawable.icon1, R.drawable.icon2,
			R.drawable.icon3, R.drawable.icon4, R.drawable.icon5,
			R.drawable.icon6, R.drawable.icon7, R.drawable.icon8,
			R.drawable.icon9, R.drawable.icon10, R.drawable.icon11,
			R.drawable.icon12, R.drawable.icon13, R.drawable.icon4,
			R.drawable.icon15, R.drawable.icon16, R.drawable.icon17,
			R.drawable.icon18, R.drawable.icon19, R.drawable.icon20,
			R.drawable.icon21, R.drawable.icon22, R.drawable.icon23,
			R.drawable.icon24, R.drawable.icon25, R.drawable.icon26,
			R.drawable.icon27, R.drawable.icon28, R.drawable.icon29,
			R.drawable.icon30, R.drawable.icon31, R.drawable.icon32,
			R.drawable.icon33, R.drawable.icon34, R.drawable.icon35,
			R.drawable.icon36, R.drawable.icon37, R.drawable.icon38,
			R.drawable.icon39, R.drawable.icon40 };

	static {
		for (short i = 0; i < NO_OF_EMOTICONS; i++) {
			addPattern(emoticons, emotionsPatterns[i], emotionResId[i]);
		}

	}

	public static int getEmotionId(String emotion) {
		for (short i = 0; i < NO_OF_EMOTICONS; i++) {
			String value = emotionsPatterns[i];
			if (value.equals(emotion))
				return emotionResId[i];
		}
		return emotionResId[0];
	}

	private static void addPattern(Map<Pattern, Integer> map, String smile,
			int resource) {
		map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	/**
	 * 
	 * @param paramContext
	 * @param paramInt
	 * @return
	 */
	public static int dpToPx(final Context paramContext, final int paramInt) {
		density = density > 0.0F ? density : paramContext.getResources()
				.getDisplayMetrics().density;
		return (int) (paramInt * density + 0.5F);
	}

	public static Bitmap getDefaultPicBitMap(Context context) {
		if (defaultPicBitMap != null)
			return defaultPicBitMap;
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
				DefaulIcon);
		defaultPicBitMap = getRoundedShape(bmp, UiUtil.dpToPx(context, 50));
		return defaultPicBitMap;
	}

	public static Bitmap getRoundBitmapFromLocal(Context context,String imagePath){
		Bitmap bmp = BitmapFactory.decodeFile(imagePath);
		Bitmap localBitmap  = getRoundedShape(bmp, UiUtil.dpToPx(context, 50));
		return localBitmap;
	}
	/**
	 * @param scaleBitmapImage
	 * @param width
	 * @return
	 */
	public static Bitmap getRoundedShape(Bitmap scaleBitmapImage, int width) {
		// TODO Auto-generated method stub
		int targetWidth = width;
		int targetHeight = width;
		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(targetBitmap);
		Path path = new Path();
		path.addCircle(((float) targetWidth - 1) / 2,
				((float) targetHeight - 1) / 2,
				(Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
				Path.Direction.CCW);
		canvas.clipPath(path);
		Bitmap sourceBitmap = scaleBitmapImage;
		canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
				sourceBitmap.getHeight()), new Rect(0, 0, targetWidth,
				targetHeight), null);
		return targetBitmap;
	}
	public static Spannable getSmiledText(Context context, CharSequence text) {
		Spannable spannable = spannableFactory.newSpannable(text);
		addSmiles(context, spannable);
		return spannable;
	}

	private static boolean addSmiles(Context context, Spannable spannable) {
		boolean hasChanges = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
			Matcher matcher = entry.getKey().matcher(spannable);
			while (matcher.find()) {
				boolean set = true;
				for (ImageSpan span : spannable.getSpans(matcher.start(),
						matcher.end(), ImageSpan.class))
					if (spannable.getSpanStart(span) >= matcher.start()
							&& spannable.getSpanEnd(span) <= matcher.end())
						spannable.removeSpan(span);
					else {
						set = false;
						break;
					}
				if (set) {
					hasChanges = true;
					spannable.setSpan(new ImageSpan(context, entry.getValue()),
							matcher.start(), matcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return hasChanges;
	}
}

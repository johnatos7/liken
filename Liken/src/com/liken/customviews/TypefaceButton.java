package com.liken.customviews;

import com.liken.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.widget.Button;

public class TypefaceButton extends Button {
	  /** An <code>LruCache</code> for previously loaded typefaces. */
    private static LruCache<String, Typeface> sTypefaceCache =
            new LruCache<String, Typeface>(12);
    
    public TypefaceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        // Get our custom attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.TypefaceTextView, 0, 0);
        
       Typeface typeface = Typeface.createFromAsset(context.getAssets(),
                "fonts/Aaargh.ttf");
         
         // Cache the Typeface object
      //   sTypefaceCache.put(typefaceName, typeface);
     
     setTypeface(typeface);
/*        try {
            String typefaceName = a.getString(
                    R.styleable.TypefaceTextView_Aaargh);
            
            if (!isInEditMode() && !TextUtils.isEmpty(typefaceName)) {
                Typeface typeface = sTypefaceCache.get(typefaceName);
                
                if (typeface == null) {
                    typeface = Typeface.createFromAsset(context.getAssets(),
                           "fonts/Aaargh.ttf");
                    
                    // Cache the Typeface object
                    sTypefaceCache.put(typefaceName, typeface);
                }
                setTypeface(typeface);
                
                // Note: This flag is required for proper typeface rendering
                setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            }
        } finally {
            a.recycle();
        }*/
    }
}

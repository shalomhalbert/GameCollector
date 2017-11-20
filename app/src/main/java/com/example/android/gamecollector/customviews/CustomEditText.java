package com.example.android.gamecollector.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.example.android.gamecollector.R;

/**
 * Created by shalom on 2017-11-14.
 */

public class CustomEditText extends EditText {
    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        /*Indicates whether this View is currently in edit mode and returns if true*/
        if(isInEditMode()){
            return;
        }

        /*Returns a TypedArray holding an array of the attribute values*/
        TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.RobotoEditText);
        /*Gets font name from typface attribute is defined. Fontname will be the filename for the wanted font*/
        String fontName = styledAttributes.getString(R.styleable.RobotoEditText_typefaceEditText);
        /*Note that TypedArray objects are a shared resource and must be recycled after use*/
        styledAttributes.recycle();

        if(fontName != null) {
            /*Creates a new typeface from the specified font data*/
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
        }
    }
}

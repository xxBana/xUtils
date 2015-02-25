package com.lidroid.xutils.view;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.view.animation.AnimationUtils;

/**
 * Author: wyouflf
 * Date: 13-11-9
 * Time: 下午3:12
 */
public class ResLoader {

    public static Object loadRes(ResType type, Context context, int id) {
        if (context == null || id < 1) return null;
        switch (type) {
            case Animation:
                return AnimationUtils.loadAnimation(context, id);
            case Boolean:
                return context.getResources().getBoolean(id);
            case Color:
                return context.getResources().getColor(id);
            case ColorStateList:
                return context.getResources().getColorStateList(id);
            case Dimension:
                return context.getResources().getDimension(id);
            case DimensionPixelOffset:
                return context.getResources().getDimensionPixelOffset(id);
            case DimensionPixelSize:
                return context.getResources().getDimensionPixelSize(id);
            case Drawable:
                return context.getResources().getDrawable(id);
            case Integer:
                return context.getResources().getInteger(id);
            case IntArray:
                return context.getResources().getIntArray(id);
            case Movie:
                return context.getResources().getMovie(id);
            case String:
                return context.getResources().getString(id);
            case StringArray:
                return context.getResources().getStringArray(id);
            case Text:
                return context.getResources().getText(id);
            case TextArray:
                return context.getResources().getTextArray(id);
            case Xml:
                return context.getResources().getXml(id);
            default:
                break;
        }

        return null;
    }
    /**
     * 另外一个加载资源得方法，传入一个字段，通过反射方式获取字段类型，然后进行资源读取。<p>
     * 目前仅支持常见而且不会冲突的类型，例如String,String[],CharSequence,CharSequence[],int[],Drawable,Movie,Xml,boolean,ColorStateList;
     * 不支持可能冲突的类型int，因为有多重可能的情况，需要继续使用ResInject再指定ID和类型
     * new by xxbana。
     * @param field
     * @param context
     * @param id
     * @return
     */
    public static Object loadRes(Field field, Context context, int id) {
    	String typeName = field.getType().getName();
        if (context == null || id < 1){
        	return null;
        }
        if(typeName.equals("boolean")){
        	return context.getResources().getBoolean(id);
		}else if(typeName.equals("float")){
			return context.getResources().getDimension(id);
		}else if(typeName.equals("[Ljava.lang.String")){
			return context.getResources().getStringArray(id);
		}else if(typeName.equals("[Lint")){
			return context.getResources().getIntArray(id);
		}else if(typeName.equals("[Ljava.lang.CharSequence")){
			return context.getResources().getTextArray(id);
		}else if(typeName.startsWith("android.")){
			if(field.getType().isAssignableFrom(ColorStateList.class)){
				return context.getResources().getColorStateList(id);
			}else if(field.getType().isAssignableFrom(Drawable.class)){
				return context.getResources().getDrawable(id);
			}else if(field.getType().isAssignableFrom(Movie.class)){
				return context.getResources().getMovie(id);
			}else if(field.getType().isAssignableFrom(XmlResourceParser.class)){
				return context.getResources().getXml(id);
			}
		}
		throw new IllegalArgumentException("程序出现问题，请联系开发人员或者运维联络人：不支持读取"+typeName+"字段所对应类型的资源，请使用ResInject代替，或检查代码，字段名："+field.getName());
		
    }
}

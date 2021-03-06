/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lidroid.xutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.util.Log;
import android.view.View;

import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.EventListenerManager;
import com.lidroid.xutils.view.ResLoader;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.ViewFinder;
import com.lidroid.xutils.view.ViewInjectInfo;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.PreferenceInject;
import com.lidroid.xutils.view.annotation.Res;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.StringInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.EventBase;

public class ViewUtils {

    private ViewUtils() {
    }

    public static void inject(View view) {
        injectObject(view, new ViewFinder(view));
    }

    public static void inject(Activity activity) {
        injectObject(activity, new ViewFinder(activity));
    }

    public static void inject(PreferenceActivity preferenceActivity) {
        injectObject(preferenceActivity, new ViewFinder(preferenceActivity));
    }

    public static void inject(Object handler, View view) {
        injectObject(handler, new ViewFinder(view));
    }

    public static void inject(Object handler, Activity activity) {
        injectObject(handler, new ViewFinder(activity));
    }

    public static void inject(Object handler, PreferenceGroup preferenceGroup) {
        injectObject(handler, new ViewFinder(preferenceGroup));
    }

    public static void inject(Object handler, PreferenceActivity preferenceActivity) {
        injectObject(handler, new ViewFinder(preferenceActivity));
    }

    @SuppressWarnings("ConstantConditions")
    private static void injectObject(Object handler, ViewFinder finder) {

        Class<?> handlerType = handler.getClass();

        // inject ContentView
        ContentView contentView = handlerType.getAnnotation(ContentView.class);
        if (contentView != null) {
            try {
                Method setContentViewMethod = handlerType.getMethod("setContentView", int.class);
                setContentViewMethod.invoke(handler, contentView.value());
            } catch (Throwable e) {
                LogUtils.e(e.getMessage(), e);
            }
        }

        // inject view
        Field[] fields = handlerType.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
            	try {
            		injectField(handler, finder, field);
            	} catch (Throwable e) {
        	        LogUtils.e(e.getMessage(), e);
        	    }
            }
        }

        // inject event
        Method[] methods = handlerType.getDeclaredMethods();
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {
                injectMethod(handler, finder, method);
            }
        }
    }

	private static void injectMethod(Object handler, ViewFinder finder,
			Method method) {
		Annotation[] annotations = method.getDeclaredAnnotations();
		if (annotations == null || annotations.length <= 0) {
			return;
		}

	    for (Annotation annotation : annotations) {
	        Class<?> annType = annotation.annotationType();
	        if (annType.getAnnotation(EventBase.class) != null) {
	            injectMethodByAnnotaion(handler, finder, method, annotation,annType);
	        }
	    }
	}

	private static void injectMethodByAnnotaion(Object handler,
			ViewFinder finder, Method method, Annotation annotation,
			Class<?> annType) {
		method.setAccessible(true);
		try {
		    // ProGuard：-keep class * extends java.lang.annotation.Annotation { *; }
		    Method valueMethod = annType.getDeclaredMethod("value");
		    Method parentIdMethod = null;
		    try {
		        parentIdMethod = annType.getDeclaredMethod("parentId");
		    } catch (Throwable e) {
		    }
		    Object values = valueMethod.invoke(annotation);
		    Object parentIds = parentIdMethod == null ? null : parentIdMethod.invoke(annotation);
		    int parentIdsLen = parentIds == null ? 0 : Array.getLength(parentIds);
		    int len = Array.getLength(values);
		    for (int i = 0; i < len; i++) {
		        ViewInjectInfo info = new ViewInjectInfo();
		        info.value = Array.get(values, i);
		        info.parentId = parentIdsLen > i ? (Integer) Array.get(parentIds, i) : 0;
		        EventListenerManager.addEventMethod(finder, info, annotation, handler, method);
		    }
		} catch (Throwable e) {
		    LogUtils.e(e.getMessage(), e);
		}
	}

	private static void injectField(Object handler, ViewFinder finder, Field field)
			throws IllegalArgumentException, IllegalAccessException {
		ViewInject viewInject = field.getAnnotation(ViewInject.class);
		if (viewInject != null) {
			View view = finder.findViewById(viewInject.value(),viewInject.parentId());
			if (view != null) {
				field.setAccessible(true);
				field.set(handler, view);
			}
			return;
		}

		Res resDefault = field.getAnnotation(Res.class);
		if (resDefault != null) {
			injectDefault(handler, finder, field, resDefault);
			return;
		}
		
		ResInject resInject = field.getAnnotation(ResInject.class);
		if (resInject != null) {
			Object res = ResLoader.loadRes(resInject.type(),finder.getContext(), resInject.id());
			if (res != null) {
				field.setAccessible(true);
				field.set(handler, res);
			}
			return;
		}
		
		StringInject stringInject = field.getAnnotation(StringInject.class);
		if (stringInject != null) {
			Object res = ResLoader.loadRes(ResType.String,finder.getContext(), stringInject.value());
			if (res != null) {
				field.setAccessible(true);
				field.set(handler, res);
			}
			return;
		}
		
		PreferenceInject preferenceInject = field.getAnnotation(PreferenceInject.class);
		if (preferenceInject != null) {
			Preference preference = finder.findPreference(preferenceInject.value());
			if (preference != null) {
				field.setAccessible(true);
				field.set(handler, preference);
			}
			return;
		}
	}

	private static void injectDefault(Object handler, ViewFinder finder,
			Field field, Res resCommon) throws IllegalAccessException {
		String typeName = field.getType().getName();
		Log.d("aiitec", field.getName()+"="+typeName);
		Object res = ResLoader.loadRes(field, finder.getContext(), resCommon.value());
		field.setAccessible(true);
		field.set(handler, res);
	}

}

package com.dongua.simpleosc.net.convert;

import android.support.annotation.Nullable;


import com.dongua.simpleosc.bean.News;
import com.orhanobut.logger.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;


/**
 * Created by dongua on 17-8-10.
 */

public class CustomConverterFactory extends Converter.Factory {

    private static CustomConverterFactory INSTANCE = new CustomConverterFactory();

    public static CustomConverterFactory create() {
        return INSTANCE;
    }


    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        ParameterizedType t = (ParameterizedType) type;
        Logger.d(t.getOwnerType());
        Logger.d(t.getRawType());
        Logger.d(t.getActualTypeArguments());

        if (t.getRawType() == List.class) {
            if (t.getActualTypeArguments()[0] == News.class)
                return NewsConverter.create();


        }
        return null;
    }


}
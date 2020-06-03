package com.m.k.mvp.net.converter;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.m.k.mvp.exception.MkResultException;
import com.m.k.mvp.net.MkDataService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/*
 * created by Cherry on 2020-01-10
 **/
public class MkGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private ParameterizedType parameterizedType;
    private Type type;

   public MkGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter, Type type) {
        this.gson = gson;
        this.adapter = adapter;
        if (type instanceof ParameterizedType) {
            this.parameterizedType = (ParameterizedType) type;
        } else {
            this.type = type;
        }

    }

    @Override
    public T convert(ResponseBody value) throws IOException {

        MediaType mediaType = value.contentType();
        Charset charset = null;
        if (mediaType != null) {
            charset = mediaType.charset(); // 得到服务器返回的json 串的字符编码
        }
        if (charset == null) {
            charset = Charset.forName("UTF-8"); // 如果服务器没有返回字符串编码，那么就用默认的编码
        }

        String json = new String(value.bytes(), charset); // 那么服务器返回的原始json 窜

        try {
            if (type != null && type == String.class && parameterizedType == null) { // 如果不需要对 json 窜做任何转换，即直接返回原始json 窜
                return (T)  handJson(json);

            } else {
                Type[] types = parameterizedType.getActualTypeArguments();
                Type resultType = types[0]; // 由于我们httpresult 这个对象只有一个泛型参数，所以取第一个
                if (resultType == String.class) {
                    value = ResponseBody.create(mediaType, json); // 不需要对json 串需改
                } else {

                    value = ResponseBody.create(mediaType, handJson(json)); // 需要对json 串进行修改
                }

                JsonReader jsonReader = gson.newJsonReader(value.charStream());
                try {
                    T result = adapter.read(jsonReader);
                    if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                        throw new JsonIOException("JSON document was not fully consumed.");
                    }
                    return result;
                } finally {
                    value.close();
                }

            }

        } catch (JSONException e) {
            throw new MkResultException(e.getMessage());
        }


    }


    protected String handJson(String json) throws JSONException, MkResultException {
        JSONObject object =  new JSONObject(json);
        String codeStr = MkDataService.getNetConfig().getCodeFieldName();
        String dataStr = MkDataService.getNetConfig().getDataFieldName();
        if(!object.isNull(codeStr)){
            int code = object.getInt(codeStr);
            if(code != MkDataService.getNetConfig().getSuccessCode()){ // 失败
                if(!object.isNull(dataStr)){
                    String value = object.getString(dataStr);
                    if(TextUtils.isEmpty(value)){
                        object.remove(dataStr);
                    }
                }
            }
        }
        return object.toString();

    }
}

package wx.develop.base;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/** Created by wuyujia on 17/4/14. */
public class Result {

    static Logger logger = LoggerFactory.getLogger(Result.class);

    private Object code;

    private String message;

    private Object data;

    private Result() {
    }

    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    private static Result newInstance() {
        return new Result();
    }

    public static String success(Object data) {
        Result result = newInstance();
        result.setData(data);
        result.setCode(String.valueOf(0));
        result.setMessage("success");
        if (data == null) {
            result.setData("");
        } else {
            Field[] fields = data.getClass().getDeclaredFields();// 遇到没有属性的空类,防止JSON转换的时候异常
            if (fields.length == 0) {
                result.setData("");
            }
        }

        String resultStr = JSONObject.toJSONString(result, true);
        logger.info(data != null ? data.getClass().getName() : "null");
        logger.info("\n"+resultStr);
        return resultStr;
    }

    public static String failure(Object errorCode, String message) {
        Result result = newInstance();
        result.setCode(errorCode);
        result.setMessage(message);
        String resultStr = JSONObject.toJSONString(result, true);
        logger.info(resultStr);
        return resultStr;
    }

    public static String failure(Object errorCode, String message, Object data) {
        Result result = newInstance();
        result.setCode(errorCode);
        result.setMessage(message);
        result.setData(data);
        if (data == null) {
            result.setData("");
        }
        String resultStr = JSONObject.toJSONString(result, true);
        logger.info(resultStr);
        return resultStr;
    }
}

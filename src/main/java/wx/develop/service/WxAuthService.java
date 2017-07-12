package wx.develop.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wx.develop.constant.ConfigProperties;
import wx.develop.utils.HttpUtils;
import wx.develop.utils.RedisUtils;
import wx.develop.utils.SHAUtil;
import wx.develop.vo.form.WxTokenAuthForm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by lzh on 2017/7/11.
 */
@Service("wxAuthService")
public class WxAuthService {

    private static Logger logger =  LoggerFactory.getLogger(WxAuthService.class);

    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    private RedisUtils redisUtils;


    /**
     * 开发者模式,验证微信信息(微信发消息来进行验证)
     * @param form
     * @return
     */
    public String verifyDevMsg(WxTokenAuthForm form){

        logger.debug("getSignature:{} ", form.getSignature());
        logger.debug("getTimestamp:{} ", form.getTimestamp());
        logger.debug("getNonce:{} ", form.getNonce());
        logger.debug("getEchostr:{} ", form.getEchostr());

        //组织数据,进行加密
        // 将 token、timestamp、nonce 三个参数进行字典序排序
        String[] arr = new String[] { configProperties.getWxToken(), form.getTimestamp()+"", form.getNonce() +""};
        Arrays.sort(arr);
        //拼接成字符串
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将拼接好的字符串使用sha1 加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = SHAUtil.byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "false";
        }

        logger.debug("加密结果串{} ", tmpStr);

        // 将 sha1 加密后的字符串可与 signature 对比，如果匹配标识该请求来源于微信,返回echoStr
        if(tmpStr != null ? tmpStr.equalsIgnoreCase(form.getSignature().toUpperCase()) : false){
            return form.getEchostr();
        }else{
            return "false";
        }
    }

    /**
     * 获取微信accessToken,并更新到缓存
     * @return
     */
    public String refreshAccessToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/token";

        Map<String, Object> param = new TreeMap<>();
        param.put("grant_type","client_credential");
        param.put("appid",configProperties.getAppId());
        param.put("secret",configProperties.getAppSecret());
        //最多循环100次获取
        for(int i = 0 ;i < 100; i++){
            String response = HttpUtils.httpsGet(url, param, HttpUtils.UTF8.toString());
            logger.debug("response :{}",response);

            if(StringUtils.isNotBlank(response)){
                JSONObject jsonObject = JSONObject.parseObject(response);
                logger.debug("accessToken :{}",jsonObject.getString("access_token"));
                Long expireTime = 0L + (2 * 60 * 60 * 1000);
                redisUtils.set("wx_access_token",jsonObject.getString("access_token") , expireTime);
                return jsonObject.getString("access_token");
            }
        }

        return "";
    }

    /**
     *  获取当前的accessToken
     * @return
     */
    public String getAccessToken() {
        String accessToken = redisUtils.get("wx_access_token", String.class);
        if(StringUtils.isBlank(accessToken)){
            accessToken = this.refreshAccessToken();
        }
        return accessToken;
    }


    /**
     * 获取微信ip地址列表   TODO 暂时不知道用来干嘛
     * @return
     */
    public String getWxIpList() {
        String url = "https://api.weixin.qq.com/cgi-bin/getcallbackip";
        //获取accessToken
        String accessToken = redisUtils.get("wx_access_token", String.class);
        if(StringUtils.isBlank(accessToken)){
            accessToken = this.refreshAccessToken();
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("access_token",accessToken);

        String response = HttpUtils.httpsGet(url, param, HttpUtils.UTF8.toString());
        logger.debug("response :{}",response);
        if(StringUtils.isNotBlank(response)){
            JSONObject jsonObject = JSONObject.parseObject(response);
            String ipList = jsonObject.getString("ip_list");
            List<String> strings = JSONObject.parseArray(ipList, String.class);

            logger.debug("ip_list :{}",strings);
            return ipList;
        }else{
            return "";
        }
    }




}

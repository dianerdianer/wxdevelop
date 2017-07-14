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
import wx.develop.vo.result.WxUserAuthInfoResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 微信授权相关
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

        logger.info("getSignature:{} ", form.getSignature());
        logger.info("getTimestamp:{} ", form.getTimestamp());
        logger.info("getNonce:{} ", form.getNonce());
        logger.info("getEchostr:{} ", form.getEchostr());

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

        logger.info("加密结果串{} ", tmpStr);

        // 将 sha1 加密后的字符串可与 signature 对比，如果匹配标识该请求来源于微信,返回echoStr
        if(tmpStr != null ? tmpStr.equalsIgnoreCase(form.getSignature().toUpperCase()) : false){
            return form.getEchostr();
        }else{
            return "false";
        }
    }

    /**
     * 获取公众号access_token,并更新到缓存
     * access_token是公众号的全局唯一票据，公众号调用各接口时都需使用
     * access_token 的存储至少要保留 512 个字符空间。access_token 的有效期目前为 2 个小时,
     * 需定时刷新，重复获取将导致上次获取的 access_token 失效！
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
            logger.info("response :{}",response);

            if(StringUtils.isNotBlank(response)){
                JSONObject jsonObject = JSONObject.parseObject(response);
                logger.info("accessToken :{}",jsonObject.getString("access_token"));
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
        logger.info("response :{}",response);
        if(StringUtils.isNotBlank(response)){
            JSONObject jsonObject = JSONObject.parseObject(response);
            String ipList = jsonObject.getString("ip_list");
            List<String> strings = JSONObject.parseArray(ipList, String.class);

            logger.info("ip_list :{}",strings);
            return ipList;
        }else{
            return "";
        }
    }

    //??
    private static String qrconnect_url = "https://open.weixin.qq.com/connect/qrconnect";

    /**
     * 跳转到授权页面
     * 官方文档:https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140842
     */
    public String toOauth(HttpServletResponse response,Integer type) throws IOException {
        //用户认证授权页面
        String authorize_uri = "https://open.weixin.qq.com/connect/oauth2/authorize";

        String calbackUrl= configProperties.getProjectDomain()+"/process/oauth";
        //是否静默授权(true 是),静默授权只能获取openId
        boolean snsapiBase;
        if(type == null){
            snsapiBase = true;
        }else{
            snsapiBase = false;
        }
        String state = "STATE";//???

        //TODO 微信对重定向请求时url后的的参数顺序有要求,这里使用TreeMap,保证组装url参数时,按照key的自然顺序拼接
        Map<String, Object> params = new TreeMap<>();
        params.put("appid", configProperties.getAppId());
        params.put("redirect_uri", calbackUrl);
        params.put("response_type", "code");
        //对于已关注公众号的用户，如果用户从公众号的会话或者自定义菜单进入本公众号的网页授权页，即使是scope为snsapi_userinfo，也是静默授权，用户无感知
        if(snsapiBase) {
            params.put("scope", "snsapi_base");
        } else {
            params.put("scope", "snsapi_userinfo");
        }

        if(StringUtils.isBlank(state)) {
            params.put("state", "wx");
        } else {
            params.put("state", state);
        }
        logger.info("requestParam :{}",JSONObject.toJSONString(params));
        //拼接回调地址url,对参数进行encode编码
        String redirectRequestUrl = HttpUtils.getMethodParamHandle(authorize_uri, params, HttpUtils.UTF8);

        redirectRequestUrl = redirectRequestUrl+"#wechat_redirect";

        logger.info("重定向链接:{}",redirectRequestUrl);
//        response.sendRedirect(redirectRequestUrl);
        return redirectRequestUrl;
    }

    /**
     * 处理授权信息(用户授权回调处理)    用code换取用户的access_token
     * 注意：由于公众号的secret和获取到的access_token安全级别都非常高，必须只保存在服务器，不允许传给客户端。后续刷新access_token、通过access_token获取用户信息等步骤，也必须从服务器发起。
     * 如果网页授权的作用域为snsapi_base，则本步骤中获取到网页授权access_token的同时，也获取到了openid，snsapi_base式的网页授权流程即到此为止。
     * @param code  code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     * @param state
     * @throws IOException
     */
    public WxUserAuthInfoResult processOauth(String code, String state) throws IOException {
        //获取授权信息api
        String url= "https://api.weixin.qq.com/sns/oauth2/access_token";

        Map<String, Object> params = new HashMap();
        params.put("appid", configProperties.getAppId());
        params.put("secret", configProperties.getAppSecret());
        params.put("code", code);
        params.put("grant_type", "authorization_code");

        logger.info("授权请求参数:{}",JSONObject.toJSONString(params));
        String response = HttpUtils.httpsGet(url, params, null);
        logger.info("用户授权信息获取结果:{}",response);
        WxUserAuthInfoResult result = new WxUserAuthInfoResult();
        if(StringUtils.isNotBlank(response)){
            JSONObject responseJson = JSONObject.parseObject(response);

            logger.info("access_token:{}",responseJson.getString("access_token"));
            logger.info("expires_in:{}",responseJson.getString("expires_in"));
            logger.info("refresh_token:{}",responseJson.getString("refresh_token"));
            logger.info("openid:{}",responseJson.getString("openid"));
            logger.info("scope:{}",responseJson.getString("scope"));

            result.setAccessToken(responseJson.getString("access_token"));
            result.setExpiresIn(responseJson.getInteger("expires_in"));
            result.setRefreshToken(responseJson.getString("refresh_token"));
            result.setOpenid(responseJson.getString("openid"));
            result.setScope(responseJson.getString("scope"));

            //TODO 需要暂存openid和refresh_token,入库后可清除(或均使用缓存数据)

        }else{
            logger.info("用户授权信息获取失败");
        }

        return result;
    }

    /**
     * 使用refresh_token刷新用户的access_token(refresh_token有效期为30天)
     * 注意：由于公众号的secret和获取到的access_token安全级别都非常高，必须只保存在服务器，不允许传给客户端。后续刷新access_token、通过access_token获取用户信息等步骤，也必须从服务器发起。
     * @throws IOException
     */
    public void refreshUserAccessToken() throws IOException {
        String url= "https://api.weixin.qq.com/sns/oauth2/refresh_token";

        Map<String, Object> params = new HashMap();
        params.put("appid", configProperties.getAppId());
        params.put("grant_type", "refresh_token");
        String refreshToken = "";//TODO 需要实时获取
        params.put("refresh_token", refreshToken);
        params.put("grant_type", "authorization_code");

        String response = HttpUtils.httpsGet(url, params, null);
        logger.info("刷新用户授权信息结果:{}",response);
        if(StringUtils.isNotBlank(response)){
            JSONObject responseJson = JSONObject.parseObject(response);
            logger.info("access_token:{}",responseJson.getString("access_token"));
            logger.info("expires_in:{}",responseJson.getString("expires_in"));
            logger.info("refresh_token:{}",responseJson.getString("refresh_token"));
            logger.info("openid:{}",responseJson.getString("openid"));
            logger.info("scope:{}",responseJson.getString("scope"));
        }else{
            logger.info("刷新用户授权信息失败");
        }

    }

    /**
     * 检验授权凭证（access_token）是否有效
     * @param openid
     * @param accessToken
     * @throws IOException
     */
    public boolean checkUserAccessTokenByOpenId(String openid,String accessToken) throws IOException {
        String url= "https://api.weixin.qq.com/sns/auth";

        Map<String, Object> params = new HashMap();
        params.put("access_token", accessToken);
        params.put("openid", openid);

        String response = HttpUtils.httpsGet(url, params, null);
        logger.info("检验授权凭证结果:{}",response);
        if(StringUtils.isNotBlank(response)){
            JSONObject responseJson = JSONObject.parseObject(response);
            Integer errcode = responseJson.getInteger("errcode");
            if(errcode == 0){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }


    //用户关注公众号,可以获取到用户的openId


}

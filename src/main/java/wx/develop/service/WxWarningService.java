package wx.develop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wx.develop.constant.ConfigProperties;
import wx.develop.utils.RedisUtils;

/**
 * 微信报警相关数据获取
 * Created by lzh on 2017/7/11.
 */
@Service("wxWarningService")
public class WxWarningService {

    private static Logger logger =  LoggerFactory.getLogger(WxWarningService.class);

    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private WxAuthService wxAuthService;

    /**
     *
     * @return
     */
    public String getWarningList() {
//        String url = "https://api.weixin.qq.com/cgi-bin/getcallbackip";
//        //获取accessToken
//        String accessToken = redisUtils.get("wx_access_token", String.class);
//        if(StringUtils.isBlank(accessToken)){
//            accessToken = wxAuthInofService.refreshAccessToken();
//        }
//        Map<String, Object> param = new TreeMap<>();
//        param.put("access_token",accessToken);
//
//        String response = HttpUtils.httpsGet(url, param, HttpUtils.UTF8.toString());
//        logger.debug("response :{}",response);
//        if(StringUtils.isNotBlank(response)){
//            JSONObject jsonObject = JSONObject.parseObject(response);
//            String ipList = jsonObject.getString("ip_list");
//            List<String> strings = JSONObject.parseArray(ipList, String.class);
//
//            logger.debug("ip_list :{}",strings);
//            return ipList;
//        }else{
//            return "";
//        }

        return "";
    }


}

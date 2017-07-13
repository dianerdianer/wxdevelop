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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 微信用户操作相关
 * Created by lzh on 2017/7/11.
 */
@Service("wxUserService")
public class WxUserService {

    private static Logger logger =  LoggerFactory.getLogger(WxUserService.class);

    @Autowired
    private WxAuthService wxAuthService;

    /**
     * 通过 openid 获取用户基本信息
     * 用户管理类接口中的“获取用户基本信息接口”，是在用户和公众号产生消息交互或关注后事件推送后，才能根据用户OpenID来获取用户基本信息。这个接口，包括其他微信接口，都是需要该用户（即openid）关注了公众号后，才能调用成功的。
     * @param openid
     * @return
     * @throws Exception
     */
    public HashMap<String, Object> getUserInfoByOpenid(String openid) throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/user/info";
        HashMap<String, Object> params = new HashMap<String, Object>();
        // TODO access_token应该是用户的accessToken和openId对应
        params.put("access_token","");
        params.put("openid", openid);  //需要获取的用户的 openid
        params.put("lang", "zh_CN");
        String subscribers = HttpUtils.httpsGet(url, params,null);
        System.out.println(subscribers);
        params.clear();
        //这里返回参数只取了昵称、头像、和性别
        params.put("subscribe", JSONObject.parseObject(subscribers).getString("subscribe"));  //用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。

        params.put("openid", JSONObject.parseObject(subscribers).getString("openid"));  //用户的标识，对当前公众号唯一
        params.put("nickname",JSONObject.parseObject(subscribers).getString("nickname")); //昵称
        params.put("sex", JSONObject.parseObject(subscribers).getString("sex"));  //性别
        params.put("city", JSONObject.parseObject(subscribers).getString("city"));  //用户所在城市
        params.put("country", JSONObject.parseObject(subscribers).getString("country"));  //用户所在国家
        params.put("province", JSONObject.parseObject(subscribers).getString("province"));  //用户所在省份
        params.put("language", JSONObject.parseObject(subscribers).getString("language"));  //用户的语言，简体中文为zh_CN
        params.put("headimgurl",JSONObject.parseObject(subscribers).getString("headimgurl"));  //用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
        //http://wx.qlogo.cn/mmopen/xbIQx1GRqdvyqkMMhEaG3nz/0

        params.put("subscribe_time", JSONObject.parseObject(subscribers).getString("subscribe_time"));  //用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
        params.put("unionid", JSONObject.parseObject(subscribers).getString("unionid"));  //只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
        params.put("remark", JSONObject.parseObject(subscribers).getString("remark"));  //公众号运营者对粉丝的备注，公众号运营者可在微信公众平台用户管理界面对粉丝添加备注
        params.put("groupid", JSONObject.parseObject(subscribers).getString("groupid"));  //用户所在的分组ID（兼容旧的用户分组接口）
        params.put("tagid_list", JSONObject.parseObject(subscribers).getString("tagid_list"));  //用户被打上的标签ID列表

        return params;
    }


    /**
     * 获取用户列表(openId)
     * @param nextOpenid
     * @return
     * @throws Exception
     */
    public HashMap<String, Object> getUserList(String nextOpenid) throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/user/get";
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("access_token",wxAuthService.getAccessToken());  //定时器中获取到的 token
        params.put("next_openid", nextOpenid);  //需要获取的用户的 openid
        String subscribers = HttpUtils.httpsGet(url, params,null);
        System.out.println(subscribers);
        params.clear();
        //这里返回参数只取了昵称、头像、和性别
        params.put("total",JSONObject.parseObject(subscribers).getString("total")); //关注该公众账号的总用户数
        params.put("count",JSONObject.parseObject(subscribers).getString("count"));  //拉取的OPENID个数，最大值为10000
        params.put("data", JSONObject.parseObject(subscribers).getString("data"));  //列表数据，OPENID的列表
        params.put("next_openid", JSONObject.parseObject(subscribers).getString("next_openid"));  //拉取列表的最后一个用户的OPENID,为空表示已经拉取完所有数据

        return params;
    }


}

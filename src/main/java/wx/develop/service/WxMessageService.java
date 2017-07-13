package wx.develop.service;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wx.develop.utils.HttpUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 微信公众号消息关系
 * Created by lzh on 2017/7/11.
 */
@Service("wxMessageService")
public class WxMessageService {

    private static Logger logger =  LoggerFactory.getLogger(WxMessageService.class);

    @Autowired
    private WxAuthService wxAuthService;




}

package wx.develop.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wx.develop.service.WxAuthService;
import wx.develop.utils.RedisUtils;

/**
 * Created by lzh on 2017/7/11.
 */
@Component("refreshWxInfo")
public class RefreshWxInfo {

    @Autowired
    private WxAuthService wxAuthService;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 每小时1次,微信accessToken有效期2小时
     */
    /**  秒 分 时 天 月 年 */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void refreshAccessToken(){
        wxAuthService.refreshAccessToken();
    }

}

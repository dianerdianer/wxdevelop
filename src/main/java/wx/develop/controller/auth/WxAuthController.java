package wx.develop.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import wx.develop.base.Result;
import wx.develop.base.ValidationUtils;
import wx.develop.exception.ValidationException;
import wx.develop.service.WxAuthService;
import wx.develop.vo.form.WxAuthTmpForm;
import wx.develop.vo.form.WxTokenAuthForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 微信开发者模式,认证-授权相关
 * Created by lzh on 2017/6/20.
 */
@Controller
public class WxAuthController {

    private static Logger logger =  LoggerFactory.getLogger(WxAuthController.class);

    @Autowired
    private WxAuthService wxAuthService;

    @Autowired
    private ValidationUtils validationUtils;

    /**
     * 开发者模式 验证消息
     * @return
     */
    @RequestMapping(value = "/msg",method = RequestMethod.GET)
    @ResponseBody
    public String tokenGet(HttpServletRequest request, HttpServletResponse response, @ModelAttribute WxTokenAuthForm form, HttpSession session, BindingResult br) throws ValidationException {
        return wxAuthService.verifyDevMsg(form);
    }

    /**
     * 手动刷新微信公众号access_token
     * @return
     */
    @RequestMapping(value = "/refresh/access/token",method = RequestMethod.GET)
    @ResponseBody
    public String refreshAccessToken(HttpServletRequest request, HttpServletResponse response, @ModelAttribute WxTokenAuthForm form, HttpSession session, BindingResult br) throws ValidationException {
        return wxAuthService.refreshAccessToken();
    }

    /**
     * 获取微信公众号access_token
     * @return
     */
    @RequestMapping(value = "/get/access/token",method = RequestMethod.GET)
    @ResponseBody
    public String getAccessToken(HttpServletRequest request, HttpServletResponse response, @ModelAttribute WxTokenAuthForm form, HttpSession session, BindingResult br) throws ValidationException {
        return wxAuthService.getAccessToken();
    }

    /**
     * 跳转授权页面
     * @return
     */
    @RequestMapping(value = "/to/oauth",method = RequestMethod.GET)
//    @ResponseBody

    public String toOauth(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ValidationException, IOException {
        String redirectRequestUrl = wxAuthService.toOauth(response);
        return "redirect:" + redirectRequestUrl;
//        return Result.success(true);
    }

    /**
     * 处理授权请求
     * @return
     */
    @RequestMapping(value = "/process/oauth")
    @ResponseBody
    public String processOauth(HttpServletRequest request, HttpServletResponse response, HttpSession session, @ModelAttribute WxAuthTmpForm form,  BindingResult br) throws ValidationException, IOException {
        return Result.success(wxAuthService.processOauth(form.getCode(),form.getState()));
    }
}

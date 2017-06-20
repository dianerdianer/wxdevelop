package wx.develop.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wx.develop.base.ParamsValidHandler;
import wx.develop.base.Result;
import wx.develop.base.ValidationUtils;
import wx.develop.constant.ConfigProperties;
import wx.develop.exception.ValidationException;
import wx.develop.utils.SHAUtil;
import wx.develop.vo.form.WxTokenAuthForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by lzh on 2017/6/20.
 */
@Controller
@RequestMapping("/wx")
public class WxTokenController {

    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    private ValidationUtils validationUtils;

    /**
     *
     * @return
     */
    @RequestMapping("/token")
    @ResponseBody
    public String accessToken(HttpServletRequest request, HttpServletResponse response, @ModelAttribute WxTokenAuthForm form, HttpSession session, BindingResult br) throws ValidationException {
        System.out.println(form);
        System.out.println(form.getSignature());
        System.out.println(form.getTimestamp());
        System.out.println(form.getNonce());
        System.out.println(form.getEchostr());

//        ParamsValidHandler.create(validationUtils).handle(br);
        String[] arr = new String[] { configProperties.getWxToken(), form.getTimestamp()+"", form.getNonce() +""};
        // 将 token、timestamp、nonce 三个参数进行字典序排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行 sha1 加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = SHAUtil.byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        content = null;
        // 将 sha1 加密后的字符串可与 signature 对比，标识该请求来源于微信
        if(tmpStr != null ? tmpStr.equals(form.getSignature().toUpperCase()) : false){
            return form.getEchostr();
        }else{
            return "false";
        }
//        return Result.success(tmpStr != null ? tmpStr.equals(form.getSignature().toUpperCase()) : false);
    }



}

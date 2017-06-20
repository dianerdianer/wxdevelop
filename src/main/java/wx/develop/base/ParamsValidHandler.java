package wx.develop.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import wx.develop.exception.ValidationException;

/**
 * Created by wuyujia on 2017/4/15.
 */
public class ParamsValidHandler {

    private ValidationUtils validationUtils;

    private static Logger logger = LoggerFactory.getLogger(ParamsValidHandler.class);

    /**
     * 私有构造方法
     */
    private ParamsValidHandler() {
    }

    private ParamsValidHandler(ValidationUtils validationUtils) {
        this.validationUtils = validationUtils;
    }

    /**
     * 创建参数校验器
     * @param validationUtils
     * @return
     */
    public static ParamsValidHandler create(ValidationUtils validationUtils) {
        ParamsValidHandler paramsValidHandler = new ParamsValidHandler(validationUtils);
        return paramsValidHandler;
    }

    /**
     * 校验异常信息
     * @param result
     * @return
     * @throws ValidationException
     */
    public ParamsValidHandler handle(BindingResult result) throws ValidationException {

        validationUtils.checkFieldError(result);

        return this;
    }
}

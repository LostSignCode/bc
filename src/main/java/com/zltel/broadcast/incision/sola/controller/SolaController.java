package com.zltel.broadcast.incision.sola.controller;

import com.zltel.broadcast.incision.sola.service.SolaProgramService;
import com.zltel.broadcast.incision.sola.utils.DESUtil;
import com.zltel.broadcast.incision.sola.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 索浪接口相关
 * SolaController class
 *
 * @author Touss
 * @date 2018/5/17
 */
@Controller
@RequestMapping(value = "/sola")
@PropertySource("classpath:sola.properties")
public class SolaController {
    @Value("${user.loginname}")
    private String loginname;
    @Value("${user.password}")
    private String password;
    @Value("${url.edit}")
    private String editUrl;
    @Value("${url.view}")
    private String viewUrl;
    @Value("${url.intermediate}")
    private String intermediateUrl;
    @Value("${user.org}")
    private int org;

    @Autowired
    private SolaProgramService solaProgramService;

    @GetMapping(value = "/edit/{id}")
    public String edit(@PathVariable("id") int id) throws UnsupportedEncodingException {
        return next(id, editUrl);
    }

    @GetMapping(value = "/view/{id}")
    public String view(@PathVariable("id") int id) throws UnsupportedEncodingException {
        return next(id, viewUrl);
    }

        @GetMapping(value = "/terminals")
    @ResponseBody
    public Object terminals() {
        return solaProgramService.queryTerminal();
    }

    private String next(int id, String url) throws UnsupportedEncodingException {
        Map<String, Object> access = new HashMap<String, Object>();
        access.put("LoginName", loginname);
        access.put("LoginPwd", password);
        String sign = DESUtil.encode(JsonUtils.jsonParams(access));
        String next = URLEncoder.encode(url + "?pkId=" + id, "UTF-8");
        return "redirect:" + intermediateUrl + "?OrgId=" + org + "&Sign=" + sign + "&Url=" + next;
    }
}

package com.qdc.demoeurekazuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Component
public class IPFilter extends ZuulFilter {


    private String[] whitelist;
    @Value("${yxwfilter.ip.whitelist}")
    private String strIpWhitelist;
    @Value("${yxwfilter.ip.whitelistenabled}")
    private String WhitelistEnabled;

    @Override
    public Object run() throws ZuulException {

        System.out.println(strIpWhitelist);
        whitelist = strIpWhitelist.split("//,");

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest req = ctx.getRequest();
        String ipAddr = this.getIpAddr(req);
        System.out.println("请求IP地址为:["+ipAddr+"]");
        //配置本地IP地址白名单，生产环境可放入数据库或者Redis中
        List<String> ips = new ArrayList<String>();
        for (int i = 0;i<whitelist.length; ++i){
            System.out.println(whitelist[i]);//这里输出abc
            ips.add(whitelist[i]);
        }
        System.out.println("whitelist:" + ips.toString());
        //配置本地IP地址白名单，生产环境可放入数据库或Redis中
        if(!ips.contains(ipAddr)){
            System.out.println("未通过IP地址校验.["+ipAddr+"]");
            ctx.setResponseStatusCode(401);
            ctx.setSendZuulResponse(false);
            ctx.getResponse( ).setContentType("application/json;charset=UTF-8");
            ctx.setResponseBody("{\"errrocode\":\"e0e01\",\"errmsg\":\"IpAddr is forbidden![" + ipAddr + "]\"}");
        }


        return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        if("true".equalsIgnoreCase(WhitelistEnabled))
            return true;
        else {
            return false;
        }
    }

    public String getIpAddr(HttpServletRequest request){
        String ip =request.getHeader("X-Forwarded-For");
        if(ip == null || ip.length() == 0 || "unkonwn".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unkonwn".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unkonwn".equalsIgnoreCase(ip)){
            ip = request.getHeader("HTTP-CLIENT-IP");
        }
        if(ip == null || ip.length() == 0 || "unkonwn".equalsIgnoreCase(ip)){
            ip = request.getHeader("HTTP-X-FORWARDED-FOR");
        }
        if(ip == null || ip.length() == 0 || "unkonwn".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }
        return ip;
    }





}

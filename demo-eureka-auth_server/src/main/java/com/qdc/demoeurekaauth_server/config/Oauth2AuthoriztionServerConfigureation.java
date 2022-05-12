package com.qdc.demoeurekaauth_server.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Component;


@Configuration
@Component
public class Oauth2AuthoriztionServerConfigureation extends AuthorizationServerConfigurerAdapter {
  @Autowired
  private UserDetailsService userDetailsService;
  @Autowired
  private DruidDataSource druidDataSource;

  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    //这对于access token认证很重要，只有具有ROLE_TRUSTED_CLIENT权限的客户才可以通过认证
    //所以要将代表oauth_client_details中客户端记录的authorities字段设置为ROLE_TRUSTED_CLIENT
    //这样才能是客户端满足条件
    security.checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
  }
  @Override

  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.withClientDetails(new JdbcClientDetailsService(druidDataSource));
  }

  //配置认证服务器的非安全属性，总之一切都通过数据库管理
  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    //用户信息查询服务
    endpoints.userDetailsService(userDetailsService);
    //数据库管理access token和refresh token
    TokenStore tokenStore=new JdbcTokenStore(druidDataSource);
    //endpoints.tokenStore(tokenStore);
    DefaultTokenServices tokenServices=new DefaultTokenServices();
    tokenServices.setTokenStore(tokenStore);
    tokenServices.setSupportRefreshToken(true);
    tokenServices.setClientDetailsService(new JdbcClientDetailsService(druidDataSource));
    tokenServices.setAccessTokenValiditySeconds(38000);
    //tokenServices.setRefreshTokenValiditySeconds(180);
    endpoints.tokenServices(tokenServices);
    //数据库管理授权码
    endpoints.authorizationCodeServices(new JdbcAuthorizationCodeServices(druidDataSource));
    //数据库管理授权信息
    ApprovalStore approvalStore=new JdbcApprovalStore(druidDataSource);
    endpoints.approvalStore(approvalStore);
  }

}

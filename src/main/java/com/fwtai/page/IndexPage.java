package com.fwtai.page;

import com.fwtai.tool.ToolClient;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;

/**
 * 前端模版引擎用法
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2020-09-29 12:48
 * @QQ号码 444141300
 * @Email service@dwlai.com
 * @官网 http://www.fwtai.com
*/
public final class IndexPage implements Handler<RoutingContext>{

  private final ThymeleafTemplateEngine thymeleaf;//Thymeleaf模版引擎

  public IndexPage(final Vertx vertx){
    thymeleaf = ThymeleafTemplateEngine.create(vertx);
  }

  @Override
  public void handle(final RoutingContext context){
    final JsonObject json = new JsonObject();
    json.put("name","从后端返回数据");
    json.put("age","35");
    thymeleaf.render(json,"templates/index.html",bufferAsyncResult ->{
      if(bufferAsyncResult.succeeded()){
        context.response().putHeader("content-type","text/html;charset=utf-8").end(bufferAsyncResult.result());
      } else {
        ToolClient.responseFailure(context,"加载页面失败");
      }
    });
  }
}
package com.fwtai;

import com.fwtai.page.IndexPage;
import com.fwtai.tool.ToolClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.HashSet;
import java.util.Set;

public class LaunchVerticle extends AbstractVerticle {

  //第一步,声明router,如果有重复的 path 路由的话,它匹配顺序是从上往下的,仅会执行第一个.那如何更改顺序呢？可以通过 order(x)来更改顺序,值越小越先执行!
  private Router router;

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {

    //创建HttpServer
    final HttpServer server = vertx.createHttpServer();

    //第二步,初始化|实例化 Router,若要添加跨域请求的话,随着就配置跨域
    router = Router.router(vertx);
    final Set<HttpMethod> methods = new HashSet<>();
    methods.add(HttpMethod.OPTIONS);
    methods.add(HttpMethod.GET);
    methods.add(HttpMethod.POST);
    //此处只能用 handler,不能使用 blockingHandler,否则会Internal Server Error报错!!!
    router.route().handler(CorsHandler.create("http://192.168.3.108").allowCredentials(true).allowedHeader("content-type").maxAgeSeconds(86400).allowedMethods(methods));
    //此处只能用 handler,不能使用 blockingHandler,否则会报Internal Server Error错!!!
    router.route().handler(BodyHandler.create());//支持文件上传的目录,ctrl + p 查看

    //第三步,配置Router解析url;前端模版引擎用法,http://127.0.0.1/thymeleaf2
    router.route("/").blockingHandler(new IndexPage(vertx));

    router.route("/*").handler(StaticHandler.create()); //配置静态资源访问

    //router.route("/handle").blockingHandler(context->{});
    //router.route("/handle").blockingHandler((context)->{});//推荐使用此方式
    router.post("/handle").handler((context)->{
      final HttpServerRequest request = context.request();
      final String path = request.getParam("path");
      final String expression = request.getParam("expression");
      ToolClient.responseSucceed(context,"操作成功");
    });

    //第四步,将router和 HttpServer 绑定[若是使用配置文件则这样实例化,如果不配置文件则把它挪动到lambda外边即可]
    server.requestHandler(router).listen(80,http -> {
      if (http.succeeded()){
        startPromise.complete();
        System.out.println("---应用启动成功---");
      } else {
        //startPromise.fail(http.cause());
        System.out.println("---Launcher应用启动失败---"+http.cause());
      }
    });
  }
}
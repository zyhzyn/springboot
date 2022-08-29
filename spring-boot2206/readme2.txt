实现发表文章的功能

流程:
1:用户在首页点击发表文章的超链接来到发表文章页面
2:在页面输入文章标题和文章内容并点击发表按钮
3:服务端将该文章保存后响应发表结果页面(成功或失败)

实现:
1:在static下新建对应的页面
  1.1:writeArticle.html 发表文章页面
      页面form表单action指定的值"/writeArticle"
  1.2:article_success.html 发表成功提示页面
  1.3:article_fail.html 发表失败提示页面
2:在controller包下新建处理文章相关的业务类:ArticleController
  并定义处理发表文章的方法:writeArticle()
  注意:@Controller注解和@RequestMapping注解不要忘记!!!!

3:在entity包下新建表示文章的对象:Article并实现序列化接口
4:在writeArticle方法中将表单提交上来的标题和文章内容以Article对象形式序列化到目录articles
  下文件名格式:标题.obj
  保存后响应发表成功。如果标题或内容没有输入则响应发表失败页面。
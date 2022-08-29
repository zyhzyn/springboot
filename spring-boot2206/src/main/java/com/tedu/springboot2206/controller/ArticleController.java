package com.tedu.springboot2206.controller;

import com.tedu.springboot2206.entity.Article;
import com.tedu.springboot2206.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ArticleController {
    private static File articleDir;
    static {
        articleDir = new File("articles");
        if(!articleDir.exists()){
            articleDir.mkdirs();
        }
    }
    @RequestMapping("/deleteArticle")
    public void deleteArticle(HttpServletRequest request,HttpServletResponse response){
        String title = request.getParameter("title");

        File file = new File(articleDir,title+".obj");
        file.delete();

        try {
            response.sendRedirect("/articleList");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequestMapping("/articleList")
    public void articleList(HttpServletRequest request,HttpServletResponse response){
        System.out.println("开始处理文章列表!!!!!!!!!!!");
        //1反序列化所有的文章信息
        List<Article> articleList = new ArrayList<>();
        File[] subs = articleDir.listFiles(f->f.getName().endsWith(".obj"));
        for(File sub : subs){
            try (
                    FileInputStream fis = new FileInputStream(sub);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ){
               Article article = (Article)ois.readObject();
               articleList.add(article);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        //2
        response.setContentType("text/html;charset=utf-8");
        try {
            PrintWriter pw = response.getWriter();
            pw.println("<!DOCTYPE html>");
            pw.println("<html lang=\"en\">");
            pw.println("<head>");
            pw.println("<meta charset=\"UTF-8\">");
            pw.println("<title>文章列表</title>");
            pw.println("</head>");
            pw.println("<body>");
            pw.println("<center>");
            pw.println("<h1>文章列表</h1>");
            pw.println("<table border=\"1\">");
            pw.println("<tr>");
            pw.println("<td>标题</td>");
            pw.println("<td>作者</td>");
            pw.println("<td>操作</td>");
            pw.println("</tr>");

            for(Article article : articleList) {
                pw.println("<tr>");
                pw.println("<td>"+article.getTitle()+"</td>");
                pw.println("<td>"+article.getAuthor()+"</td>");
                pw.println("<td><a href='/deleteArticle?title="+ article.getTitle() +"'>删除</a></td>");
                pw.println("</tr>");
            }

            pw.println("</table>");
            pw.println("</center>");
            pw.println("</body>");
            pw.println("</html>");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @RequestMapping("/writeArticle")
    public void writeArticle(HttpServletRequest request, HttpServletResponse response){
        System.out.println("处理发表文章!!!!!!!!!!!");
        //获取表单数据
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String content = request.getParameter("content");
        System.out.println(title+","+author+","+content);

        //数据验证
        if(title==null||title.trim().isEmpty()||
           author==null||author.trim().isEmpty()||
           content==null||content.trim().isEmpty()){
            try {
                response.sendRedirect("/article_fail.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        Article article = new Article(title,author,content);

        File file = new File(articleDir,title+".obj");
        try (
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ){
           oos.writeObject(article);
           response.sendRedirect("/article_success.html");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

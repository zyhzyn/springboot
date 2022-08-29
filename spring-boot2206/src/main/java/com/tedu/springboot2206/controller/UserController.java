package com.tedu.springboot2206.controller;

import com.tedu.springboot2206.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    //表示保存所有用户信息的目录users
    private static File userDir;

    static{
        userDir = new File("./users");
        if(!userDir.exists()){
           userDir.mkdirs();
        }
    }

    @RequestMapping("/deleteUser")
    public void delete(HttpServletRequest request,HttpServletResponse response){
        System.out.println("开始处理删除用户!!!!!!!!!!!!!!!!!!!!!!!");
        //http://localhost:8080/deleteUser?username=fancq
        String username = request.getParameter("username");
        System.out.println("要删除的用户是:"+username);

        File file = new File(userDir,username+".obj");
        file.delete();

        try {
            response.sendRedirect("/userList");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/userList")
    public void userList(HttpServletRequest request,HttpServletResponse response){
        System.out.println("开始处理动态页面!!!!!!!!!!!!!!!!!!!");
        /*
            1:读取users目录下的所有obj文件并反序列化然后将得到的所有User对象存入一个List集合
            2:生成HTML页面并将所用用户信息体现在其中将其发送给浏览器
         */
        //1
        List<User> userList = new ArrayList<>();
        //1.1获取users目录中的所有obj文件
        File[] subs = userDir.listFiles(f->f.getName().endsWith(".obj"));
        //1.2将每个文件都反序列化得到User对象
        for(File file : subs){
            try (
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ){
                User user = (User)ois.readObject();
                userList.add(user);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println(userList);

        //2
        try {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter pw = response.getWriter();
            pw.println("<!DOCTYPE html>");
            pw.println("<html lang=\"en\">");
            pw.println("<head>");
            pw.println("<meta charset=\"UTF-8\">");
            pw.println("<title>用户列表</title>");
            pw.println("</head>");
            pw.println("<body>");
            pw.println("<center>");
            pw.println("<h1>用户列表</h1>");
            pw.println("<table border=\"1\">");
            pw.println("<tr>");
            pw.println("<td>用户名</td>");
            pw.println("<td>密码</td>");
            pw.println("<td>昵称</td>");
            pw.println("<td>年龄</td>");
            pw.println("<td>操作</td>");
            pw.println("</tr>");

            for(User user : userList) {
                pw.println("<tr>");
                pw.println("<td>"+user.getUsername()+"</td>");
                pw.println("<td>"+user.getPassword()+"</td>");
                pw.println("<td>"+user.getNickname()+"</td>");
                pw.println("<td>"+user.getAge()+"</td>");
                /*
                    http://localhost:8080/deleteUser?username=fancq

                    "<td><a href='/deleteUser?username='>删除</a></td>"
                    "<td><a href='/deleteUser?username=" + str + "'>删除</a></td>"

                    <td><a href='/deleteUser?username=fancq'>删除</a></td>

                 */
                pw.println("<td><a href='/deleteUser?username="+ user.getUsername() +"'>删除</a></td>");
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

    @RequestMapping("/loginUser")
    public void login(HttpServletRequest request,HttpServletResponse response){
        System.out.println("开始处理登录!!!!!!!!!!!!!!!!!!!!!!");
        //1
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if(username==null||username.isEmpty()||password==null||password.isEmpty()){
            try {
                response.sendRedirect("/login_info_error.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        //2
        //根据登录用户的用户名去users目录下寻找该用户信息
        File file = new File(userDir,username+".obj");
        if(file.exists()){//文件存在则说明该用户存在(用户名输入正确)
            //反序列化文件中该用户曾经的注册信息
            try (
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ){
                User user = (User)ois.readObject();
                //比较登录密码和注册时输入的密码是否一致
                // a = "abc123"    b = "AbC123"
                //a.equals(b) ==> false
                //a.equalsIgnoreCase(b) ==> true
                if(user.getPassword().equals(password)){
                    //登录成功
                    response.sendRedirect("/login_success.html");
                    return;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        //登录失败
        try {
            response.sendRedirect("/login_fail.html");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     *
     * @param request   请求对象，封装着浏览器发送过来的所有内容
     * @param response  响应对象，封装着我们即将给浏览器回复的内容
     */
    @RequestMapping("/regUser")
    public void reg(HttpServletRequest request, HttpServletResponse response){
        System.out.println("开始处理注册！！！！！！！！！！！！！！");
        /*
            处理注册的大致过程
            1:获取用户在注册页面上输入的注册信息(通过请求对象获取浏览器提交的表单数据)
            2:处理注册
            3:设置响应对象，将处理结果回馈给浏览器
         */
        //1获取注册页面上的表单信息
        /*
            HttpServletRequest的重要方法:
            String getParameter(String name)
            获取浏览器传递过来的某个参数的值
            这里的传入的是参数名，对应的是页面表单输入框的名字(name属性对应的值)
         */
        String username = request.getParameter("username");//获取用户名
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String ageStr = request.getParameter("age");
        System.out.println(username+","+password+","+nickname+","+ageStr);
        /*
            String username = request.getParameter("username");
            作用:获取浏览器传递过来的参数username对应的值
            返回的字符串可能存在2种特殊情况:
            1:返回空字符串,说明用户在该输入框上没有输入任何内容
            2:返回null,说明没有传递该参数过来
         */
        if(username==null||username.isEmpty()||password==null||password.isEmpty()||
          nickname==null||nickname.isEmpty()||ageStr==null||ageStr.isEmpty()||
          !ageStr.matches("[0-9]+")){
            try {
                response.sendRedirect("/reg_info_error.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        int age = Integer.parseInt(ageStr);//将年龄转换为int值
        /*
            2
            将该注册用户信息以User对象形式表示并序列化到文件中保存

            将来所有的保存用户信息的文件都统一放在users目录下，并且每个保存用户的文件的名字格式:用户名.obj

         */
        User user = new User(username,password,nickname,age);
        /*
            File的重载构造器
            File(File parent,String child)
            该File对象表达的是在parent表示的目录中的子项child
         */
        //在userDir(该对象表达当前项目目录下的users目录)目录中的文件xxx.obj(xxx就是当前注册用户名)
        //          new File(userDir,"fancq.obj")
        File file = new File(userDir,username+".obj");

        //如果该文件存在则说明这是一个重复的用户
        if(file.exists()){
            try {
                response.sendRedirect("/have_user.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try(
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //3
        try {
            /*
                让浏览器重定向到指定的路径查看处理结果页面
                这里的路径"/reg_success.html"是发送给浏览器让其理解的.
                所以浏览器还是当这里的"/"为抽象路径中第一个"/"的位置
                相当于浏览器会根据该路径请求:
                http://localhost:8080/reg_success.html
             */
            response.sendRedirect("/reg_success.html");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

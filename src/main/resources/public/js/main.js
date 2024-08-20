layui.use(['element', 'layer', 'layuimini','jquery','jquery_cookie'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        $ = layui.jquery_cookie($);

    // 菜单初始化
    $('#layuiminiHomeTabIframe').html('<iframe width="100%" height="100%" frameborder="0"  src="welcome"></iframe>')
    layuimini.initTab();

    /**
     * ⽤户退出
     * 删除cookie
     */
    $(".login-out").click(function () {
        console.log('233333');
        var ele = document.getElementById("a_username");
        ele.innerHTML = "<h2>${user.userPwd}</h2>";
        layer.confirm('确定退出系统吗？',{title: '系统提示'},function (index) {
            // 关闭询问框
            layer.close(index);
            // 删除 cookie
            $.removeCookie("userIdStr", {domain:"localhost",path:"/crm"});
            $.removeCookie("userName", {domain:"localhost",path:"/crm"});
            $.removeCookie("trueName", {domain:"localhost",path:"/crm"});
            // 跳转到登录⻚⾯ (⽗窗⼝跳转)
            window.parent.location.href = ctx + "/index";
        });

    });

});
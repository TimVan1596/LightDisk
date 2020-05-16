//退出登录
function logout() {
    layui.data('block', null); //删除test表
    layui.data('cache', null); //删除test表
    //加载层-风格2
    layer.load(1);
    //请求完关闭
    $.ajax({
        type: "post",
        url: "/shutdown",
        data: {
        },
        success: function (ret) {

            layer.closeAll('loading');

            ret = eval("(" + ret + ")");
            if (ret['code'] === 0) {
                window.location.href="/";
            } else {
                layer.msg(ret['msg'], {
                });

                //延时2秒
                setTimeout(function (){
                    window.location.href="/";
                }, 1500);
            }

        },
        error: function () {
            layer.closeAll('loading');
            layer.msg("请求注销失败");
        }
    });


}
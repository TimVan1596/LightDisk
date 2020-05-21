layui.use(['form', 'layer', 'jquery'], function () {
    var layer = layui.layer;

    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer
    $ = layui.jquery;
    // 登录过期的时候，跳出ifram框架
    if (top.location != self.location) top.location = self.location;

    // 粒子线条背景
    $(document).ready(function () {
        $('.layui-container').particleground({
            dotColor: '#7ec7fd',
            lineColor: '#7ec7fd'
        });
    });

    // 进行登录操作
    form.on('submit(login)', function (data) {
        data = data.field;
        console.log(data);
        if (data.username == '') {
            layer.msg('用户名不能为空');
            return false;
        }
        if (data.password == '') {
            layer.msg('密码不能为空');
            return false;
        }


        //登录loading
        layer.load(1);

        //种子节点的IP地址
        let seedNodeIP = data.ip_b_1+"."+data.ip_b_2+"."+data.ip_b_3+"."+data.ip_b_4;

        $.ajax({
            type: "post",
            url: "/login",
            data: {
                userName: data.username
                , passWord: data.password
                , ip: data.ip
                , port: data.uriType
                , seedNodeIP: seedNodeIP
                , seedNodePort: data.seedPort
            },
            success: function (ret) {
                layer.closeAll('loading');
                ret = eval("(" + ret + ")");
                if (ret['code'] === 0) {
                    layer.msg('登录成功', function () {
                        window.location = '../index.html';
                    });
                } else {
                    layer.msg(ret['msg'], {
                        time: 5000
                    });
                }
            },
            error: function () {
                layer.closeAll('loading');
                layer.msg("未知错误");
            }
        });


        return false;
    });

    $.ajax({
        type: "post",
        url: "/getLocalIP",
        data: {},
        success: function (ret) {

            layer.closeAll('loading');

            ret = eval("(" + ret + ")");
            if (ret['code'] === 0) {
                let ip = ret["data"];
                //给表单赋值
                form.val("login-form", {
                    "ip": ip
                });
            } else {
                layer.msg(ret['msg'], {
                });
                //给表单赋值
                form.val("login-form", {
                    "ip": "localhost"
                });
            }
        },
        error: function () {
            layer.closeAll('loading');
            layer.msg("请求IP地址出错");
            //给表单赋值
            form.val("login-form", {
                "ip": "localhost"
            });

        }
    });


});

function registry() {
    //iframe层
    layer.open({
        type: 2,
        title: '生成新钱包，请牢记！',
        shadeClose: true,
        shade: 0.8,
        area: ['520px', '620px'],
        content: ['page/newwallet.html']
    });
}

var goal = ".IPInput";
var ip_max = 255;
$(goal).bind("keydown", function (event) {
    var code = event.keyCode;
    if ((code < 48 && 8 != code && 37 != code && 39 != code && 9 != code) || (code > 57 && code < 96) || (code > 105 && 110 != code && 190 != code)) {
        return false;
    }
    if (code == 110 || code == 190 || code == 39 || code == 40) {
        $(this).next().focus().select();
        return false;
    }
    if (code == 37 || code == 38) {
        $(this).prev().focus().select();
        return false;
    }
})
$(goal).bind("keyup", function (event) {
    var code = event.keyCode;
    var value = $(this).val();
    if (value != null && value != '' && parseInt(value) > ip_max) {
        $(this).val("255");
        return false;
    }
    if ((code !== 9 && code !== 110 && code !== 190 && code !== 39 && code !== 37 && code !== 38 && code !== 40) && value != null && value != '' && parseInt(value) >= 100 && parseInt(value) <= ip_max) {
        $(this).next().focus().select();
        return false;
    }
    if (value != null && value != '' && parseInt(value) != 0) {
        value = parseInt(value);
        if (isNaN(value)) {
            $(this).val("");
        } else {
            $(this).val("" + value);
        }
    }
})
$(goal).bind("blur", function () {
    var value = $(this).val();
    if (value == null || value == '' || value == undefined) {
        $(this).css("border-color", "#F08080");
    } else {
        $(this).css("border-color", "");
    }
})
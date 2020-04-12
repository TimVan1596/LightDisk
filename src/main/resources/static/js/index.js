layui.use('layer', function () {
    var layer = layui.layer;
});


$(function () {

    // //获取余额
    // //检查"#li_indexHtml"是否加载，若加载则高亮，否则一直循环
    // var isLoadPage =  setInterval(function(){
    //     var  mod = $("#public-key").text();
    //     if(mod.length > 2){
    //         clearInterval(isLoadPage);
    //     }
    // },100);


    //获取账户信息
    getWallet();

});

//获取账户信息
function getWallet() {
    $.post("/common/GetWallet",
        {

        }
        , function (ret) {
            ret = eval("(" + ret + ")");
            if (ret['code'] === 0) {
                $("#public-key").text(ret["data"]["publickey"]);
                $("#private-key").text(ret["data"]["privatekey"]);
            } else {
                layer.msg('余额加载失败');
            }
        });
}
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
    //获取IP地址和种子节点
    getIP();

});

//获取账户信息
function getWallet() {
    $.post("/common/GetWallet",
        {}
        , function (ret) {
            ret = eval("(" + ret + ")");
            if (ret['code'] === 0) {
                $("#public-key").text(ret["data"]["publickey"]);
                $("#private-key").text(ret["data"]["privatekey"]);
            } else {
                layer.msg('信息加载失败');
                logout();

            }
        });
}

//获取IP地址和种子节点
function getIP(){
    $.post("/common/GetIP",
        {}
        , function (ret) {
            ret = eval("(" + ret + ")");
            if (ret['code'] === 0) {
                $("#uri").text(ret["data"]["URI"]);
                $("#seedNode").text(ret["data"]["SeedNode"]);
            } else {
                layer.msg('信息加载失败');
                logout();

            }
        });
}

//隐藏秘钥控件
function displayPrivateKey(){
    let privateKeyID=document.getElementById("private-key");
    let hiddenID=document.getElementById("private-key-hidden");
    let privateKeyHiddenTextID=document.getElementById("privateKey-hidden-text");
    //图标
    let privateKeyHiddenFacID=document.getElementById("privateKey-hidden-i");


    if(privateKeyID.style.display==="none"){
        privateKeyID.style.display="";
        hiddenID.style.display="none";
        privateKeyHiddenTextID.innerHTML="隐藏";

        privateKeyHiddenFacID.classList.add("fa-toggle-on");
        privateKeyHiddenFacID.classList.remove("fa-toggle-off");
    }else{
        privateKeyID.style.display="none";
        hiddenID.style.display="";
        privateKeyHiddenTextID.innerHTML="显示";
        privateKeyHiddenFacID.classList.add("fa-toggle-off");
        privateKeyHiddenFacID.classList.remove("fa-toggle-on");
    }
}




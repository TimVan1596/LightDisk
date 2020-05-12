
//下载文件
function downloadFile(url,name='file'){
    var a = document.createElement("a")
    a.setAttribute("href",url)
    a.setAttribute("download",name)
    a.setAttribute("target","_blank")
    let clickEvent = document.createEvent("MouseEvents");
    clickEvent.initEvent("click", true, true);
    a.dispatchEvent(clickEvent);
}

$(function () {

    //加载区块数据
    let height = getQueryString("height");
    let blockData =  blockSession[height+""];
    // alert(data['hash']);
    //
    // eval("let value = blockSession."+hash+";");
    // let value = blockSession.hash;
    // console.log(value);
    // alert(value);
    // let command ="let value = blockSession."+"hash"+";";
    // eval(command)
    // alert(blockSession['hash']);
    // console.log(value);
    // alert(eval(command));
    // alert(command);
    // console.log(blockSession);

    let hash = blockData["hash"];
    let difficultyTarget = blockData["difficultyTarget"];
    let timestamp = blockData["timestamp"];
    let merkleRoot = blockData["merkleRoot"];
    let nonce = blockData["nonce"];
    let prevBlockHash = blockData["prevBlockHash"];
    let time = formatterTime(parseInt(timestamp));
    let diffDate = timesFun(time);
    $("#block-height").text(height);
    $("#block-hash").text(hash);
    $("#block-difficultyTarget").text(difficultyTarget);
    $("#block-timestamp").text(timestamp);
    $("#block-data").text(time);
    $("#block-merkleRoot").text(merkleRoot);
    $("#block-nonce").text(nonce);
    $("#block-prevBlockHash").text(prevBlockHash);
    $("#block-diffDate").text(diffDate.timesString);


    // console.log(blockData);
    let transactions = blockData["transactions"];
    // console.log(transactions[0]);
    let coinbase = transactions[0];

    let coinbaseTimestamp=  coinbase.timestamp;
    let tx0Date = formatterTime(parseInt(coinbaseTimestamp));
    $("#tx0-data").text(coinbase.scriptString);
    let scriptBytes = Base64.decode(coinbase.scriptBytes);
    $("#tx0-publicKey").text(coinbase.publicKey);
    $("#tx0-hash").text(coinbase.hash);
    // $("#tx0-scriptString").text(scriptBytes);
    let file =  $.parseJSON(scriptBytes);
    let fileName = file.filename;
    let fileData = file.data;

    $("#tx0-filename").text(fileName);
    $("#tx0-data").text(fileData);

    $("#tx0-timestamp").text(coinbaseTimestamp);
    $("#tx0-date").text(tx0Date);


    layui.data('block', {
        key: height+""
        ,remove: true
    });

});
function formatterTime(timestamp) {
    // 传入时间戳
    let date = new Date(timestamp);
    let year = date.getFullYear();
    let getMonth = formatZero(date.getMonth() + 1);
    let getDay = formatZero(date.getDate());
    let getHours = formatZero(date.getHours());
    let getMinutes = formatZero(date.getMinutes());
    let getSeconds = formatZero(date.getSeconds());
    return year + "-" + getMonth + "-" + getDay + " " + getHours + ":" + getMinutes + ":" + getSeconds;
}

function formatZero(num) {
    return num < 10 ? ('0' + num) : num;
}

function timesFun(timesData) {
    //如果时间格式是正确的，那下面这一步转化时间格式就可以不用了
    var dateBegin = new Date(timesData.replace(/-/g, "/"));//将-转化为/，使用new Date
    var dateEnd = new Date();//获取当前时间
    var dateDiff = dateEnd.getTime() - dateBegin.getTime();//时间差的毫秒数
    var dayDiff = Math.floor(dateDiff / (24 * 3600 * 1000));//计算出相差天数
    var leave1 = dateDiff % (24 * 3600 * 1000)    //计算天数后剩余的毫秒数
    var hours = Math.floor(leave1 / (3600 * 1000))//计算出小时数
    //计算相差分钟数
    var leave2 = leave1 % (3600 * 1000)    //计算小时数后剩余的毫秒数
    var minutes = Math.floor(leave2 / (60 * 1000))//计算相差分钟数
    //计算相差秒数
    var leave3 = leave2 % (60 * 1000)      //计算分钟数后剩余的毫秒数
    var seconds = Math.round(leave3 / 1000);
    var timesString = '';

    if (dayDiff != 0) {
        timesString = dayDiff + '天前';
    } else if (dayDiff == 0 && hours != 0) {
        timesString = hours + '小时前';
    } else if (dayDiff == 0 && hours == 0) {
        timesString = minutes + '分钟前';
    }

    return {
        timesString: timesString
    }
}

function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

function downloadFile(url,name='file'){
    var a = document.createElement("a")
    a.setAttribute("href",url)
    a.setAttribute("download",name)
    a.setAttribute("target","_blank")
    let clickEvent = document.createEvent("MouseEvents");
    clickEvent.initEvent("click", true, true);
    a.dispatchEvent(clickEvent);
}

//通过Base64下载
function downloadFileByBase64(fileData, fileName) {
    var myBlob = dataURLtoBlob(fileData)
    var myUrl = URL.createObjectURL(myBlob)
    downloadFile(myUrl, fileName)
}


//下载文件调用JS
function onClickDownloadFileBTN() {
    downloadFileByBase64(decryptFileData, decryptFileName);
}

//
function dataURLtoBlob(dataurl) {
    var arr = dataurl.split(','), mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
    while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
    }
    return new Blob([u8arr], {type: mime});
}
let cacheSession = layui.sessionData('cache');
let decryptFileName = "";
let decryptFileData = "";

$(function () {


    let decryptString = cacheSession["decodeJsonFromPrivateKey"];
    layui.sessionData('cache', {
        key: 'decodeJsonFromPrivateKey'
        ,remove: true
    });

    console.log("解密数据:"+decryptString);

    //加载交易信息
    let coinbaseTimestamp= getQueryString("timestamp");
    let tx0Date = formatterTime(parseInt(coinbaseTimestamp));

    $("#tx0-publicKey").text(getQueryString("publicKey"));
    $("#tx0-hash").text(getQueryString("hash"));
    // $("#tx0-scriptString").text(scriptBytes);
    let file =  $.parseJSON(decryptString);
    let fileName = file.filename;
    let fileData = file.data;
    decryptFileName = fileName;
    decryptFileData = fileData;

    //省略的数据
    let omitFileData = fileData;

    //最长显示字符数字
    let MAX_LENGTH = 800;
    if(omitFileData.length > MAX_LENGTH){
        omitFileData = omitFileData.substring(0,MAX_LENGTH);
        omitFileData += "...";
    }

    $("#tx0-filename").text(fileName);
    $("#tx0-data").text(omitFileData);

    $("#tx0-timestamp").text(coinbaseTimestamp);
    $("#tx0-date").text(tx0Date);


    layui.data('block', {
        key: height+""
        ,remove: true
    });

});
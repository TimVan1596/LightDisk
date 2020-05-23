layui.use('layer', function () {
    var layer = layui.layer;

});

function onDownloadBTN() {
    //prompt层
    layer.prompt({title: '请输入文件秘钥', formType: 1, maxlength: 1000}, function (pass, index) {
        layer.close(index);

        let height = getQueryString("height");
        //加载层-风格2

        layer.load(1);
        //此处演示关闭


        $.ajax({
            type: "post",
            url: "/block/decodeJsonFromPrivateKey",
            data: {
                privateKey: pass
                , data: decryptString
            },
            success: function (ret) {

                layer.closeAll('loading');

                ret = eval("(" + ret + ")");
                if (ret['code'] === 0) {
                    let decryptString = ret["data"];
                    layui.sessionData('cache', {
                        key: 'decodeJsonFromPrivateKey'
                        , value: decryptString
                    });
                    var newTab = layer.open({
                        title: '',
                        type: 2,
                        shade: 0.2,
                        maxmin: true,
                        shadeClose: true,
                        area: ['90%', '90%'],
                        content: 'txView.html?' + "height="
                            + height,
                    });
                } else {
                    layer.msg(ret['msg'], {});
                }
            },
            error: function () {
                layer.closeAll('loading');
                layer.msg("检验私钥匹配请求错误");
            }
        });


    });
}

function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

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

let Base64 = {

    // private property
    _keyStr: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="

    // public method for encoding
    , encode: function (input) {
        var output = "";
        var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
        var i = 0;

        input = Base64._utf8_encode(input);

        while (i < input.length) {
            chr1 = input.charCodeAt(i++);
            chr2 = input.charCodeAt(i++);
            chr3 = input.charCodeAt(i++);

            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;

            if (isNaN(chr2)) {
                enc3 = enc4 = 64;
            } else if (isNaN(chr3)) {
                enc4 = 64;
            }

            output = output +
                this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
                this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);
        } // Whend

        return output;
    } // End Function encode


    // public method for decoding
    , decode: function (input) {
        var output = "";
        var chr1, chr2, chr3;
        var enc1, enc2, enc3, enc4;
        var i = 0;

        input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
        while (i < input.length) {
            enc1 = this._keyStr.indexOf(input.charAt(i++));
            enc2 = this._keyStr.indexOf(input.charAt(i++));
            enc3 = this._keyStr.indexOf(input.charAt(i++));
            enc4 = this._keyStr.indexOf(input.charAt(i++));

            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;

            output = output + String.fromCharCode(chr1);

            if (enc3 != 64) {
                output = output + String.fromCharCode(chr2);
            }

            if (enc4 != 64) {
                output = output + String.fromCharCode(chr3);
            }

        } // Whend

        output = Base64._utf8_decode(output);

        return output;
    } // End Function decode


    // private method for UTF-8 encoding
    , _utf8_encode: function (string) {
        var utftext = "";
        string = string.replace(/\r\n/g, "\n");

        for (var n = 0; n < string.length; n++) {
            var c = string.charCodeAt(n);

            if (c < 128) {
                utftext += String.fromCharCode(c);
            } else if ((c > 127) && (c < 2048)) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            } else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }

        } // Next n

        return utftext;
    } // End Function _utf8_encode

    // private method for UTF-8 decoding
    , _utf8_decode: function (utftext) {
        var string = "";
        var i = 0;
        var c, c1, c2, c3;
        c = c1 = c2 = 0;

        while (i < utftext.length) {
            c = utftext.charCodeAt(i);

            if (c < 128) {
                string += String.fromCharCode(c);
                i++;
            } else if ((c > 191) && (c < 224)) {
                c2 = utftext.charCodeAt(i + 1);
                string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                i += 2;
            } else {
                c2 = utftext.charCodeAt(i + 1);
                c3 = utftext.charCodeAt(i + 2);
                string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                i += 3;
            }

        } // Whend

        return string;
    } // End Function _utf8_decode

}

let blockSession = layui.sessionData('block');

let decryptString = null;

$(function () {


    //加载区块数据
    let height = getQueryString("height");
    let blockData = blockSession[height + ""];
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

    let coinbaseTimestamp = coinbase.timestamp;
    let tx0Date = formatterTime(parseInt(coinbaseTimestamp));
    let scriptString = coinbase.scriptString;
    decryptString = coinbase.scriptString;

    $("#tx0-size").text(getFileSize(getBytesLength(decryptString)));

    //最长显示字符数字
    let MAX_LENGTH = 800;
    if (scriptString.length > MAX_LENGTH) {
        scriptString = scriptString.substring(0, MAX_LENGTH);
        scriptString += "...";
    }


    $("#tx0-data").text(scriptString);
    // let scriptBytes = Base64.decode(coinbase.scriptBytes);
    // $("#tx0-publicKey").text(coinbase.publicKey);
    // $("#tx0-hash").text(coinbase.hash);
    // // $("#tx0-scriptString").text(scriptBytes);
    // let file =  $.parseJSON(scriptBytes);
    // let fileName = file.filename;
    // let fileData = file.data;
    //
    // $("#tx0-filename").text(fileName);
    // $("#tx0-data").text(fileData);
    //
    $("#tx0-timestamp").text(coinbaseTimestamp);
    $("#tx0-date").text(tx0Date);
    $("#tx0-publicKey").text(coinbase.publicKey);
    $("#tx0-hash").text(coinbase.hash);
    layui.data('block', {
        key: height + ""
        , remove: true
    });

});

function getFileSize(fileByte) {
    var fileSizeByte = fileByte;
    var fileSizeMsg = "";
    if (fileSizeByte < 1048576) fileSizeMsg = (fileSizeByte / 1024).toFixed(2) + "KB";
    else if (fileSizeByte == 1048576) fileSizeMsg = "1MB";
    else if (fileSizeByte > 1048576 && fileSizeByte < 1073741824) fileSizeMsg = (fileSizeByte / (1024 * 1024)).toFixed(2) + "MB";
    else if (fileSizeByte > 1048576 && fileSizeByte == 1073741824) fileSizeMsg = "1GB";
    else if (fileSizeByte > 1073741824 && fileSizeByte < 1099511627776) fileSizeMsg = (fileSizeByte / (1024 * 1024 * 1024)).toFixed(2) + "GB";
    else fileSizeMsg = "文件超过1TB";
    return fileSizeMsg;
}

function getBytesLength(str) {
// 在GBK编码里，除了ASCII字符，其它都占两个字符宽
    return str.replace(/[^\x00-\xff]/g, 'xx').length;
}



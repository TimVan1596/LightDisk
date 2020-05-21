layui.use(['form', 'table'], function () {
    var $ = layui.jquery,
        form = layui.form,
        table = layui.table;
    // miniTab.listen();

    table.render({
        elem: '#currentTableId',
        url: '/heartbeat/GetCrdtList',
        method: 'post',
        defaultToolbar: ['filter', 'exports', 'print', {
            title: '提示',
            layEvent: 'LAYTABLE_TIPS',
            icon: 'layui-icon-tips'
        }],
        cols: [[
            {type: "checkbox", width: 50},
            {field: 'key', width: 130, title: '键名'},
            // {field: 'txSize', width: 100, title: '交易数量'},
            {
                field: 'value', width: 850, title: '值', templet: function (d) {
                    //最长显示字符数字
                    let MAX_LENGTH = 110;
                    let scriptString = d.value;
                    scriptString = Base64.decode(scriptString);
                    if(scriptString.length > MAX_LENGTH){
                        scriptString = scriptString.substring(0,MAX_LENGTH);
                        scriptString += "...";
                    }
                    return scriptString;
                }
            },
            {title: '操作', minWidth: 150, toolbar: '#currentTableBar', align: "center"}
        ]],
        limits: [10, 15, 25, 50, 100],
        limit: 10,
        page: true,
        skin: 'line',
    });

    table.on('tool(currentTableFilter)', function (obj) {
        var data = obj.data;
        // console.log(obj.data) //得到当前行数据
        if (obj.event === 'edit') {

            var index = layer.open({
                title: '编辑用户',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: true,
                area: ['100%', '100%'],
                content: '../page/table/edit.html',
            });
            $(window).on("resize", function () {
                layer.full(index);
            });
            return false;
        } else if (obj.event === 'delete') {
            layer.confirm('真的删除行么', function (index) {
                obj.del();
                layer.close(index);
            });
        } else if (obj.event === 'view') {
            // console.log(parseParams(data));

            var index = layer.open({
                title: '区块详情',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: true,
                area: ['60%', '60%'],
                content: 'gossipBoard/gossipDetail.html?'
                    + "key="
                    + data.key
                    + "&value="
                    + data.value,
            });

            $(window).on("resize", function () {
                layer.full(index);
            });
            return false;
        }
    });

});



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
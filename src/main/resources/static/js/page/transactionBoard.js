layui.use(['form', 'table'], function () {
    var $ = layui.jquery,
        form = layui.form,
        table = layui.table;
    // miniTab.listen();

    table.render({
        elem: '#currentTableId',
        url: '/transaction/GetTransactionList',
        toolbar: '#toolbarDemo',
        method: 'post',
        defaultToolbar: ['filter', 'exports', 'print', {
            title: '提示',
            layEvent: 'LAYTABLE_TIPS',
            icon: 'layui-icon-tips'
        }],
        cols: [[
            {type: "checkbox", width: 50},
            {
                field: 'timestamp', width: 150, title: '日期',
                templet: function (d) {
                    return formatterTime(parseInt(d.timestamp));
                }
                , sort: true
            },
            {field: 'publicKey', width: 450, title: '公钥'},
            {field: 'hash', width: 450, title: '哈希'},
            // {field: 'txSize', width: 100, title: '交易数量'},
            {title: '操作', minWidth: 150, toolbar: '#currentTableBar', align: "center"}
        ]],
        limits: [5, 12, 15, 20, 50],
        limit: 5,
        page: true,
        skin: 'line',
        initSort: {
            field: 'timestamp' //排序字段，对应 cols 设定的各字段名
            , type: 'desc' //排序方式  asc: 升序、desc: 降序、null: 默认排序
        }
    });

    // 监听搜索操作
    form.on('submit(data-search-btn)', function (data) {
        var result = JSON.stringify(data.field);

        //执行搜索重载
        table.reload('currentTableId', {
            page: {
                curr: 1
            }
            , where: {
                publicKey: data.field.publicKey
            }
        }, 'data');

        return false;
    });

    /**
     * toolbar监听事件
     */
    table.on('toolbar(currentTableFilter)', function (obj) {
        if (obj.event === 'add') {  // 监听添加操作
            var index = layer.open({
                title: '挖矿操作',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: true,
                area: ['100%', '100%'],
                content: '../page/mineblock.html',
            });
            $(window).on("resize", function () {
                layer.full(index);
            });
        } else if (obj.event === 'delete') {  // 监听删除操作
            var checkStatus = table.checkStatus('currentTableId')
                , data = checkStatus.data;
            layer.alert(JSON.stringify(data));
        }
    });

    //监听表格复选框选择
    table.on('checkbox(currentTableFilter)', function (obj) {
        console.log(obj)
    });

    table.on('tool(currentTableFilter)', function (obj) {
        var data = obj.data;
        // console.log(obj.data) //得到当前行数据
        if (obj.event === 'view') {
            // console.log(parseParams(data));

            //prompt层
            layer.prompt({title: '请输入文件秘钥', formType: 1, maxlength: 1000}, function (pass, index) {
                layer.close(index);

                //加载层-风格2
                layer.load(1);
                //此处演示关闭

                $.ajax({
                    type: "post",
                    url: "/block/decodeJsonFromPrivateKey",
                    data: {
                        privateKey: pass
                        , data: data.scriptString
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
                                content: 'transaction/transactionView.html' +
                                    '?hash=' + data.hash
                                    + '&publicKey=' + data.publicKey
                                    + '&timestamp=' + data.timestamp
                            });
                            $(window).on("resize", function () {
                                layer.full(newTab);
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


            return false;
        }
    });

});

function parseParams(data) {
    try {
        var tempArr = [];
        for (var i in data) {
            if (Object.prototype.toString.call(data[i]) === '[object Array]'
            ) {

                var key = encodeURIComponent(i);
                var value = encodeURIComponent(JSON.stringify(data[i]));
                tempArr.push(key + '=' + value);
            } else {
                var key = encodeURIComponent(i);
                var value = encodeURIComponent(data[i]);
                tempArr.push(key + '=' + value);
            }

        }
        return tempArr.join('&');
    } catch (err) {
        return '';
    }
}

function onMineBlockBTN() {
    // 打开新的窗口
    miniTab.openNewTabByIframe({
        href: "page/mineblock.html",
        title: "挖矿",
    });
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


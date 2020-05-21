layui.use(['form', 'table'], function () {
    var $ = layui.jquery,
        form = layui.form,
        table = layui.table;
    // miniTab.listen();

    table.render({
        elem: '#currentTableId',
        url: '/heartbeat/GetLiveMemberList',
        method: 'post',
        cols: [[
            {type: "checkbox", width: 50},
            {field: 'id', width: 100, title: 'id'},
            // {field: 'txSize', width: 100, title: '交易数量'},
            {field: 'uri', width: 200, title: 'URI'},
            {field: 'heartbeat', minWidth: 200, title: '心跳'},
            {title: '操作', minWidth: 150, toolbar: '#currentTableBar', align: "center"}
        ]],
        limits: [5, 10, 15, 20, 30],
        limit: 5,
        page: true,
        skin: 'line',
    });

    table.render({
        elem: '#currentDeadTableId',
        url: '/heartbeat/GetDeadMemberList',
        method: 'post',
        cols: [[
            {type: "checkbox", width: 50},
            {field: 'id', width: 100, title: 'id'},
            // {field: 'txSize', width: 100, title: '交易数量'},
            {field: 'uri', width: 200, title: 'URI'},
            {field: 'heartbeat', minWidth: 200, title: '心跳'},
            {title: '操作', minWidth: 150, toolbar: '#currentTableBar', align: "center"}
        ]],
        limits: [5, 10, 15, 20, 30],
        limit: 5,
        page: true,
        skin: 'line',
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
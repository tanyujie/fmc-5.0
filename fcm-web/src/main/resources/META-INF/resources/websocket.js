window.onload = function()
{
    /**
     * websocket连接建立成功回调
     * @param frame
     * @returns
     */
    function wsEstablishSuccess(frame)
    {
    	console.log("websocket连接成功：" + frame);
    	
        // 订阅服务端发送过来的消息
        this.subscribe('/topic/getResponse', function(obj) {
            document.getElementById("messageBorad").innerHTML += ("<p>"+obj+"</p>");
        });
    }

    /**
     * websocket连接建立失败/连接断开等异常情况回调
     * @param frame
     * @returns
     */
    function wsEstablishFail(frame)
    {
    	console.log("websocket连接失败：" + frame);
    }
    
    var client, protocol = window.SockJS ? "http" : "ws";
    
    //服务端websocket发布路径
    var url = protocol + "://" + window.location.host + "/fcm/client-message-entry";
    if (window.SockJS)
    {
        var sock = new SockJS(url);
        client = Stomp.over(sock);
    }
    else
    {
        client = Stomp.client(url);
    }
    client.connect({
      'Authorization' : "鬼地方鬼地方鬼地方广东佛山"
    }, wsEstablishSuccess, wsEstablishFail);

    document.getElementById("send1").onclick = function(){
        client.send("/app/hello-client", {}, "山东黄金粉红色的简客分还是大家客户方法就是电话风扫地");
    };

    document.getElementById("send2").onclick = function(){
        client.send("/app/hello-user-client", {}, "分时度假还是角度看和法国军队开始告诉大家发货速度加快老化速度记录卡号风扫地机会风扫地健康还是端口连接和");
    };

    document.getElementById("send3").onclick = function(){
        
    };
}






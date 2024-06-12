## LocakSocket

자바원시언어로 핸들러 쉐이크구현 (spring의 @onOpen , @webSokcet , js 의 onOpen)

WebSocket은 tcp/ip 통신에서 구현

핸들러쉐이크
- `http get 요청으로 클라이언트의 요청 `
>  GET / HTTP/1.1               : `get 요청이 있어야함 http 와`

Host: 127.0.0.1:8811

Connection: Upgrade

Pragma: no-cache

Cache-Control: no-cache

User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36

Upgrade: websocket                                    `Upgrade: websocket 핸들러쉐이크 이후 변경할 프로토클을 기제해야함`  

Origin: http://192.168.0.136:8090

Sec-WebSocket-Version: 13

Accept-Encoding: gzip, deflate, br, zstd

Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7

Sec-WebSocket-Key: DVqRpbDEv+q64+ABHCX0Rw==

Sec-WebSocket-Extensions: permessage-deflate; client_max_window_bits

- `응답 해더`
HTTP/1.1 101 Switching Protocols       :`변경될 헤더가 들어있어야함`

Upgrade: websocket

Connection: Upgrade

Sec-WebSocket-Accept: HSmrc0sMlYUkAGmm5OPpG2HaGWk=

Sec-WebSocket-Protocol: chat


## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

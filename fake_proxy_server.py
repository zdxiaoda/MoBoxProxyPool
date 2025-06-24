import http.server
import socketserver
import json

PORT = 7891
TOKEN = "DVlQAPHHeAgVuKFx6BNLxebcJM0O7E"
PROXY_USER = "sermon2494"
PROXY_PASSWORD = "v561S3ut5GsEWsFP"


class FakeProxyHandler(http.server.SimpleHTTPRequestHandler):
    def do_POST(self):
        try:
            content_length = int(self.headers["Content-Length"])
            post_data = self.rfile.read(content_length)
            data = json.loads(post_data)

            response_data = {"status": False}

            if data.get("token") != TOKEN:
                response_data["error"] = "无效的Token"
            elif data.get("type") != "getProxy":
                response_data["error"] = "无效的请求类型"
            else:
                response_data["status"] = True
                response_data["proxyHost"] = "127.0.0.1:1080"
                response_data["proxyUser"] = PROXY_USER
                response_data["proxyPassword"] = PROXY_PASSWORD
                response_data["remain"] = 3600
                # sessionID is in request but not used in this fake server
                # response_data["sessionID"] = data.get("sessionID")

            self.send_response(200)
            self.send_header("Content-type", "application/json")
            self.end_headers()
            self.wfile.write(json.dumps(response_data).encode("utf-8"))

        except Exception as e:
            self.send_response(400)
            self.send_header("Content-type", "application/json")
            self.end_headers()
            error_response = {"status": False, "error": str(e)}
            self.wfile.write(json.dumps(error_response).encode("utf-8"))

    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-type", "text/html; charset=utf-8")
        self.end_headers()
        html = """
        <html>
        <head><title>Fake Proxy Server</title></head>
        <body>
        <h1>这是一个假的代理池服务器</h1>
        <p>请使用 POST 方法请求代理。</p>
        <p>请求示例:</p>
        <pre>
curl -X POST http://localhost:7891 \\
-H "Content-Type: application/json" \\
-d '{
  "token": "DVlQAPHHeAgVuKFx6BNLxebcJM0O7E",
  "type": "getProxy",
  "sessionID": "some_session_id"
}'
        </pre>
        </body>
        </html>
        """
        self.wfile.write(html.encode("utf-8"))


with socketserver.TCPServer(("", PORT), FakeProxyHandler) as httpd:
    print(f"在端口 {PORT} 启动虚假代理服务器...")
    print(f"使用 Token: {TOKEN}")
    print("按 Ctrl+C 停止服务器。")
    httpd.serve_forever()

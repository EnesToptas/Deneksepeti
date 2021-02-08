import socket
import json

HOST = 'localhost'  # The server's hostname or IP address
PORT = 3000        # The port used by the server

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((HOST, PORT))
    s.sendall(b"""signup
{
      "name": "ahmet",
      "surname": "mehmet",
      "email": "keyfe_keder_alayina_giderrr@hotmale.com",
      "password": "3169"
}

""")
    data = s.recv(1024)

print(data)

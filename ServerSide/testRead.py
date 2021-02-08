import socket
import json

HOST = 'ec2-3-89-104-95.compute-1.amazonaws.com'  # The server's hostname or IP address
PORT = 3000        # The port used by the server

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((HOST, PORT))
    s.sendall(b"""getPost
    	a

""")
    data = s.recv(1024)

print(data)

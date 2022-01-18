from msilib.schema import ControlEvent
import socket
from threading import Thread
import sys
from enum import Enum
import time

server_addr = ['0.0.0.0', 8080]
g_server = None

timeout = 50.0

class Status(Enum):
    Free = 1
    Matched = 2

class Connection:

    connection_list = []

    def __init__(self, socket):
        self.socket = socket
        if len(Connection.connection_list) == 0:
            self.index = 0
        else: 
            self.index = Connection.connection_list[-1].index + 1
        self.name = "Unknown"
        self.status = Status.Free
        self.latest = time.time()
        Connection.connection_list.append(self)
        self.socket.send(f"0 Server recv conn {self.index}\r\n".encode("utf8"))

    def remove_connection(self):
        try:
            self.socket.send("5 Unknown issue\r\n")
        except:
            pass
        self.socket.close()
        if self in Connection.connection_list:
            Connection.connection_list.remove(self)
        if self.status == Status.Matched and self.match_player in Connection.connection_list:
            self.match_player.socket.send("5 match quit\r\n".encode("utf8"))
            self.match_player.status = Status.Free
            self.match_player.remove_connection()
            
    def print_connections():
        for conn in Connection.connection_list:
            print(f"Conn {conn.index}:{conn.name} {conn.status}")
        if len(Connection.connection_list) == 0:
            print("No connections")

    def detect_heartbeat():
        while True:
            now = time.time()
            remove = []
            for conn in Connection.connection_list:
                if now - conn.latest > timeout and conn.status == Status.Matched:
                    try:
                        conn.socket.send("5 Timeout kickout\r\n".encode("utf8"))
                    except:
                        pass
                    remove.append(conn)
            for conn in remove:
                conn.remove_connection()
            time.sleep(1)

    def handle_message(self):
        while True:
            try:
                bytes = self.socket.recv(1024)
                self.latest = time.time()
                self.message_parser(bytes.decode(encoding="utf8"))
            except:
                self.remove_connection()
        if self in Connection.connection_list:
            self.remove_connection()

    def match(self, name):
        target = None
        for conn in Connection.connection_list:
            if conn.name == name:
                target = conn
                break
        self.match_player = target
        target.match_player = self
        self.status = Status.Matched
        target.status = Status.Matched

    def set_piece(self, x, y):
        try:
            self.match_player.socket.send(f"3 {x} {y}\r\n".encode("utf8"))
        except:
            Connection.connection_list.remove(self.match_player)

    def message_parser(self, message):
        # message format:
        # [0] heartbeat message, no need to handle
        # [1] [String] change name
        # [2] [String] match request with given name
        # [3] [int] [int] place piece
        # [4] request list of names
        # [5] quit
        tokens = message.split()
        if len(tokens) < 2:
            return
        if tokens[0] == '0':
            self.socket.send("0 OK\r\n".encode("utf8"))
        if tokens[0] == '1':
            self.name = str(self.index) + "." + tokens[1]
            self.socket.send(f"1 name set to:{self.index}.{tokens[1]}\r\n".encode("utf8"))
        if tokens[0] == '2':
            self.match(tokens[1])
            self.socket.send(f"2 matched to {tokens[1]}\r\n".encode("utf8"))
            self.match_player.socket.send(f"2 matched to {self.name}\r\n".encode("utf8"))
        if tokens[0] == '3':
            self.set_piece(int(tokens[1]), int(tokens[2]))
        if tokens[0] == '4':
            msg = list(filter(lambda conn:conn.status == Status.Free, [conn for conn in Connection.connection_list]))
            msg = [conn.name for conn in msg]
            msg = " ".join(msg)
            self.socket.send(("4 " + msg + "\r\n").encode("utf8"))
        if tokens[0] == '5':
            self.remove_connection()


def accept_conn():
    while True:
        client, _ = g_server.accept()
        conn = Connection(client)
        thread = Thread(target = conn.handle_message, args = tuple())
        thread.setDaemon(True)
        thread.start()


if __name__ == '__main__':
    if len(sys.argv) > 2:
        server_addr[0] = sys.argv[1]
        server_addr[1] = int(sys.argv[2])
    g_server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    g_server.bind(tuple(server_addr))
    g_server.listen(5)
    print("Server listening on", server_addr)

    thread = Thread(target = accept_conn)
    thread.setDaemon(True)
    thread.start()

    heartbeat = Thread(target = Connection.detect_heartbeat)
    heartbeat.setDaemon(True)
    heartbeat.start()

    while True:
        cmd = input('''1. List current connections\n2. Quit\n''')
        cmd = int(cmd)
        if cmd == 1:
            Connection.print_connections()
        if cmd == 2:
            quit()

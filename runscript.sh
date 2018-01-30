#!/bin/fish

tmux kill-session -t "Sysdev"
tmux new-session -s "Sysdev" -n "TCPServer" -d
tmux send-keys -t "Sysdev:0" 'mvn -e exec:java  -Dexec.mainClass="tcpserver.Server"' ENTER
tmux new-window -t "Sysdev:1" -n "Jersey"
tmux send-keys -t "Sysdev:1" 'mvn -e exec:java  -Dexec.mainClass="sysdev.Main"' ENTER
sleep 1
tmux new-window -t "Sysdev:2" -n "Webserver"
tmux send-keys -t "Sysdev:2" 'cd SysDevInterface' ENTER
tmux send-keys -t "Sysdev:2" 'npm run develop' ENTER

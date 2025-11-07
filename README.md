1) Установлен Docker версии 28.4.0
2) 2 слоя
3) ~20кб
4) Отвечает за выполнение скрипта по выводу содержимого файла
5) Потому что мы храним по сути только текстовый файл и инструкцию по его выводу
6) docker pull - скачивание образа, docker run - запуск образа
7) C:\Users\...\AppData\Local\Docker\wsl
8) Server:
   Containers: 5
   Running: 0
   Paused: 0
   Stopped: 5
   Images: 4
   Server Version: 28.4.0
   Storage Driver: overlayfs
   driver-type: io.containerd.snapshotter.v1
   Logging Driver: json-file
   Cgroup Driver: cgroupfs
   Cgroup Version: 2
   Plugins:
   Volume: local
   Network: bridge host ipvlan macvlan null overlay
   Log: awslogs fluentd gcplogs gelf journald json-file local splunk syslog
   CDI spec directories:
   /etc/cdi
   /var/run/cdi
   Discovered Devices:
   cdi: docker.com/gpu=webgpu
   Swarm: inactive
   Runtimes: io.containerd.runc.v2 nvidia runc
   Default Runtime: runc
   Init Binary: docker-init
   containerd version: 05044ec0a9a75232cad458027ca83437aae3f4da
   runc version: v1.2.5-0-g59923ef
   init version: de40ad0
   Security Options:
   seccomp
   Profile: builtin
   cgroupns
   Kernel Version: 6.6.87.2-microsoft-standard-WSL2
   Operating System: Docker Desktop
   OSType: linux
   Architecture: x86_64
   CPUs: 16
   Total Memory: 7.619GiB
   Name: docker-desktop
   ID: bd3a872b-ecb5-4a02-b3c0-4e9fd73848b3
   Docker Root Dir: /var/lib/docker
   Debug Mode: false
   HTTP Proxy: http.docker.internal:3128
   HTTPS Proxy: http.docker.internal:3128
   No Proxy: hubproxy.docker.internal
   Labels:
   com.docker.desktop.address=npipe://\\.\pipe\docker_cli
   Experimental: false
   Insecure Registries:
   hubproxy.docker.internal:5555
   ::1/128
   127.0.0.0/8
   Live Restore Enabled: false
9) 4 контейнера
10) Самый большой образ - Ubuntu, т.к это образ полнофункциональной ОС
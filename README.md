# lingdianqiudui
零点球队website
Python如何生成windows可执行的exe文件
https://segmentfault.com/a/1190000016087451?utm_source=tag-newest

0. Test Dashboard
https://dev.azure.com/pallets/click/_build?definitionId=6

1. Global variable accross files
https://www.cnblogs.com/darkknightzh/p/8108640.html

2. whoami
windows whoami
import getpass
print getpass.getuser()

3. subprocess
import pkg_resources
from subprocess import call

packages = [dist.project_name for dist in pkg_resources.working_set]
call("pip install --upgrade " + ' '.join(packages), shell=True)

4. process automation
n8n

5. GitdataV
https://hongqingcao.github.io/GitDataV/data/randyguogithub

6. oslo.config

# -*- coding: utf-8 -*-

import sys
from oslo_config import cfg
from oslo_config import types

# 自定义端口类型，范围在(1, 65535)
PortType = types.Integer(1, 65535)

opts = [
    cfg.StrOpt('ip',
               default='127.0.0.1',
               help='IP address to listen on.'),
    cfg.Opt('port',
            type=PortType,
            default=8080,
            help='Port number to listen on.')
]

# 注册选项
cfg.CONF.register_opts(opts)

# group database
database = cfg.OptGroup(name='database',
                      title='group database Options')
opts = [
    cfg.StrOpt('connection',
               default='',
               help='item connection in group database.')
]
cfg.CONF.register_group(database)
cfg.CONF.register_opts(opts, group=database)

# 指定配置文件
cfg.CONF(default_config_files=['config.conf'])

print('DEFAULT: ip=%s  port=%s' %(cfg.CONF.ip,cfg.CONF.port))
print('database: connection=%s' %(cfg.CONF.database.connection))


config.conf :

[DEFAULT]
ip = 8.8.8.8
port = 9090

[database]
connection = mysql+pymysql://root:secret@127.0.0.1/cinder?charset=utf8



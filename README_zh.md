# GWSD Open PTT
Gwsd Open PTT support message,video/audio call, ptt call

## 页面简介
1. AudioCallActivity:全双工语音呼叫界面
2. ChatActivity:聊天界面
3. ChatListActivity:消息列表界面
4. GroupDetailActivity:群组详情界面
5. MemberListActivity:群组在线成员页面
6. PttCallActivity:群组或者成员半双工呼叫
7. VideoActivity:视频呼叫页面
8. LoginActivity:登录页面
## 配置
1. DeviceConfig:设备配置
2. ServerAddressConfig:服务器地址配置

## 登录账号说明
1. 调用web api 创建账号并选择对应套餐，同时创建群组 建立账号与群组的绑定关系

## 套餐说明
套餐类型对应web api创建账号的接口中的fgrp参数，说明如下

frgp|套餐|功能描述
-----|------|-----
34|套餐A|基础对讲+定位
43|套餐B|基础对讲+定位+消息+全双工语音
33|套餐C|基础对讲+定位+消息+全双工语音+视频通话

## 注意事项
1. 全双工语音通话需要账号具备全双工权限，创建账号时请选择套餐B或套餐C
2. 消息聊天需要账号具备消息权限，创建账号时请选择套餐B或套餐C。
3. 视频通话需要账号具备视频通话权限，创建账号时选择套餐C

## 设备配置
### DeviceConfig.java
1. DEVICE_KEY_BROADCAST：配置设备按键广播
广播接收器为KeyReceiver，在该类中添加处理具体按键广播的业务逻辑
2. DEVICE_CAMERA_ORIENTATION：配置前置后置摄像头的翻转角度
3. getDeviceImei：进行获取设备的imei用于imei平台上远程放号使用，需要获取一个唯一标识即可，如果因为android版本过高无法获取imei使用androidID也可以，不需要时可以返回固定值
4. getDeviceIccid：获取iccid用于平台iccid远程放号，需要获取一个唯一标识即可，如果因为android版本过高无法获取imei货期androidID也可以，不需要时可返回固定值
5. getDeviceBattery：系统获取电量也可返回固定值
6. getDeviceNetwork：获取网络模式如（4g，5g，wifi等）

## 服务器地址配置
1. PTT_SERVER_ADDRESS：对讲服务器
2. MSG_SERVER_ADDRESS：消息服务器
3. DISPATCH_SERVER_ADDRESS：调度服务器
4. VIDEO_SERVER_ADDRESS：视频服务器
5. FILE_SERVER_ADDRESS：上传文件地址

如果需要上公网时代平台对讲服务器，消息服务器，调度服务器，视频服务器，上传文件地址都不需要更改用demo中的即可
如果需要私有化部署请联系运维
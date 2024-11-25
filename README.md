# GWSD Open PTT
Gwsd Open PTT support message,video/audio call, ptt call

## Interface introduction
1. AudioCallActivity:Full-duplex voice call interface
2. ChatActivity:Chat interface
3. ChatListActivity:Message list interface
4. GroupDetailActivity:Group details screen
5. MemberListActivity:Group online member page
6. PttCallActivity:Group or member half-duplex call
7. VideoActivity:Video call page
8. LoginActivity:Login page
## Configuration
1. DeviceConfig:Equipment configuration
2. ServerAddressConfig:Server address configuration

## Login account description
1.invoke web api Create an account, select a package, and create a group. Bind an account to a group

## Package description
The package type corresponds to the fgrp parameter in the web api interface for creating an account

frgp|Set|Functions
----|---|--------
34|Set A|basic intercom + positioning
43|Set B|Basic intercom + location + message + full-duplex voice
33|Set C|Basic intercom + positioning + messaging + full-duplex voice + video call

## Matters needing attention
1. Full-duplex voice calls require an account with full-duplex rights. Select Package B or C when creating an account
2. Message chat requires an account to have message rights, please select package B or C when creating an account.
3. video call requires the account to have video call rights, select package C when creating an account

## Equipment configuration
### DeviceConfig.java
1. DEVICE_KEY_BROADCAST：Configure device key broadcasting
Broadcast receiver KeyReceiver，Add business logic to this class that handles specific keystroke broadcasts
2. DEVICE_CAMERA_ORIENTATION：Configure the rotation Angle of the front and rear cameras
3. getDeviceImei：To obtain the imei of the device for remote number allocation on the imei platform, a unique identifier needs to be obtained. If the imei cannot be obtained because the android version is too high, androidID can also be used, and a fixed value can be returned when it is not needed
4. getDeviceIccid：Obtain iccid for platform iccid remote number allocation, it is necessary to obtain a unique ID, if the android version is too high to obtain the imei delivery date androidID can also be used, it can return a fixed value when not needed
5. getDeviceBattery：The system can also return a fixed value
6. getDeviceNetwork：Get network modes such as (4g, 5g, wifi, etc.)

## Server address setting
1. PTT_SERVER_ADDRESS：Intercom server
2. MSG_SERVER_ADDRESS：Message server
3. DISPATCH_SERVER_ADDRESS：Scheduling server
4. VIDEO_SERVER_ADDRESS：Video server
5. FILE_SERVER_ADDRESS：Upload file address

If you need to use the GWSD platform intercom server, message server, scheduling server, video server, upload file address do not need to change the demo
Contact O&M for private deployment
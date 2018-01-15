m
st

请求参数：

| 参数 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| password | TRUE | String | 登录密码
| phone | TRUE | String | 绑定手机号


返回 JSON 数据结构如下:

| 返回字段 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| accountId| TRUE | Integer| 用户账号
| driverId| TRUE | Integer| 司机团队个人账号
| token| TRUE | String| 用户登录令牌，后续调用接口放于请求头中
- **返回示例**
>    
```json 
{
  "code": 0,
  "msg": "成功",
  "data": {
    "accountId": 227,
    "token": "eJxNkN1PgzAUxf8XXjFa*sHEZA9kYkZCZ5SyhSdSaWHdB9RSdLr4vzsQjfftnt895yT37LAkve7EvuBaK*HcORB4PkBw5lyNSJ60MrLglZXmQj1CCAQATHQ0FdwWyAzeX9mqoxyOcRBggH2MJl0J2VhVqTEK-nXwsmz7xhb2Q8t-KZ2qLxuNskV8L9G6*vR3dZQexUt7Wqzj3sR1*sSSLGA6BxQejLt0GS1DFYW37-nsccdeU8KTkt5sYbxdEkqxTx5CN*73z*WKb1aHIBP5fCp7k6ZTbfPzAOJBBIZxvr4B68lU8A__"
  }
}
```

## 退出
地址：`/114.55.40.180:8085/driver/loginOut`

方法：Post

请求参数：

| 参数 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:


返回 JSON 数据结构如下:

| 返回字段 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| code| TRUE | Integer|
| msg| TRUE | String| 
- **返回示例**
>    
```json 
{
  "code": 0,
  "msg": "成功"
}
```


## 获取个人主页信息
地址：`/114.55.40.180:8085/driver/information`

方法：GET

请求参数：

| 参数 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| accountId| TRUE |Integer|  用户Id|

返回 JSON 数据结构如下:

| 返回字段 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| account| TRUE | String| 用户账号
| name| TRUE | String| 用户姓名
| balance| TRUE | Integer| 账户余额（单位：分）
| couponNum| TRUE | Integer| 优惠券余额（单位：分）
| messageNum| TRUE | Integer| 未读信息数量
| city| TRUE | String| 用户注册城市
- **返回示例**
>    
```json 
{
  "code": 0,
  "msg": "成功",
  "data": {
    "account": "2017041914002600000192",
    "name": "陈某人",
    "balance": -100000,
    "couponNum": 0,
    "messageNum": 0
  }
```

## 消费记录
地址：`/114.55.40.180:8085/driver/exchange/list`

方法：GET

请求参数：

| 参数 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| accountId| true |Integer,|  用户Id|
| pageNo|  false|Integer,|  页码|
| pageSize|  false|Integer,|  每页长度|

返回 JSON 数据结构如下:

| 返回字段 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| serialNum| TRUE | String| 订单号
| createDate| TRUE | Long| 消费时间
| stationName| TRUE | String| 站点名称
| carNumber| TRUE | String| 车牌号
| realMile| TRUE | Integer| 计费里程（单位：公里）
| referMiles| TRUE | Integer| 总里程（单位：公里）
| realFare| TRUE | Integer| 实际支付（单位：分）
| coupon| TRUE | Integer| 优惠券（单位：分）
| balance| TRUE | Integer| 余额（单位：分）
- **返回示例**
>    
```json 
{
  "code": 0,
  "msg": "成功",
  "total": 3,
  "data": [
    {
      "serialNum": "2017041916243400000132",
      "createDate": 1492590274000,
      "stationName": "数源",
      "carNumber": "苏E03C7F",
      "realMile": null,
      "referMiles": 3878,
      "realFare": null,
      "coupon": 0
    }
  ]
}
```

## 充值记录
地址：`/114.55.40.180:8085/driver/recharge/list`

方法：GET

请求参数：

| 参数 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| accountId| true |Integer,|  用户Id|
| pageNO|  false|Integer,|  页码|
| pageSize|  false|Integer,|  每页长度|

返回 JSON 数据结构如下:

| 返回字段 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| createDate| TRUE | Long| 充值时间
| stationName| TRUE | String| 充值站点
| rechargeAmount| TRUE | Integer| 充值金额（单位：分）
| rechargeType| TRUE | Integer| 充值类型（1支付宝 2财务 3 微信）

- **返回示例**
>    
```json 
{
  "code": 0,
  "msg": "成功",
  "total": 7,
  "data": [
    {
      "createDate": 1498613968000,
      "stationName": null,
      "rechargeAmount": 50000,
      "rechargeType": 2
    }
  ]
}
```

## 换电站列表
地址：`/114.55.40.180:8085/driver/station/list`

方法：GET

请求参数：

| 参数 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| cityId|  false|Integer| 为空时取所有城市换电站|
| pageNO|  false|Integer|  页码|
| pageSize|  false|Integer|  每页长度|

返回 JSON 数据结构如下:

| 返回字段 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| stationName| TRUE | String| 换电站名字
| address| TRUE | String| 地址
| phone| TRUE | String| 电话
| beginTime| TRUE | Date| 营业开始时间
| endTime| TRUE | Date| 营业结束时间
| longitude| TRUE | Double| 经度
| latitude| TRUE | Double| 纬度
| lineCount| TRUE | Integer| 换电排队数量
| batteryCount| TRUE | Integer| 电池数量


- **返回示例**
>    
```json 
{
  "code": 0,
  "msg": "成功",
  "total": 3,
  "data": [
    {
      "stationName": "数源",
      "address": "教工路1号",
      "phone": "18388888888",
      "beginTime": 1800000,
      "endTime": 52200000
    }
  ]
}
```


## 优惠券
地址：`/114.55.40.180:8085/driver/coupon/list`

方法：GET

请求参数：

| 参数 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| accountId| true |Integer,|  用户Id|
| pageNO|  false|Integer,|  页码|
| pageSize|  false|Integer,|  每页长度|

返回 JSON 数据结构如下:

| 返回字段 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| fare| TRUE | Integer| 优惠券总金额
| used| TRUE | Integer| 优惠券使用状态（0未使用 1已使用 2使用完）
| usedAmouont| TRUE | Integer| 优惠券使用金额
| createDate| TRUE | Date| 优惠券创建时间
| endDate| TRUE | Date| 优惠券使用结束时间
| prescription| TRUE | Integer| 有效规则（1不限 2 当月有效）
| couponTitle| TRUE | String| 优惠券标题


- **返回示例**
>    
```json 
{
  "code": 0,
  "msg": "成功",
  "total": 4,
  "data": [
    {
      "fare": 8040,
      "used": 0,
      "usedAmouont": 0,
      "createDate": 1499235203000,
      "prescription": 1,
      "couponTitle": "贵阳1号站优惠券"
    },
    {
      "fare": 4800,
      "used": 0,
      "usedAmouont": 0,
      "createDate": 1499234917000,
      "prescription": 1,
      "couponTitle": "贵阳1号站优惠券"
    }
  ]
}
```

## 获取城市
地址：`/114.55.40.180:8085/driver/city/list`

方法：GET

请求参数：

| 参数 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| cityId| true |Integer,| 获取省列表时传0，获取市级别列表时传当前省的id|

返回 JSON 数据结构如下:

| 返回字段 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| id| TRUE | Integer| 地区id
| name| TRUE | String| 地区名称


- **返回示例**
>    
```json 
{
  "code": 0,
  "msg": "成功",
  "total": 0,
  "data": [
    {
      "id": 110100,
      "name": "北京市"
    }
  ]
}
```

## 根据城市名称获取城市id
地址：`/114.55.40.180:8085/driver/city/getCityByName`

方法：GET

请求参数：

| 参数 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| name| true |String,| 城市名称|

返回 JSON 数据结构如下:

| 返回字段 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| id| TRUE | Integer| 地区id
| name| TRUE | String| 地区名称


- **返回示例**
>    
```json 
{
  "code": 0,
  "msg": "成功",
  "data": {
    "id": 411700,
    "name": "驻马店市"
  }
}
```


## 系统消息列表
地址：`/114.55.40.180:8085/driver/message/sysMsgList`

方法：GET

请求参数：

| 参数 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| accountId| true |Integer| 用户id|
| pageNO|  false|Integer,|  页码|
| pageSize|  false|Integer,|  每页长度|

返回 JSON 数据结构如下:

| 返回字段 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| id| TRUE | Integer| 消息Id
| createDate| TRUE | Date| 创建时间
| content| TRUE | String| 消息内容

- **返回示例**
>    
```json 
{
  "code": 0,
  "msg": "成功",
  "total": 1,
  "data": [
    {
      "id": 4,
      "createDate": 1500356194000,
      "content": "hello你好啊 大家好啊"
    }
  ]
}
```

## 通知消息列表
地址：`/114.55.40.180:8085/driver/message/list`

方法：GET

请求参数：

| 参数 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| accountId| true |Integer| 用户id|
| pageNO|  false|Integer,|  页码|
| pageSize|  false|Integer,|  每页长度|

返回 JSON 数据结构如下:

| 返回字段 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| id| TRUE | Integer| 消息Id
| createDate| TRUE | Date| 创建时间
| content| TRUE | String| 消息内容

- **返回示例**
>    
```json 
{
  "code": 0,
  "msg": "成功",
  "total": 1,
  "data": [
    {
      "id": 4,
      "createDate": 1500356194000,
      "content": "hello你好啊 大家好啊"
    }
  ]
}
```
## 我的团队
地址：`/114.55.40.180:8085/driver/team/teamInformation`

方法：GET

请求参数：

| 参数 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| driverId| true |Integer| 司机团队系统用户id|


返回 JSON 数据结构如下:

| 返回字段 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| name| TRUE | String| 姓名
| phone| TRUE | String| 电话
| rank| TRUE | integer| 等级 大于等于1为队长
| yesterdayCurrent| TRUE | String| 昨日流水
| totalCurrent| TRUE | String| 总流水
| serverRank| TRUE | String| 服务等级

- **返回示例**
>    
```json 
{
  "code": 0,
  "msg": "成功",
  "data": {
    "name": "driver1",
    "phone": "15757129606",
    "yesterdayCurrent": 0,
    "totalCurrent": 0,
    "serverRank": "",
    "rank": 1,
    "del": 0,
    "teamName": "公牛"
  }
}
```

## 成员信息
地址：`/114.55.40.180:8085/driver/team/teamMember`

方法：GET

请求参数：

| 参数 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| driverId| true |Integer| 司机团队系统用户id|


返回 JSON 数据结构如下:

| 返回字段 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| name| TRUE | String| 姓名
| phone| TRUE | String| 电话
| rank| TRUE | integer| 等级 大于等于1为队长
| yesterdayCurrent| TRUE | String| 昨日流水
| totalCurrent| TRUE | String| 总流水
| serverRank| TRUE | String| 服务等级

- **返回示例**
>    
```json 
{
  "code": 0,
  "msg": "成功",
  "data": [
    {
      "id": 12,
      "name": "ddff",
      "teamName": null,
      "phone": "15757129606",
      "rank": 0,
      "yesterdayCurrent": 0,
      "serverRank": "",
      "totalCurrent": null
    }
  ]
}
```

## 微信、支付宝支付
地址：`/114.55.40.180:8085/driver/pay`

方法：GET

请求参数：

| 参数 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| accountId| true |Integer| 用户id|
| rechargeAmount| true |Float| 充值金额|
| type| true |Integer| 类型 1 支付宝 0 微信|


返回 JSON 数据结构如下:

| 返回字段 | 必填 | 类型 | 说明
| :---: | :---: | :---: | :---:
| type| TRUE | Integer| 类型 1 支付宝 0 微信
| tradeCode| TRUE | String| 支付订单参数

- **返回示例**
>    
```json 
{
  "code": 0,
  "msg": "成功",
  "data": {
    "type": 1,
    "tradeCode": "alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2018010801687080&biz_content=%7B%22body%22%3A%22%E6%8D%A2%E7%94%B5%E8%B4%B9%E7%94%A8%E5%85%85%E5%80%BC%22%2C%22out_trade_no%22%3A%22RD3JBMi1515660762732%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22%E6%8D%A2%E7%94%B5%E8%B4%B9%E7%94%A8%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%22192.0%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=114.55.40.180%3A8088%2Fdrivertest%2Falipay%2FnotifyUrl&sign=alUd5Z8SHMGdf2fUyzNF%2FsrpbbwwQM%2FRGhHEJoZ9d%2FZcvXspAZiHZ%2B3V1x%2BLHkBi3E5OrVL79OdDnTUerXbupoKw6L28lTmrn5kKqjY%2FUloBrtUPb3Rzd8AwiaPGcV3q7GpI7WxDctD5L95tGXQ2XnGavnIEAJhU6yy7Q8Q9wyaHHl0Bn1JLZMITiuSi%2FWQPYvlmXPF8jSSBVDnRpNbfXgC%2FfjeeMCjI%2FnHWx5LhnPtNaByUYha5DVVO%2Fa9tqEnglQ9RMxd8rJk0hAyx1t4Fh%2FxheQ3qDrrxIeZMyT%2F1PzBghckrdKeU9u8djwhHho3lHSNr4np9eH9eg%2Bgc%2FiPygw%3D%3D&sign_type=RSA2&timestamp=2018-01-11+16%3A52%3A42&version=1.0"
  }
}
```

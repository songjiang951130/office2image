# office 转 img

主要使用 aspose pdfbox poi 将 office 文件 的前 3 页转为图片,图片上传至七牛云,需要自行配置七牛云文件 qiniu.properties

配置参数如下

```
ACCESS_KEY=
SECRET_KEY=
BUCKET_NAME=
DOMAIN=
```
目前支持的文件类型

doc
docx
xlsx
xls
ppt
pptx
pdf


## 请求方式
- get

  /upload

|参数|类型|说明|
|:---|:---|:---|
|fileUrl|url|文件地址|


示例：
/upload?fileUrl=http://playback.haibian.com/upload_tc/6L6-5pGp5ZCO5Y-wMDQxOQ==1558592359056/达摩后台0419.pdf


- post

  /upload


|参数|类型|说明|
|:---|:---|:---|
|file|file|post上传文件流|


示例：
/upload?fileUrl=http://playback.haibian.com/upload_tc/6L6-5pGp5ZCO5Y-wMDQxOQ==1558592359056/达摩后台0419.pdf

返回值：
```json
{
    "errcode": "0",
    "data": [
        {
            "url": "https://avatar.haibian.com/material_preview/tmp/word_15538173858037972.jpeg"
        },
        {
            "url": "https://avatar.haibian.com/material_preview/tmp/word_15538173858108781.jpeg"
        },
        {
            "url": "https://avatar.haibian.com/material_preview/tmp/word_15538173863245676.jpeg"
        }
    ],
    "errmsg": "success"
}
```

todo：
根据文件真实扩展名而不是文件的后缀，来处理文件（bug: 名为***.ppt的文件实际上是pptx文件）
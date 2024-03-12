# upscayl-provider

提供了一个简单的服务，用于[Upscayl](https://github.com/upscayl/upscayl)图片。

## [upscayl-provider](https://github.com/muedsa/upscayl-provider)  
提供了一个简单的接口, 重定向到指定URL图片Upscayl后的URL(如果图片还未Upscayl，则快速重定向到原URL，并分发Upscayl任务到Redis Pub/Sub)。

## [upscayl-runner](https://github.com/muedsa/upscayl-runner)
从 Redis Pub/Sub 中获取并执行 Upscayl 任务, Upscayl后的图片将上传到[Zipline](https://github.com/diced/zipline)。
通过[swiftshader](https://swiftshader.googlesource.com/SwiftShader)来使用CPU模拟显卡以运行Upscayl,
swiftshader在Docker镜像`openjdk:11`中编译,你可能需要自行编译并替换`/upscayl/linux/libvulkan.so.1`。

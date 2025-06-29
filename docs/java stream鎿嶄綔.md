# 操作类型

![img](https:////oss-grow2.alibaba.com/content/2024/03/99780b83-18a7-4d54-a950-1c329bc51e03.jpg?Expires=1749892634&OSSAccessKeyId=LTAI5tMHgGsZrXJ6WkiYuXFN&Signature=pIWoHVqAYViAX3E4seJaMYH2bao%3D&x-oss-process=style%2Fcontent)

# stream 源码实现

![img](https:////oss-grow2.alibaba.com/content/2024/03/710fd6a3-7a09-497d-9145-d9ed8b2cd962.jpg?Expires=1749892634&OSSAccessKeyId=LTAI5tMHgGsZrXJ6WkiYuXFN&Signature=YRjk74wKqKeoIzZv7JN9AG6Bmt0%3D&x-oss-process=style%2Fcontent)

BaseStream和Stream为最顶端的接口类。BaseStream主要定义了流的基本接口方法，例如，spliterator、isParallel等；Stream则定义了一些流的常用操作方法，例如，map、filter等。

ReferencePipeline是一个结构类，他通过定义内部类组装了各种操作流。他定义了Head、StatelessOp、StatefulOp三个内部类，实现了BaseStream与Stream的接口方法。

Sink接口是定义每个Stream操作之间关系的协议，他包含begin()、end()、cancellationRequested()、accpt()四个方法。ReferencePipeline最终会将整个Stream流操作组装成一个调用链，而这条调用链上的各个Stream操作的上下关系就是通过Sink接口协议来定义实现的。


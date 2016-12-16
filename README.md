# delete_by_query
elasticsearch 2.4.1 delete_by_query plugin

1、将项目下载下来。

2、可以使用eclipse导出jar包, jar包名称要是delete_by_query。

3、在elasticsearch安装目录下的plugin文件夹下面新建一个文件夹取名为delete_by_query。

4、将jar包拷到该文件夹下并将plugin-descriptor.properties文件也拷到该文件夹下。

5、重启es，可以使用head插件测试一下，DELETE http://xxx:9200/index/type/_query  {"term":{"name":"test"}}

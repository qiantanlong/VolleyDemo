#WebDemo介绍 #
## 添加volley的依赖 ##
	dependencies {
    	compile fileTree(dir: 'libs', include: ['*.jar'])
    	testCompile 'junit:junit:4.12'
    	compile 'com.android.support:appcompat-v7:24.2.0'

		********添加volley依赖，也可以导入volley的jar包
    	compile 'eu.the4thfloor.volley:com.android.volley:2015.05.28'
    	
    	*******
}
## 添加gson的jar包##

	自行从网上下载gson的jar添加并加入到工程，json解析使用这个框架

##GsonRequest、XMLRequest的使用##
	这两个类使用方法都很简单，在MainActivity中都有使用的示例代码，并有详细解释

## ImageLoader##
	ImageLoader的使用也在MainActivity这个类中，volley已经实现了硬盘缓存，在示例代码中定义了内存缓存的代码，可以参考。

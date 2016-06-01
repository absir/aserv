<a name="cn-title"></a>
# achieve-server

[https://github.com/absir/absir](https://github.com/absir/absir "achieve-server")


* [命名和配置](#cn-name)
* [基础模块](#cn-base)
* [Ioc启动](#cn-ioc)
* [Ioc功能核心](#cn-ioc-featrue)
* [Aop功能核心](#cn-aop-featrue)
* [其他.特色功能](#cn-other)


<a name="cn-name"></a>
## 命名和配置

本框架在命名上花了很大功夫，做到了基本上通过包名，类名，方法名，参数名就可以看出功能和需求。框架上非复杂逻辑一般没有注释。

框架本身运行不需要配置，但是同时又支持配置，目的是为了特定环境下配置文件覆盖约定设置。约定优于配置。

<a name="cn-base"></a>
## 基础模块
在java开发过程中或多或少需要使用动态类型，反射调用,成员路径访问,Dump对象等脚本语言中才能快速使用的特性。absir-lang模块就是对这些功能最好的补充。
详细参考使用类 KernelReflect, KernelObject, KernelDyna, UtilAccessor, UtilDump。

<a name="cn-ioc"></a>
## IOC启动
在以功能模块化的框架中IOC是必须的，灵活扩展自定义类，共用类扫描，解析和参与IOC对象的生成就可以非常灵活的配置IOC的扩展功能。absir-bean 模块就是IOC框架的基础实现。

ICO的容器为BeanFactory，支持容器嵌套，BeanFactoryUtils类提供了常用静态方法。

生成对象定义为BeanDefine，有四种Scope类型，有别于spring ioc，这里的scope只用作生命周期的配置。

	/** 单例对象 SINGLETON */
	SINGLETON,

	/** 单例延迟对象 LAZYINIT */
	LAZYINIT,

	/** 原型对象 PROTOTYPE */
	PROTOTYPE,

	/** 软引用单例 SOFTREFERENCE */
	SOFTREFERENCE,
	
**以下所有接口若有多个实现，按Orderable排序，小的先执行**

###1.Ioc入口
BeanFactoryProvider类，可传入Application级别对象(即程序生命周期中一直存在的对象)，在程序开始和介绍需要分表调用started和stopped入口。

[回目录](#cn-title)

###2.配置文件
配置文件入口在classes目录下的config.properties文件

配置环境中有个3个重要的属性

	`environment`  环境参数 #DEVELOP|DEBUG|TEST|PRODUCT (影响配置文件读取和错误提示日志等)

	`classPath` 类目录 于程序紧密相关的目录地址为主class目录或主jar目录

	`resourcePath` 资源目录初始化等于classPath，可以通过配置文件设置

**配置文件基本语法**

A.#为单行注释

B.name=value 简单指定

C.name|TEST|PRODUCT=value 为指定在对应的environment下才生效

D.name.=value name#=value name+=value 分别为指定name的value 做数组加 字符串加 和字符串加带换行符的运算

E.C和D为配置语法可以任意组合搭配使用

F.`配置文件表达式`

	value支持转义和变量,转移为\符号
	例如 
	" value dsd " 则为带左右空格的字符串

	\" value 为 " value

	value${name} 为value拼接已经配置的name值

G.文本块
	
	{"

	"}

	name=value value为空则value为上一个语句块，否则为上一个语句块加换行符加value

	配置文件可以指定environment，resourcePath(即资源路径，可以指定任意目录可以将资源不放到class目录下)

H.可以扩展配置文件的特定的name
参考在BeanFactoryProvider中已经实现扩展

	environment 设置环境变量
	resourcePath 设置资源目录
	propterties 添加配置文件
	include ioc扫描包
	exclude ioc扫描包排除
	filter ioc扫描包排除正则表达式
	bean ioc手动添加生产对象
	context ioc手动添加context配置文件(absir-context 的 BeanFactoryProviderContext中)

<a name="cn-ioc-configure-extend"></a>
##### ＝＝＝配置文件扩展＝＝＝
###### 1.Context.xml
absir-context模块中，即入口的BeanFactoryProvider为BeanFactoryProviderContext或子类即可。
配置文件中可自定义一个或多个生成对象。

定义生成对象的dom格式为


	<bean class="类名" name="生成对象名" scope="生命周期">
		<constructor>
			对象定义或嵌套
			对象定义或嵌套
			...
		</constructor>
		<property name="" value="">
			对象定义或嵌套
		</property>
		<method name="" at="started|stopping">
			对象定义或嵌套
			对象定义或嵌套
			...
		</method>
	</bean>

对象定义或嵌套的内容格式为

	<array>
		对象定义或嵌套
		对象定义或嵌套
		...
	</array>
	
	<map>
		对象定义或嵌套(附加key="")
		对象定义或嵌套(附加key="")
		...
	</map>
	
	<ref value="引用IOC生成中的名称" required="是否必须">
	
##### 2.conf文件

absir-developer模块中，可以为继承ConfigureBase类添加conf配置文件。

通过ConfigureUtils获取ConfigureBase配置对象，可以自动加载为于classPath加conf/加类simpleName.conf里面的配置信息(路径规则可以通过子类复写)。

配置文件读取语法和主配置文件一样。

##### 3.xls文件

absir-developer模块中，可以通过XlsUtils获取XlsBase配置对象列表，自动读取位于resourcePath的xls文件。支持任意格式对象。

同时XlsUtils可以提供导出xls数据模版,achieve-site模块的测试类 TestXlsExport可以演示。

[回目录](#cn-title)

<a name="cn-ioc-featrue"></a>
## Ioc功能核心

#### 1.基础标签说明

	`@Basis` Ioc基础功能组件

	`@Base{order}` 可以覆盖生成对象 order决定生成覆盖顺序， 小的覆盖大的， 相同则子类覆盖父类

	`@Configure` 注入配置类，类中存在注入属性或生成对象的静态方法

	`@Bean` 生成对象 支持 class, constructor,method

	`@Inject{value,type}` 

在类上则会执行Class类中静态方法
value为匹配名称 type 为匹配类型

	/** 必须模式 */
	Required,

	/** 可选模式 */
	Selectable,

	/** 观察者模式 */
	ObServed,

	/** 实观察者模式(必须有值调用) */
	ObServeRealed,
	
	其中必须模式和可选模式为是否一定需要注入值存在。
	观察者模式和实观察者模式，是检测BeanFactory内部变化，BeanFactory加入生成(或移除生成对象)时通知属性或方法

在属性上则注入属性 支持单一对象,数组,列表,字典。
在方法上则执行注入方法

	`@Value{value,defaultValue}` 从配置文件中注入或方法参数， value可配置name,defaultValue为无配置信息时默认值, 支持配置文件表达式。

	`@Orders` 注入的数组或列表字段需要强制排序， 实现Orderable的为 order值 否则为0

	`@InjectOrder` @Inject注入方法，调用排序， order小的先调用

	`@Started` 程序开始调用

	`@Stopping` 程序关闭调用

	**特殊接口 `IBeanDefineEager` 和 @Inject出现在类上效果一直，都是提前载入类标签**

#### 2.功能模块的Ioc实现
IOC的基础功能模块需要添加添加@Basis标签同时至少实现下列功能接口中的一种

	`IBeanTypeFilter` 过滤提供的IOC类

	`IBeanFactoryAware` BeanFactory生成前后通知

	`IBeanDefineSupply` 发现并提供BeanDefine定义 （扫描包）

	`IBeanDefineProcessor` 再处理得到的BeanDefine （AOP等）

	`IBeanDefineAware` 对BeanDefine注册和移除通知

	`IBeanObjectProcessor` 生成完的对象再加工（注入属性等）

	`IBeanSoftReferenceAware` 软引用对象的注册和移除通知

	`IBeanFactoryStarted` 程序启动在BeanFacotry的映射通知

	`IBeanFactoryStopping` 程序关闭在BeanFacotry的映射通知

	**`InjectBeanFactory`组织功能模块**

需要添加添加@Bean标签同时至少实现下列功能接口中的一种

	`ITypeSupport` 类注入支持

	`IFieldSupport` 属性注入支持

	`IMethodEntry` 方法扫描定义

	`IMethodSupport` 方法注入支持

	`IMethodDefine` 方法注入定义

#### 3.Ioc功能特色

###### 1.支持静态属性，方法，注入和定义生产对象

###### 2.支持自身注入，即可以注入到本身的属性或方法中

###### 3.软引用注入，注入后有引用则保留，否则移除

[回目录](#cn-title)

<a name="cn-aop-featrue"></a>
## Aop功能核心

### 1.Aop功能的具体实现
absir-aop提供了基础的`AopBeanDefine`的`IBeanDefineProcessor`处理类。Aop对象都是通过AopBeanDefine生产得到。AopBeanDefine在生成中会获取调用所有AopMethodDefine对象来得到是否对对象中的Method有Aop定义。这样我们就很容易扩展自己的Aop功能。

标签说明

	`@Proxy{jdk, impl}` 代理模式为jdk还是为cglib

	`@Impl{value}` 代理对象实现对象类（通过IOC容器获取）

### 2.Aop的具体功能应用

	`@Aync{timeout, notifier}` 异步执行, timeout 异步超时时间， notifier通知执行(采取最后通知机制，避免并发执行)

	`@Transaction` 事务执行，执行前后开启提交事务，具体参数参考

	`@DataCache` 缓存执行返回

	`@DataSession` 配置查询语句session获取name

	`@DataQuery` 配置查询语句

	`IMethodAdvice` 方法Aop执行点（拦截任意生成对象方法，包括jar包中的生成对象）

	几个抽象现实类 `MethodBefore` `MethodAfter` `MethodRound` `MethodReturn` `MethodRound` 

[回目录](#cn-title)

spring拥有的功能基本都有 还有好多要写 好多......

<a name="cn-other"></a>
先提前简单介绍一下几个特色功能

## 抽象输入的MVC
即无论http请求，还是socket数据包，或者用户的命令行输入都可以抽象为一个Input请求，即可以作用在Server服务对象上
Server对象本身是通过IOC创建，支持拦截器，自定义mappingPath，mappingPathResolver, 方法输入参数自动数据绑定等

例如如下代码

	@Server
	public class admin_user extends AdminServer {

	/**
	 * @param input
	 */
	public void password(Input input) {
		input.getModel().put("userId", SecurityService.ME.getUserBase(input).getUserId());
	}

	/**
	 * 修改密码
	 * 
	 * @param password
	 * @param newPassword
	 * @param input
	 * @return
	 */
	@Mapping(method = InMethod.POST)
	public String password(@Param String password, @Param String newPassword, Input input) {
	
	
	就可以服务所有Input请求 请求默认地址为 user/password
	
	想定制修改地址 或者 默认地址规则都很简单
	
在java web开发时通常会将大量的静态数据请求放在一个空格的web project里面，那是大多数url匹配引擎是采用顺序匹配的方法，效率非常底下。

absir-server模块中独创的 二分法url匹配机制，效率是顺序匹配的指数级，再也不需要考虑数url匹配效率了。

## 模版生成
absir-developerImpl 模块提功能居于实体的一套生成开发模版的基础服务，强大的property支持的覆盖机制，让你可以在自由的定制实体属性规则。

ServerDiyView 可以提供默认用developer模块生成，实际模版的方法。

其中自带的absir-layout 在开发者环境或开发者登录的情况下请求url后加上?diy 就可以用absir-layout自由拖拽布局，模版。

## 自动菜单

achieve-server中 MenuUtils中的包扫描获取默认菜单，让你不需要手动维护开发过程中的菜单列表。

## 权限机制
自带userrole权限机制， 自带url和 entity两种权限规则，entity权限支持到字段级别。

## 字段可以定义权限规则

	@MappedSuperclass
	public class JbUser extends JbBean {

	@JaLang("开发者")
	@JaEdit(editable = JeEditable.DISABLE)
	@JaField(assocClasses = JbPermission.class, referencEntityClass = DeveloperAssoc.class)
	private boolean developer;
	
	这样一个简单定义获取用户列表时，非developer用户就会排除列表中的eveloper用户。

	public class DeveloperAssoc implements IAssocDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.dao.IAssocDao#supportAssocClass(java.lang.Class,
	 * java.lang.String, com.absir.aserv.system.bean.proxy.JiUserBase,
	 * com.absir.aserv.support.entity.value.JePermission)
	 */
	@Override
	public boolean supportAssocClass(Class<? extends JiAssoc> assocClass, String rootEntityName, JiUserBase user, JePermission permission) {
		return user != null && !user.isDeveloper();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.dao.IAssocDao#assocConditions(java.lang.String,
	 * com.absir.aserv.system.bean.proxy.JiUserBase,
	 * com.absir.aserv.support.entity.value.JePermission, java.lang.Object,
	 * com.absir.aserv.jdbc.JdbcCondition,
	 * com.absir.aserv.jdbc.JdbcCondition.Conditions,
	 * com.absir.aserv.jdbc.JdbcCondition.Conditions)
	 */
	@Override
	public void assocConditions(String rootEntityName, JiUserBase user, JePermission permission, Object strategies, JdbcCondition jdbcCondition, Conditions includeConditions,
			Conditions excludeConditions) {
		excludeConditions.add(jdbcCondition.getCurrentPropertyAlias() + ".developer");
		excludeConditions.add(false);
	}
	}


## 语言层面支持国际化

	国际化开关就一个 `lang.i18n` 配置属性
	
	`lang.locale` 定义默认语言
	
	`lang.locales` 定义支持语言 
	
	`lang.resouce` 定义语言文件目录 默认为${classPath}lang/
	
	语言文件同样支持配置文件读取语法
	

	@Embeddable
	public static class LangEmbed {

		public String name;

		/**
		 * @return the name
		 */
		@Langs
		public String getName() {
			return name;
		}
	}
	
	只是在get方法上加一个@Langs标签 就使的国际化打开后 LangEmbed的name属性支持国际化。
	
	同时所有的国际化信息全部存储到JLocale表中不同的localeCode字段下，是不是批量处理也很方便。
	
	entityName id  		name  			_0 	_1 	_2
	LangBean	3		name				
	LangBean	langEmbed@3	name				

	
	在开发模版中用 Pag.getLang(name, echo) 就可以定义默认显示语言， 国际化打开后又可以支持国际化。
	
	是不是国际化很爽。
	
## 自带entityApi和entityAdmin

默认实现了entity的api接口 和 entity的管理后台（暂时只有absir-developerJsp）

大多数情况我们只需要创建实体 其他什么都不用写。。。。


暂时先写这么多，
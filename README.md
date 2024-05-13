---
aliases:
  - DI
  - Spring IOC/DI
Created-Date: 2024-05-13T10:04:00
Last-Modified-Date: 2024-05-13T10:04:00
備註: 主要紀錄 Spring IOC 的配置方式
---
>[!NOTE] Spring IOC 的配置步驟
>Step.1:
>編寫配置訊息(xml、annotation、配置類)，配置有哪些 Class 要成為 IOC 組件，以及組件之間的引用關係。 
>Step.2 指定使用哪個配置訊息並實例化 IOC 容器物件。
>Step.3 在 JAVA 中獲取 Spring IOC 裡的組件(@Autowired 等)。

***大部分 IOC 觀念都是在 XML 配置方式介紹，Annotation、配置類僅做一個配置展示。***
## Spring IOC 相關依賴
>[!NOTE] Spring-Context 與 Spring-Core 差別
>目前僅知道 Spring-Core 提供了關於 IOC、DI 等相關基礎功能，而 Spring-Context 則是使用 Spring-Core 提供的這些功能再做進一步的功能擴充，具體增強了什麼還不知道。
```xml
<dependencies>
    <!--spring context 依賴-->
    <!--當引入 Spring Context 依賴後，即可使用 Spring IOC 的相關功能-->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>6.0.6</version>
    </dependency>
    <!--junit5 Test-->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
        <version>5.3.1</version>
    </dependency>
</dependencies>
```
---
## 基於 XML 的 Spring IOC/DI 配置方式
### 具體實現圖
首先自定義 Class，並且透過 XML 配置指定此 Class 成為 Spring Bean，然後創建 Spring IOC 容器，若是以 xml 配置的話即是 ClassPathXMLApplicationContext，並且指定此 IOC 容器要以哪個 XML 配置，之後 IOC 容器根據 XML 配置檔去創建組件。
![[Pasted image 20240513102509.png]]

Spring 會把 XML 轉換成一個 BeanDefinition 的物件。
![[Pasted image 20240513115707.png]]
### 創建 Bean 的四種方式
*根據 JAVA Class 實例化物件的不同方式，若想將 Class 加入 Spring IOC 容器，則在 XML 的配置方式也不同，又分為以下四種。*
#### 三種方式的 XML 配置檔(不含有參Contructor)
```XML
<?xml version="1.0" encoding="UTF-8"?>  
<beans xmlns="http://www.springframework.org/schema/beans"  
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">  
<!-- 無參Contructor、靜態工廠、非靜態工廠 的要求就是 Class 必須有者 無參Contructor -->  
<!-- 1. 使用 無參Contructor 實例化物件的 Class，進行 IOC 配置的範例 -->  
<!--  
- id: 代表的是這個 bean 的唯一標識。所以不得跟其他 bean 重複。  
- class: 指定要變成 bean 的 class 是誰。  
- 默認配置下， bean 是 singleton 的。  
- 一個 bean 標籤就是一個 IOC組件(Bean)。  
- 以下範例實例化了兩個 singleton 的 bean。  
-->  
<bean id="happyComponet1" class="com.atguigu.ioc_01.HappyComponent"/>  
<bean id="happyComponet2" class="com.atguigu.ioc_01.HappyComponent"/>  
<!-- 2. 使用 靜態工廠 實例化物件的 CLass，進行 IOC 配置的範例 -->  
<!--  
- id:同 1。  
- class: 指定的是 靜態工廠 的 Class。  
- factory-method: 靜態工廠方法  
- Spring 會調用指定的 class 的 factory-method 來創建 bean個人猜測: 當使用靜態工廠時，只要指定 工廠類及方法 即可，Spring 應該會自動將 return 回來  
的物件放入 Spring IOC Containter-->  
<bean id="clientService1" class="com.atguigu.ioc_01.ClientService" factory-method="createInstance"/>  
<!-- 3. 使用 非靜態工廠 實例化物件的 Class，進行 IOC 配置的範例 -->  
<!-- 3.1 先配置 非靜態工廠 Class 成為 bean --><bean id="DefaultServiceLocator1" class="com.atguigu.ioc_01.DefaultServiceLocator"/>  
<!-- 3.2 通過指定 非靜態工廠bean及方法， 來完成配置 bean 的生成方式 -->  
<bean id="clienServiceImpl1" factory-bean="DefaultServiceLocator1" factory-method="createClientServiceInstance"/>  
</beans>
```
##### 無參Contructor Class
```java
public class HappyComponent {  
  
//默認包含了無參Contructor  
  
public void doWork() {  
System.out.println("HappyComponent.doWork");  
}  
}
```
##### 靜態工廠 Class
```java
public class ClientService {  
private static ClientService clientService = new ClientService();  
private ClientService() {}  
  
public static ClientService createInstance() {  
  
return clientService;  
}  
}
```
##### 非靜態工廠 Class
```java
public class DefaultServiceLocator {  
  
private static ClientServiceImpl clientServiceImpl = new ClientServiceImpl();  
  
public ClientServiceImpl createClientServiceInstance() {  
return clientServiceImpl;  
}  
}
```

```java
public class ClientServiceImpl {  
}
```
#### 有參Contructor Class、Bean 之間的相互引用(依賴注入 Dependency Injected)
*這裡介紹的是，若 Bean 之間需要相互引用，該如何在 XML 進行配置，主要有以下兩種方式。*
- 基於有參構造函數的依賴注入。
- 基於 Setter 的依賴注入。
##### 示意圖
![[Pasted image 20240513120515.png]]
##### 使用 有參Contructor 實現 DI 的範例
###### Class 範例
```java
public class UserService {  
  
private UserDao userDao;  
private int age;  
private String userName;  
  
public UserService(UserDao userDao) {  
this.userDao = userDao;  
}  
  
public UserService(UserDao userDao, int age, String userName) {  
this.userDao = userDao;  
this.age = age;  
this.userName = userName;  
}  
}
```

```java
public class UserDao {  
}
```
###### 使用 有參Contructor 的 XML 配置檔
```XML
<?xml version="1.0" encoding="UTF-8"?>  
<beans xmlns="http://www.springframework.org/schema/beans"  
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">  
  
<!-- 單個參數的 Contructor 依賴注入 -->  
<!--  
step 1. 將 object 都放入到 ioc contatiner聲明的順序不會影響到 DI 賦值，譬如 userService 要注入 userDao，但不會因為先聲明 userService而導致不能注入 userDao。  
-->  
<bean id="userDao" class="com.atguigu.ioc_02.UserDao"/>  
<bean id="userService1" class="com.atguigu.ioc_02.UserService">  
<!--  
step 2. 使用 contructor 的 DI 配置  
< contructor-arg DI 配置:  
value:若 contructor 的參數是直接屬性值，譬如 String、int 等等就使用這個賦值。  
ref: 若 contructor 的參數是引用數據類型，就使用這個賦值。  
-->  
<constructor-arg ref="userDao" />  
</bean>  
<!-- 多個參數的 Contructor 依賴注入 -->  
<bean id="userService2" class="com.atguigu.ioc_02.UserService">  
<!-- 賦值的方式又分為以下幾種:  
- 依照 contructor 的參數順序賦值，不推薦故不展示。  
- 依照 contructor 的參數名進行賦值，這裡展示的為此種。  
- 依照 contructor 的參數的 index 進行賦值，譬如 contructor 的參數列表是 userDao、  
age、userName，故對應到 index 0、1、2，不推薦故不展示。  
-->  
<constructor-arg name="userDao" ref="userDao"/>  
<constructor-arg name="age" value="20"/>  
<constructor-arg name="userName" value="小白"/>  
</bean>  
</beans>
```

##### 使用 Setter 的 實現 DI 的範例
###### Class 範例
```java
public class SimpleMovieLister {  
  
private MovieFinder movieFinder;  
  
private String movieName;  
  
public void setMovieFinder(MovieFinder movieFinder) {  
this.movieFinder = movieFinder;  
}  
  
public void setMovieName(String movieName){  
this.movieName = movieName;  
}  
  
// business logic that actually uses the injected MovieFinder is omitted...  
}
```

```java
public class MovieFinder {  
}
```
###### 使用 Setter 的 XML 配置檔
```XML
<?xml version="1.0" encoding="UTF-8"?>  
<beans xmlns="http://www.springframework.org/schema/beans"  
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">  
<!-- 使用 setter 方法進行 DI 的 XML 配置-->  
  
<bean id="movieFinder" class="com.atguigu.ioc_02.MovieFinder"/>  
<bean id="simpleMovieLister" class="com.atguigu.ioc_02.SimpleMovieLister">  
<!--  
name 並不是 Class Field Name，而是 setter method 去掉 set 後首個字母小寫的值  
在 mybatis 中，property name 也是這樣的，看 setter 後的字母是誰，而非 Field Name。  
value、ref 則跟之前的使用方式一樣。  
-->  
<property name="movieName" value="大白"/>  
<property name="movieFinder" ref="movieFinder"/>  
</bean>  
</beans>
```
### Spring-Config 的約束
在使用 XML 配置 Spring IOC Bean 時，檔案前面都會有以下程式:
```XML
<?xml version="1.0" encoding="UTF-8"?>  
<beans xmlns="http://www.springframework.org/schema/beans"  
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">  
</beans>
```
而這個的作用是用來約束 XML 標籤的，因為 XML 的標籤是可自定義擴充的，但當引入 約束檔 後，就必須按照約束檔的格式來配置 XML 檔，變成像是 HTML 那樣，得使用被規範好的標籤。
## 基於 XML 的 IOC Container 創建方式
*前面使用 XML 配置了 Spring IOC 的相關訊息，但此步驟就只是配置了哪些 Class 是要被變成 Bean 的，最後還要把此 XML 配置文件傳遞給 Spring IOC Container 讀取，才會真正創建 XML 配置檔裡的 Bean 等等。*
### Test Class
此 JUnit 使用的 Class 是 package ioc_03 下的 HappyComponent，具體去看 github。
```java
public class SpringIOCTest {  
  
/**  
* 如何創建 IOC Container 並讀取配置文件  
*/  
@Test  
public void createIOC(){  
/**  
* interface:spring提供了 BeanFactory 與 ApplicationContext 兩個 interface  
* BeanFactory: BeanFactory 是 Spring IOC Container 的一個核心接口，  
* 定義了 Spring IOC Container 的最基本規範，所以，這代表所有的具體實現類都要遵守  
* BeanFactory  
* ApplicationContext: 是基於 BeanFactory 的擴展，但具體擴充了什麼我沒研究  
* Implement Class:  
* 以下皆可透過 Contructor 直接創建  
* 具體的 IOC Container 實現類，分為以下:  
* ClassPathXmlApplicationContext: 讀取 classpath 下的 xml 配置文件的 ioc container  
* 也就是編譯後產生的 classes 下的 XML 配置文件  
* FileSystemXmlApplicationContext: 讀取指定文件位置的 xml 配置文件的 ioc container  
* AnnotationConfigApplicationContext: 讀取配置類的 ioc container  
* WebApplicationContext: 讀取 web 項目專屬的配置的 ioc container  
*/  
  
// 方式 1: 創建同時直接指定 XML(推薦)  
ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-04.xml");  
  
// 方式 2: 先創建 ioc 容器，再指定 XML，再刷新(了解即可)  
// 這方式通常是源碼在用的，像方式1內部的 CODE 其實也是這種方式  
ClassPathXmlApplicationContext applicationContext1 = new ClassPathXmlApplicationContext();  
applicationContext1.setConfigLocations("spring-04.xml"); // 配置外部 XMLapplicationContext1.refresh(); // 調用 IOC、DI 的過程  
}  
  
/**  
* 如何獲取 IOC Container 裡面的 Bean  
*/  
@Test  
public void getBeanFromIOC() {  
// 1. 創建 IOC ContainterClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-04.xml");  
// 2. 讀取 IOC Container 裡的 Bean// 這裡 3 個方案獲得的 bean 都是同一個，因為 spring 默認是使用 singleton 的，若是設定成 prototype，才會每 getBean 一次就得到一個不同的 bean obj// 方案1: 只根據 bean id 獲得，getBean 得到的是一個 Object 類型的物件  
HappyComponent happyComponent1 = (HappyComponent) applicationContext.getBean("happyComponent");  
// 方案2: 同時根據 bean id 與 Class 類型獲取  
HappyComponent happyComponent2 = applicationContext.getBean("happyComponent", HappyComponent.class);  
// 方案3: 直接根據 Class 獲取，若用此種方式，須注意，此 Class 在 IOC 中只能有一個 Bean，也就是必須是 singleton 的，  
// 否則會報 NoUniqueBeanDefinitionExceptionHappyComponent happyComponent3 = applicationContext.getBean(HappyComponent.class);  
happyComponent1.doWork();  
System.out.println(happyComponent1==happyComponent2);  
System.out.println(happyComponent1==happyComponent3);  
  
/* result:  
HappyComponent.doWork  
true  
true  
*/  
}  
}
```
## Bean(組件)的週期方法與作用域
### 週期方法
週期方法指的是，此方法到了某個時間節點，就會被主動調用，譬如 servlet 裡的 init、service、destroy，我們只要編寫方法裡的業務邏輯即可，不需要主動地去調用它們，而 servlet 的週期方法是被 tomcat 調用的，Bean 則是被 Spring IOC Container 調用。
而在 Spring 中，週期方法會在 Bean 的實例化與銷毀時被調用，而週期方法並不像 servlet 一樣是透過繼承來決定的，而是在配置文件中去指名誰是週期方法。
#### 範例
##### Test Class
```java
@Test  
public void BeanLifeCycleTest() {  
ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-05.xml");  
// 必須手動的釋放 IOC Container 才會去調用 Bean 的 destroy-methodapplicationContext.close();  
}
```

```java
public class JavaBean {  
  
/**  
* 對於週期方法，唯一的要求就是必須是 public void，命名隨便  
*/  
public void init() {  
System.out.println("Bean 初始化方法調用");  
}  
  
public void clear() {  
System.out.println("Bean 銷毀方法調用");  
}  
}
```
##### XML 配置檔
```XML
<?xml version="1.0" encoding="UTF-8"?>  
<beans xmlns="http://www.springframework.org/schema/beans"  
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">  
<!-- init-method: 指定 Bean 實例化時會調用的方法  
destroy-method: 指定 Bean 被銷毀時會調用的方法  
Spring IOC 容器就會在特定的時間節點，去調用這些方法  
-->  
<bean id="javaBean" class="com.atguigu.ioc_04.JavaBean" init-method="init" destroy-method="clear"/>  
</beans>
```

### 作用域
在 Spring 中，XML 配置文件會被轉成 Spring 中的一個 BeanDefinition Object，而 Spring IOC Container 則可以根據 BeanDefinition 提供的 Bean 訊息去透過反射創建多個 Bean Object。
而具體要創建多少個 Bean Object，則由 Bean 的作用域(Scope) 來決定。
![[Pasted image 20240513141645.png]]

因為就只是多介紹一個 scope 的使用，故直接取教學文件的 CODE 下來，就不實際自己寫一遍了。
```XML
<!-- scope属性：取值singleton（默认值），bean在IOC容器中只有一个实例，IOC容器初始化时创建对象 -->
<!-- scope属性：取值prototype，bean在IOC容器中可以有多个实例，getBean()时创建对象 -->
<bean id="happyMachine8" scope="prototype" class="com.atguigu.ioc.HappyMachine">
    <property name="machineName" value="happyMachine"/>
</bean>

<bean id="happyComponent8" scope="singleton" class="com.atguigu.ioc.HappyComponent">
    <property name="componentName" value="happyComponent"/>
</bean>
```

## FactoryBean 的使用與特性
先跳用，這通常是提供給第三方框架在使用的。


---
## 基於 Annotation 的 Spring IOC/DI 配置方式
Annotation 就相當於是 XML 的 bean 標籤，用來配置哪些 Class 是要成為 Bean 的，只是配置的方式從 XML 變成 JAVA Annotation，而 Spring 掃描到這些 Annotation 後，會根據具體的 Annotation 做出對應的動作，***但 Spring 並不會去掃描全部的路徑，因此我們還需要編寫 XML 配置文件，但此時配置的就不是 bean 的訊息，而是告訴 Spring 它要去掃描哪些路徑，也就是告訴  Spring，我們的哪些路徑下可能有 Annotation 需要被掃描。***
使 Class 成為 Bean 的 Annotation 有 @Component、@Controller、@Service、@Repository，因為 Annotation 的方式個人很熟悉，故不做這裡的筆記，只記錄 XML 如何配置掃描。
### XML 掃描配置文件
這是直接從 影片教學文件 Copy 下來的。
```XML
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd">
    <!-- 配置自动扫描的包 -->
    <!-- 1.包要精准,提高性能!
         2.会扫描指定的包和子包内容
         3.多个包可以使用,分割 例如: com.atguigu.controller,com.atguigu.service等
    -->
    <context:component-scan base-package="com.atguigu.components"/>
  
</beans>
```
### TEST Class
此時在創建 IOC Container 時，讀取的就是 XML 掃描配置文件。
![[Pasted image 20240513145050.png]]
## 週期方法與作用域的 Annotation
### 週期方法
```java
public class BeanOne {

  //周期方法要求： 方法命名随意，但是要求方法必须是 public void 无形参列表
  @PostConstruct  //注解制指定初始化方法
  public void init() {
    // 初始化逻辑
  }
}

public class BeanTwo {
  
  @PreDestroy //注解指定销毁方法
  public void cleanup() {
    // 释放资源逻辑
  }
}
```
### 作用域
```java
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON) //单例,默认值
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE) //多例  二选一
public class BeanOne {

  //周期方法要求： 方法命名随意，但是要求方法必须是 public void 无形参列表
  @PostConstruct  //注解制指定初始化方法
  public void init() {
    // 初始化逻辑
  }
}
```
## 依賴注入的 Annotation
@Autowired、@Resource 等等 Annotation，有需要直接看教學文件即可，這裡不多做介紹。
@Value、@Qualifier 等等 Annotation 也是。

## 使用 Annotation 時，XML 配置檔的職責
- 配置掃描 Annotation 的路徑。
- 將第三方庫添加到 Spring IOC 中。
- 導入外部文件，譬如一些 properties 檔案，供 @Value 使用。
---
## 基於 配置類 的方式管理 Bean
***就算使用 Annotation 的方式來實現 Spring IOC/DI，依然需要配置 XML 文件來指定 Spring 需要掃描哪些路徑，並且如果要將第三方庫的Class 加入到 IOC 時，因為不能修改第三方庫的 Code，故不能添加@Component 等 Annotation，因此還是得透過 XML 來配置，而配置類就是用來整個取代 XML 配置文件的，只要在 Class 上添加 @Configuration，此 Class 就是一個 配置類了。***
![[Pasted image 20240513152755.png]]
### 配置類範例
當使用配置類而非 XML 時，此時 Spring IOC 容器就不是 `ClassPathXmlApplicationContext` 而是 `AnnotationConfigApplicationContext`。

```java
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

//标注当前类是配置类，替代application.xml    
@Configuration
//使用注解读取外部配置，替代 <context:property-placeholder标签
@PropertySource("classpath:application.properties")
//使用@ComponentScan注解,可以配置扫描包,替代<context:component-scan标签
@ComponentScan(basePackages = {"com.atguigu.components"})
public class MyConfiguration {
    
}
```
## @Bean 的使用與週期方法、作用域
@Bean 主要是用來添加一些無法添加 @Component 的第三方庫的，像連接池等等。
至於使用 @Bean 成為 Bean 的物件該如何管理週期方法與作用域，直接看課件。
## @Import
略

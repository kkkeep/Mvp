

[TOC]







## MVP 之网络请求使用方式：



### MVP 移植



```groovy
在你项目的根目录下的 build.gradle 文件中加入依赖：


	allprojects {
        repositories {
            ....
            maven { url 'https://jitpack.io' } // 添加这一行 
            
        }
    }


在自己的app 目录下的build.gradle 文件里面加入：



```



 

### Mvp 初始化

> 框架的初始化一般写在 Application 的 onCreate 方法中.**（注意要在AndroidMainfest.xml 文件中使用Application）**
>
> 步骤：
>
> ```java
> //1. new  一个 MkNetConfig 对象 传入 BaseUrl 
> MkNetConfig netConfig = new MkNetConfig("https://www.xxx.com"){
>          @Override
>          public HashMap<String, String> getCommonParams() {
> 
>              // 返回一个装有公共参参数 hashmap,如果没有公共参数，不需要重新这个方法
>          }
> 
>          @Override
>          public HashMap<String, String> getCommonHeaders() {
>             // 返回一个装有公共参请求头 hashmap,如果没有公共请求头，不需要重新这个方法
>          }
>   
>   
>      				@Override
>             public String getDataFieldName() {
>                 return "response"; // 父类默认返回的 data,如果你也是data 就不用重写
>             }
> 
>             @Override
>             public int getSuccessCode() {
>                 return 0;// 父类默认返回 1 表示成功，如果如果你也是1 就不用重写
>             }
> 
>             @Override
>             public String getCodeFieldName() {
>                 return "code";// 父类默认返回 code 如果如果你也 code 就不用重写
>             }
>   
>   
>      };
> 
> //2 设置自定义拦截器（如果有）。接受一个List<Interceptor> appIntercepters 类型，没有就不调用
> netConfig.setAppIntercepters(interceptors); // 可选
> 
> //3  设置自定义 Json  转换器（如果有）,接受 Converter.Factory factory 类型，，没有就不调用
> netConfig.setFactory(JDJsonFactory.create()); // 可选
> 
> 
>  /**
>      * 4. 设置 Retrofit 的 api service 接口，如果MkApiService 满足不了你的需求，那么需要调用传入自己的 api service,否则不需要调用。目前 MkApiService 支持:
>      * 
>      * 1 Get ，
>      *
>      * 2.@FormUrlEncoded  的 Post
>      *
>      * 3. 带参数的文件上传
>      * 4. 
>      *
>      */
> netConfig.setAppApiService(JDApiService.class); // 可选
> 
> // 5. 设置 网络回来的 数据实体类（java bean 对象），该类必须实现 INetEntity 接口
> 
>  netConfig.setResponseEntityClass(HttpResult.class) // 必须调用
>    
>    
>    
> // 6. 调用  init 方法 初始化，一个参数 context,第二个参数 MkNetConfig 对象,第三个参数是User实体类的class, 用户实体类，必须实现MkIUser 接口
>    
>     MvpManager.init(this,netConfig,User.class);
> 
> 
> ```
>
> 

###  第一种：Smart(最简单的使用方式)

> 不需要写P 层。M的层。M 层只是把网络数据加载回来，交给 P 层，M 层里面没有对数据做任何其他操作，比如缓存之类的，P 层也只是做了一个简单的中转。因此这种方式只适用与一个页面，最多不超**3种数**据类型，而且不需要在M 层里面对请求回来的数据做任何操作的需求
>
> ```java
> //使用方式：
> 
> // 第一步：写一个V 层（Fragment 或者 Activity 的子类 ） 继承 MkBaseSmartActivity 或者 MkBaseSmartFragment,并制定一个泛型参数（你需要的数据类型）。然后实现 onServerSuccess 和 onFail 的接口方法
> 
> // 例如：class  UserFragment extends MkBaseSmartFragment<User>
> 
> // 第二步：new 一个 rquest,设置好 URL  和参数 或者 headers
> // get 请求
> MkDataRequest dataRequest = new MkGetRequest("url");// 或者
> //post 请求  new MkPostRequest("url")
> // 上传文件请求 MkUpLoadFileRequest（"url","key","filepaht"），key 是上传文件的请求参数对应的key
> //dataRequest.putAllParams(hashMap) // 批量添加参数
> //dataRequest.putAllHeaders(hashMap); // 批量添加请求头
> //dataRequest.putParams(String key,String value) // 单个添加参数
> //requet.putHeader(String key,String value) // 单个添加请求头
> //dataRequest.isEnableCancel = true // 设置网络请求是否支持取消操作。默认不支持。
> // dataRequest.setRequestType(RequestType requestType) // 设置请求类型（第一次，刷新，加载更多），默认为第一次请求
> 
> // 第三步：直接调用 P  层的 doRequest 方法，把第一步 new 的request 传递进去，当请求回来时，如果成功回调 V 层onSuccess 方法 ，失败 回调V 层的onFail
> 	
> // 第四步：直接调用 BaseSmartFragment/Activity 的 doRequest
>  doRequest(dataRequest);
> 
> // 第五步：数据回调，在V 层里面重写onResult 方法，
> // dataResponse 里面包含了我们需要的结果
> // dataResponse.isOk() 判断是数据是否请求成功
> // dataResponse.getData() 获取返回的数据，
> // dataResponse.getMessage() 获取错误信息
> // dataResponse.getResponseType() 数据从哪儿来的，服务器，内存 或者sdcard
> // dataResponse.getRequestType() 是第一次请求回来的，还是刷新，还是加载更多。
> onResult(MkDataResponse<ColumnData> dataResponse)
>   
> ```



### 第二种：需要写P层和M 层

> 需要自己去写契约类，在契约类里面定义 V,P,M 层的接口。这种方式适用于以下情况：
>
> 1. V 层 涉及到的 数据类型（实体类/javabean） 超过 3 种
> 2. 如果需要在M 层对数据进行处理，并且这种处理是耗时操作，比如做缓存
>
> ```java
> // 契约类，自己写 V ，P.M 层的接口，
> public interface PageNewsContract {
>   
> 	// V 层 接口 继承 MkIBaseView,并且传入 P 层 的类型IPageNewsPresenter作为泛型参数
>     interface IPageNewView extends IBaseView<IPageNewsPresenter>{
> 
>         // 第一次数据加载回来（有可能是内存（viewpager 销毁后 重建后第一次请求），也有可能是从服务器（app 启动 sdcard 里面没有缓存））
>         void onFirstLoadSuccess(RecommendPageData data);
> 
>         // 第一次数据加载回来（指的是app 冷启动时第一次加载数据，从sdcard 里面 获取的）
>         void onFirstLoadSuccessNeedRefresh(RecommendPageData data);
> 
>         // 第一次加载失败（指的是从服务器请求失败）
>         void onFirstLoadFail(String error);
> 
>         // 刷新成功
>         void onRefreshSuccess(RecommendPageData data);
>         // 刷新是吧
>         void onRefreshFail(String error);
> 
>         // 加载更多成功
>         void onLoadMoreSuccess(RecommendPageData data);
> 
>         // 加载更多失败
>         void onLoadMoreFail(String error);
> 
>     }
>     
> // P 层 接口 继承 MkIBasePresenter,并且传入 V 层 的类型作IPageNewView为泛型参数
>     interface IPageNewsPresenter extends IBasePresenter<IPageNewView> {
> 
>         void getNewsListFirst(String columnId); // 第一次加载数据
> 
>         void refreshNewsList( String columnId); // 刷新
> 
>         void loadMoreNewsList(String columnId); // 记载更多
> 
>     }
> 
> // M 层接口，简单，不需要继承任何东西
>     interface IPageNewsMode {
>         void getNews(LifecycleProvider provider, Request dataRequest, MkICacheCallBack<RecommendPageData> callBack);
>     }
> 
> }
> 
> 
> 
> 
> 
> // P 层 继承 MkBasePresenter 传入 契约类里面V 层 接口作为泛型参数IPageNewView ，并且实现契约类里面定义的P 层的接口 IPageNewsPresenter
> 
> 
> public class PageNewsPresenter extends MkBasePresenter<IPageNewView> implements PageNewsContract.IPageNewsPresenter {
>   // 声明 M 层的 成员变量，类型为 契约类里面 M 层的接口类型
>     private PageNewsContract.IPageNewsMode mRepository;
>   
>   
>     public PageNewsPresenter() {
>       // 实例化一个 M 的一个对象
>         mRepository = RecommendNewsRepository.getInstance();
>     }
>     @Override
>     public void getNewsListFirst(String columnId) {
>     }
>     @Override
>     public void refreshNewsList(String columnId) {   
>     }
>     @Override
>     public void loadMoreNewsList(String columnId) {  
>     }
> }
> 
> 
> // V 层 继承 MkBaseMvpFragment 传入 契约类里面 定义的P 层的接口 IPageNewsPresenter 作为泛型参数
> // 同时实现 约类里面 定义的 V 层的接口
> public class PageNewsFragment extends MkBaseMvpFragment<PageNewsContract.IPageNewsPresenter> implements PageNewsContract.IPageNewView {
>     @Override
>     public int getLayoutId() {
>         return R.layout.fragment_news_page;
>     }
>     @Override
>     protected void initView() { 
>     }
> 
>     @Override
>     protected void loadData() {
>        // 找P 层要数据
>         mPresenter.getNewsListFirst(mColumnId);
>     }
> 
>     @Override
>     public void onFirstLoadSuccess(RecommendPageData data) {       
>     }
>     @Override
>     public void onFirstLoadSuccessNeedRefresh(RecommendPageData data) {
>     }
>     @Override
>     public void onFirstLoadFail(String error) {
>     }
> 
>     @Override
>     public void onRefreshSuccess(RecommendPageData data) {      
>     }
> 
>     @Override
>     public void onRefreshFail(String error) {}
> 
>     @Override
>     public void onLoadMoreSuccess(RecommendPageData data) { 
>     }
>     @Override
>     public void onLoadMoreFail(String error) {}
> 
> 	
>     @Override
>     public PageNewsContract.IPageNewsPresenter createPresenter() {
>       // 创建一个上面自己定义的P 层的对象
>         return new PageNewsPresenter();
>     }
> }
> 
> 
> // M 层  只需要实现契约类里面 M 层定义的接口
> 
> public class RecommendNewsRepository extends MkBaseMvpModel implements PageNewsContract.IPageNewsMode {
> 
>   
>     @Override
>     public void getNews(LifecycleProvider provider, MkDataRequest dataRequest, ICacheCallBack<RecommendPageData> callBack) {
>       
>       
> // 调用父类的 doRequest 请求数据，如果需要对数据处理，就new Consumer，
> doRequest(provider, dataRequest, new Consumer<RecommendPageData>() {
>             @Override
>             public void accept(RecommendPageData data) throws Exception {
> 	// 该方法被子线程执行，可以做耗时操作
>             	
>             }
>         }, callBack);
>       
>       
>       
>      // 如果你自定在App 里面定了 retrofit 的 ApiService 接口。想在M 层里面使用自己的 接口，那么调用 父类的subscribe 方法。
>       subscribe(provider, dataRequest, JDDataService.getApiService().getNews(dataRequest.getParams()), new Consumer<RecommendPageData>() {
>             @Override
>             public void accept(RecommendPageData data) throws Exception {
> 					// 该方法被子线程执行，可以做耗时操作
>             }
>         }, callBack);
> 
> 
> }
> 
> 
> ```

​	

### 第三种，只写一个Mode 层



```java
//  1.写一个 自己的M 层 继承 MkBaseMvpModel

// 2. 重写父类的 这个方法（三个参数） public <D> void doRequest(LifecycleProvider provider, MkDataRequest dataRequest, MkIBaseCallBack<D> callBack) 

// 3. 在重写的方法里面调用父类的方法（四个参数） public <D> void doRequest(LifecycleProvider provider, final MkDataRequest dataRequest, Consumer<D> consumer, final MkIBaseCallBack<D> callBack) ，其中第三个参数传入一个自己的额 Consumer 对象。

// 例如 ，用户登录时需要在子线程保持用户信息

public class LoginRepository extends MkBaseMvpModel {


    @Override
    public <D> void doRequest(LifecycleProvider provider, MkDataRequest dataRequest, MkIBaseCallBack<D> callBack) {
        super.doRequest(provider, dataRequest, new Consumer<D>() {
            @Override
            public void accept(D o) throws Exception {
              // 在这儿判断请求是否为登录请求，如果View 层只有一个请求，那就不用判断
                if (dataRequest.getUrl().equals(AppConstant.Url.LOGIN)) {
                    MvpUserManager.login((IUser) o);
                }
            }
        }, callBack);
    }
}



```


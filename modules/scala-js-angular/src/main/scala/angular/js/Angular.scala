package angular

import scala.scalajs.js._
import scala.scalajs.js.annotation._
import jquery.js._

package js {
trait Injectable extends Object {
  var `$inject`: Array[String] = _
}

package ng {

@JSName("ng.IServiceProvider")
trait IServiceProvider extends Object {
  def `$get`(): Dynamic = ???
}

@JSName("ng.IAngularStatic")
trait IAngularStatic extends Object {
  def bind(context: Any, fn: Function, args: Any*): Function = ???
  def bootstrap(element: String, modules: Array[Any]): auto.IInjectorService = ???
  def bootstrap(element: String): auto.IInjectorService = ???
  //def bootstrap(element: JQuery, modules: Array[Any]): auto.IInjectorService = ???
  //def bootstrap(element: JQuery): auto.IInjectorService = ???
  def bootstrap(element: Element, modules: Array[Any]): auto.IInjectorService = ???
  def bootstrap(element: Element): auto.IInjectorService = ???
  def bootstrap(element: Document, modules: Array[Any]): auto.IInjectorService = ???
  def bootstrap(element: Document): auto.IInjectorService = ???
  def copy(source: Any, destination: Any): Dynamic = ???
  def copy(source: Any): Dynamic = ???
  val element: JQueryStatic = ???
  def equals(value1: Any, value2: Any): Boolean = ???
  def extend(destination: Any, sources: Any*): Dynamic = ???
  def forEach(obj: Any, iterator: Function2[Any, Any, Any], context: Any): Dynamic = ???
  def forEach(obj: Any, iterator: Function2[Any, Any, Any]): Dynamic = ???
  def fromJson(json: String): Dynamic = ???
  def identity(arg: Any): Dynamic = ???
  def identity(): Dynamic = ???
  def injector(modules: Array[Any]): auto.IInjectorService = ???
  def injector(): auto.IInjectorService = ???
  def isArray(value: Any): Boolean = ???
  def isDate(value: Any): Boolean = ???
  def isDefined(value: Any): Boolean = ???
  def isElement(value: Any): Boolean = ???
  def isFunction(value: Any): Boolean = ???
  def isNumber(value: Any): Boolean = ???
  def isObject(value: Any): Boolean = ???
  def isString(value: Any): Boolean = ???
  def isUndefined(value: Any): Boolean = ???
  def lowercase(str: String): String = ???
  def module(name: String, requires: Array[String], configFunction: Any): IModule = ???
  def module(name: String, requires: Array[String]): IModule = ???
  def module(name: String): IModule = ???
  def noop(args: Any*): Unit = ???
  def toJson(obj: Any, pretty: Boolean): String = ???
  def toJson(obj: Any): String = ???
  def uppercase(str: String): String = ???
  var version: Any = _
}

@JSName("ng.IModule")
trait IModule extends Object {
  def animation(name: String, animationFactory: Function): IModule = ???
  def animation(name: String, inlineAnnotadedFunction: Array[Any]): IModule = ???
  def animation(`object`: Object): IModule = ???
  def config(configFn: Function): IModule = ???
  def config(inlineAnnotadedFunction: Array[Any]): IModule = ???
  def constant(name: String, value: Any): IModule = ???
  def constant(`object`: Object): IModule = ???
  def controller(name: String, controllerConstructor: Function): IModule = ???
  def controller(name: String, inlineAnnotadedConstructor: Array[Any]): IModule = ???
  def controller(`object`: Object): IModule = ???
  def directive(name: String, directiveFactory: Function): IModule = ???
  def directive(name: String, inlineAnnotadedFunction: Array[Any]): IModule = ???
  def directive(`object`: Object): IModule = ???
  def factory(name: String, serviceFactoryFunction: Function): IModule = ???
  def factory(name: String, inlineAnnotadedFunction: Array[Any]): IModule = ???
  def factory(`object`: Object): IModule = ???
  def filter(name: String, filterFactoryFunction: Function): IModule = ???
  def filter(name: String, inlineAnnotadedFunction: Array[Any]): IModule = ???
  def filter(`object`: Object): IModule = ???
  def provider(name: String, serviceProviderConstructor: Function): IModule = ???
  def provider(name: String, inlineAnnotadedConstructor: Array[Any]): IModule = ???
  def provider(`object`: Object): IModule = ???
  def run(initializationFunction: Function): IModule = ???
  def run(inlineAnnotadedFunction: Array[Any]): IModule = ???
  def service(name: String, serviceConstructor: Function): IModule = ???
  def service(name: String, inlineAnnotadedConstructor: Array[Any]): IModule = ???
  def service(`object`: Object): IModule = ???
  def value(name: String, value: Any): IModule = ???
  def value(`object`: Object): IModule = ???
  var name: String = _
  var requires: Array[String] = _
}

@JSName("ng.IAttributes")
trait IAttributes extends Object {
  @JSBracketAccess
  def apply(name: String): Any = ???
  @JSBracketAccess
  def update(name: String, v: Any): Unit = ???
  def $addClass(classVal: String): Unit = ???
  def $removeClass(classVal: String): Unit = ???
  def $set(key: String, value: Any): Unit = ???
  def $observe(name: String, fn: Function1[Any, Any]): Function = ???
  var $attr: Object = _
}

@JSName("ng.IFormController")
trait IFormController extends Object {
  var `$pristine`: Boolean = _
  var `$dirty`: Boolean = _
  var `$valid`: Boolean = _
  var `$invalid`: Boolean = _
  var `$error`: Any = _
  def `$addControl`(control: ng.INgModelController): Unit = ???
  def `$removeControl`(control: ng.INgModelController): Unit = ???
  def `$setDirty`(): Unit = ???
  def `$setPristine`(): Unit = ???
}

@JSName("ng.INgModelController")
trait INgModelController extends Object {
  def `$render`(): Unit = ???
  def `$setValidity`(validationErrorKey: String, isValid: Boolean): Unit = ???
  def `$setViewValue`(value: Any): Unit = ???
  var `$viewValue`: Any = _
  var `$modelValue`: Any = _
  var `$parsers`: Array[IModelParser] = _
  var `$formatters`: Array[IModelFormatter] = _
  var `$error`: Any = _
  var `$pristine`: Boolean = _
  var `$dirty`: Boolean = _
  var `$valid`: Boolean = _
  var `$invalid`: Boolean = _
}

@JSName("ng.IModelParser")
trait IModelParser extends Object {
  def apply(value: Any): Dynamic = ???
}

@JSName("ng.IModelFormatter")
trait IModelFormatter extends Object {
  def apply(value: Any): Dynamic = ???
}

@JSName("ng.IScope")
trait IScope extends Object {
  def `$apply`(): Dynamic = ???
  def `$apply`(exp: String): Dynamic = ???
  def `$apply`(exp: Function1[IScope, Any]): Dynamic = ???
  def `$broadcast`(name: String, args: Any*): IAngularEvent = ???
  def `$destroy`(): Unit = ???
  def `$digest`(): Unit = ???
  def `$emit`(name: String, args: Any*): IAngularEvent = ???
  def `$eval`(expression: String, args: Object): Dynamic = ???
  def `$eval`(expression: String): Dynamic = ???
  def `$eval`(expression: Function1[IScope, Any], args: Object): Dynamic = ???
  def `$eval`(expression: Function1[IScope, Any]): Dynamic = ???
  def `$evalAsync`(expression: String): Unit = ???
  def `$evalAsync`(expression: Function1[IScope, Any]): Unit = ???
  def `$new`(isolate: Boolean): IScope = ???
  def `$new`(): IScope = ???
  def `$on`(name: String, listener: Function): Function = ???
  def `$watch`(watchExpression: String, listener: String, objectEquality: Boolean): Function = ???
  def `$watch`(watchExpression: String, listener: String): Function = ???
  def `$watch`(watchExpression: String): Function = ???
  def `$watch`(watchExpression: String, listener: Function3[Any, Any, IScope, Any], objectEquality: Boolean): Function = ???
  def `$watch`(watchExpression: String, listener: Function3[Any, Any, IScope, Any]): Function = ???
  def `$watch`(watchExpression: Function1[IScope, Any], listener: String, objectEquality: Boolean): Function = ???
  def `$watch`(watchExpression: Function1[IScope, Any], listener: String): Function = ???
  def `$watch`(watchExpression: Function1[IScope, Any]): Function = ???
  def `$watch`(watchExpression: Function1[IScope, Any], listener: Function3[Any, Any, IScope, Any], objectEquality: Boolean): Function = ???
  def `$watch`(watchExpression: Function1[IScope, Any], listener: Function3[Any, Any, IScope, Any]): Function = ???
  def `$watchCollection`(watchExpression: String, listener: Function3[Any, Any, IScope, Any]): Function = ???
  def `$watchCollection`(watchExpression: Function1[IScope, Any], listener: Function3[Any, Any, IScope, Any]): Function = ???
  var `$parent`: IScope = _
  var `$id`: Number = _
  var `$$isolateBindings`: Any = _
  var `$$phase`: Any = _
}

@JSName("ng.IAngularEvent")
trait IAngularEvent extends Object {
  var targetScope: IScope = _
  var currentScope: IScope = _
  var name: String = _
  var preventDefault: Function = _
  var defaultPrevented: Boolean = _
  var stopPropagation: Function = _
}

@JSName("ng.IWindowService")
trait IWindowService extends Window {
}

@JSName("ng.IBrowserService")
trait IBrowserService extends Object {
}

@JSName("ng.ITimeoutService")
trait ITimeoutService extends Object {
  def apply(func: Function, delay: Number, invokeApply: Boolean): IPromise[Any] = ???
  def apply(func: Function, delay: Number): IPromise[Any] = ???
  def apply(func: Function): IPromise[Any] = ???
  def cancel(promise: IPromise[Any]): Boolean = ???
}

@JSName("ng.IIntervalService")
trait IIntervalService extends Object {
  def apply(func: Function, delay: Number, count: Number, invokeApply: Boolean): IPromise[Any] = ???
  def apply(func: Function, delay: Number, count: Number): IPromise[Any] = ???
  def apply(func: Function, delay: Number): IPromise[Any] = ???
  def cancel(promise: IPromise[Any]): Boolean = ???
}

@JSName("ng.IFilterService")
trait IFilterService extends Object {
  def apply(name: String): Function = ???
}

@JSName("ng.IFilterProvider")
trait IFilterProvider extends IServiceProvider {
  def register(name: String, filterFactory: Function): IServiceProvider = ???
}

@JSName("ng.ILocaleService")
trait ILocaleService extends Object {
  var id: String = _
  var NUMBER_FORMATS: ILocaleNumberFormatDescriptor = _
  var DATETIME_FORMATS: ILocaleDateTimeFormatDescriptor = _
  var pluralCat: Function1[Any, String] = _
}

@JSName("ng.ILocaleNumberFormatDescriptor")
trait ILocaleNumberFormatDescriptor extends Object {
  var DECIMAL_SEP: String = _
  var GROUP_SEP: String = _
  var PATTERNS: Array[ILocaleNumberPatternDescriptor] = _
  var CURRENCY_SYM: String = _
}

@JSName("ng.ILocaleNumberPatternDescriptor")
trait ILocaleNumberPatternDescriptor extends Object {
  var minInt: Number = _
  var minFrac: Number = _
  var maxFrac: Number = _
  var posPre: String = _
  var posSuf: String = _
  var negPre: String = _
  var negSuf: String = _
  var gSize: Number = _
  var lgSize: Number = _
}

@JSName("ng.ILocaleDateTimeFormatDescriptor")
trait ILocaleDateTimeFormatDescriptor extends Object {
  var MONTH: Array[String] = _
  var SHORTMONTH: Array[String] = _
  var DAY: Array[String] = _
  var SHORTDAY: Array[String] = _
  var AMPMS: Array[String] = _
  var medium: String = _
  var short: String = _
  var fullDate: String = _
  var longDate: String = _
  var mediumDate: String = _
  var shortDate: String = _
  var mediumTime: String = _
  var shortTime: String = _
}

@JSName("ng.ILogService")
trait ILogService extends Object {
  var debug: ILogCall = _
  var error: ILogCall = _
  var info: ILogCall = _
  var log: ILogCall = _
  var warn: ILogCall = _
}

@JSName("ng.ILogCall")
trait ILogCall extends Object {
  def apply(args: Any*): Unit = ???
}

@JSName("ng.IParseService")
trait IParseService extends Object {
  def apply(expression: String): ICompiledExpression = ???
}

@JSName("ng.ICompiledExpression")
trait ICompiledExpression extends Object {
  def apply(context: Any, locals: Any): Dynamic = ???
  def apply(context: Any): Dynamic = ???
  def assign(context: Any, value: Any): Dynamic = ???
}

@JSName("ng.ILocationService")
trait ILocationService extends Object {
  def absUrl(): String = ???
  def hash(): String = ???
  def hash(newHash: String): ILocationService = ???
  def host(): String = ???
  def path(): String = ???
  def path(newPath: String): ILocationService = ???
  def port(): Number = ???
  def protocol(): String = ???
  def replace(): ILocationService = ???
  def search(): Dynamic = ???
  def search(parametersMap: Any): ILocationService = ???
  def search(parameter: String, parameterValue: Any): ILocationService = ???
  def url(): String = ???
  def url(url: String): ILocationService = ???
}

@JSName("ng.ILocationProvider")
trait ILocationProvider extends IServiceProvider {
  def hashPrefix(): String = ???
  def hashPrefix(prefix: String): ILocationProvider = ???
  def html5Mode(): Boolean = ???
  def html5Mode(active: Boolean): ILocationProvider = ???
}

@JSName("ng.IDocumentService")
trait IDocumentService extends Document {
}

@JSName("ng.IExceptionHandlerService")
trait IExceptionHandlerService extends Object {
  def apply(exception: Error, cause: String): Unit = ???
  def apply(exception: Error): Unit = ???
}

//@JSName("ng.IRootElementService")
//trait IRootElementService extends JQuery {
//}

@JSName("ng.IQService")
trait IQService extends Object {
  def all(promises: Array[IPromise[Any]]): IPromise[Array[Any]] = ???
  def defer[T](): IDeferred[T] = ???
  def reject(reason: Any): IPromise[Unit] = ???
  def reject(): IPromise[Unit] = ???
  def when[T](value: T): IPromise[T] = ???
}

@JSName("ng.IPromise")
trait IPromise[T] extends Object {
  // FIXME: def then[TResult](successCallback: Function1[T, IHttpPromise[TResult]], errorCallback: Function1[Any, Any], notifyCallback: Function1[Any, Any]): IPromise[TResult] = ???
  //def then[TResult](successCallback: Function1[T, IHttpPromise[TResult]], errorCallback: Function1[Any, Any]): IPromise[TResult] = ???
  //def then[TResult](successCallback: Function1[T, IHttpPromise[TResult]]): IPromise[TResult] = ???
  //def then[TResult](successCallback: Function1[T, IPromise[TResult]], errorCallback: Function1[Any, Any], notifyCallback: Function1[Any, Any]): IPromise[TResult] = ???
  //def then[TResult](successCallback: Function1[T, IPromise[TResult]], errorCallback: Function1[Any, Any]): IPromise[TResult] = ???
  //def then[TResult](successCallback: Function1[T, IPromise[TResult]]): IPromise[TResult] = ???
  def then[TResult](successCallback: Function1[T, TResult], errorCallback: Function1[Any, TResult], notifyCallback: Function1[Any, Any]): IPromise[TResult] = ???
  def then[TResult](successCallback: Function1[T, TResult], errorCallback: Function1[Any, TResult]): IPromise[TResult] = ???
  def then[TResult](successCallback: Function1[T, TResult]): IPromise[TResult] = ???
  // FIXME:def `catch`[TResult](onRejected: Function1[Any, IHttpPromise[TResult]]): IPromise[TResult] = ???
  //def `catch`[TResult](onRejected: Function1[Any, IPromise[TResult]]): IPromise[TResult] = ???
  def `catch`[TResult](onRejected: Function1[Any, TResult]): IPromise[TResult] = ???
  def `finally`[TResult](finallyCallback: Function0[Any]): IPromise[TResult] = ???
}

@JSName("ng.IDeferred")
trait IDeferred[T] extends Object {
  def resolve(value: T): Unit = ???
  def resolve(): Unit = ???
  def reject(reason: Any): Unit = ???
  def reject(): Unit = ???
  def notify(state: Any): Unit = ???
  var promise: IPromise[T] = _
}

@JSName("ng.IAnchorScrollService")
trait IAnchorScrollService extends Object {
  def apply(): Unit = ???
}

@JSName("ng.IAnchorScrollProvider")
trait IAnchorScrollProvider extends IServiceProvider {
  def disableAutoScrolling(): Unit = ???
}

@JSName("ng.ICacheFactoryService")
trait ICacheFactoryService extends Object {
  def apply(cacheId: String, optionsMap: Any): ICacheObject = ???
  def apply(cacheId: String): ICacheObject = ???
  def info(): Dynamic = ???
  def get(cacheId: String): ICacheObject = ???
}

@JSName("ng.ICacheObject")
trait ICacheObject extends Object {
  def info(): Any = ???
  def put(key: String, value: Any): Unit = ???
  def put(key: String): Unit = ???
  def get(key: String): Dynamic = ???
  def remove(key: String): Unit = ???
  def removeAll(): Unit = ???
  def destroy(): Unit = ???
}

@JSName("ng.ICompileService")
trait ICompileService extends Object {
  def apply(element: String, transclude: ITemplateLinkingFunction, maxPriority: Number): ITemplateLinkingFunction = ???
  def apply(element: String, transclude: ITemplateLinkingFunction): ITemplateLinkingFunction = ???
  def apply(element: String): ITemplateLinkingFunction = ???
  def apply(element: Element, transclude: ITemplateLinkingFunction, maxPriority: Number): ITemplateLinkingFunction = ???
  def apply(element: Element, transclude: ITemplateLinkingFunction): ITemplateLinkingFunction = ???
  def apply(element: Element): ITemplateLinkingFunction = ???
  //def apply(element: JQuery, transclude: ITemplateLinkingFunction, maxPriority: Number): ITemplateLinkingFunction = ???
  //def apply(element: JQuery, transclude: ITemplateLinkingFunction): ITemplateLinkingFunction = ???
  //def apply(element: JQuery): ITemplateLinkingFunction = ???
}

@JSName("ng.ICompileProvider")
trait ICompileProvider extends IServiceProvider {
  def directive(name: String, directiveFactory: Function): ICompileProvider = ???
  def directive(directivesMap: Any): ICompileProvider = ???
}

@JSName("ng.ITemplateLinkingFunction")
trait ITemplateLinkingFunction extends Object {
  //def apply(scope: IScope, cloneAttachFn: Function2[JQuery, IScope, Any]): JQuery = ???
  def apply(scope: IScope): JQuery = ???
}

@JSName("ng.IControllerService")
trait IControllerService extends Object {
  def apply(controllerConstructor: Function, locals: Any): Dynamic = ???
  def apply(controllerConstructor: Function): Dynamic = ???
  def apply(controllerName: String, locals: Any): Dynamic = ???
  def apply(controllerName: String): Dynamic = ???
}

@JSName("ng.IControllerProvider")
trait IControllerProvider extends IServiceProvider {
  def register(name: String, controllerConstructor: Function): Unit = ???
  def register(name: String, dependencyAnnotadedConstructor: Array[Any]): Unit = ???
}

@JSName("ng.IHttpService")
trait IHttpService extends Object {
  def apply(config: IRequestConfig): IHttpPromise[Any] = ???
  def get(url: String, RequestConfig: Any): IHttpPromise[Any] = ???
  def get(url: String): IHttpPromise[Any] = ???
  def delete(url: String, RequestConfig: Any): IHttpPromise[Any] = ???
  def delete(url: String): IHttpPromise[Any] = ???
  def head(url: String, RequestConfig: Any): IHttpPromise[Any] = ???
  def head(url: String): IHttpPromise[Any] = ???
  def jsonp(url: String, RequestConfig: Any): IHttpPromise[Any] = ???
  def jsonp(url: String): IHttpPromise[Any] = ???
  def post(url: String, data: Any, RequestConfig: Any): IHttpPromise[Any] = ???
  def post(url: String, data: Any): IHttpPromise[Any] = ???
  def put(url: String, data: Any, RequestConfig: Any): IHttpPromise[Any] = ???
  def put(url: String, data: Any): IHttpPromise[Any] = ???
  var defaults: IRequestConfig = _
  var pendingRequests: Array[Any] = _
}

@JSName("ng.IRequestConfig")
trait IRequestConfig extends Object {
  var method: String = _
  var url: String = _
  var params: Any = _
  var headers: Any = _
  var cache: Any = _
  var withCredentials: Boolean = _
  var data: Any = _
  var transformRequest: Any = _
  var transformResponse: Any = _
  var timeout: Any = _
}

@JSName("ng.IHttpPromiseCallback")
trait IHttpPromiseCallback[T] extends Object {
  def apply(data: T, status: Number, headers: Function1[String, String], config: IRequestConfig): Unit = ???
}

@JSName("ng.IHttpPromiseCallbackArg")
trait IHttpPromiseCallbackArg[T] extends Object {
  var data: T = _
  var status: Number = _
  var headers: Function1[String, String] = _
  var config: IRequestConfig = _
}

@JSName("ng.IHttpPromise")
trait IHttpPromise[T] extends IPromise[T] {
  def success(callback: IHttpPromiseCallback[T]): IHttpPromise[T] = ???
  def error(callback: IHttpPromiseCallback[T]): IHttpPromise[T] = ???
  //def then[TResult](successCallback: Function1[IHttpPromiseCallbackArg[T], TResult], errorCallback: Function1[IHttpPromiseCallbackArg[T], Any]): IPromise[TResult] = ???
  //def then[TResult](successCallback: Function1[IHttpPromiseCallbackArg[T], TResult]): IPromise[TResult] = ???
  // FIXME: def then[TResult](successCallback: Function1[IHttpPromiseCallbackArg[T], IPromise[TResult]], errorCallback: Function1[IHttpPromiseCallbackArg[T], Any]): IPromise[TResult] = ???
  // FIXME: def then[TResult](successCallback: Function1[IHttpPromiseCallbackArg[T], IPromise[TResult]]): IPromise[TResult] = ???
}

@JSName("ng.IHttpProvider")
trait IHttpProvider extends IServiceProvider {
  var defaults: IRequestConfig = _
  var interceptors: Array[Any] = _
  var responseInterceptors: Array[Any] = _
}

@JSName("ng.IHttpBackendService")
trait IHttpBackendService extends Object {
  def apply(method: String, url: String, post: Any, callback: Function, headers: Any, timeout: Number, withCredentials: Boolean): Unit = ???
  def apply(method: String, url: String, post: Any, callback: Function, headers: Any, timeout: Number): Unit = ???
  def apply(method: String, url: String, post: Any, callback: Function, headers: Any): Unit = ???
  def apply(method: String, url: String, post: Any, callback: Function): Unit = ???
  def apply(method: String, url: String, post: Any): Unit = ???
  def apply(method: String, url: String): Unit = ???
}

@JSName("ng.IInterpolateService")
trait IInterpolateService extends Object {
  def apply(text: String, mustHaveExpression: Boolean): IInterpolationFunction = ???
  def apply(text: String): IInterpolationFunction = ???
  def endSymbol(): String = ???
  def startSymbol(): String = ???
}

@JSName("ng.IInterpolationFunction")
trait IInterpolationFunction extends Object {
  def apply(context: Any): String = ???
}

@JSName("ng.IInterpolateProvider")
trait IInterpolateProvider extends IServiceProvider {
  def startSymbol(): String = ???
  def startSymbol(value: String): IInterpolateProvider = ???
  def endSymbol(): String = ???
  def endSymbol(value: String): IInterpolateProvider = ???
}

@JSName("ng.ITemplateCacheService")
trait ITemplateCacheService extends ICacheObject {
}

@JSName("ng.IRootScopeService")
trait IRootScopeService extends IScope {
}

@JSName("ng.ISCEService")
trait ISCEService extends Object {
  def getTrusted(`type`: String, mayBeTrusted: Any): Dynamic = ???
  def getTrustedCss(value: Any): Dynamic = ???
  def getTrustedHtml(value: Any): Dynamic = ???
  def getTrustedJs(value: Any): Dynamic = ???
  def getTrustedResourceUrl(value: Any): Dynamic = ???
  def getTrustedUrl(value: Any): Dynamic = ???
  def parse(`type`: String, expression: String): Function2[Any, Any, Any] = ???
  def parseAsCss(expression: String): Function2[Any, Any, Any] = ???
  def parseAsHtml(expression: String): Function2[Any, Any, Any] = ???
  def parseAsJs(expression: String): Function2[Any, Any, Any] = ???
  def parseAsResourceUrl(expression: String): Function2[Any, Any, Any] = ???
  def parseAsUrl(expression: String): Function2[Any, Any, Any] = ???
  def trustAs(`type`: String, value: Any): Dynamic = ???
  def trustAsHtml(value: Any): Dynamic = ???
  def trustAsJs(value: Any): Dynamic = ???
  def trustAsResourceUrl(value: Any): Dynamic = ???
  def trustAsUrl(value: Any): Dynamic = ???
  def isEnabled(): Boolean = ???
}

@JSName("ng.ISCEProvider")
trait ISCEProvider extends IServiceProvider {
  def enabled(value: Boolean): Unit = ???
}

@JSName("ng.ISCEDelegateService")
trait ISCEDelegateService extends Object {
  def getTrusted(`type`: String, mayBeTrusted: Any): Dynamic = ???
  def trustAs(`type`: String, value: Any): Dynamic = ???
  def valueOf(value: Any): Dynamic = ???
}

@JSName("ng.ISCEDelegateProvider")
trait ISCEDelegateProvider extends IServiceProvider {
  def resourceUrlBlacklist(blacklist: Array[Any]): Unit = ???
  def resourceUrlWhitelist(whitelist: Array[Any]): Unit = ???
}

@JSName("ng.IDirective")
trait IDirective extends Object {
  var compile: Function3[Any, IAttributes, Function2[IScope, Function, Unit], Any] = _
  var controller: Any = _
  var controllerAs: String = _
  var link: Function4[IScope, Any, IAttributes, Any, Unit] = _
  var name: String = _
  var priority: Number = _
  var replace: Boolean = _
  var require: Any = _
  var restrict: String = _
  var scope: Any = _
  var template: Any = _
  var templateUrl: Any = _
  var terminal: Boolean = _
  var transclude: Any = _
}

/* FIXME: 
@JSName("ng.IAugmentedJQueryStatic")
trait IAugmentedJQueryStatic extends JQueryStatic {
  def apply(selector: String, context: Any): IAugmentedJQuery = ???
  def apply(selector: String): IAugmentedJQuery = ???
  def apply(element: Element): IAugmentedJQuery = ???
  def apply(`object`: Any): IAugmentedJQuery = ???
  def apply(elementArray: Array[Element]): IAugmentedJQuery = ???
  def apply(`object`: JQuery): IAugmentedJQuery = ???
  def apply(func: Function): IAugmentedJQuery = ???
  def apply(array: Array[Any]): IAugmentedJQuery = ???
  def apply(): IAugmentedJQuery = ???
}

@JSName("ng.IAugmentedJQuery")
trait IAugmentedJQuery extends JQuery {
  def find(selector: String): IAugmentedJQuery = ???
  def find(element: Any): IAugmentedJQuery = ???
  def find(obj: JQuery): IAugmentedJQuery = ???
  def controller(name: String): Dynamic = ???
  def injector(): Dynamic = ???
  def scope(): IScope = ???
  def inheritedData(key: String, value: Any): JQuery = ???
  def inheritedData(obj: Any): JQuery = ???
  def inheritedData(key: String): Dynamic = ???
  def inheritedData(): Dynamic = ???
}*/

package auto {

@JSName("ng.auto.IInjectorService")
trait IInjectorService extends Object {
  def annotate(fn: Function): Array[String] = ???
  def annotate(inlineAnnotadedFunction: Array[Any]): Array[String] = ???
  def get(name: String): Dynamic = ???
  def instantiate(typeConstructor: Function, locals: Any): Dynamic = ???
  def instantiate(typeConstructor: Function): Dynamic = ???
  def invoke(func: Function, context: Any, locals: Any): Dynamic = ???
  def invoke(func: Function, context: Any): Dynamic = ???
  def invoke(func: Function): Dynamic = ???
}

@JSName("ng.auto.IProvideService")
trait IProvideService extends Object {
  def constant(name: String, value: Any): Unit = ???
  def decorator(name: String, decorator: Function): Unit = ???
  def decorator(name: String, decoratorInline: Array[Any]): Unit = ???
  def factory(name: String, serviceFactoryFunction: Function): ng.IServiceProvider = ???
  def provider(name: String, provider: ng.IServiceProvider): ng.IServiceProvider = ???
  def provider(name: String, serviceProviderConstructor: Function): ng.IServiceProvider = ???
  def service(name: String, constructor: Function): ng.IServiceProvider = ???
  def value(name: String, value: Any): ng.IServiceProvider = ???
}

}

}
}

package object js extends GlobalScope {
  var angular: ng.IAngularStatic = _
}

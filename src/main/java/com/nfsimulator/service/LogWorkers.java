package com.nfsimulator.service;

import org.antlr.stringtemplate.StringTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by netstorm on 19/3/17.
 */
public class LogWorkers implements Runnable {
    private volatile String logLocation;
    private volatile String logFormat;
    private String msgType;
    private int waitTimeInsec;
    private int noOfMsgsPerBurst;
    private static int accessGenCount=0;
    private static int setaGenCount=0;
    private static int errorGenCount=0;
    private static int warningGenCount=0;
    private static int gcGenCount=0;
    private static int infoGenCount=0;
    private static int instanceGenCount=0;
    private boolean writeToFile;

    private static final String[] users = {"james", "tony", "robert", "john", "ricky" ,"jain","sumona","ayaan", "james","mishra","singh","shah","kumar","kishore","khan","vinni","paul","catherine","joseph","jennifer","arnold","nesky", "mike","daniel","harry","ron","lucis"};
    private static final String[] httpMethods = {"GET", "POST", "PUT", "DELETE"};
    private static final String[] uriPathParameters = {
            "/include/catalog/findstore.jsp?skuId=03513968&productId=297449",
            "/store/checkout.jsp?skuId=03513968&productId=297449",
            "/catalog/products.jsp",
            "/openapi-rest-web/v1/catalog?inStoreEnabled=false&keyword=bright%20stars&offset=1&limit=12&isSolr=true  inStoreEnabled=false&keyword=bright%20stars&offset=1&limit=12&isSolr=true",
            " /openapi-rest-web/v1/kohlsCash",
            "/openapi-rest-web/v1/profile",
            "/openapi-rest-web/akamai-sureroute-test-object.htm",
            "/openapi-rest-web/index.html",
            "/openapi-rest-web/v1/order/calculation?trEnabled=true&omniEnabled=true  trEnabled=true&omniEnabled=true 200",
            "/rest/model//atg/userprofiling/ProfileActor/login",
            "/rest/model//com/kohls/commerce/order/OrderActor/orderCalcServiceDecider",
            "/omniture/sitecatalyst/tracking_analytics.jsp?a=&b=Shoes%7cMens&c=Department%7cGender&d=Department%3aShoes%7cGender%3aMens&e=catalog&f=&j=false&k=&l=true&m=    a=&b=Shoes%7cMens&c=Department%7cGender&d=Department%3aShoes%7cGender%3aMens&e=catalog&f=&j=false&k=&l=true&m=",
            "/catalog/oneida-kitchen-dining.jsp?CN=4294875069+4294719799&N=4294875069+4294719799+76794&om_mid=_161125_DG_Email_FridayA&om_etid=147821479&utm_source=MAR&utm_medium=ET&utm_term=147821479&utm_content=199490&utm_campaign=_161125_DG_Email_FridayA    CN=4294875069+4294719799&N=4294875069+4294719799+76794&om_mid=_161125_DG_Email_FridayA&om_etid=147821479&utm_source=MAR&utm_medium=ET&utm_term=147821479&utm_content=199490&utm_campaign=_161125_DG_Email_FridayA ",
            "/upgrade/myaccount/order_status_login.jsp",
            "/checkout/v2/includes/kohlsCash.jsp?shouldIncludeForms=true     shouldIncludeForms=true ",
            "/price/2066321.jsp?type=product type=product   ",
            "/persistentbar/persistent_bar_checkout.jsp",
            " /typeahead/toast%20appliances.jsp?callback=categoryTypeaheadResult&ta_exp=d&_=1480119344143     callback=categoryTypeaheadResult&ta_exp=d&_=1480119344143 ",
            "/healthcheck.jsp",
            "/common/session.jsp "
    };
    private static final String[] uriParams = {"james", "tony", "robert", "john"};
    private static final String[] statusCodes = {"200", "404", "403", "401","302"};
    private static final String[] userAgents = {
            "\"Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405\"",
            "\"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.2; .NET4.0C)\"",
            "\"Opera/7.21 (X11; FreeBSD i386; U)  [en]\"",
            "\"Googlebot/1.0 (googlebot@googlebot.com http://googlebot.com/)\"",
            "kohls/7.0.25 (iPhone; iOS 10.1.1; Scale/2.00)",
            "\"Apache-HttpClient/4.3.3 (java 1.5)\"",
            "Mozilla/5.0 (Linux; Android 4.4.2; A1-810 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.85 Safari/537.36 SkavaECP",
            "/common/session.jsp",
            " \"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36\"",
            "\"Mozilla/5.0 (compatible; Yahoo! Slurp; http://help.yahoo.com/help/us/ysearch/slurp)\"",
            "\"Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)\""

    };
    private static final String[] xForwards = {
            "\"2001:db8:85a3:8d3:1319:8a2e:370:7348\"",
            "\"203.0.113.195\"",
            "\"203.0.113.195, 70.41.3.18, 150.172.238.178\"",
            "\"73.181.16.97, 96.17.111.168, 63.238.216.221\" ",
            "\"10.202.5.117\"",
            "\"73.241.210.227, 63.217.232.17\"",
            "\"173.206.140.40, 72.246.43.239\"",
            "\"107.199.169.136, 65.158.47.199\"",
            "10.202.1.254    10.202.4.168:7003",
            "\"67.165.250.49, 10.8.112.112, 209.8.112.122\" ",
            " \"65.189.221.211, 104.96.3.31\"",
            "\"69.64.173.238, 72.247.190.76\""
    };
    private static final String[] accessGreedyStrings = {
            "\"[corRID::m21584f9843e1-26ed-40ce-943c-223c43f8a2a1][SID::3ECfNwef9tz1V18SxMO-EdtsqtARDbfGpV6YsWfSOsib2C0T4al5!-1902206761!957554599!1480139933599][PID::2254023574946854][UID::moore][OID::7021732168][dcroute::pkyb][VisitorId::2254023574946854][kohls_klbc_cookie::2818886154.23323.0000][kohls_klbd_cookie::204223148.20480.0000][DCRouteS::pkyb-1480139997]\"",
            "\"[corRID::h21584f9843e2-26ed-40ce-943c-223c43f8a2a2][SID::4ECfNwef9tz1V18SxMP-EdtsqtARDbfGpV6YsWfSOsib2C0T4al6!-1902206762!957554597!1480139933597][PID::7864223574976852][UID::kennitar][OID::8071432168][dcroute::pkyb][VisitorId::3674023574946859][kohls_klbc_cookie::2818886154.23323.0000][kohls_klbd_cookie::204223148.20480.0000][DCRouteS::pkyb-1480139997]\"",
            "\"[corRID::h21584f9843e3-26ed-40ce-943c-223c43f8a2a3][SID::5ECfNwef9tz1V18SxMQ-EdtsqtARDbfGpV6YsWfSOsib2C0T4al7!-1902206763!957554598!1480139933598][PID::1234342574846829][UID::kenny][OID::6081232168][dcroute::pkyb][VisitorId::4564023574946854][kohls_klbc_cookie::2818886154.23323.0000][kohls_klbd_cookie::204223148.20480.0000][DCRouteS::pkyb-1480139997]\"",
            " [corRID::NAID-iOS-401CF513-8B1D-42EF-A7BE-BD55308FDFC3-1480132471.795550]       [KEY::8c718eee40e998f6ac1cbc0a7038e4cf] [UID::-]\n",
            "[corRID::NAID-iOS-029AAC90-FBCC-4506-9D2E-ADA949D0E8B0-1480132470.532939]       [KEY::NUNlVgCqlFOCiGKSuhpmLL8MEpqVvxW9] [UID::-]\n",
            "[corRID::TCID-169-1480132471795]        [KEY::KXnAJSVrW68lQ55zd5qoz5qvJavMhckD] [UID::tashieb2002]\n",
            "[corRID::null]  [KEY::Apigee-HTTPMonitor-Order][UID::-]\n",
            "[corRID::MCID-7220604-1480139999157]    [KEY::oAEItDJyetG8AmEheOFsh8419ymLgSJq] [UID::bridgetmh73]\n",
            "\"[corRID::TID-1244354-1480053598444][SID::uRuaEaoa_-G8EHOS4C_F6nlYpSozEQ86qE87xYD3FSpwJjXHfEGs!-1337763339!1480053598746][apiKey::web][UID::frobert][dcroute::null][kohls_klbc_cookie::null][kohls_klbd_cookie::null]\"  -       -       -       -\n",
            "\"[corRID::MCID-166-1480053599181][SID::BqGaEawlet50lF0wAWl4Y_wU7Rb1NggL13Qx7jDW6WGWEnrhuufj!-1337763339!1480053599269][apiKey::oAEItDJyetG8AmEheOFsh8419ymLgSJq][UID::ANONYMOUS][dcroute::null][kohls_klbc_cookie::null][kohls_klbd_cookie::null]\"      -       -       -       -\n",
            "\"[corRID::m21414aa825ee-0ddf-4d23-ab41-3179e3af660c][SID::v3eatn2w6OIxuuXLyvHO3JNg8ilgjr5UbE17L862rL1iupYo_7wz!-400377897!-255435387!1480064400816][PID::2254031759887036][UID::null][OID::null][dcroute::null][VisitorId::null][kohls_klbc_cookie::null][kohls_klbd_cookie::null][DCRouteS::null]\"\n",
            "\"[corRID::m214135dd52db-8e00-4460-bcd3-9e1b6868e117][SID::TByatneweBmlmwck61qMQyYNMimASvio1-sZY2ngbyX_IVg5hFJf!-400377897!-255435387!1480064399280][PID::2254031759887034][UID::null][OID::6995126425][dcroute::pkyp][VisitorId::2254031759887034][kohls_klbc_cookie::2533673482.23323.0000][kohls_klbd_cookie::187642540.20480.0000][DCRouteS::pkyp-1480064399]\"",
            "\"[corRID::m2158533073ee-63aa-441f-8422-2239a8ca0e2d][SID::J96dxUykwrS52kz0YbBRGDXceQUWoK8ezBsontRFfxPJbHz6skYR!-1902206761!957554599!1480115702948][PID::2254031799319309][UID::null][OID::7012575068][dcroute::pkyb][VisitorId::2254031799310142][kohls_klbc_cookie::2818886154.23323.0000][kohls_klbd_cookie::204354220.20480.0000][DCRouteS::pkyb-1480119343]\"\n",
            "\"[corRID::h21607969f6e3-94ff-4209-b133-e3e6c097ac10][SID::KBRRHqTP-3ORALLL9gF7FAmDhuwgDVcLlwZ4_b8PSiSextg2gxYa!2089100685!-914306882!1504599516367][PID::2253999749115391][UID::karen606][OID::7355370676][dcroute::scsb][VisitorId::2253999749115391][kohls_klbc_cookie::2860829194.22811.0000][kohls_klbd_cookie::212677292.20480.0000][DCRouteS::scsb-1504601519]\""
    };

    private static final String[] hosts = {"h121618vaps4355","h121618vaps2354","h121618vaps2435","h121618vaps2006","h121618vaps2307","h121618vaps2144","m221616vaps2150","h121618vaps2444","h121618vaps2551","h121618vaps2557","h121618vaps2241", "h121618vaps2243", "h121618vaps2246", "m121618vaps4241", "m221616vaps2152", "m221616vaps2155", "m221616vaps2165", "h121618vaps2145","h121618vaps2303", "h121618vaps2398","h121618vaps3243","h121618vaps7897","h121618vaps3240","h121618vaps3245","h121618vaps3247","h121618vaps3249","h121618vaps3250","m121618vaps2265","m221616vaps2343","m22161819vaps2344"};
    private static final String[] apps = {"ATG", "PROD", "COM"};
    private static final String[] servers = {"ORACLE", "WEBLOGIC"};
    private static final String[] infoactions = {
            "/rest/model/com/kohls/commerce/order/OrderActor/restLogin",
            "/rest/model/com/kohls/commerce/order/OrderActor/createLoginResult",
            "/rest/model/com/kohls/commerce/order/OrderActor/validateCart",
            "/rest/model/com/kohls/commerce/order/OrderActor/getCart",
            "/rest/model/atg/commerce/order/purchase/ShippingGroupActor/doBillingShipping",
            "/rest/model/com/kohls/commerce/order/OrderActor/doAddressValidation"
    };
    private static final String[] actions = {
            "custLoginSuccess",
            "custLoginFailure",
            "custCreateAccount",
            "ccAuthSuccess",
            "ccAuthFailure"
    };
    private static final String[] errActions = {
            "/catalog/navigation.jsp",
            "/myaccount/v2/purchase_history_json.jsp",
            "/checkout/shipping_billing_information.jsp",
            "/rest/model/com/kohls/commerce/order/OrderActor/addItems",
            "/rest/model//com/kohls/commerce/service/rest/kohlsCash/actor/BalanceActor/getGiftCardBalance",
            "/rest/model/com/kohls/commerce/order/OrderActor/submitAndReview",
            "/rest/model//atg/userprofiling/ProfileActor/create",
            "/checkout/v2/json/applied_discounts_json.jsp",
            "/myaccount/json/myinfo/customer_address_statesList_Json.jsp",
            "/checkout/v2/json/checkout_data_json.jsp",
            "/checkout/v2/json/order_review_errors_json.jsp",
            "/myaccount/kohls_rewards.jsp",
            "/upgrade/checkout/fm/updateFraudPreventionMessage.jsp",
            "/checkout/v2/json/persistent_bar_components_json.jsp",
            "/checkout/v2/json/shopping_bag_json.jsp"
    };
    private static final String[] warnActions = {
            "/catalog/search/pmp_search_base_navigation.jsp",
            "/myaccount/kohls_rewards.jsp",
            "/upgrade/myaccount/guest_order_login.jsp",
            "/checkout/v2/json/sign_in_json.jsp",
            "/rest/model//com/kohls/commerce/service/rest/product/actor/ProductRestServiceActor/getProductsByIds",
            "/rest/model//atg/userprofiling/ProfileActor/create",
            "/rest/model/com/kohls/commerce/order/OrderActor/submitAndReview",
            "/rest/model/com/kohls/commerce/order/OrderActor/submitPayment",
            "/rest/model/atg/commerce/order/purchase/ShippingGroupActor/doBillingShipping",
            "/rest/model/atg/commerce/order/purchase/ShippingGroupActor/createMultiShipments",
            "/rest/model//atg/commerce/order/OrderLookupActor/retrieveOrderDetail",
            "/rest/model/com/kohls/commerce/order/OrderActor/validateCart",
            "/rest/model/com/kohls/commerce/order/OrderActor/addItems",
            "/upgrade/myaccount/guest_order_login.jsp"
    };
    private static final String[] statuses = {
            "Customer Login Success",
            "Customer Login Failed",
            "Customer Create Account",
            "Credit Card Auth Success",
            "Credit Card Auth Failure"
    };
    private static final String[] metricData = {
            "Diagnostics",
            "WorkManager",
            "Health"
    };
    private static final String[] server ={
            "m221578vaps2661.att.kohls.com",
            "m221616vaps2302.att.kohls.com",
            "m221616vaps2301.att.kohls.com",
            "m221343vaps1542.att.kohls.com",
            "m122454vaps6524.att.kohls.com",
            "h121545vaps3245.att.kohls.com",
            "h331234vaps8456.att.kohls.com",
            "h232313vaps4535.att.kohls.com",
            "h152343vaps8521.att.kohls.com",
            "h223213vaps7854.att.kohls.com",
            "h231423vaps6532.att.kohls.com",
            "m221323vaps4343.att.kohls.com",
            "m213231vaps5647.att.kohls.com",
            "h123213vaps8745.att.kohls.com"
    };
    private static final String[] instance ={
            "kls-oma-01",
            "kls-sal-02",
            "kls-coh-12",
            "kls-rest-04",
            "kls-ivh-3",
            "kls-van-6",
            "kls-app-9",
            "kls-web-8",
            "kls-ojn-2",
            "kls-inx-4",
            "kls-mna-10",
            "kls-red-12"
    };
    private static final String[] threadInfo ={
            "[ACTIVE] ExecuteThread: '0' for queue: 'weblogic.kernel.Default (self-tuning)'",
            "[ACTIVE] ExecuteThread: '5' for queue: 'weblogic.kernel.Default (self-tuning)'",
            "[ACTIVE] ExecuteThread: '2' for queue: 'weblogic.kernel.Default (self-tuning)'",
            "weblogic.GCMonitor",
            "[ACTIVE] ExecuteThread: '21' for queue: 'weblogic.kernel.Default (self-tuning)'",
            "[ACTIVE] ExecuteThread: '31' for queue: 'weblogic.kernel.Default (self-tuning)'",
            "[ACTIVE] ExecuteThread: '28' for queue: 'weblogic.kernel.Default (self-tuning)'",
            "[ACTIVE] ExecuteThread: '29' for queue: 'weblogic.kernel.Default (self-tuning)'",
            "[ACTIVE] ExecuteThread: '84' for queue: 'weblogic.kernel.Default (self-tuning)'"
    };
    private static final String[] instanceGreedyData = {
            "<<WLS Kernel>> <> <> <1496210422264> <BEA-320145> <Size based data retirement operation completed on archive EventsDataArchive. Retired 0 records in 1 ms.> ",
            "<<WLS Kernel>> <> <> <1496206818080> <BEA-320143> <Scheduled 1 data retirement tasks as per configuration.> ",
            "<<WLS Kernel>> <> <> <1496206818080> <BEA-002936> <maximum thread constraint HARVESTER_WM is reached> ",
            "<<anonymous>> <> <> <1496224683170> <BEA-310002> <18% of the total memory in the server is free.> ",
            "<<WLS Kernel>> <> <> <1496224818080> <BEA-320144> <Size based data retirement operation started on archive HarvestedDataArchive.> ",
            "<<anonymous>> <BEA1-7E4209C1945FAE3C0280> <> <1467889005409> <BEA-000627> <Reached maximum capacity of pool \"ATGCSCProductionDS\", making \"0\" new resource instances instead of \"1\".> \n",
            "<<WLS Kernel>> <> <> <1467867697476> <BEA-170027> <The server has successfully established a connection with the Domain level Diagnostic Service.> ",
            "<<WLS Kernel>> <> <> <1467867698607> <BEA-149059> <Module rl of application CSC is transitioning from STATE_ADMIN to STATE_ACTIVE on server kls-csc-01.>",
            "<<WLS Kernel>> <> <> <1467867698602> <BEA-149059> <Module /rest of application CSC is transitioning from STATE_ADMIN to STATE_ACTIVE on server kls-csc-01.> ",
            " <<anonymous>> <> <> <1467784157821> <BEA-310002> <95% of the total memory in the server is free.> \n",
            "<WLS Kernel>> <> <> <1467809716134> <BEA-002939> <The maximum thread constraint weblogic.jms.kls-JmsServer-01.Limited has been reached 19 times for the last 182 seconds.> \n"
    };
    private static final String[] setaGreedyStrings = {
            "src=10.202.193.253 user=2254005365114518",
            "src=10.202.193.252 user=2254006849709259",
            "src=10.202.193.254 user=2254006849709211",
            "src=10.202.193.252 user=2254005365091562",
            "src=10.202.193.252 user=2254006849709442",
            "src=10.202.193.253 user=2254006849709443",
            "sequence_number=087300909993 avs_reponse=Y action_code=8 auth_code= src=67.163.61.11 customer=2254015499126913 ccv2_response=--- cc_id=pg4767855246",
            "amount=55.58 admin=false returnRequestId=5534301335 src=10.202.135.253 replacementOrderId=--- customer=2254015244109625 user=tkb6316 orderId=5366795456",
            "src=72.241.100.133 user=2254027578582370 cc_id=Anonymous user, Order id=5533782578, CreditCardUniqueNickName=kohlsCharge - 0403"
    };

    private static final String[] infoGreedyData = {
            "KLSOrderTools-getCommerceItemToShippingAddressMap(-,-,-), Address ID(Profile Key) value is : 56297194514323705 for shippingGroup :sg6621688805 ,for the order id : 7011370438",
            "Time Consumed - 22 ms",
            "TypeAhead algorithm used is : b Description :  TypeAhead results with updated omniture feed",
            "Entering ESK260ProcScheduler task",
            "TaxProcessorTaxCal.NSC check Failed TaxProcessorTaxCal.NSC check Failed.shippingCity is null or empty for 7355281768, SGId=sg9541136470, profileId=2254040993045146,AddressId= null",
            "  ORDER STATUS UPDATE FOR ORDER ID : 7238690441 STARTED",
            "KLSDigitalKohlsCashServiceImpl.getBalanceBulk() successful for correlationId:null,GetBalanceResponse:KLSDKCGetBalanceResponse [mKohlsCash=[KLSKohlsCashSuccess [EventTypeCode=27, Balance=0.0, Barcode=22505******9220, ExpirationDate=2017-09-07, StartDate=2017-08-08,TransactionVoided=false]], mErrors=null, mStatus=null],StatusCode:200",
            "The GIV Store locator RESDOC or store nodes inside that RESDOC is NULL or EMPTY",
            "KLSAllStoresAvailabilityServiceAPI.getAllAvailableStores...message:com.kohls.omnichannel.findinstore.exception.KLSGIVInventoryException: GIV Store locator response doc or its node array is null or empty"
    };
    private static final String[] errGreedyData = {
            "Unknown EXCEPTION occured  : java.lang.NullPointerException",
           " Exception inside KLSOrderManager.getCombinedOrdersAsync while getting order List from OMS for profileID:2253999743707448        java.util.concurrent.TimeoutException  \n" +
                   "\t\t\tat java.util.concurrent.FutureTask.get(FutureTask.java:201) \n" +
                   "\t\t\tat com.kohls.commerce.order.KLSOrderManager.getCombinedOrdersAsync(KLSOrderManager.java:20183)\n" +
                   "\t\t\tat com.kohls.commerce.order.KLSOrderManager.getOrdersListfromRepoAndOmsbyProfileId(KLSOrderManager.java:20118)\n" +
                   "\t\t\tat com.kohls.commerce.common.droplets.KLSOrderHistoryDroplet.populatePurchaseHistoryDetailsImproved(KLSOrderHistoryDroplet.java:301)\n" +
                   "        at com.kohls.commerce.common.droplets.KLSOrderHistoryDroplet.service(KLSOrderHistoryDroplet.java:129)",
            " CommerceException for the payment id -  atg.commerce.CommerceException: The PaymentGroup with id pg7237481363 failed to be authorized. Reason: kls_error_checkout_action_err_referral\n" +
                    "        at com.kohls.commerce.payment.KLSPaymentManager.newPciAuthorize(KLSPaymentManager.java:448)\n" +
                    "        at com.kohls.commerce.payment.KLSPaymentManager.authorize(KLSPaymentManager.java:318)\n" +
                    "        at com.kohls.commerce.order.processor.ProcKLSAuthorizePayment.runProcess(ProcKLSAuthorizePayment.java:187)\n" +
                    "        at atg.service.pipeline.PipelineLink.runProcess(PipelineLink.java:255)\n" +
                    "        at atg.service.pipeline.PipelineChain.runProcess(PipelineChain.java:365)\n",
           "Exception inside NUDataWSAdaptor.processScoreCall() method.java.util.concurrent.TimeoutException        com.kohls.nudata.exception.NUDataServiceException: java.util.concurrent.TimeoutException\n" +
                   "        at com.kohls.nudata.service.NUDataServiceImpl.getDeviceFingerPrintScoreDetails(NUDataServiceImpl.java:226)\n" +
                   "        at com.kohls.integrations.nudata.manager.NUDataWSAdaptor.processScoreCall(NUDataWSAdaptor.java:811)\n" +
                   "        at com.kohls.integrations.nudata.manager.NUDataWSAdaptor.getNUDataDeviceFingerPrintScoreDetails(NUDataWSAdaptor.java:87)\n" +
                   "        at com.kohls.integrations.nudata.manager.NUDataManager.getNUDataDeviceFingerPrintScore(NUDataManager.java:129)\n" +
                   "        at com.kohls.commerce.order.processor.ProcProcessDeviceFingerPrintScore.getDfpScoreDetailsForClientSideInitializedNudata(ProcProcessDeviceFingerPrintScore.java:221)\n",
           "  CONTAINER:atg.commerce.CommerceException: An unknown error occurred while removing an item to the Order with id [7021735830].; SOURCE:atg.commerce.order.CommerceItemNotFoundException: CommerceItem with id 8780203655 is not in container.\n" +
                   "        at atg.commerce.order.purchase.PurchaseProcessHelper.deleteItems(PurchaseProcessHelper.java:1143)\n" +
                   "        at com.kohls.commerce.order.purchase.KLSPurchaseProcessManager.deleteItems(KLSPurchaseProcessManager.java:1313)\n" +
                   "        at atg.commerce.order.purchase.CartModifierFormHandler.deleteItems(CartModifierFormHandler.java:3102)\n" +
                   "        at com.kohls.commerce.order.purchase.KLSCartModifierFormHandler.deleteItems(KLSCartModifierFormHandler.java:8544)\n" +
                   "        at atg.commerce.order.purchase.CartModifierFormHandler.handleRemoveItemFromOrder(CartModifierFormHandler.java:4260)\n",
           "com.kohls.commerce.order.KLSPurchaseProcessException: kls_error_checkout_action_gc_err_decline\n" +
                   "        at com.kohls.commerce.payment.KLSPaymentManager.getGiftCardBalance(KLSPaymentManager.java:2889)\n" +
                   "        at com.kohls.commerce.order.purchase.KLSPurchaseProcessManager.applyGiftcard(KLSPurchaseProcessManager.java:267)\n" +
                   "        at com.kohls.commerce.order.purchase.KLSPaymentInfoFormHandler.handleApplyGiftCard(KLSPaymentInfoFormHandler.java:2201)\n" +
                   "        at sun.reflect.GeneratedMethodAccessor2599.invoke(Unknown Source)\n",
            "  /com/kohls/prescreen/service/PrescreenServiceImpl       PrescreenServiceImpl/prescreenWebstoreCustomer  - ThreadLogicEnabled TimeoutException occured   java.util.concurrent.TimeoutException\n" +
                    "        at java.util.concurrent.FutureTask.get(FutureTask.java:201)\n" +
                    "        at com.kohls.prescreen.service.PrescreenServiceImpl.prescreenWebstoreCustomer(PrescreenServiceImpl.java:340)\n" +
                    "        at com.kohls.integrations.prescreen.KLSPrescreenWSAdaptor.invokePrescreenWebstoreCustomerService(KLSPrescreenWSAdaptor.java:125)\n" +
                    "        at com.kohls.integrations.prescreen.KLSPrescreenWSAdaptor.prescreenWebstoreCustomer(KLSPrescreenWSAdaptor.java:94)\n",
           " CONTAINER:atg.commerce.CommerceException; SOURCE:java.lang.RuntimeException: An exception was thrown from the context of the link named [loadRelationshipObjects].\n" +
                   "        at com.kohls.commerce.profile.KLSProfileTools.loadUserShoppingCartForLogin(KLSProfileTools.java:3147)\n" +
                   "        at atg.commerce.profile.CommerceProfileTools.loadUserShoppingCartForLogin(CommerceProfileTools.java:1575)\n" +
                   "        at atg.commerce.profile.CommerceProfileRequestServlet.initProfileAfterAutoLogin(CommerceProfileRequestServlet.java:115)\n" +
                   "        at atg.userprofiling.ProfileRequestServlet.initializeProfileFromRequest(ProfileRequestServlet.java:780)\n",
            "Exception Occured for profile Id :2254031540939344 while invalidating corrupt persistent cart:  atg.repository.RemovedItemException: Item's row in primary table not found: shipItemRel:r8957470715\n" +
                    "        at atg.adapter.gsa.GSAItemDescriptor.loadProperties(GSAItemDescriptor.java:6034)\n" +
                    "        at atg.adapter.gsa.GSAItemDescriptor.loadProperty(GSAItemDescriptor.java:6124)\n",
            "TimeoutException inside NUDataServiceImpl.getNUDataScore() method. null java.util.concurrent.TimeoutException\n" +
                    "        at java.util.concurrent.FutureTask.get(FutureTask.java:201)\n" +
                    "        at com.kohls.nudata.service.NUDataServiceImpl.getDeviceFingerPrintScoreDetails(NUDataServiceImpl.java:211)\n" +
                    "        at com.kohls.integrations.nudata.manager.NUDataWSAdaptor.processScoreCall(NUDataWSAdaptor.java:811)\n",
            "MashServiceException LoyaltyEnrollmentDroplet.service() occured  enrollment, mashExp:   com.kohls.loyaltymash.util.MashServiceException: Loyalty profile already exist.\n" +
                    "        at com.kohls.loyaltymash.util.LoyaltyMdmMashServiceUtil.processCreateResponseData(LoyaltyMdmMashServiceUtil.java:874)\n" +
                     "        at com.kohls.loyaltymash.service.LoyaltyMdmMashServiceImpl.createLoyaltyProfile(LoyaltyMdmMashServiceImpl.java:739)\n"

    };

    private static final String[] errUriPathParameters ={
            "/com/kohls/commerce/userprofiling/droplet/KLSAddressValidationDroplet",
            " /atg/dynamo/servlet/pipeline/RequestScopeManager/RequestScope-553786/atg/commerce/order/purchase/CartModifierFormHandle",
            "/rest/model/com/kohls/commerce/order/purchase/CartRestServiceActor/updateCartService",
            "/rest/model/com/kohls/commerce/order/OrderActor/addItems",
            "/rest/model/com/kohls/commerce/order/OrderActor/applyPayment",
            "/rest/model/atg/commerce/order/purchase/ShippingGroupActor/createMultiShipments",
            "/rest/model/com/kohls/commerce/order/OrderActor/submitAndReview",
            "/rest/model/atg/commerce/order/purchase/ShippingGroupActor/createMultiShipments",
            "/rest/model//com/kohls/commerce/order/purchase/CartRestServiceActor/estimateCart",
            "/atg/commerce/payment/PaymentManager",
            "/com/kohls/integrations/nudata/manager/NUDataWSAdaptor",
            "/com/kohls/nudata/service/NUDataService",
            "/atg/dynamo/servlet/pipeline/RequestScopeManager/RequestScope-1500682/atg/commerce/order/purchase/CartModifierFormHandler"
    };
    private static final String[] warnUriPathParameters ={
            "/com/kohls/commerce/solr/search/service/SolrSearchManager",
            ""
    };
    private static final String[] infoUriPathParameters = {
            "com/kohls/search/typeahead/util/TypeAheadQueryParameters",
            "/atg/dynamo/service/jdbc/SQLRepository_production",
            "/atg/userprofiling/LogicalOrganizationRepository ",
            "/atg/commerce/locations/LocationRepository ",
            "/atg/svc/ui/framework/ServiceFrameworkRepository_production ",
            "/com/kohls/batch/distributed/esk260/processor/ESK260JobProcessor",
            "/atg/userprofiling/ProfileActor",
            "/atg/commerce/order/purchase/ShippingGroupActor",
            "/atg/dynamo/servlet/pipeline/RequestScopeManager/RequestScope-548910/com/kohls/commerce/order/purchase/KLSBillingShippingInfoFormHandler",
            "/atg/dynamo/servlet/dafpipeline/ActorServlet"
    };
    private static final String[] warningGreedyData ={
            " The current request URI does not match the URI last updated for dsp:page (/upgrade/myaccount/guest_manage_order_tracking.jsp != /upgrade/myaccount/guest_order_login.jsp)",
            "  Returning default transition value 0 for unregistered property value bopusShippingGroup.\n",
            "  Simultaneous use of the form handler by multiple requests.\n",
            "  KLSCoherenceProductTileContentCacheDroplet.service() method :: Empty keyCollection []\n",
            " KLSCoherenceFacetContentCacheDroplet.service() allProductsCount is Null\n",
            "KLSCoherenceFacetContentCacheDroplet.service() marketplaceCount is Null\n",
            " Property \"secure\" is set to false for component /com/kohls/commerce/omnichannel/findinstore/StoreAvailabilitySearch\n",
            "Products returned from Solr is empty\n",
            "  KLSCoherenceFacetContentCacheDroplet.service() kohlsCount is Null\n",
            "Referer: https://www.kohls.com/myaccount/kohls_login.jsp?action=softSignin&softlognredirecturlvalue=\n",
            "Products returned from Solr is empty for Query String : pagetype=catalog&pagename=mens-helix-shorts-bottoms-clothing&CN=Gender:Mens+Brand:Helix+Product:Shorts+Category:Bottoms+Department:Clothing&kwid=p10233524300&UTM_Adgroupid=58700001150755179&dclid=CKi5rYf1ztICFQ9_YgodgL8AeQ"
    };
    private static final String [] keys = {"123", "162", "615","975","354","513","615","954","213","3413","975","698","324","8745","6746","984","458"};

    private static final String [] fp = {
            "[FP:16108:4653977634002364353:4616963674564910873f:4653977634002364353]",
            "[FP:16109:5653977634002364353:5616963674564910873f:5653977634002364353]",
            "[FP:16110:6653977634002364353:6616963674564910873f:6653977634002364353]",
            "[FP:32142:45523641853224:215675974485f:45523641853224]",
            "[FP:45721:32145778512345:323254135154f:32145778512345]",
            "[FP:62412:95642367456244:235484512334f:95642367456244]",
            "[FP:3221:582126423547885:65242131234f:582126423547885]",
            "[FP:8745:231425541:12321435f:231425541]",
            "[FP:5234:5423678945:18131545864541f:5423678945]",
            "[FP:87459:115467984556:7352983468349f:115467984556]",
            "[FP:2346:11254587554:7395235234f:11254587554]",
            "[FP:6453:68931427:364412578994f:68931427]"
    };

    private static final String [] topo = {
            "[TOPO:16108:OpenAPI-SAL:PKY-SAL5Stress2:kls-api-05]",
            "[TOPO:16109:OpenAPI-REST:PKY-REST5Stress2:kls-api-06]",
            "[TOPO:16110:OpenAPI-ATG-APP:PKY-ATG-APP5Stress2:kls-api-07]",
            "[TOPO:53624:ATG-APP:m221616vaps2141:kls-atg-02]",
            "[TOPO:65789:ATG-OPEN-API-SAL:m221616vaps2304:kls-sal-02]",
            "[TOPO:15418:ATG-OPEN-API-REST:m221616vaps2341:kls-rest-4]",
            "[TOPO:3654:ATG-CSC-BCC:m221616vaps2001:kls-csc-02]",
            "[TOPO:9857:WCS-DELIVERY:m221616vaps2006:kls-wcs-9]",
            "[TOPO:3246:KOHLS-BATCH:m221616vaps2631:kls-batc-3]",
            "[TOPO:9456:GS-XAP-OFFER-GRID:m221616vaps2771:kls-ofr-5]",
            "[TOPO:6541:COHERENCE:m221616vaps2121:kls-coh-4]",
            "[TOPO:3248:ENDECA-MDEX:m221616vaps2392:kls-endeca-6]",
            "[TOPO:5352:GS-XAP-OFFER-GRID:m221616vaps2772:kls-ofrgrid-2]",
            "[TOPO:6378:STAGE:m121312vaps231:kls-stge-02"
    };
    public LogWorkers(String logLocation, String logFormat, String msgType, int waitTimeInsec, int noOfMsgsPerBurst, boolean writeToFile){
        this.logLocation = logLocation;
        this.logFormat = logFormat;
        this.msgType = msgType;
        this.waitTimeInsec = waitTimeInsec;
        this.noOfMsgsPerBurst = noOfMsgsPerBurst;
        this.writeToFile = writeToFile;
    }
    @Override
    public void run() {
        StringBuilder builder = new StringBuilder();
        while (true){
            LocalDateTime currentDateTime = LocalDateTime.now();
            //System.out.println("Current DateTime: " + currentDateTime);

            LocalDate localDate = currentDateTime.toLocalDate();
            LocalTime localTime = currentDateTime.toLocalTime();

            for (int i = 0; i < noOfMsgsPerBurst; i++){
                StringTemplate query = new StringTemplate(logFormat);
                if(msgType.equals("SETALOG")){
                    createSetaLogQuery(query, localDate, localTime);
                    setaGenCount++;
                }
                else if(msgType.equals("ACCESSLOG")){
                    createAccessQuery(query, localDate, localTime);
                    accessGenCount++;
                }
                else if(msgType.equals("ERRORLOG")){
                    createErrorLogQuery(query, localDate, localTime);
                    errorGenCount++;
                }
                else if(msgType.equals("GCLOG")){
                    createSetaLogQuery(query, localDate, localTime);
                    gcGenCount++;
                }
                else if(msgType.equals("INFOLOG")){
                    createInfoLogQuery(query, localDate, localTime);
                    infoGenCount++;
                }
                else if(msgType.equals("INSTANCELOG")){
                    createInstanceLogQuery(query, localDate, localTime);
                    instanceGenCount++;
                }
                else if (msgType.equals("WARNINGLOG")){
                    createWarningLogQuery(query, localDate, localTime);
                    warningGenCount++;
                }
                builder.append(query);
                builder.append("\n");
            }
            System.out.println("AccessLog count is: " + accessGenCount);
            System.out.println("SetaLog count is: " + setaGenCount);
            System.out.println("ErrorLog count is: " + errorGenCount);
            System.out.println("GcLog count is: " + gcGenCount);
            System.out.println("InfoLog count is: " + infoGenCount);
            System.out.println("WarningLog count is: " + warningGenCount);
            System.out.println("InstanceLog count is: " + instanceGenCount);
            System.out.println("=======================================");
            BufferedWriter writer = null;
            try {
                String msg = builder.toString();
                if(writeToFile){
                    File file = new File(logLocation);
                    file.createNewFile();
                    writer = new BufferedWriter(new FileWriter(file));
                    writer.write(msg);
                }
                else{
                    System.out.print(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(waitTimeInsec*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createInfoLogQuery(StringTemplate query, LocalDate localDate, LocalTime localTime) {
        query.setAttribute("action", getRandomString(infoactions));
        query.setAttribute("corRID", UUID.randomUUID());
        query.setAttribute("key", getRandomString(keys));
        query.setAttribute("sid", UUID.randomUUID());
        query.setAttribute("pid", generateRandomIntInRange(1000, 3000));
        query.setAttribute("uid", getRandomString(users));
        query.setAttribute("oid", generateRandomIntInRange(1735920, 1735930));
        query.setAttribute("bopus", UUID.randomUUID());
        query.setAttribute("datetime", getFormatedDate(localDate, "EEE MMM dd") + " "
                + localTime + " IST " + getFormatedDate(localDate, "yyyy"));
        Instant instant = Instant.now();
        long timeStampMillis = instant.toEpochMilli();
        query.setAttribute("timestamp", timeStampMillis);
        query.setAttribute("uripath", getRandomString(infoUriPathParameters));
        query.setAttribute("greedydata", getRandomString(infoGreedyData));
    }
    private void createInstanceLogQuery(StringTemplate query,LocalDate localDate, LocalTime localTime){
        query.setAttribute("metricData",getRandomString(metricData));
        query.setAttribute("server",getRandomString(server));
        query.setAttribute("instance" ,getRandomString(instance));
        query.setAttribute("threadInfo",getRandomString(threadInfo));
        query.setAttribute("greedydata", getRandomString(instanceGreedyData));
        query.setAttribute("datetime",getFormatedDate(localDate,"MMM dd, yyyy")+ " " + localTime + " CDT ");
        Instant instant = Instant.now();
        long timeStampMillis = instant.toEpochMilli();
        query.setAttribute("timestamp", timeStampMillis);
    }
    private void createWarningLogQuery(StringTemplate query, LocalDate localDate, LocalTime localTime) {
        query.setAttribute("action", getRandomString(warnActions));
        query.setAttribute("corRID", UUID.randomUUID());
        query.setAttribute("key", getRandomString(keys));
        query.setAttribute("sid", UUID.randomUUID());
        query.setAttribute("pid", generateRandomIntInRange(1000, 3000));
        query.setAttribute("uid", getRandomString(users));
        query.setAttribute("oid", generateRandomIntInRange(1735920, 1735930));
        query.setAttribute("bopus", UUID.randomUUID());
        query.setAttribute("datetime", getFormatedDate(localDate, "EEE MMM dd") + " "
                + localTime + " IST " + getFormatedDate(localDate, "yyyy"));
        Instant instant = Instant.now();
        long timeStampMillis = instant.toEpochMilli();
        query.setAttribute("timestamp", timeStampMillis);
        query.setAttribute("uripath", getRandomString(infoUriPathParameters));
        query.setAttribute("greedydata", getRandomString(warningGreedyData));
    }
    private void createErrorLogQuery(StringTemplate query,LocalDate localDate,LocalTime localTime){
        query.setAttribute("action", getRandomString(errActions));
        query.setAttribute("corRID",UUID.randomUUID());
        query.setAttribute("key",getRandomString(keys));
        query.setAttribute("sid",UUID.randomUUID());
        query.setAttribute("pid",generateRandomIntInRange(1000,3000));
        query.setAttribute("uid",getRandomString(users));
        query.setAttribute("oid",generateRandomIntInRange(1000,3000));
        query.setAttribute("datetime",getFormatedDate(localDate,"EEE MMM dd") + " "
                + localTime + " IST " + getFormatedDate(localDate, "yyyy"));
        Instant instant = Instant.now();
        long timeStampMillis = instant.toEpochMilli();
        query.setAttribute("timestamp",timeStampMillis);
        query.setAttribute("uripath",getRandomString(errUriPathParameters));
        query.setAttribute("greedydata",getRandomString(errGreedyData));
    }

    private void createSetaLogQuery(StringTemplate query, LocalDate localDate, LocalTime localTime) {
        query.setAttribute("month", getFormatedDate(localDate, "MMM"));
        query.setAttribute("day", getFormatedDate(localDate, "dd"));
        query.setAttribute("year", getFormatedDate(localDate, "yyyy"));
        query.setAttribute("time", getFormatedTime(localTime, "HH:mm:ss"));
        query.setAttribute("hostname", getRandomString(hosts));
        query.setAttribute("cefno", generateRandomIntInRange(1,5));
        query.setAttribute("app", getRandomString(servers));
        query.setAttribute("serverType", getRandomString(apps));
        query.setAttribute("version", generateRandomIntInRange(10, 20) + new Random().nextFloat());
        query.setAttribute("actionPerformed", getRandomString(actions));
        query.setAttribute("status", getRandomString(statuses));
        query.setAttribute("timetaken", generateRandomIntInRange(1, 9));
        query.setAttribute("greedydata", getRandomString(setaGreedyStrings));
    }



    private void createAccessQuery(StringTemplate query, LocalDate localDate, LocalTime localTime) {
        query.setAttribute("clientip", generateIp());
        query.setAttribute("destip", generateIp());
        query.setAttribute("destport", generateRandomIntInRange(8000, 9000));
        query.setAttribute("username", getRandomString(users));
        query.setAttribute("date", getFormatedDate(localDate, "yyyy-MM-dd"));
        query.setAttribute("time", getFormatedTime(localTime, "HH:mm:ss"));
        query.setAttribute("resptime", new Random().nextFloat());
        query.setAttribute("httpmethod", getRandomString(httpMethods));
        query.setAttribute("uripathparam", getRandomString(uriPathParameters));
        query.setAttribute("uriparam", getRandomString(uriParams));
        query.setAttribute("httpstatuscode", getRandomString(statusCodes));
        query.setAttribute("sizeinbytes", generateRandomIntInRange(1000, 2000));
        query.setAttribute("useragent", getRandomString(userAgents));
        query.setAttribute("xForwardedFor", getRandomString(xForwards));
        query.setAttribute("FP", getRandomString(fp));
        query.setAttribute("TOPO", getRandomString(topo));
        query.setAttribute("greedydata", getRandomString(accessGreedyStrings));
    }

        private String getFormatedTime(LocalTime localTime, String pattern) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(pattern);
        return localTime.format(formatter);
    }

    private String getFormatedDate(LocalDate localDate, String pattern) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(pattern);
        return localDate.format(formatter);
    }

    private String generateIp(){
        Random r = new Random();
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }
    private int generateRandomIntInRange(int start, int end){
        Random r = new Random();
        return start + r.nextInt(end - start);
    }

    private String getRandomString(String[] strings){
        int idx = new Random().nextInt(strings.length);
        String random = (strings[idx]);
        return random;
    }
}

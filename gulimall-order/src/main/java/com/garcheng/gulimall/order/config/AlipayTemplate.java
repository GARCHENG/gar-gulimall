package com.garcheng.gulimall.order.config;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.garcheng.gulimall.order.vo.PayVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Slf4j
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private String app_id = "9021000129680599";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCrLfSKa+69icYw7l8YUi0ALBt6cARa+wDe4NkOxra7aFBHb7VRtz75N77eK5YNBQEaJIeCzWKk/+QvHJTneTz5ACYKanLZa4m4h6nSq5wu2rWfFgb1mAAN5IRewLo+XUx/CJaQR/Vt4fCGlkhG2GZtBI1qBGoJatNlJP4ZTBZhWPT5gmRHoNsuVi97QWXdDiw3mSb/29FcqhpkMXPhY5QmctElcjRx4BzHQPdQShsBgUS1Du4MqqeuySSYQ0f24SVuXBmE3ceMtlcLyJQOIt5ZAeWmOaIGP7BuYDWP65dllyxcvA5yVbHlypAj0b302S43LHXU8/WiqmAX2+ChupirAgMBAAECggEBAI5OlxSYZBJYPlahZJcggd65zGI+S7CjKPcg7/IzyugD8XFh8dgsQgMHj/q9O3I5bdAg/DMGsfI/9aAg372LceADepyAIFiV6RlqVwkXt7DQC9tkgYacxbbG+jCXgn2kLJsTNbBe0i92fPI2b4HOF2CcP0AayVho4bMdTM0VDyn/XfikLMaJaSE6UrfHZTJcEUeADnRoQZFJ8nhwtCYu73q2OCPq1mgg75xRABBjmr/SwMAMSVFstThv4r8qyj7LmOHTMRvCiidBVwzCP7UhZn0FLbtOPu3NGO4ULd6ltRyXAtbMulK9Devvm9oL9hYMfVNx7nOOmiYJxU1Qfezq/1ECgYEA74CS7ExM6m0nYwx8Eaciw2cuRPAASqx1IoeZl73rQb6EeQial5rXN+xkvenMXXDjc64Lhi+h2pGGQFPrzCH9Zn8bbtMq5Kq3fS7d4qRvY50GTj+ffNKDZ4J1D/ZMKFnXHOkV+pzcy5DRVFozotqw0WpqOaqUlRSdm4wZ5kP62rkCgYEAtviQ3hYNpQIj8Kn7xLLv3sXNMccz4rNd4Yux07IMIE9ZSaA6Tr9dn1TiChi5bXbEOJohPabK1OFunMHKR97XnYK54AZWnjd9sP0GwGUy/+YIeyxgMx6U7gAo+3B5M5PcqzcJkUS+09yAMhiCh0txyGfHCGvGEaV5LMSHTwj8DIMCgYA6pZFUq5pqDTCH4JbA+9xXXPzHhL2Ni41jVzEVJzxkbPKjcKfqV9A7sXc5yfZxKyOHQCcyRJetndTyC5rB8dDLOHTPPoZSWDt2O/O5e+qKyfixUIMNXb8vqZJgxj7/4n/6ZFktdRvb/SpFLZFXX5XMsfIxDMQ1fUbawAPXju3ugQKBgBNclD/cB4+ZfWIfKrQy1y00szT8RmJL5rhSRvwG0D+vBdzDpsfkeiy9F7pHyX9q3PK8ZJ4Yg0gpnsHZw+T9EMXrKm4pXYsahiNvcwJy8wWfOvCFzpw6NTpGLBAHY9vwXp6DkXCc3LCgmbQzYcSdWCoDyUz70pbVctAo78SOR9WvAoGBAKTluILfuyYbdPCJhuRi8A1cv/rnpXEgv+8vEvXCDFjXN1TKrEL44EbK8XisPbFRGKHHuOtwFo/IflgSmf6GBqiTOMmQIjF4n//ULF/tOA2cP++LBYMz1xZXAih/aZKlNt/sgJD4RfUphO7vIFDzA51YvCl73xY0t2ha93l/fvMu";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjSBYfSZdo2eGNAKoDdRpv6QuybyN0D9vQW57wKYGxzJJdvZ0fvMIHCpIEXhEdnxaAuOLO+R5MbOlAXIWR2DeiBuowr8HerJrjDg5SlFAb5+VZTaQIy41ueKh6boIu1zIhYoxG0wYB0+PO3fgMzAs/ZMGVYICPFXzj+cogEq5MJK1T61mvBO6rXUwKYzQfN+jaY0GCnr1zlwKN7u9I7EwkSppFx9a94gjhdhP24LwkmykNsxXXz3Hf+iRoIF8/lDu6zvy0J9UERkUaqiYwwoOuCK4MGugf69gSqL8COdnDM/H7oKf8Eve7X6iK2vjyFELcdVXMn84APLgpLSs8uXp0QIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url = "https://780j80t677.goho.co/alipay/notify_url";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url = "http://member.gulimall.com/memberOrder.html";

    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "utf-8";

    private String timeoutExpress = "1m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"" + timeoutExpress + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);

        return result;

    }

    public void AlipayClose(String outTradeNo) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl, app_id, merchant_private_key, "json", charset, alipay_public_key, sign_type);
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);
        request.setBizContent(bizContent.toString());
        AlipayTradeCloseResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            log.warn("支付宝手动关单成功");
        } else {
            log.error("支付宝手动关单失败");
        }
    }

}

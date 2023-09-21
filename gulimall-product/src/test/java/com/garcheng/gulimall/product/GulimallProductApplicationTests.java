package com.garcheng.gulimall.product;

import com.garcheng.gulimall.product.dao.AttrGroupDao;
import com.garcheng.gulimall.product.dao.SkuSaleAttrValueDao;
import com.garcheng.gulimall.product.vo.SkuItemSaleAttrVo;
import com.garcheng.gulimall.product.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.util.List;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Test
    public void contextLoads() throws Exception {

//        // RAM用户的访问密钥（AccessKey ID和AccessKey Secret）。
//        String accessKeyId = "";
//        String accessKeySecret = "";
//// 使用代码嵌入的RAM用户的访问密钥配置访问凭证。
//        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);
//        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
//        String endpoint = "";
////        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
////        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
//        // 填写Bucket名称，例如examplebucket。
//        String bucketName = "gar-gulimall";
//        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
//        String objectName = "test/test.txt";
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);
//
//        try {
//            String content = "Hello OSS";
//            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content.getBytes()));
//        } catch (OSSException oe) {
//            System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                    + "but was rejected with an error response for some reason.");
//            System.out.println("Error Message:" + oe.getErrorMessage());
//            System.out.println("Error Code:" + oe.getErrorCode());
//            System.out.println("Request ID:" + oe.getRequestId());
//            System.out.println("Host ID:" + oe.getHostId());
//        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
//        }
//        ossClient.putObject("gar-gulimall","test2.webp",new FileInputStream("C:\\Users\\jzgar\\Desktop\\pic\\HomeMainRotation\\banner1.webp"));
//        System.out.println("上传成功");

    }

    @Test
    public void test(){
        System.out.println(redissonClient);
    }

    @Test
    public void test1(){
        List<SpuItemAttrGroupVo> attrGroupWithAttrBySpuIdAndCatelogId = attrGroupDao.findAttrGroupWithAttrBySpuIdAndCatelogId(1l, 225l);
    }

    @Test
    public void test3(){
        List<SkuItemSaleAttrVo> saleAttrVosBySpuId = skuSaleAttrValueDao.findSaleAttrVosBySpuId(1l);
    }

}

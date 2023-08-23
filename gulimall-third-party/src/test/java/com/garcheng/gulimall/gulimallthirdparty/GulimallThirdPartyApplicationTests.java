package com.garcheng.gulimall.gulimallthirdparty;

import com.aliyun.oss.OSSClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallThirdPartyApplicationTests {

    @Autowired
    private OSSClient ossClient;

    @Test
    public void contextLoads() throws FileNotFoundException {
        ossClient.putObject("gar-gulimall","test3.webp",new FileInputStream("C:\\Users\\jzgar\\Desktop\\pic\\HomeMainRotation\\banner1-min.webp"));
        System.out.println("上传成功");
        ossClient.shutdown();

    }

}

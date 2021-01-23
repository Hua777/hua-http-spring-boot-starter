package com.github.hua777.huahttp.test;

import com.github.hua777.huahttp.test.global.Constant;
import com.github.hua777.huahttp.test.http.DeleteService;
import com.github.hua777.huahttp.test.http.GetService;
import com.github.hua777.huahttp.test.http.HeaderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCase {

    @Autowired
    HeaderService headerService;

    @Autowired
    GetService getService;

    @Autowired
    DeleteService deleteService;

    @Test
    public void testHeader() {
        Map<String, String> headers = headerService.case1(Constant.TEST);
        Assert.assertEquals(headers.get(Constant.PARAM_HEADER), Constant.TEST);
        Assert.assertEquals(headers.get(Constant.TOP_HEADER), Constant.TEST);
        Assert.assertEquals(headers.get(Constant.MY_HEADER), Constant.TEST);
        Assert.assertEquals(headers.get(Constant.CREATE_HEADER), Constant.TEST);
    }

    @Test
    public void testGet() {
        Assert.assertEquals(getService.case1(), Constant.TEST);
        Assert.assertEquals(getService.case2(Constant.TEST), Constant.TEST);
        Assert.assertEquals(getService.case2rename(Constant.TEST), Constant.TEST);
        Assert.assertEquals(getService.case3(Constant.TEST), Constant.TEST);
        Assert.assertEquals(getService.case3rename(Constant.TEST), Constant.TEST);
        Assert.assertEquals(getService.case4(Constant.TEST, Constant.TEST), Constant.TEST + "," + Constant.TEST);
        Assert.assertEquals(getService.case5(Constant.TEST, Constant.TEST, Constant.TEST), Constant.TEST + "," + Constant.TEST + "," + Constant.TEST);
    }

    @Test
    public void testDelete() {
        Assert.assertEquals(deleteService.case1(), Constant.TEST);
        Assert.assertEquals(deleteService.case2(Constant.TEST), Constant.TEST);
        Assert.assertEquals(deleteService.case2rename(Constant.TEST), Constant.TEST);
        Assert.assertEquals(deleteService.case3(Constant.TEST), Constant.TEST);
        Assert.assertEquals(deleteService.case3rename(Constant.TEST), Constant.TEST);
        Assert.assertEquals(deleteService.case4(Constant.TEST, Constant.TEST), Constant.TEST + "," + Constant.TEST);
        Assert.assertEquals(deleteService.case5(Constant.TEST, Constant.TEST, Constant.TEST), Constant.TEST + "," + Constant.TEST + "," + Constant.TEST);
    }

}

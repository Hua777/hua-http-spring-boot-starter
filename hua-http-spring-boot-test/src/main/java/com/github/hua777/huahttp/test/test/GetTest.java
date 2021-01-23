package com.github.hua777.huahttp.test.test;

import com.github.hua777.huahttp.test.global.Constant;
import com.github.hua777.huahttp.test.http.GetService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetTest {

    @Autowired
    GetService getService;

    @Test
    public void test() {
        Assert.assertEquals(getService.case1(), Constant.TEST);
        Assert.assertEquals(getService.case2(Constant.TEST), Constant.TEST);
        Assert.assertEquals(getService.case2rename(Constant.TEST), Constant.TEST);
        Assert.assertEquals(getService.case3(Constant.TEST), Constant.TEST);
        Assert.assertEquals(getService.case3rename(Constant.TEST), Constant.TEST);
        Assert.assertEquals(getService.case4(Constant.TEST, Constant.TEST), Constant.TEST + "," + Constant.TEST);
        Assert.assertEquals(getService.case5(Constant.TEST, Constant.TEST, Constant.TEST), Constant.TEST + "," + Constant.TEST + "," + Constant.TEST);
    }

}
